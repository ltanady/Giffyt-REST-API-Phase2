package controllers;

import exception.GiffytRESTAPIException;
import global.Global;
import mapper.GiffytObjectMapper;
import models.Order;
import models.Product;
import models.User;
import org.codehaus.jackson.JsonNode;
import play.i18n.Messages;
import play.libs.Akka;
import play.libs.F;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import response.APIResponse;
import serializers.OrderSerializer;
import serializers.ProductSerializer;
import serializers.ProductSimpleSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: ltanady
 * Date: 18/5/13
 * Time: 3:31 AM
 * To change this template use File | Settings | File Templates.
 */
public class Gifts extends Controller {

    /**
     * Get gifts count from friends with PENDING_RECIPIENT status.
     * @return Result
     * @throws GiffytRESTAPIException
     */
    @BodyParser.Of(BodyParser.Json.class)
    public static Result count() throws GiffytRESTAPIException {
        play.Logger.info("Gifts.count(): start");
        JsonNode jsonNode = request().body().asJson();

        final GiffytObjectMapper objectMapper = new GiffytObjectMapper();
        objectMapper.addSerializer(new ProductSimpleSerializer());
        objectMapper.addSerializer(new OrderSerializer());
        objectMapper.registerModule();

        try {
            if(jsonNode == null) {
                play.Logger.error("Gifts.count(): BadRequest, " + Messages.get("EXPECTING_JSON_DATA"));
                APIResponse apiResponse = new APIResponse(request().path(), null, Messages.get("EXPECTING_JSON_DATA"));
                return badRequest(objectMapper.writeValueAsString(apiResponse));

            }

            JsonNode facebookAccessTokenNode = jsonNode.findPath("facebookAccessToken");

            if(facebookAccessTokenNode.isMissingNode()) {
                play.Logger.error("Gifts.count(): BadRequest, " + Messages.get("MISSING_PARAMETER", "facebookAccessToken"));
                APIResponse apiResponse = new APIResponse(request().path(), null, Messages.get("MISSING_PARAMETER", "facebookAccessToken"));
                return badRequest(objectMapper.writeValueAsString(apiResponse));

            }

            final User user = User.authenticateFacebook(facebookAccessTokenNode.getTextValue());

            // Facebook Access Token invalid
            if(user == null) {
                play.Logger.error("Gifts.count(): BadRequest, " + Messages.get("FACEBOOK_ACCESS_TOKEN_INVALID"));
                APIResponse apiResponse = new APIResponse(request().path(), null, Messages.get("FACEBOOK_ACCESS_TOKEN_INVALID"));
                return badRequest(objectMapper.writeValueAsString(apiResponse));
            }

            F.Promise<Long> giftsPromise = Akka.future(new Callable<Long>() {
                @Override
                public Long call() throws Exception {
                    return (user.getGifts() != null) ? user.getGifts().size() : 0L;

                }
            });

            F.Promise<Result> resultPromise = giftsPromise.map(new F.Function<Long, Result>() {
                @Override
                public Result apply(Long giftsCount) throws Throwable {

                    APIResponse apiResponse = new APIResponse(request().path(), giftsCount, Messages.get("RECORD_FOUND", giftsCount, "Gift(s)"));
                    play.Logger.info("Gifts.count(): get gifts count ok");
                    return ok(objectMapper.writeValueAsString(apiResponse));

                }
            });
            return async(resultPromise);

        }catch(IOException e) {
            play.Logger.error("Gifts.count(): IOException, " + e.getMessage() );
            APIResponse apiResponse = new APIResponse(request().path(), null, e.getMessage());
            throw new GiffytRESTAPIException(apiResponse, e);

        }catch(Exception e) {
            e.printStackTrace();
            play.Logger.error("Gifts.count(): Exception, " + e.getMessage() );
            APIResponse apiResponse = new APIResponse(request().path(), null, e.getMessage());
            throw  new GiffytRESTAPIException(apiResponse, e);

        }

    }

