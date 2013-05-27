package global;

import akka.actor.ActorRef;
import akka.actor.Props;
import exception.GiffytRESTAPIException;
import jobs.SendNotificationsJob;
import mapper.GiffytObjectMapper;
import play.Application;
import play.GlobalSettings;
import play.Play;
import play.i18n.Messages;
import play.libs.Akka;
import play.mvc.Http;
import play.mvc.Result;
import response.APIResponse;
import scala.concurrent.duration.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static play.mvc.Results.badRequest;
import static play.mvc.Results.internalServerError;
import static play.mvc.Results.unauthorized;

/**
 * Created with IntelliJ IDEA.
 * User: ltanady
 * Date: 23/4/13
 * Time: 3:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class Global extends GlobalSettings {

    /**
     * Perform tasks when application starts
     * @param app
     */
    @Override
    public void onStart(Application app) {
        if(Play.isDev()) {
            play.Logger.info("Global.onStart(): STARTING APPLICATION IN DEV MODE......");

        }else if(Play.isProd()) {
            play.Logger.info("Global.onStart(): STARTING APPLICATION IN PROD MODE......");

        }
        play.Logger.info("Global.onStart(): creating SendNotificationsJob");
        new SendNotificationsJob();

    }

    @Override
    public Result onBadRequest(Http.RequestHeader request, String error) {

        try {
            play.Logger.error("Global.onBadRequest(): " + request.remoteAddress());
            GiffytObjectMapper objectMapper = new GiffytObjectMapper();
            objectMapper.registerModule();

            APIResponse apiResponse = new APIResponse(request.path(), null, error);

            return badRequest(objectMapper.writeValueAsString(apiResponse));

        }catch(Exception e) {
            play.Logger.error("Global.onError(): " + e.getClass());
            play.Logger.error("Global.onError(): " + e.getMessage());
            return onError(request, e);

        }

    }

    // In progress, Still need Mailer
    @Override
    public Result onError(Http.RequestHeader request, Throwable t) {

        Exception exception = Exception.class.cast(t.getCause());
        play.Logger.error("Global.onError(): " + request.remoteAddress());

        try {
            // Know exception, GiffytRESTAPIException
            if(exception instanceof GiffytRESTAPIException) {
                play.Logger.error("Global.onError(): GiffytRESTAPIException");
                play.Logger.error("Global.onError(): " + exception.getMessage());

                GiffytObjectMapper objectMapper = new GiffytObjectMapper();
                objectMapper.registerModule();

                GiffytRESTAPIException giffytRESTAPIException = (GiffytRESTAPIException) exception;

                return internalServerError(objectMapper.writeValueAsString(giffytRESTAPIException.apiResponse));

            }else {
                play.Logger.error("Global.onError(): " + exception.getClass());
                play.Logger.error("Global.onError(): " + exception.getMessage());
                exception.printStackTrace();

                return internalServerError(exception.getMessage());

            }
        }catch(Exception e) {
            play.Logger.error("Global.onError(): " + e.getClass());
            play.Logger.error("Global.onError(): " + e.getMessage());
            return internalServerError(e.getMessage());

        }

    }

}
