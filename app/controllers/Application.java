package controllers;

import exception.GiffytRESTAPIException;
import mapper.GiffytObjectMapper;
import models.Country;
import models.Product;
import models.User;
import org.codehaus.jackson.JsonNode;
import play.db.ebean.Transactional;
import play.i18n.Messages;
import play.libs.Akka;
import play.libs.F;
import play.mvc.*;
import response.APIResponse;
import serializers.UserSerializer;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

public class Application extends Controller {
  
    public static Result index() {
        return ok(views.html.index.render("Your new application is ready."));

    }

    /**
     * Login via Facebook
     * @return Result
     * @throws GiffytRESTAPIException
     */
    @Transactional
    @BodyParser.Of(BodyParser.Json.class)
    public static Result login() throws GiffytRESTAPIException {
        play.Logger.info("Application.login(): start");
        JsonNode jsonNode = request().body().asJson();

        final GiffytObjectMapper objectMapper = new GiffytObjectMapper();
        objectMapper.addSerializer(new UserSerializer());
        objectMapper.registerModule();

        try {
            if(jsonNode == null) {
                play.Logger.info("Application.login(): BadRequest, " + Messages.get("EXPECTING_JSON_DATA"));
                APIResponse apiResponse = new APIResponse(request().path(), null, Messages.get("EXPECTING_JSON_DATA"));
                return badRequest(objectMapper.writeValueAsString(apiResponse));

            }
            JsonNode facebookAccessTokenNode = jsonNode.findPath("facebookAccessToken");
            if(facebookAccessTokenNode.isMissingNode()) {
                play.Logger.error("Application.login(): BadRequest, " + Messages.get("MISSING_PARAMETER", "facebookAccessToken"));
                APIResponse apiResponse = new APIResponse(request().path(), null, Messages.get("MISSING_PARAMETER", "facebookAccessToken"));
                return badRequest(objectMapper.writeValueAsString(apiResponse));

            }
            final String facebookAccessToken = facebookAccessTokenNode.getTextValue();

            F.Promise<User> facebookUserPromise = Akka.future(new Callable<User>() {
                @Override
                public User call() throws Exception {
                    play.Logger.info("Application.login(): authenticating via Facebook");
                    return User.authenticateFacebook(facebookAccessToken);

                }
            });

            F.Promise<Result> resultPromise = facebookUserPromise.map(new F.Function<User, Result>() {
                @Override
                public Result apply(User facebookUser) throws Throwable {
                    APIResponse apiResponse = null;
                    if(facebookUser == null) {
                        play.Logger.error("Application.login(): BadRequest, " + Messages.get("INVALID_OBJECT", "facebookAccessToken", facebookAccessToken));
                        apiResponse = new APIResponse(request().path(), null, Messages.get("INVALID_OBJECT", "facebookAccessToken", facebookAccessToken));
                        return badRequest(objectMapper.writeValueAsString(apiResponse));

                    }
                    session().put(facebookAccessToken, facebookUser.uid);
                    apiResponse = new APIResponse(request().path(), facebookUser, Messages.get("RECORD_FOUND", 1, "User"));
                    play.Logger.info("Application.login(): Facebook authentication ok");
                    return ok(objectMapper.writeValueAsString(apiResponse));

                }
            });
            play.Logger.info("Application.login(): end");
            return async((resultPromise));

        }catch(IOException e) {
            play.Logger.error("Application.login(): IOException, " + e.getMessage() );
            APIResponse apiResponse = new APIResponse(request().path(), null, e.getMessage());
            throw new GiffytRESTAPIException(apiResponse, e);

        }catch(Exception e) {
            play.Logger.error("Application.login(): Exception, " + e.getMessage() );
            APIResponse apiResponse = new APIResponse(request().path(), null, e.getMessage());
            throw  new GiffytRESTAPIException(apiResponse, e);

        }

    }
  
}
