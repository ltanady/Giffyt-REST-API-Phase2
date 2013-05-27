package controllers;

import exception.GiffytRESTAPIException;
import mapper.GiffytObjectMapper;
import models.Order;
import org.codehaus.jackson.JsonNode;
import payment.PaypalPayment;
import play.db.ebean.Transactional;
import play.i18n.Messages;
import play.libs.Akka;
import play.libs.F;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import response.APIResponse;
import serializers.DeliveryAreaOptionSerializer;
import serializers.OrderSerializer;
import serializers.ProductSimpleSerializer;

import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: ltanady
 * Date: 23/11/12
 * Time: 8:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class Payments extends Controller {

    /**
     * Payment authorization
     * @return Result
     * @throws GiffytRESTAPIException
     */
    @Transactional
    @BodyParser.Of(BodyParser.Json.class)
    public static Result authorizePaypalPayment() throws GiffytRESTAPIException {
        play.Logger.info("Payments.authorizePaypalPayment(): start");
        JsonNode json = request().body().asJson();
        final GiffytObjectMapper objectMapper = new GiffytObjectMapper();
        objectMapper.addSerializer(new ProductSimpleSerializer());
        objectMapper.addSerializer(new OrderSerializer());
        objectMapper.registerModule();

        try {

            if(json == null) {
                play.Logger.error("Payments.authorizePaypalPayment(): BadRequest, " + Messages.get("EXPECTING_JSON_DATA"));
                APIResponse apiResponse = new APIResponse(request().path(), null, Messages.get("EXPECTING_JSON_DATA"));
                return badRequest(objectMapper.writeValueAsString(apiResponse));

            }

            JsonNode orderIdNode = json.findPath("orderId");
            if(orderIdNode.isMissingNode()) {
                play.Logger.error("Payments.authorizePaypalPayment(): BadRequest, " + Messages.get("MISSING_PARAMETER", "orderId"));
                APIResponse apiResponse = new APIResponse(request().path(), null, Messages.get("MISSING_PARAMETER", "orderId"));
                return badRequest(objectMapper.writeValueAsString(apiResponse));

            }

            Long orderId = orderIdNode.getLongValue();
            final Order order = Order.findById(orderId, Order.ORDER_STATUS.PENDING_PAYMENT);

            if(order == null) {
                play.Logger.error("Payments.authorizePaypalPayment(): BadRequest, " + Messages.get("ORDER_NOT_FOUND", orderId));
                APIResponse apiResponse = new APIResponse(request().path(), null, Messages.get("MISSING_PARAMETER", "orderId"));

            }

            play.Logger.info("Payments.authorizePaypalPayment(): Authorizing payment for order " + order.id);
            
            F.Promise<Boolean> authorizePaypalPaymentPromise = Akka.future(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return PaypalPayment.authorizePayment(order);

                }
            });

            F.Promise<Result> authorizePaypalPaymentResultPromise = authorizePaypalPaymentPromise.map(new F.Function<Boolean, Result>() {
                @Override
                public Result apply(Boolean isSuccessful) throws Throwable {
                    APIResponse apiResponse = null;
                    if(!isSuccessful) {
                        play.Logger.error("Payments.authorizePaypalPayment(): BadRequest, " + Messages.get("PAYMENT_UNSUCCESSFUL", order.id));
                        apiResponse = new APIResponse(request().path(), null, Messages.get("PAYMENT_UNSUCCESSFUL", order.id));
                        return badRequest(objectMapper.writeValueAsString(apiResponse));

                    }
                    apiResponse = new APIResponse(request().path(), order, Messages.get("PAYMENT_AUTHORIZATION_SUCCESSFUL", order.id));
                    play.Logger.info("Payments.authorizePaypalPayment(): " + Messages.get("PAYMENT_AUTHORIZATION_SUCCESSFUL", order.id));
                    return ok(objectMapper.writeValueAsString(apiResponse));

                }
            });
            return async(authorizePaypalPaymentResultPromise);

        }catch(IOException e) {
            play.Logger.error("Payments.authorizePaypalPayment(): IOException, " + e.getMessage() );
            APIResponse apiResponse = new APIResponse(request().path(), null, e.getMessage());
            throw new GiffytRESTAPIException(apiResponse, e);

        }catch(Exception e) {
            e.printStackTrace();
            play.Logger.error("Payments.authorizePaypalPayment(): Exception, " + e.getMessage() );
            APIResponse apiResponse = new APIResponse(request().path(), null, e.getMessage());
            throw  new GiffytRESTAPIException(apiResponse, e);

        }

    }

    /**
     * Payment authorization
     * @return Result
     * @throws GiffytRESTAPIException
     */
    @Transactional
    @BodyParser.Of(BodyParser.Json.class)
    public static Result confirmPaypalPayment() throws GiffytRESTAPIException {

        play.Logger.info("Payments.confirmPaypalPayment(): start");
        JsonNode json = request().body().asJson();
        final GiffytObjectMapper objectMapper = new GiffytObjectMapper();
        objectMapper.addSerializer(new ProductSimpleSerializer());
        objectMapper.addSerializer(new OrderSerializer());
        objectMapper.registerModule();

        try {

            if(json == null) {
                play.Logger.error("Payments.confirmPaypalPayment(): BadRequest, " + Messages.get("EXPECTING_JSON_DATA"));
                APIResponse apiResponse = new APIResponse(request().path(), null, Messages.get("EXPECTING_JSON_DATA"));
                return badRequest(objectMapper.writeValueAsString(apiResponse));

            }

            JsonNode orderIdNode = json.findPath("orderId");
            if(orderIdNode.isMissingNode()) {
                play.Logger.error("Payments.confirmPaypalPayment(): BadRequest, " + Messages.get("MISSING_PARAMETER", "orderId"));
                APIResponse apiResponse = new APIResponse(request().path(), null, Messages.get("MISSING_PARAMETER", "orderId"));
                return badRequest(objectMapper.writeValueAsString(apiResponse));

            }

            Long orderId = orderIdNode.getLongValue();
            final Order order = Order.findById(orderId, Order.ORDER_STATUS.PENDING_PAYMENT);

            if(order == null) {
                play.Logger.error("Payments.confirmPaypalPayment(): BadRequest, " + Messages.get("ORDER_NOT_FOUND", orderId));
                APIResponse apiResponse = new APIResponse(request().path(), null, Messages.get("MISSING_PARAMETER", "orderId"));

            }

            play.Logger.info("Payments.confirmPaypalPayment(): Confirming payment for order " + order.id);

            F.Promise<Boolean> authorizePaypalPaymentPromise = Akka.future(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return PaypalPayment.confirmPayment(order);

                }
            });

            F.Promise<Result> authorizePaypalPaymentResultPromise = authorizePaypalPaymentPromise.map(new F.Function<Boolean, Result>() {
                @Override
                public Result apply(Boolean isSuccessful) throws Throwable {
                    APIResponse apiResponse = null;
                    if(!isSuccessful) {
                        play.Logger.error("Payments.confirmPaypalPayment(): BadRequest, " + Messages.get("PAYMENT_UNSUCCESSFUL", order.id));
                        apiResponse = new APIResponse(request().path(), null, Messages.get("PAYMENT_UNSUCCESSFUL", order.id));
                        return badRequest(objectMapper.writeValueAsString(apiResponse));

                    }
                    apiResponse = new APIResponse(request().path(), order, Messages.get("PAYMENT_CONFIRMATION_SUCCESSFUL", order.id));
                    play.Logger.info("Payments.confirmPaypalPayment(): " + Messages.get("PAYMENT_CONFIRMATION_SUCCESSFUL", order.id));
                    return ok(objectMapper.writeValueAsString(apiResponse));

                }
            });
            return async(authorizePaypalPaymentResultPromise);

        }catch(IOException e) {
            play.Logger.error("Payments.confirmPaypalPayment(): IOException, " + e.getMessage() );
            APIResponse apiResponse = new APIResponse(request().path(), null, e.getMessage());
            throw new GiffytRESTAPIException(apiResponse, e);

        }catch(Exception e) {
            e.printStackTrace();
            play.Logger.error("Payments.confirmPaypalPayment(): Exception, " + e.getMessage() );
            APIResponse apiResponse = new APIResponse(request().path(), null, e.getMessage());
            throw  new GiffytRESTAPIException(apiResponse, e);

        }

    }

}