    /**
     * Get a list of gifts from friends with PENDING_RECIPIENT status.
     * @return Result
     * @throws GiffytRESTAPIException
     */
    @BodyParser.Of(BodyParser.Json.class)
    public static Result list() throws GiffytRESTAPIException {
        play.Logger.info("Gifts.list(): start");
        JsonNode jsonNode = request().body().asJson();

        final GiffytObjectMapper objectMapper = new GiffytObjectMapper();
        objectMapper.addSerializer(new ProductSimpleSerializer());
        objectMapper.addSerializer(new OrderSerializer());
        objectMapper.registerModule();

        try {
            if(jsonNode == null) {
                play.Logger.error("Gifts.list(): BadRequest, " + Messages.get("EXPECTING_JSON_DATA"));
                APIResponse apiResponse = new APIResponse(request().path(), null, Messages.get("EXPECTING_JSON_DATA"));
                return badRequest(objectMapper.writeValueAsString(apiResponse));

            }

            JsonNode facebookAccessTokenNode = jsonNode.findPath("facebookAccessToken");

            if(facebookAccessTokenNode.isMissingNode()) {
                play.Logger.error("Gifts.list(): BadRequest, " + Messages.get("MISSING_PARAMETER", "facebookAccessToken"));
                APIResponse apiResponse = new APIResponse(request().path(), null, Messages.get("MISSING_PARAMETER", "facebookAccessToken"));
                return badRequest(objectMapper.writeValueAsString(apiResponse));

            }

            final User user = User.authenticateFacebook(facebookAccessTokenNode.getTextValue());

            // Facebook Access Token invalid
            if(user == null) {
                play.Logger.error("Gifts.list(): BadRequest, " + Messages.get("INVALID_OBJECT", "facebookAccessToken", facebookAccessTokenNode.getTextValue()));
                APIResponse apiResponse = new APIResponse(request().path(), null, Messages.get("INVALID_OBJECT", "facebookAccessToken", facebookAccessTokenNode.getTextValue()));
                return badRequest(objectMapper.writeValueAsString(apiResponse));

            }

            F.Promise<List<Order>> giftsPromise = Akka.future(new Callable<List<Order>>() {
                @Override
                public List<Order> call() throws Exception {
                    return user.getGifts();

                }
            });

            F.Promise<Result> resultPromise = giftsPromise.map(new F.Function<List<Order>, Result>() {
                @Override
                public Result apply(List<Order> gifts) throws Throwable {
                    Integer numGifts = (gifts != null) ? gifts.size() : 0;

                    APIResponse apiResponse = new APIResponse(request().path(), gifts, Messages.get("RECORD_FOUND", numGifts, "Gift(s)"));
                    play.Logger.info("Gifts.list(): get list of gifts ok");
                    return ok(objectMapper.writeValueAsString(apiResponse));

                }
            });
            return async(resultPromise);

        }catch(IOException e) {
            play.Logger.error("Gifts.list(): IOException, " + e.getMessage() );
            APIResponse apiResponse = new APIResponse(request().path(), null, e.getMessage());
            throw new GiffytRESTAPIException(apiResponse, e);

        }catch(Exception e) {
            e.printStackTrace();
            play.Logger.error("Gifts.list(): Exception, " + e.getMessage() );
            APIResponse apiResponse = new APIResponse(request().path(), null, e.getMessage());
            throw  new GiffytRESTAPIException(apiResponse, e);

        }

    }

    /**
     * Get the gift by temporaryToken with PENDING_RECIPIENT status.
     * @return Result
     * @throws GiffytRESTAPIException
     */
    @BodyParser.Of(BodyParser.Json.class)
    public static Result get() throws GiffytRESTAPIException {
        play.Logger.info("Gifts.get(): start");
        JsonNode jsonNode = request().body().asJson();

        final GiffytObjectMapper objectMapper = new GiffytObjectMapper();
        objectMapper.addSerializer(new ProductSerializer());
        objectMapper.addSerializer(new OrderSerializer());
        objectMapper.registerModule();

        try {
            if(jsonNode == null) {
                play.Logger.error("Gifts.get(): BadRequest, " + Messages.get("EXPECTING_JSON_DATA"));
                APIResponse apiResponse = new APIResponse(request().path(), null, Messages.get("EXPECTING_JSON_DATA"));
                return badRequest(objectMapper.writeValueAsString(apiResponse));

            }

            final JsonNode temporaryTokenNode = jsonNode.findPath("temporaryToken");
            if(temporaryTokenNode.isMissingNode()) {
                play.Logger.error("Gifts.get(): BadRequest, " + Messages.get("MISSING_PARAMETER", "temporaryToken"));
                APIResponse apiResponse = new APIResponse(request().path(), null, Messages.get("MISSING_PARAMETER", "temporaryToken"));
                return badRequest(objectMapper.writeValueAsString(apiResponse));

            }

            F.Promise<HashMap<String, Object>> giftPromise = Akka.future(new Callable<HashMap<String, Object>>() {
                @Override
                public HashMap<String, Object> call() throws Exception {
                    HashMap<String, Object> giftHashMap = new HashMap<String, Object>();

                    Order gift = Order.findByTemporaryToken(temporaryTokenNode.getTextValue(), Order.ORDER_STATUS.PENDING_RECIPIENT);
                    List<Product> products = Product.findByCountryAndBudget(gift.product.getCountry(), gift.initialAmount);
                    Map<String, List<Product>> productsMap = Product.groupProducts(products);

                    giftHashMap.put("gift", gift);
                    giftHashMap.put("otherProducts", productsMap);

                    return giftHashMap;
                }
            });

            F.Promise<Result> resultPromise = giftPromise.map(new F.Function<HashMap<String, Object>, Result>() {
                @Override
                public Result apply(HashMap<String, Object> giftHashMap) throws Throwable {
                    APIResponse apiResponse = null;
                    if(giftHashMap == null) {
                        play.Logger.error("Gifts.get(): BadRequest, " + Messages.get("INVALID_OBJECT", "temporaryToken", temporaryTokenNode.getTextValue()));
                        apiResponse = new APIResponse(request().path(), null, Messages.get("INVALID_OBJECT", "temporaryToken", temporaryTokenNode.getTextValue()));
                        return badRequest(objectMapper.writeValueAsString(apiResponse));

                    }
                    apiResponse = new APIResponse(request().path(), giftHashMap, Messages.get("RECORD_FOUND", 1, "gift"));
                    return ok(objectMapper.writeValueAsString(apiResponse));

                }

            });
            return async(resultPromise);

        }catch(IOException e) {
            play.Logger.error("Gifts.get(): IOException, " + e.getMessage() );
            APIResponse apiResponse = new APIResponse(request().path(), null, e.getMessage());
            throw new GiffytRESTAPIException(apiResponse, e);

        }catch(Exception e) {
            e.printStackTrace();
            play.Logger.error("Gifts.get(): Exception, " + e.getMessage() );
            APIResponse apiResponse = new APIResponse(request().path(), null, e.getMessage());
            throw  new GiffytRESTAPIException(apiResponse, e);

        }

    }

}
