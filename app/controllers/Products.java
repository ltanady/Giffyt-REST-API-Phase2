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
import serializers.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: ltanady
 * Date: 26/4/13
 * Time: 6:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class Products extends Controller {

    /**
     * Get list of products give countryCode and optional budget.
     * @return Result
     * @throws GiffytRESTAPIException
     */
    @BodyParser.Of(BodyParser.Json.class)
    public static Result list() throws GiffytRESTAPIException {
        play.Logger.info("Products.list(): start");
        JsonNode jsonNode = request().body().asJson();

        final GiffytObjectMapper objectMapper = new GiffytObjectMapper();
        objectMapper.addSerializer(new CountrySerializer());
        objectMapper.addSerializer(new MerchantSerializer());
        objectMapper.addSerializer(new ProductSerializer());
        objectMapper.registerModule();

        try {

            if(jsonNode == null) {
                play.Logger.info("Products.list(): BadRequest, " + Messages.get("EXPECTING_JSON_DATA"));
                APIResponse apiResponse = new APIResponse(request().path(), null, Messages.get("EXPECTING_JSON_DATA"));
                return badRequest(objectMapper.writeValueAsString(apiResponse));

            }
            JsonNode countryCodeNode = jsonNode.findPath("countryCode");
            if(countryCodeNode.isMissingNode()) {
                play.Logger.error("Products.list(): BadRequest, " + Messages.get("MISSING_PARAMETER", "countryCode"));
                APIResponse apiResponse = new APIResponse(request().path(), null, Messages.get("MISSING_PARAMETER", "countryCode"));
                return badRequest(objectMapper.writeValueAsString(apiResponse));

            }
            // Optional budget
            final JsonNode budgetNode = jsonNode.findPath("budget");

            final String countryCode = countryCodeNode.getTextValue();
            final Country country = Country.findbyCode(countryCode);

            if(country == null) {
                play.Logger.error("Products.list(): BadRequest, " + Messages.get("INVALID_OBJECT", "country", countryCode));
                APIResponse apiResponse = new APIResponse(request().path(), null, Messages.get("INVALID_OBJECT", "country", countryCode));
                return badRequest(objectMapper.writeValueAsString(apiResponse));

            }
            F.Promise<Map<String, List<Product>>> productsPromise = Akka.future(new Callable<Map<String, List<Product>>>() {
                @Override
                public Map<String, List<Product>> call() throws Exception {
                    play.Logger.info("Products.list(): getting list of products");
                    Map<String, List<Product>> productsMap = null;
                    List<Product> products = new ArrayList<Product>();
                    List<Product> nonCampaignProducts = null;
                    // No budget
                    if(budgetNode.isMissingNode() || budgetNode.isNull())
                        nonCampaignProducts = Product.findByCountry(country);
                    else
                        nonCampaignProducts = Product.findByCountryAndBudget(country, budgetNode.asDouble());

                    if(nonCampaignProducts != null)
                        products.addAll(nonCampaignProducts);

                    productsMap = Product.groupProducts(products);
                    return productsMap;

                }
            });

            F.Promise<Result> resultPromise = productsPromise.map(new F.Function<Map<String, List<Product>>, Result>() {
                @Override
                public Result apply(Map<String, List<Product>> products) throws Throwable {
                    Integer numProducts = (products != null) ? products.size() : 0;

                    APIResponse apiResponse = new APIResponse(request().path(), products, Messages.get("RECORD_FOUND", numProducts, "Product(s)"));
                    play.Logger.info("Products.list(): get list of products ok");
                    return ok(objectMapper.writeValueAsString(apiResponse));

                }
            });
            play.Logger.info("Products.list(): end");
            return async(resultPromise);

        }catch(IOException e) {
            play.Logger.error("Products.list(): IOException, " + e.getMessage() );
            APIResponse apiResponse = new APIResponse(request().path(), null, e.getMessage());
            throw new GiffytRESTAPIException(apiResponse, e);

        }catch(Exception e) {
            play.Logger.error("Products.list(): Exception, " + e.getMessage() );
            APIResponse apiResponse = new APIResponse(request().path(), null, e.getMessage());
            throw  new GiffytRESTAPIException(apiResponse, e);

        }

    }

    /**
     * Get a product given productId
     * @return Result
     * @throws GiffytRESTAPIException
     */
    @BodyParser.Of(BodyParser.Json.class)
    public static Result get() throws GiffytRESTAPIException {
        play.Logger.info("Products.get(): start");
        JsonNode jsonNode = request().body().asJson();

        final GiffytObjectMapper objectMapper = new GiffytObjectMapper();
        objectMapper.addSerializer(new CountrySerializer());
        objectMapper.addSerializer(new MerchantWithDeliverySerializer());
        objectMapper.addSerializer(new DeliveryAreaOptionSerializer());
        objectMapper.addSerializer(new DeliveryTimeOptionSerializer());
        objectMapper.addSerializer(new ProductSerializer());
        objectMapper.registerModule();

        try {
            if(jsonNode == null) {
                play.Logger.info("Products.get(): BadRequest, " + Messages.get("EXPECTING_JSON_DATA"));
                APIResponse apiResponse = new APIResponse(request().path(), null, Messages.get("EXPECTING_JSON_DATA"));
                return badRequest(objectMapper.writeValueAsString(apiResponse));

            }
            JsonNode productIdNode = jsonNode.findPath("productId");
            if(productIdNode.isMissingNode()) {
                play.Logger.error("Products.get(): BadRequest, " + Messages.get("MISSING_PARAMETER", "productId"));
                APIResponse apiResponse = new APIResponse(request().path(), null, Messages.get("MISSING_PARAMETER", "productId"));
                return badRequest(objectMapper.writeValueAsString(apiResponse));

            }
            final Long productId = productIdNode.getLongValue();

            if(productId == null) {
                play.Logger.error("Products.get(): BadRequest, " + Messages.get("MISSING_PARAMETER", "productId"));
                APIResponse apiResponse = new APIResponse(request().path(), null, Messages.get("MISSING_PARAMETER", "productId"));
                return badRequest(objectMapper.writeValueAsString(apiResponse));

            }
            F.Promise<Product> productPromise = Akka.future(new Callable<Product>() {
                @Override
                public Product call() throws Exception {
                    play.Logger.info("Products.get(): getting product");
                    return Product.findById(productId);

                }
            });

            F.Promise<Result> resultPromise = productPromise.map(new F.Function<Product, Result>() {
                @Override
                public Result apply(Product product) throws Throwable {
                    if(product == null) {
                        play.Logger.error("Products.get(): BadRequest, " + Messages.get("INVALID_OBJECT", "product", productId));
                        APIResponse apiResponse = new APIResponse(request().path(), null, Messages.get("INVALID_OBJECT", "product", productId));
                        return badRequest(objectMapper.writeValueAsString(apiResponse));

                    }
                    APIResponse apiResponse = new APIResponse(request().path(), product, Messages.get("RECORD_FOUND", 1, "Product"));
                    play.Logger.info("Products.get(): get product ok");
                    return ok(objectMapper.writeValueAsString(apiResponse));

                }
            });
            play.Logger.info("Products.get(): end");
            return async(resultPromise);

        }catch(IOException e) {
            play.Logger.error("Products.get(): IOException, " + e.getMessage() );
            APIResponse apiResponse = new APIResponse(request().path(), null, e.getMessage());
            throw new GiffytRESTAPIException(apiResponse, e);

        }catch(Exception e) {
            play.Logger.error("Products.get(): Exception, " + e.getMessage() );
            APIResponse apiResponse = new APIResponse(request().path(), null, e.getMessage());
            throw  new GiffytRESTAPIException(apiResponse, e);

        }

    }

}
