package util;

import com.avaje.ebean.SqlRow;
import com.typesafe.plugin.MailerAPI;
import com.typesafe.plugin.MailerPlugin;
import formatter.GiffytDateTimeFormatter;
import models.Order;
import models.ProductAttribute;
import models.StockProduct;
import org.joda.time.format.DateTimeFormatter;
import play.Play;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ltanady
 * Date: 14/5/13
 * Time: 9:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class Mailer {

    public static String SELF_CONFIRMATION_NOTIFICATION_SUBJECT = "Order Confirmation";
    public static String SELF_CONFIRMATION_NOTIFICATION_CONTENT = "Order: ";

    public static String CONFIRMATION_NOTIFICATION_SUBJECT = "Gift Confirmation";
    public static String CONFIRMATION_NOTIFICATION_CONTENT = "You've just sent a gift to ";

    public static String PENDING_NOTIFICATION_SUBJECT = "You've received a GIFT from ";
    public static String PENDING_NOTIFICATION_CONTENT = "Please click the following link to view your gift http://www.giffyt.com/gifts/";

    public static String REMINDER_NOTIFICATION_TO_SENDER = "Reminder - Your friend has not opened the gift";
    public static String REMINDER_NOTIFICATION_TO_RECIPIENT = "Reminder - You haven't opened the gift from ";

    public static String PENDING_PAYMENT_NOTIFICATION_SUBJECT = "Your Friend has selected a GIFT!!";
    public static String PENDING_PAYMENT_NOTIFICATION_CONTENT = "Please click the following link to view the gift that your friend has selected http://www.giffyt.com/gifts/";

    public static String GIFFYT_EXCEPTION_SUBJECT = "Giffyt Exception Encountered";

    public static String NEW_EXCEPTION_SUBJECT = "New Exception Encountered";

    public static String ERROR_SUBJECT = "Error Encountered - Error Code ";

    public static String COMMON_RECIPIENT = "support@giffyt.com";
    public static String DEFAULT_SENDER_EMAIL = ConfigReader.getValue("email.defaultsender");
    public static String DEFAULT_EXCEPTION_EMAIL = ConfigReader.getValue("email.defaulterror");

    public static String FOOTER = "Giffyt is a social application that makes gift giving simple. Check us out at <a href='www.giffyt.com'>www.giffyt.com</a> to learn more about us.";

    private static MailerAPI mail;

    static {
        mail = Play.application().plugin(MailerPlugin.class).email();

    }

    /**
     * Send order notification for self purchases.
     * @param order
     */
    public static void sendSelfOrderNotification(Order order) {
        if(order != null) {
            if(order.product != null) {
                // StockProduct type
                if(order.product instanceof StockProduct) {
                    StockProduct stockProduct = (StockProduct) order.product;
                    ProductAttribute itemAttribute = stockProduct.getProductAttributes().get(0);
                    String attributeName = itemAttribute.name;
                    String attributeValue = itemAttribute.value;
                    String confirmationHeadline = "<h3>" + SELF_CONFIRMATION_NOTIFICATION_CONTENT + stockProduct.name + " (" + attributeName + ": " + attributeValue + ") " + "by " + stockProduct.brand + "</h3>";
                    String addressHeadline = "<h3>Delivery Address: " + order.deliveryDetail.address + ", " + order.deliveryDetail.city + ", " + order.deliveryDetail.state + ", " + order.deliveryDetail.country.name + " " + order.deliveryDetail.postalCode  +"</h3>";
                    String deliveryTimeHeadline = "<h3>Delivery Time Frame: " + order.preferredDeliveryDate + order.deliveryTimeOption.description +"</h3>";
                    mail.addFrom(DEFAULT_SENDER_EMAIL);
                    mail.setSubject(SELF_CONFIRMATION_NOTIFICATION_SUBJECT + " (Reference: " + order.id + ")");
                    mail.addRecipient(order.recipientEmail);
                    mail.sendHtml(
                            "<html xmlns='http://www.w3.org/1999/xhtml'><head>" +
                                    "<head></head>" +
                                    "<body style='font-family:Arial, Helvetica, sans-serif;color:#999;font-size:16px;font-weight:normal;'>" +
                                    "<table width='100%'>" +
                                    "<tr><td>" +
                                    "<img src='https://s3-ap-southeast-1.amazonaws.com/giffyt/logo.png' alt='Giffyt'/>" +
                                    "</td></tr>" +
                                    "<tr align='left'><td>" +
                                    confirmationHeadline +
                                    "</td></tr>" +
                                    "<tr align='left'><td>" +
                                    addressHeadline +
                                    "</td></tr>" +
                                    "<tr align='left'><td>" +
                                    deliveryTimeHeadline +
                                    "</td></tr>" +
                                    "<tr align='left'><td>" +
                                    "<br/>" +
                                    "<div>" + FOOTER + "</div>" +
                                    "</td></tr>" +
                                    "</table>" +
                                    "</body>" +
                                    "</html>");

                }

            }

        }

    }

    /**
     * Send order notification to sender.
     * @param order
     */
    public static void sendOrderNotification(Order order) {
        if(order != null) {
            if(order.product != null) {
                if(order.product instanceof StockProduct) {
                    mail.addFrom(DEFAULT_SENDER_EMAIL);
                    mail.setSubject(CONFIRMATION_NOTIFICATION_SUBJECT);
                    mail.addRecipient(order.senderEmail);
                    mail.sendHtml(
                            "<html xmlns='http://www.w3.org/1999/xhtml'><head>" +
                                    "<head></head>" +
                                    "<body style='font-family:Arial, Helvetica, sans-serif;color:#999;font-size:16px;font-weight:normal;'>" +
                                    "<table width='100%'>" +
                                    "<tr><td>" +
                                    "<img src='https://s3-ap-southeast-1.amazonaws.com/giffyt/logo.png' alt='Giffyt'/>" +
                                    "</td></tr>" +
                                    "<tr align='center'><td>" +
                                    "<h3>" + CONFIRMATION_NOTIFICATION_CONTENT + order.recipientEmail + "(" + order.recipientEmail  + ")." + " (Order reference: " + order.id + ")" + "</h3>" +
                                    "</td></tr>" +
                                    "<tr align='left'><td>" +
                                    "<br/>" +
                                    "<div>" + FOOTER + "</div>" +
                                    "</td></tr>" +
                                    "</table>" +
                                    "</body>" +
                                    "</html>");

                }

            }

        }

    }

    /**
     * Send gift notification to sender.
     * @param order
     */
    public static void sendGiftNotification(Order order) {
        if(order != null) {
            if(order.product != null) {
                if(order.product instanceof StockProduct) {
                    mail.addFrom(DEFAULT_SENDER_EMAIL);
                    mail.setSubject(PENDING_NOTIFICATION_SUBJECT + order.senderName);
                    mail.addRecipient(order.recipientEmail);
                    mail.sendHtml(
                            "<html xmlns='http://www.w3.org/1999/xhtml'><head>" +
                                    "<head></head>" +
                                    "<body style='font-family:Arial, Helvetica, sans-serif;color:#999;font-size:16px;font-weight:normal;'>" +
                                    "<table width='100%'>" +
                                    "<tr><td>" +
                                    "<img src='https://s3-ap-southeast-1.amazonaws.com/giffyt/logo.png' alt='Giffyt'/>" +
                                    "</td></tr>" +
                                    "<tr align='center'><td>" +
                                    "<h3>" + order.senderName + " has sent you a gift." + " (Gift reference: " + order.id + ")" + "</h3>" +
                                    "<div>To view your gift, click here</div>" +
                                    "<div><a href='http://www.giffyt.com/gifts/" + order.temporaryToken + "'> <img src='https://s3-ap-southeast-1.amazonaws.com/giffyt/button-open.png' alt='Open Gift'/></a></div>" +
                                    "</td></tr>" +
                                    "<tr align='left'><td>" +
                                    "<br/>" +
                                    "<div>" + FOOTER + "</div>" +
                                    "</td></tr>" +
                                    "</table>" +
                                    "</body>" +
                                    "</html>");

                }

            }

        }

    }

    public static void sendGiftConfirmationNotification(Order order) {
        if(order != null) {
            if(order.product != null) {
                if(order.product instanceof StockProduct) {
                    StockProduct stockProduct = (StockProduct) order.product;
                    ProductAttribute itemAttribute = stockProduct.getProductAttributes().get(0);
                    String attributeName = itemAttribute.name;
                    String attributeValue = itemAttribute.value;
                    String confirmationHeadline = "<h3>" + SELF_CONFIRMATION_NOTIFICATION_CONTENT + stockProduct.name + " (" + attributeName + ": " + attributeValue + ") " + "by " + stockProduct.brand + "</h3>";
                    String addressHeadline = "<h3>Delivery Address: " + order.deliveryDetail.address + ", " + order.deliveryDetail.city + ", " + order.deliveryDetail.state + ", " + order.deliveryDetail.country.name + " " + order.deliveryDetail.postalCode  +"</h3>";
                    String deliveryTimeHeadline = "<h3>Delivery Time Frame: " + order.preferredDeliveryDate + order.deliveryTimeOption.description +"</h3>";
                    mail.addFrom(DEFAULT_SENDER_EMAIL);
                    mail.setSubject(CONFIRMATION_NOTIFICATION_SUBJECT + " (Reference: " + order.id + ")");
                    mail.addRecipient(order.recipientEmail);
                    mail.sendHtml(
                            "<html xmlns='http://www.w3.org/1999/xhtml'><head>" +
                                    "<head></head>" +
                                    "<body style='font-family:Arial, Helvetica, sans-serif;color:#999;font-size:16px;font-weight:normal;'>" +
                                    "<table width='100%'>" +
                                    "<tr><td>" +
                                    "<img src='https://s3-ap-southeast-1.amazonaws.com/giffyt/logo.png' alt='Giffyt'/>" +
                                    "</td></tr>" +
                                    "<tr align='left'><td>" +
                                    confirmationHeadline +
                                    "</td></tr>" +
                                    "<tr align='left'><td>" +
                                    addressHeadline +
                                    "</td></tr>" +
                                    "<tr align='left'><td>" +
                                    deliveryTimeHeadline +
                                    "</td></tr>" +
                                    "<tr align='left'><td>" +
                                    "<br/>" +
                                    "<div>" + FOOTER + "</div>" +
                                    "</td></tr>" +
                                    "</table>" +
                                    "</body>" +
                                    "</html>");

                }

            }


        }

    }


    /**
     * Remind the sender that the recipient has not opened the gift.
     * @param order
     */
    public static void sendReminderNotificationToSender(Order order) {
        if(order != null) {
            if(order.product != null) {
                if(order.product instanceof StockProduct) {
                    DateTimeFormatter formatter = GiffytDateTimeFormatter.getDateTimeFormatter(GiffytDateTimeFormatter.LONG_DATE);
                    String orderDate = formatter.print(order.created);
                    mail.addFrom(DEFAULT_SENDER_EMAIL);
                    mail.setSubject(REMINDER_NOTIFICATION_TO_SENDER);
                    mail.addRecipient(order.senderEmail);
                    mail.sendHtml(
                            "<html xmlns='http://www.w3.org/1999/xhtml'><head>" +
                                    "<head></head>" +
                                    "<body style='font-family:Arial, Helvetica, sans-serif;color:#999;font-size:16px;font-weight:normal;'>" +
                                    "<table width='100%'>" +
                                    "<tr><td>" +
                                    "<img src='https://s3-ap-southeast-1.amazonaws.com/giffyt/logo.png' alt='Giffyt'/>" +
                                    "</td></tr>" +
                                    "<tr align='left'><td>" +
                                    "<div>The gift you sent to " + order.recipientName + " ("+ order.recipientEmail +") on " + orderDate +" has not been opened." + " (Order reference: " + order.id + ")" + "</div>" +
                                    "<div>Please note that if the gift remains unopened for 2 weeks from the date of order, it will be cancelled and we will refund the amount.</div>" +
                                    "</td></tr>" +
                                    "<tr align='left'><td>" +
                                    "<br/>" +
                                    "<div>" + FOOTER + "</div>" +
                                    "</td></tr>" +
                                    "</table>" +
                                    "</body>" +
                                    "</html>");

                }

            }

        }

    }

    /**
     * Remind the recipient that he/she has not opened the gift.
     * @param order
     */
    public static void sendPendingNotificationToRecipient(Order order) {
        if(order != null) {
            if(order.product != null) {
                if(order.product instanceof StockProduct) {
                    mail.addFrom(DEFAULT_SENDER_EMAIL);
                    mail.setSubject(REMINDER_NOTIFICATION_TO_RECIPIENT + order.senderName);
                    mail.addRecipient(order.recipientEmail);
                    mail.sendHtml(
                            "<html xmlns='http://www.w3.org/1999/xhtml'><head>" +
                                    "<head></head>" +
                                    "<body style='font-family:Arial, Helvetica, sans-serif;color:#999;font-size:16px;font-weight:normal;'>" +
                                    "<table width='100%'>" +
                                    "<tr><td>" +
                                    "If you are having trouble reading this email, please click <a href='http://www.giffyt.com/gifts/" + order.temporaryToken + "'> here."+
                                    "</td></tr>" +
                                    "<tr><td>" +
                                    "<img src='https://s3-ap-southeast-1.amazonaws.com/giffyt/logo.png' alt='Giffyt'/>" +
                                    "</td></tr>" +
                                    "<tr align='center'><td>" +
                                    "<h3>You still have not opened the gift that " + order.senderName + " has sent you." + " (Gift reference: " + order.id + ")" + "</h3>" +
                                    "<div>To view your gift, click here</div>" +
                                    "<div><a href='http://www.giffyt.com/gifts/" + order.temporaryToken + "'> <img src='https://s3-ap-southeast-1.amazonaws.com/giffyt/button-open.png' alt='Open Gift'/></a></div>" +
                                    "</td></tr>" +
                                    "<tr align='left'><td>" +
                                    "<br/>" +
                                    "<div>" + FOOTER +"</div>" +
                                    "</td></tr>" +
                                    "</table>" +
                                    "</body>" +
                                    "</html>");


                }

            }

        }


    }

    // In progress
    public static void sendOrderToDistributor(String distributorEmail, List<SqlRow> orders) {
        StringBuilder orderString = new StringBuilder();

        orderString.append("<html><table border=1>");
        orderString.append("<tr><td>ITEM BRAND</td><td>ITEM SKU</td><td>ITEM NAME</td><td>QUANTITY</td></tr>");

        for(SqlRow order: orders) {
            orderString.append("<tr>");
            orderString.append("<td>" + order.get("itembrand") + "</td>");
            orderString.append("<td>" + order.get("itemsku") + "</td>");
            orderString.append("<td>" + order.get("itemname") + "</td>");
            orderString.append("<td>" + order.get("quantity") + "</td>");
            orderString.append("</tr>");

        }
        orderString.append("</table></html>");

        mail.addFrom(DEFAULT_SENDER_EMAIL);
        mail.setSubject("Order for today");
        mail.addRecipient(COMMON_RECIPIENT, distributorEmail);
        mail.sendHtml(orderString.toString());

    }

    // In progress
    public static void sendOrderToRetailer(String retailerEmail, List<Order> orders) {
        //String orderString = "";
        StringBuilder orderString = new StringBuilder();
        DateTimeFormatter formatter = GiffytDateTimeFormatter.getDateTimeFormatter(GiffytDateTimeFormatter.LONG_DATE);

        for(Order order: orders) {
            if(order.product instanceof StockProduct) {
                StockProduct item = (StockProduct) order.product;
                orderString.append("Order number: " + order.id + "\n");
                orderString.append("==================================================\n");
                orderString.append("Sender: " + order.senderName + "\n");
                orderString.append("Recipient: " + order.recipientName + "\n");
                orderString.append("Recipient Phone Number: " + order.deliveryDetail.contactNumber + "\n");
                orderString.append("Item brand: " + item.brand + "\n");
                orderString.append("Item sku: " + item.sku + "\n");
                orderString.append("Item name: " + item.name + "\n");
                orderString.append("Item description: " + item.description + "\n");
                orderString.append("Message: " + order.message + "\n");
                orderString.append("Shipment Address: " + order.deliveryDetail.address + ", " + order.deliveryDetail.city + ", " + order.deliveryDetail.state + ", " + order.deliveryDetail.country.name + " " + order.deliveryDetail.postalCode + "\n");
                orderString.append("Delivery Time: " + formatter.print(order.preferredDeliveryDate) + "(" + order.deliveryTimeOption.description + ")");
                orderString.append("==================================================\n\n");
            }

        }
        mail.addFrom(DEFAULT_SENDER_EMAIL);
        mail.setSubject("Order for today");
        mail.addRecipient(COMMON_RECIPIENT, retailerEmail);
        mail.send(orderString.toString());

    }

    public static void sendGiffytExceptionError(String message){
        mail.addFrom(DEFAULT_EXCEPTION_EMAIL);
        mail.setSubject(GIFFYT_EXCEPTION_SUBJECT);
        mail.addRecipient(DEFAULT_EXCEPTION_EMAIL);
        mail.send(message);
    }

    public static void sendNewExceptionError(String message){
        mail.addFrom(DEFAULT_EXCEPTION_EMAIL);
        mail.setSubject(NEW_EXCEPTION_SUBJECT);
        mail.addRecipient(DEFAULT_EXCEPTION_EMAIL);
        mail.send(message);
    }

    public static void sendError(String errorCode, String message){
        mail.addFrom(DEFAULT_EXCEPTION_EMAIL);
        mail.setSubject(ERROR_SUBJECT + errorCode);
        mail.addRecipient(DEFAULT_EXCEPTION_EMAIL);
        mail.send(message);
    }


}
