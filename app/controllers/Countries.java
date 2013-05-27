package controllers;

import exception.GiffytRESTAPIException;
import mapper.GiffytObjectMapper;
import models.Country;
import models.Product;
import org.codehaus.jackson.JsonNode;
import play.i18n.Messages;
import play.libs.Akka;
import play.libs.F;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import response.APIResponse;
import serializers.CountrySerializer;
import serializers.MerchantSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: ltanady
 * Date: 18/5/13
 * Time: 12:29 AM
 * To change this template use File | Settings | File Templates.
 */
public class Countries extends Controller {

    /**
     * Get list of countries
     * @return Result
     * @throws exception.GiffytRESTAPIException
     */
    @BodyParser.Of(BodyParser.Json.class)
    public static Result list() throws GiffytRESTAPIException {
        play.Logger.info("Countries.list(): start");
        JsonNode jsonNode = request().body().asJson();

        final GiffytObjectMapper objectMapper = new GiffytObjectMapper();
        objectMapper.addSerializer(new CountrySerializer());
        objectMapper.registerModule();

        try {

            if(jsonNode == null) {
                play.Logger.error("Countries.list(): BadRequest, " + Messages.get("EXPECTING_JSON_DATA"));
                APIResponse apiResponse = new APIResponse(request().path(), null, Messages.get("EXPECTING_JSON_DATA"));
                return badRequest(objectMapper.writeValueAsString(apiResponse));

            }

            F.Promise<List<Country>> countriesPromise = Akka.future(new Callable<List<Country>>() {
                @Override
                public List<Country> call() throws Exception {
                    return Country.find.where().eq("isActive", true).orderBy("code asc").findList();

                }
            });

            F.Promise<Result> resultPromise = countriesPromise.map(new F.Function<List<Country>, Result>() {
                @Override
                public Result apply(List<Country> countries) throws Throwable {
                    Integer numCountries = (countries != null) ? countries.size() : 0;

                    APIResponse apiResponse = new APIResponse(request().path(), countries, Messages.get("RECORD_FOUND", numCountries, "Countries"));
                    play.Logger.info("Countries.list(): get list of countries ok");
                    return ok(objectMapper.writeValueAsString(apiResponse));


                }
            });
            play.Logger.info("Countries.list(): end");
            return async(resultPromise);

        }catch(Exception e) {
            play.Logger.error("Countries.list(): Exception, " + e.getMessage() );
            APIResponse apiResponse = new APIResponse(request().path(), null, e.getMessage());
            throw  new GiffytRESTAPIException(apiResponse, e);

        }

    }

}
