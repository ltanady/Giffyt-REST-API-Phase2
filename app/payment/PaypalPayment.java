package payment;

import models.Order;
import models.Product;
import play.libs.F;
import play.libs.WS;
import util.ConfigReader;

import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: ltanady
 * Date: 14/5/13
 * Time: 1:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class PaypalPayment {

    private static final String ENVIRONMENT = ConfigReader.getValue("paypal.environment");

    private static final String SANDBOX_API_URL = ConfigReader.getValue("paypal.sandbox.url");
    private static final String SANDBOX_USER = ConfigReader.getValue("paypal.sandbox.user");
    private static final String SANDBOX_PWD = ConfigReader.getValue("paypal.sandbox.password");
    private static final String SANDBOX_SIGNATURE = ConfigReader.getValue("paypal.sandbox.signature");
    private static final String SANDBOX_VERSION = ConfigReader.getValue("paypal.sandbox.version");
    private static final String SANDBOX_CANCEL_URL = ConfigReader.getValue("paypal.sandbox.cancelurl");
    private static final String SANDBOX_RETURN_URL = ConfigReader.getValue("paypal.sandbox.returnurl");

    private static final String PRODUCTION_API_URL = ConfigReader.getValue("paypal.production.url");
    private static final String PRODUCTION_USER = ConfigReader.getValue("paypal.production.user");
    private static final String PRODUCTION_PWD = ConfigReader.getValue("paypal.production.password");
    private static final String PRODUCTION_SIGNATURE = ConfigReader.getValue("paypal.production.signature");
    private static final String PRODUCTION_VERSION = ConfigReader.getValue("paypal.production.version");
    private static final String PRODUCTION_CANCEL_URL = ConfigReader.getValue("paypal.production.cancelurl");
    private static final String PRODUCTION_RETURN_URL = ConfigReader.getValue("paypal.production.returnurl");

    private static String url;
    private static String user;
    private static String pwd;
    private static String signature;
    private static String version;
    private static String cancelUrl;
    private static String returnUrl;

    static {
        if(ENVIRONMENT.equalsIgnoreCase("SANDBOX")) {
            url = SANDBOX_API_URL;
            user = SANDBOX_USER;
            pwd = SANDBOX_PWD;
            signature = SANDBOX_SIGNATURE;
            version = SANDBOX_VERSION;
            cancelUrl = SANDBOX_CANCEL_URL;
            returnUrl = SANDBOX_RETURN_URL;

        }else if(ENVIRONMENT.equalsIgnoreCase("PRODUCTION")) {
            url = PRODUCTION_API_URL;
            user = PRODUCTION_USER;
            pwd = PRODUCTION_PWD;
            signature = PRODUCTION_SIGNATURE;
            version = PRODUCTION_VERSION;
            cancelUrl = PRODUCTION_CANCEL_URL;
            returnUrl = PRODUCTION_RETURN_URL;

        }
    }

    private static void initialize() {

    }

    /**
     * Authorize payment
     * @param order
     * @return Boolean
     */
    public static Boolean authorizePayment(Order order) {
        Boolean isSuccessful = false;

        // Initiate paypal payment.
        play.Logger.info("Payments.authorizePayment(): Initiating payment authorization for order " + order.id);
        String authorizePaymentResponse = setExpressCheckout(order);
        HashMap<String, Object> authorizePaymentResponseMap = PaypalPayment.decodeResponse(authorizePaymentResponse);
        if(authorizePaymentResponseMap.containsKey("ACK") &&
                authorizePaymentResponseMap.get("ACK").toString().equalsIgnoreCase("Success") &&
                authorizePaymentResponseMap.get("TOKEN") != null) {

            // Save token only for pending payment status
            if(order.orderStatus == Order.ORDER_STATUS.PENDING_PAYMENT) {
                order.paypalToken = authorizePaymentResponseMap.get("TOKEN").toString();
                play.Logger.info("PaypalPayment.authorizePayment(): Payment authorization for order " + order.id + " OK");
                isSuccessful = true;

            }else {
                isSuccessful = false;

            }

        }
        order.save();


        return isSuccessful;

    }

    /**
     * Confirm payment
     * @param order
     * @return Boolean
     */
    public static Boolean confirmPayment(Order order) {
        Boolean isSuccessful = false;

        // Get payment details.
        Double totalAmount = 0.0;
        if(order.paypalToken != null) {
            play.Logger.info("Payments.confirmPayment(): Getting payment details for order " + order.id);
            String paymentDetailsResponse = getExpressCheckoutDetails(order.paypalToken);
            HashMap<String, Object> paymentDetailsResponseMap = PaypalPayment.decodeResponse(paymentDetailsResponse);
            if(paymentDetailsResponseMap.containsKey("ACK") && paymentDetailsResponseMap.get("ACK").toString().equalsIgnoreCase("Success")) {
                String payerId = paymentDetailsResponseMap.get("PAYERID").toString();
                order.paypalPayerId = payerId;
                totalAmount = Double.parseDouble(paymentDetailsResponseMap.get("PAYMENTREQUEST_0_AMT").toString());
                play.Logger.info("Payment.confirmPayment(): Get payment details for order " + order.id + " OK");
                isSuccessful = true;

            }else {
                isSuccessful = false;

            }

        }

        // Confirm payment authorization.
        if(order.paypalToken != null && order.paypalPayerId != null && totalAmount != 0.0) {
            play.Logger.info("Payments.confirmPayment(): Confirming payment authorization for order " + order.id);
            String doExpresschecoutPaymentResponse = doExpressCheckoutPayment(order.paypalToken, order.paypalPayerId, totalAmount);
            HashMap<String, Object> doExpresschecoutPaymentResponseMap = PaypalPayment.decodeResponse(doExpresschecoutPaymentResponse);
            if(doExpresschecoutPaymentResponseMap.containsKey("ACK") && doExpresschecoutPaymentResponseMap.get("ACK").toString().equalsIgnoreCase("Success")) {
                String authorizationId = (String)doExpresschecoutPaymentResponseMap.get("PAYMENTINFO_0_TRANSACTIONID");
                order.paypalAuthorizationId = authorizationId;
                order.orderStatus = Order.ORDER_STATUS.PENDING_RECIPIENT;
                play.Logger.info("Payments.confirmPayment(): Payment for order " + order.id + " confirmed");
                isSuccessful = true;

            }else {
                isSuccessful = false;

            }

        }
        order.save(); // update the order

        // Order is a surprise or send to self, capture payment immediately.
        if(order.isSurprise || order.senderEmail.equalsIgnoreCase(order.recipientEmail)) {
            PaypalPayment.capturePayment(order);

        }
        return isSuccessful;

    }

    /**
     * Capture payment
     * @param order
     * @return Boolean
     */
    public static Boolean capturePayment(Order order) {
        Boolean isSuccessful = false;

        play.Logger.info("Payments.capturePayment(): Capturing payment for order " + order.id);
        String doCaptureResponse = doCapture(order);
        HashMap<String, Object> doCaptureResponseMap = PaypalPayment.decodeResponse(doCaptureResponse);
        if(doCaptureResponseMap.containsKey("ACK") && doCaptureResponseMap.get("ACK").toString().equalsIgnoreCase("Success")) {
            order.orderStatus = Order.ORDER_STATUS.CONFIRMED;
            isSuccessful = true;

        }else {
            isSuccessful = false;

        }
        order.save();

        return isSuccessful;

    }

    /**
     * Set express check out action for paypal
     * @param order
     * @return String
     */
    private static String setExpressCheckout(Order order) {
        play.Logger.info("PaypalPayment.setExpressCheckout(): start");
        String setExpressCheckoutResponse = null;
        DecimalFormat twoDecimalForm = new DecimalFormat("#.##");

        String returnUrl = url + "/" + order.id;
        Double taxAmount = 0.0;
        Double totalAmount = 0.0;

        if(order != null && order.product != null) {
            Product product = order.product;
            totalAmount = order.initialAmount;

            WS.WSRequestHolder setExpressCheckoutRequestHolder = WS.url(url);
            String setExpressCheckoutBody = "USER=" + user + "&" +
                    "PWD=" + pwd + "&" +
                    "SIGNATURE=" + signature + "&" +
                    "METHOD=SetExpressCheckout&" +
                    "VERSION=" + version + "&" +
                    "PAYMENTREQUEST_0_PAYMENTACTION=Authorization&" +
                    "PAYMENTREQUEST_0_ITEMAMT=" + twoDecimalForm.format(product.price) + "&" +
                    "PAYMENTREQUEST_0_SHIPPINGAMT=" + twoDecimalForm.format(Order.calculateShippingAndHandling(order)) + "&" +
                    "PAYMENTREQUEST_0_TAXAMT=" + twoDecimalForm.format(taxAmount) + "&" +
                    "PAYMENTREQUEST_0_CURRENCYCODE=" +  product.getCountry().currency + "&" +
                    "PAYMENTREQUEST_0_AMT=" + twoDecimalForm.format(totalAmount) + "&" +
                    "cancelUrl=" + cancelUrl +"&" +
                    "returnUrl=" + returnUrl + "&" +
                    "NOSHIPPING=1&";

            String productBody = "L_PAYMENTREQUEST_0_NAME0=" + product.code + "&" +
                    "L_PAYMENTREQUEST_0_NUMBER0=" + product.id + "&" +
                    "L_PAYMENTREQUEST_0_DESC0="+ product.brand + "&" +
                    "L_PAYMENTREQUEST_0_AMT0=" + product.price + "&" +
                    "L_PAYMENTREQUEST_0_QTY0=1";

            setExpressCheckoutBody += productBody;

            F.Promise<WS.Response> responsePromise = setExpressCheckoutRequestHolder.post(setExpressCheckoutBody);
            F.Promise<String> stringPromise = responsePromise.map(new F.Function<WS.Response, String>() {
                @Override
                public String apply(WS.Response response) throws Throwable {
                    return response.getBody();

                }
            });
            setExpressCheckoutResponse = stringPromise.get(120L, TimeUnit.SECONDS);

        }
        play.Logger.info("PaypalPayment.setExpressCheckout(): end");
        return setExpressCheckoutResponse;

    }

    /**
     * Get express checkout details for paypal
     * @param token
     * @return String
     */
    private static String getExpressCheckoutDetails(String token) {
        play.Logger.info("PaypalPayment.getExpressCheckoutDetails(): start");
        String getExpressCheckoutDetailsResponse = null;

        if(token != null) {
            WS.WSRequestHolder getExpressCheckoutDetailsRequestHolder = WS.url(url);
            String checkoutDetailsBody = "USER=" + user + "&" +
                    "PWD=" + pwd + "&" +
                    "SIGNATURE=" + signature + "&" +
                    "METHOD=GetExpressCheckoutDetails&" +
                    "VERSION=" + version + "&" +
                    "TOKEN=" + token;


            F.Promise<WS.Response> responsePromise = getExpressCheckoutDetailsRequestHolder.post(checkoutDetailsBody);
            F.Promise<String> stringPromise = responsePromise.map(new F.Function<WS.Response, String>() {
                @Override
                public String apply(WS.Response response) throws Throwable {
                    return response.getBody();

                }
            });
            getExpressCheckoutDetailsResponse = stringPromise.get(120L, TimeUnit.SECONDS);

        }
        play.Logger.info("PaypalPayment.getExpressCheckoutDetails(): end");
        return getExpressCheckoutDetailsResponse;

    }

    /**
     * Do express checkout payment for paypal
     * @param token
     * @param PayerID
     * @param totalAmount
     * @return String
     */
    private static String doExpressCheckoutPayment(String token, String PayerID, Double totalAmount) {
        play.Logger.info("PaypalPayment.doExpressCheckoutPayment(): start");
        String doExpressCheckoutResponse = null;

        if(token != null && PayerID != null && totalAmount != null) {
            WS.WSRequestHolder doExpressCheckoutRequestHolder = WS.url(url);
            String doExpresschecoutPaymentBody = "USER=" + user + "&" +
                    "PWD=" + pwd + "&" +
                    "SIGNATURE=" + signature + "&" +
                    "METHOD=DoExpressCheckoutPayment&" +
                    "VERSION=" + version + "&" +
                    "TOKEN=" + token + "&" +
                    "PAYERID=" + PayerID + "&" +
                    "PAYMENTREQUEST_0_PAYMENTACTION=Authorization&" +
                    "PAYMENTREQUEST_0_AMT=" + totalAmount + "&" +
                    "PAYMENTREQUEST_0_CURRENCYCODE=SGD";


            F.Promise<WS.Response> responsePromise = doExpressCheckoutRequestHolder.post(doExpresschecoutPaymentBody);
            F.Promise<String> stringPromise = responsePromise.map(new F.Function<WS.Response, String>() {
                @Override
                public String apply(WS.Response response) throws Throwable {
                    return response.getBody();

                }
            });
            doExpressCheckoutResponse = stringPromise.get(120L, TimeUnit.SECONDS);

        }
        play.Logger.info("PaypalPayment.doExpressCheckoutPayment(): end");

        return doExpressCheckoutResponse;

    }

    /**
     * Do capture for paypal
     * @param order
     * @return String
     */
    private static String doCapture(Order order) {
        play.Logger.info("PaypalPayment.doCapture(): start");
        String doCaptureResponse = null;

        if(order != null && order.paypalAuthorizationId != null) {
            WS.WSRequestHolder doCaptureRequestHolder = WS.url(url);
            String doCaptureBody = "USER=" + user + "&" +
                    "PWD=" + pwd + "&" +
                    "SIGNATURE=" + signature + "&" +
                    "METHOD=DoCapture" + "&" +
                    "VERSION=" + version + "&" +
                    "AUTHORIZATIONID=" + order.paypalAuthorizationId + "&" +
                    "AMT=" + order.finalAmount + "&" +
                    "CURRENCYCODE=" + order.product.getCountry().currency + "&" +
                    "COMPLETETYPE=Complete";

            F.Promise<WS.Response> responsePromise = doCaptureRequestHolder.post(doCaptureBody);
            F.Promise<String> stringPromise = responsePromise.map(new F.Function<WS.Response, String>() {
                @Override
                public String apply(WS.Response response) throws Throwable {
                    return response.getBody();

                }
            });
            doCaptureResponse = stringPromise.get(120L, TimeUnit.SECONDS);

        }
        play.Logger.info("PaypalPayment.doCapture(): end");

        return doCaptureResponse;

    }

    /**
     * Decode response for paypal responses
     * @param responseBody
     * @return HashMap<String, Object>
     */
    private static HashMap<String, Object> decodeResponse(String responseBody) {
        HashMap<String, Object> responseMap = new HashMap<String, Object>();
        try {
            for(String param: responseBody.split("&")) {
                String pair[] = param.split("=");
                if(pair.length == 2) {
                    String key = URLDecoder.decode(pair[0], "UTF-8");
                    String value = URLDecoder.decode(pair[1], "UTF-8");
                    responseMap.put(key, value);

                }

            }
            return responseMap;

        }catch(Exception e) {
            return null;

        }

    }


}
