package controllers;

import com.avaje.ebean.annotation.Transactional;
import exception.GiffytRESTAPIException;
import formatter.GiffytDateTimeFormatter;
import mapper.GiffytObjectMapper;
import models.*;
import org.codehaus.jackson.JsonNode;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormatter;
import play.data.Form;
import play.i18n.Messages;
import play.libs.Akka;
import play.libs.F;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import response.APIResponse;
import serializers.OrderSerializer;
import serializers.ProductSimpleSerializer;

import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: ltanady
 * Date: 10/5/13
 * Time: 12:01 AM
 * To change this template use File | Settings | File Templates.
 */
public class Orders extends Controller {

    /**
     * Create and save a new order
     * @return Result
     * @throws GiffytRESTAPIException
     */
    @Transactional
    @BodyParser.Of(BodyParser.Json.class)
    public static Result save() throws GiffytRESTAPIException {
        play.Logger.info("Orders.save(): start");
        DateTimeFormatter dateTimeFormatter = GiffytDateTimeFormatter.getDateTimeFormatter(GiffytDateTimeFormatter.UK_DATE);
        DateTime preferredNotificationDate = null;
        DateTime preferredDeliveryDate = null;
        final JsonNode jsonNode = request().body().asJson();

        final GiffytObjectMapper objectMapper = new GiffytObjectMapper();
        objectMapper.addSerializer(new ProductSimpleSerializer());
        objectMapper.addSerializer(new OrderSerializer());
        objectMapper.registerModule();

        try {
            if(jsonNode == null) {
                play.Logger.info("Orders.save(): BadRequest, " + Messages.get("EXPECTING_JSON_DATA"));
                APIResponse apiResponse = new APIResponse(request().path(), null, Messages.get("EXPECTING_JSON_DATA"));
                return badRequest(objectMapper.writeValueAsString(apiResponse));

            }

            // Check for valid product
            JsonNode productIdNode = jsonNode.findPath("productId");
            if(productIdNode.isMissingNode()) {
                play.Logger.info("Orders.save(): BadRequest, " + Messages.get("MISSING_PARAMETER", "productId"));
                APIResponse apiResponse = new APIResponse(request().path(), null, Messages.get("MISSING_PARAMETER", "productId"));
                return badRequest(objectMapper.writeValueAsString(apiResponse));

            }
            final Long productId = productIdNode.getLongValue();
            final Product product = Product.findById(productId);
            if(product == null) {
                play.Logger.info("Orders.save(): BadRequest, " + Messages.get("INVALID_OBJECT", "product", productId));
                APIResponse apiResponse = new APIResponse(request().path(), null, Messages.get("INVALID_OBJECT", "product", productId));
                return badRequest(objectMapper.writeValueAsString(apiResponse));

            }

            // Check for valid delivery area option
            DeliveryAreaOption deliveryAreaOption = null;
            JsonNode deliveryAreaOptionIdNode = jsonNode.findPath("deliveryAreaOptionId");
            if(!deliveryAreaOptionIdNode.isMissingNode()) {
                Long deliveryAreaOptionId = deliveryAreaOptionIdNode.getLongValue();
                deliveryAreaOption = product.getValidDeliveryAreaOption(deliveryAreaOptionId);
                if(deliveryAreaOption == null) {
                    play.Logger.info("Orders.save(): BadRequest, " + Messages.get("INVALID_OBJECT", "deliveryAreaOption", deliveryAreaOptionId));
                    APIResponse apiResponse = new APIResponse(request().path(), null, Messages.get("INVALID_OBJECT", "deliveryAreaOption", deliveryAreaOptionId));
                    return badRequest(objectMapper.writeValueAsString(apiResponse));

                }

            }

            // Check for valid delivery time option
            DeliveryTimeOption deliveryTimeOption = null;
            JsonNode deliveryTimeOptionIdNode = jsonNode.findPath("deliveryTimeOptionId");
            if(!deliveryTimeOptionIdNode.isMissingNode()) {
                Long deliveryTimeOptionId = deliveryTimeOptionIdNode.getLongValue();
                deliveryTimeOption = product.getValidDeliveryTimeOption(deliveryTimeOptionId);
                if(deliveryTimeOption == null) {
                    play.Logger.info("Orders.save(): BadRequest, " + Messages.get("INVALID_OBJECT", "deliveryTimeOption", deliveryTimeOptionId));
                    APIResponse apiResponse = new APIResponse(request().path(), null, Messages.get("INVALID_OBJECT", "deliveryTimeOption", deliveryTimeOptionId));
                    return badRequest(objectMapper.writeValueAsString(apiResponse));

                }

            }

            JsonNode preferredNotificationDateNode = jsonNode.findPath("preferredNotificationDate");
            if(!preferredNotificationDateNode.isMissingNode()) {
                preferredNotificationDate = dateTimeFormatter.parseDateTime(preferredNotificationDateNode.getTextValue());

            }

            JsonNode preferredDeliveryDateNode = jsonNode.findPath("preferredDeliveryDateNode");
            if(!preferredDeliveryDateNode.isMissingNode()) {
                preferredDeliveryDate = dateTimeFormatter.parseDateTime(preferredDeliveryDateNode.getTextValue());

            }

            final DeliveryAreaOption tempDeliveryAreaOption = deliveryAreaOption;
            final DeliveryTimeOption tempDeliveryTimeOption = deliveryTimeOption;

            // Bind form from json data
            final Form<Order> orderForm = Form.form(Order.class).bind(jsonNode);
            if(orderForm.hasErrors()) {
                play.Logger.info("Orders.save(): BadRequest, " + Messages.get("MISSING_PARAMETER", orderForm.errorsAsJson()));
                APIResponse apiResponse = new APIResponse(request().path(), null, Messages.get("MISSING_PARAMETER", orderForm.errorsAsJson()));
                return badRequest(objectMapper.writeValueAsString(apiResponse));

            }


            F.Promise<Order> orderPromise = Akka.future(new Callable<Order>() {
                @Override
                public Order call() throws Exception {
                    play.Logger.info("Orders.save(): creating new Order");
                    Order order = orderForm.get();
                    order.product = product;
                    order.deliveryAreaOption = tempDeliveryAreaOption;
                    order.deliveryTimeOption = tempDeliveryTimeOption;
                    if(order.preferredNotificationDate == null)
                        order.preferredNotificationDate = new DateTime();

                    if(order.preferredDeliveryDate == null) {
                        order.preferredDeliveryDate = new DateTime().plus(Period.days(order.product.minimumOrderDays.intValue()));


                    }
                    return Order.create(order);

                }
            });

            F.Promise<Result> resultPromise = orderPromise.map(new F.Function<Order, Result>() {
                @Override
                public Result apply(Order order) throws Throwable {
                    APIResponse apiResponse = null;
                    if(order == null) {
                        play.Logger.info("Orders.save(): BadRequest, " + Messages.get("INVALID_OBJECT", "order", null));
                        apiResponse = new APIResponse(request().path(), null, Messages.get("INVALID_OBJECT", "order", null));
                        return badRequest(objectMapper.writeValueAsString(apiResponse));

                    }
                    apiResponse = new APIResponse(request().path(), order, Messages.get("ORDER_CREATED", order.id));
                    play.Logger.info("Orders.save(): " + Messages.get("ORDER_CREATED", order.id));
                    return ok(objectMapper.writeValueAsString(apiResponse));

                }
            });
            return async(resultPromise);

        }catch(IOException e) {
            play.Logger.error("Orders.save(): IOException, " + e.getMessage() );
            APIResponse apiResponse = new APIResponse(request().path(), null, e.getMessage());
            throw new GiffytRESTAPIException(apiResponse, e);

        }catch(Exception e) {
            e.printStackTrace();
            play.Logger.error("Orders.save(): Exception, " + e.getMessage() );
            APIResponse apiResponse = new APIResponse(request().path(), null, e.getMessage());
            throw  new GiffytRESTAPIException(apiResponse, e);

        }

    }

}
