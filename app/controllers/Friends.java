package controllers;

import exception.GiffytRESTAPIException;
import facebook.FacebookEvent;
import mapper.GiffytObjectMapper;
import models.Country;
import models.User;
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
import serializers.ProductSerializer;
import serializers.UserSerializer;
import serializers.facebook.FacebookEventSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: ltanady
 * Date: 22/4/13
 * Time: 6:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class Friends extends Controller {

    /**
     * Get Facebook Events
     * @return Result
     * @throws GiffytRESTAPIException
     */
    @BodyParser.Of(BodyParser.Json.class)
    public static Result events() throws GiffytRESTAPIException {
        play.Logger.info("Friends.events(): start");
        JsonNode jsonNode = request().body().asJson();

        final GiffytObjectMapper objectMapper = new GiffytObjectMapper();
        objectMapper.addSerializer(new UserSerializer());
        objectMapper.addSerializer(new FacebookEventSerializer());
        objectMapper.addSerializer(new CountrySerializer());
        objectMapper.registerModule();

        try {
            if(jsonNode == null) {
                play.Logger.info("Friends.events(): BadRequest, " + Messages.get("EXPECTING_JSON_DATA"));
                APIResponse apiResponse = new APIResponse(request().path(), null, Messages.get("EXPECTING_JSON_DATA"));
                return badRequest(objectMapper.writeValueAsString(apiResponse));

            }
            JsonNode facebookAccessTokenNode = jsonNode.findPath("facebookAccessToken");
            if(facebookAccessTokenNode.isMissingNode()) {
                play.Logger.error("Friends.events(): BadRequest, " + Messages.get("MISSING_PARAMETER", "facebookAccessToken"));
                APIResponse apiResponse = new APIResponse(request().path(), null, Messages.get("MISSING_PARAMETER", "facebookAccessToken"));
                return badRequest(objectMapper.writeValueAsString(apiResponse));

            }
            final String facebookAccessToken = facebookAccessTokenNode.getTextValue();

            F.Promise<FacebookEvent> facebookEventPromise = Akka.future(new Callable<FacebookEvent>() {
                @Override
                public FacebookEvent call() throws Exception {
                    play.Logger.info("Friends.events(): getting Facebook events");
                    return User.getFacebookEvents(facebookAccessToken);

                }
            });

            F.Promise<Result> resultPromise = facebookEventPromise.map(new F.Function<FacebookEvent, Result>() {
                @Override
                public Result apply(FacebookEvent facebookEvent) throws Throwable {
                    APIResponse apiResponse = null;
                    if(facebookEvent == null) {
                        play.Logger.error("Friends.events(): BadRequest, " + Messages.get("INVALID_OBJECT", "events", facebookAccessToken));
                        apiResponse = new APIResponse(request().path(), null, Messages.get("INVALID_OBJECT", "events", facebookAccessToken));
                        return badRequest(objectMapper.writeValueAsString(apiResponse));

                    }
                    apiResponse = new APIResponse(request().path(), facebookEvent, Messages.get("RECORD_FOUND", 1, "Event(s)"));
                    play.Logger.info("Friends.events(): get Facebook events ok");
                    return ok(objectMapper.writeValueAsString(apiResponse));

                }
            });
            play.Logger.info("Friends.events(): end");
            return async(resultPromise);

        }catch(IOException e) {
            play.Logger.error("Friends.events(): IOException, " + e.getMessage() );
            APIResponse apiResponse = new APIResponse(request().path(), null, e.getMessage());
            throw new GiffytRESTAPIException(apiResponse, e);

        }catch(Exception e) {
            play.Logger.error("Friends.events(): Exception, " + e.getMessage() );
            APIResponse apiResponse = new APIResponse(request().path(), null, e.getMessage());
            throw  new GiffytRESTAPIException(apiResponse, e);

        }

    }

    /**
     * Get Facebook Friends
     * @return Result
     * @throws GiffytRESTAPIException
     */
    @BodyParser.Of(BodyParser.Json.class)
    public static Result friends() throws GiffytRESTAPIException {
        play.Logger.info("Friends.friends(): start");
        JsonNode jsonNode = request().body().asJson();

        final GiffytObjectMapper objectMapper = new GiffytObjectMapper();
        objectMapper.addSerializer(new UserSerializer());
        objectMapper.addSerializer(new CountrySerializer());
        objectMapper.registerModule();

        try {
            if(jsonNode == null) {
                play.Logger.info("Friends.friends(): BadRequest, " + Messages.get("EXPECTING_JSON_DATA"));
                APIResponse apiResponse = new APIResponse(request().path(), null, Messages.get("EXPECTING_JSON_DATA"));
                return badRequest(objectMapper.writeValueAsString(apiResponse));

            }
            JsonNode facebookAccessTokenNode = jsonNode.findPath("facebookAccessToken");
            if(facebookAccessTokenNode.isMissingNode()) {
                play.Logger.error("Friends.friends(): BadRequest, " + Messages.get("MISSING_PARAMETER", "facebookAccessToken"));
                APIResponse apiResponse = new APIResponse(request().path(), null, Messages.get("MISSING_PARAMETER", "facebookAccessToken"));
                return badRequest(objectMapper.writeValueAsString(apiResponse));

            }
            final String facebookAccessToken = facebookAccessTokenNode.getTextValue();

            F.Promise<List<User>> facebookFriendsPromise = Akka.future(new Callable<List<User>>() {
                @Override
                public List<User> call() throws Exception {
                    play.Logger.info("Friends.friends(): getting Facebook friends");
                    return User.getFacebookFriends(facebookAccessToken);

                }
            });

            F.Promise<Result> resultPromise = facebookFriendsPromise.map(new F.Function<List<User>, Result>() {
                @Override
                public Result apply(List<User> facebookFriends) throws Throwable {
                    APIResponse apiResponse = null;
                    if(facebookFriends == null) {
                        play.Logger.error("Friends.friends(): BadRequest, " + Messages.get("INVALID_OBJECT", "friends", facebookAccessToken));
                        apiResponse = new APIResponse(request().path(), null, Messages.get("INVALID_OBJECT", "friends", facebookAccessToken));
                        return badRequest(objectMapper.writeValueAsString(apiResponse));

                    }
                    apiResponse = new APIResponse(request().path(), facebookFriends, Messages.get("RECORD_FOUND", facebookFriends.size(), "Friend(s)"));
                    play.Logger.info("Friends.friends(): get Facebook friends ok");
                    return ok(objectMapper.writeValueAsString(apiResponse));

                }
            });
            play.Logger.info("Friends.friends(): end");
            return async(resultPromise);


        }catch(IOException e) {
            play.Logger.error("Friends.friends(): IOException, " + e.getMessage() );
            APIResponse apiResponse = new APIResponse(request().path(), null, e.getMessage());
            throw new GiffytRESTAPIException(apiResponse, e);

        }catch(Exception e) {
            play.Logger.error("Friends.friends(): Exception, " + e.getMessage() );
            APIResponse apiResponse = new APIResponse(request().path(), null, e.getMessage());
            throw  new GiffytRESTAPIException(apiResponse, e);

        }

    }

    public static Result get() throws GiffytRESTAPIException {
        play.Logger.info("Friends.get(): start");
        JsonNode jsonNode = request().body().asJson();
        final GiffytObjectMapper objectMapper = new GiffytObjectMapper();
        objectMapper.addSerializer(new UserSerializer());
        objectMapper.addSerializer(new CountrySerializer());
        objectMapper.registerModule();

        try {
            if(jsonNode == null) {
                play.Logger.error("Friends.get(): BadRequest, " + Messages.get("EXPECTING_JSON_DATA"));
                APIResponse apiResponse = new APIResponse(request().path(), null, Messages.get("EXPECTING_JSON_DATA"));
                return badRequest(objectMapper.writeValueAsString(apiResponse));

            }
            JsonNode facebookAccessTokenNode = jsonNode.findPath("facebookAccessToken");
            if(facebookAccessTokenNode.isMissingNode()) {
                play.Logger.error("Friends.get(): BadRequest, " + Messages.get("MISSING_PARAMETER", "facebookAccessToken"));
                APIResponse apiResponse = new APIResponse(request().path(), null, Messages.get("MISSING_PARAMETER", "facebookAccessToken"));
                return badRequest(objectMapper.writeValueAsString(apiResponse));

            }
            JsonNode friendIdNode = jsonNode.findPath("friendId");
            if(friendIdNode.isMissingNode()) {
                play.Logger.error("Friends.get(): BadRequest, " + Messages.get("MISSING_PARAMETER", "friendId"));
                APIResponse apiResponse = new APIResponse(request().path(), null, Messages.get("MISSING_PARAMETER", "friendId"));
                return badRequest(objectMapper.writeValueAsString(apiResponse));

            }
            final String facebookAccessToken = facebookAccessTokenNode.getTextValue();
            final String friendId = friendIdNode.getTextValue();

            F.Promise<User> friendPromise = Akka.future(new Callable<User>() {
                @Override
                public User call() throws Exception {
                    User friend = User.getFacebookFriendInfo(facebookAccessToken, friendId);
                    if(friend == null) {
                        friend = new User();
                        friend.email = friendId;

                    }
                    return friend;
                }
            });

            F.Promise<Result> resultPromise = friendPromise.map(new F.Function<User, Result>() {
                @Override
                public Result apply(User facebookFriend) throws Throwable {
                    APIResponse apiResponse = null;
                    if(facebookFriend == null) {
                        play.Logger.error("Friends.get(): BadRequest, " + Messages.get("INVALID_OBJECT", "friend", facebookAccessToken));
                        apiResponse = new APIResponse(request().path(), null, Messages.get("INVALID_OBJECT", "friend", facebookAccessToken));
                        return badRequest(objectMapper.writeValueAsString(apiResponse));

                    }
                    apiResponse = new APIResponse(request().path(), facebookFriend, Messages.get("RECORD_FOUND", 1, "Friend"));
                    play.Logger.info("Friends.get(): get Facebook friend ok");
                    return ok(objectMapper.writeValueAsString(apiResponse));

                }
            });
            return async(resultPromise);

        }catch(IOException e) {
            play.Logger.error("Friends.get(): IOException, " + e.getMessage() );
            APIResponse apiResponse = new APIResponse(request().path(), null, e.getMessage());
            throw new GiffytRESTAPIException(apiResponse, e);

        }catch(Exception e) {
            play.Logger.error("Friends.get(): Exception, " + e.getMessage() );
            APIResponse apiResponse = new APIResponse(request().path(), null, e.getMessage());
            throw  new GiffytRESTAPIException(apiResponse, e);

        }

    }

    /**
     * Get list of items according to selected friend's current location,
     * return nothing if the friend has no country or invalid country.
     * @return Result
     * @throws GiffytRESTAPIException
     */
    @BodyParser.Of(BodyParser.Json.class)
    public static Result celebrateFriend() throws GiffytRESTAPIException {
        play.Logger.info("Friends.celebrateFriend(): start");
        JsonNode jsonNode = request().body().asJson();
        final GiffytObjectMapper objectMapper = new GiffytObjectMapper();
        objectMapper.addSerializer(new UserSerializer());
        objectMapper.addSerializer(new CountrySerializer());
        objectMapper.addSerializer(new ProductSerializer());
        objectMapper.registerModule();

        try {
            if(jsonNode == null) {
                play.Logger.error("Friends.celebrateFriend(): BadRequest, " + Messages.get("EXPECTING_JSON_DATA"));
                APIResponse apiResponse = new APIResponse(request().path(), null, Messages.get("EXPECTING_JSON_DATA"));
                return badRequest(objectMapper.writeValueAsString(apiResponse));

            }
            JsonNode facebookAccessTokenNode = jsonNode.findPath("facebookAccessToken");
            if(facebookAccessTokenNode.isMissingNode()) {
                play.Logger.error("Friends.celebrateFriend(): BadRequest, " + Messages.get("MISSING_PARAMETER", "facebookAccessToken"));
                APIResponse apiResponse = new APIResponse(request().path(), null, Messages.get("MISSING_PARAMETER", "facebookAccessToken"));
                return badRequest(objectMapper.writeValueAsString(apiResponse));

            }
            JsonNode friendIdNode = jsonNode.findPath("friendId");
            if(friendIdNode.isMissingNode()) {
                play.Logger.error("Friends.celebrateFriend(): BadRequest, " + Messages.get("MISSING_PARAMETER", "friendId"));
                APIResponse apiResponse = new APIResponse(request().path(), null, Messages.get("MISSING_PARAMETER", "friendId"));
                return badRequest(objectMapper.writeValueAsString(apiResponse));

            }
            final String facebookAccessToken = facebookAccessTokenNode.getTextValue();
            final String friendId = friendIdNode.getTextValue();
            play.Logger.info("Friends.celebrateFriend(): Getting Facebook friend info");
            final User friend = User.getFacebookFriendInfo(facebookAccessToken, friendId);

            F.Promise<Map<String, List<Product>>> productsPromise = Akka.future(new Callable<Map<String, List<Product>>>() {
                @Override
                public Map<String, List<Product>> call() throws Exception {
                    Map<String, List<Product>> productsMap = null;

                    if(friend != null && friend.getCountry() != null) {
                        play.Logger.info("Friends.celebrateFriend(): Getting products for " + friend.name + " (" + friend.uid + ")");
                        List<Product> products = new ArrayList<Product>();
                        List<Product> nonCampaignProducts = Product.findByCountry(friend.getCountry());
                        if(nonCampaignProducts != null)
                            products.addAll(nonCampaignProducts);

                        play.Logger.info("Friends.celebrateFriend(): Grouping products for " + friend.name + " (" + friend.uid + ")");
                        productsMap = Product.groupProducts(products);

                    }
                    return productsMap;

                }
            });

            F.Promise<Result> resultPromise = productsPromise.map(new F.Function<Map<String, List<Product>>, Result>() {
                @Override
                public Result apply(Map<String, List<Product>> productsMap) throws Throwable {

                    Integer productsCount = productsMap != null ? productsMap.size() : 0;
                    final List<Country> countryList = Country.find.where().eq("isActive", true).findList();
                    HashMap<String, Object> friendHashMap = new HashMap<String, Object>() {};
                    friendHashMap.put("friend", friend);
                    friendHashMap.put("countries", countryList); // get list of available countries
                    friendHashMap.put("products", productsMap);

                    APIResponse apiResponse = new APIResponse(request().path(), friendHashMap, Messages.get("RECORD_FOUND", productsCount, "products"));
                    play.Logger.info("Friends.celebrateFriend(): OK");

                    return ok(objectMapper.writeValueAsString(apiResponse));
                }
            });
            return async(resultPromise);

        }catch(IOException e) {
            play.Logger.error("Friends.friends(): IOException, " + e.getMessage() );
            APIResponse apiResponse = new APIResponse(request().path(), null, e.getMessage());
            throw new GiffytRESTAPIException(apiResponse, e);

        }catch(Exception e) {
            play.Logger.error("Friends.friends(): Exception, " + e.getMessage() );
            APIResponse apiResponse = new APIResponse(request().path(), null, e.getMessage());
            throw  new GiffytRESTAPIException(apiResponse, e);

        }

    }

}
