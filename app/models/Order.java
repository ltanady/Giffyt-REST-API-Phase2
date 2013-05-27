package models;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.joda.time.DateTime;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import javax.validation.Valid;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: ltanady
 * Date: 6/8/12
 * Time: 5:36 PM
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "GIFFYT_ORDER")
public class Order extends Model {

    public enum ORDER_STATUS {
        PENDING_PAYMENT(0),
        PENDING_RECIPIENT(1),
        CONFIRMED(2),
        SHIPPED(3),
        CANCELLED(4),
        RESEND_LINK(5),
        CONFIRMED_AFTER_RESEND(6),
        REFUNDED(7);

        private final int value;
        ORDER_STATUS(int v) {
            value = v;
        }

        @org.codehaus.jackson.annotate.JsonValue
        public int value() {
            return value;
        }
        @org.codehaus.jackson.annotate.JsonCreator
        public static ORDER_STATUS fromValue(int typeCode) {
            for (ORDER_STATUS c: ORDER_STATUS.values()) {
                if (c.value==typeCode) {
                    return c;
                }
            }
            throw new IllegalArgumentException("Invalid Status type code: " + typeCode);

        }

    }

    @Id
    public Long id;

    @Column(name = "external_reference_id")
    public Long externalReferenceId;

    @Column(name = "message")
    public String message;

    @OneToOne
    @Column(name = "old_product_id")
    public Product oldProduct;

    @OneToOne
    //@Constraints.Required
    @Column(name = "product_id")
    public Product product;

    @OneToOne
    @Column(name = "sender_facebook_id")
    public String senderFacebookId;

    @Constraints.Required
    @Column(name = "sender_name")
    public String senderName;

    @Constraints.Required
    @Column(name = "sender_email")
    public String senderEmail;

    @Column(name = "recipient_facebook_id")
    public String recipientFacebookId;

    @Constraints.Required
    @Column(name = "recipient_name")
    public String recipientName;

    @Constraints.Required
    @Column(name = "recipient_email")
    public String recipientEmail;

    @Column(name = "preferred_notification_date")
    public DateTime preferredNotificationDate;

    @Column(name = "preferred_delivery_date")
    public DateTime preferredDeliveryDate;

    @Column(name = "preferred_delivery_time")
    public String preferredDeliveryTime;

    @OneToOne
    @Column(name = "delivery_area_option_id")
    public DeliveryAreaOption deliveryAreaOption;

    @OneToOne
    @Column(name = "delivery_time_option_id")
    public DeliveryTimeOption deliveryTimeOption;

    @OneToOne
    @Column(name = "delivery_detail_id")
    public DeliveryDetail deliveryDetail;

    @Constraints.Required
    @Column(name = "temporaryToken")
    public String temporaryToken;

    @Constraints.Required
    @Column(name = "is_surprise")
    public Boolean isSurprise;

    @Constraints.Required
    @Column(name = "initial_amount")
    public Double initialAmount;

    @Constraints.Required
    @Column(name = "final_amount")
    public Double finalAmount;

    @Constraints.Required
    @Column(name = "order_status")
    public ORDER_STATUS orderStatus;

    @Column(name = "paypal_token")
    public String paypalToken;

    @Column(name = "paypal_payer_id")
    public String paypalPayerId;

    @Column(name = "paypal_authorization_id")
    public String paypalAuthorizationId;

    @Column(name = "shipment_date")
    public DateTime shipmentDate;

    @JsonIgnore
    @Constraints.Required
    @Column(name = "created")
    public DateTime created;

    @JsonIgnore
    @Constraints.Required
    @Column(name = "last_update")
    public DateTime lastUpdate;

    public static Finder<Long, Order> find = new Finder<Long, Order>(Long.class, Order.class);

    public Order() {
        this.temporaryToken = UUID.randomUUID().toString();
        this.orderStatus = ORDER_STATUS.PENDING_PAYMENT;
        this.isSurprise = false;
        this.initialAmount = 0.0;
        this.finalAmount = 0.0;
        this.shipmentDate = null;
        this.created = new DateTime();
        this.lastUpdate = this.created;

    }

    public Order(String message, Product product, String senderFacebookId, String senderName, String senderEmail, String recipientFacebookId, String recipientName, String recipientEmail, DateTime preferredNotificationDate, DateTime preferredDeliveryDate) {
        this.message = message;
        this.product = product;
        this.senderFacebookId = senderFacebookId;
        this.senderName = senderName;
        this.senderEmail = senderEmail;
        this.recipientFacebookId = recipientFacebookId;
        this.recipientName = recipientName;
        this.recipientEmail = recipientEmail;
        this.preferredNotificationDate = preferredNotificationDate;
        this.preferredDeliveryDate = preferredDeliveryDate;
        this.temporaryToken = UUID.randomUUID().toString();
        this.orderStatus = ORDER_STATUS.PENDING_PAYMENT;
        this.isSurprise = false;
        this.initialAmount = 0.0;
        this.finalAmount = 0.0;
        this.shipmentDate = null;
        this.created = new DateTime();
        this.lastUpdate = this.created;

    }

    /**
     * Find an order by orderId and orderStatus
     * @param id
     * @param orderStatus
     * @return
     */
    public static Order findById(Long id, ORDER_STATUS orderStatus) {
        Order order = null;
        if(orderStatus != null) {
            order = Order.find.where().eq("id", id).eq("orderStatus", orderStatus).findUnique();

        }
        return order;

    }

    /**
     * Find an order by temporaryToken and orderStatus
     * @param temporaryToken
     * @param orderStatus
     * @return Order
     */
    public static Order findByTemporaryToken(String temporaryToken, ORDER_STATUS orderStatus) {
        Order order = null;
        if(orderStatus != null) {
            order = Order.find.where().eq("temporaryToken", temporaryToken).eq("orderStatus", orderStatus).findUnique();

        }
        return order;

    }

    /**
     * Check for valid deliveryDetail.
     * @param deliveryDetail
     * @return Boolean
     */
    public static Boolean hasValidDeliveryDetail(DeliveryDetail deliveryDetail) {

        Boolean isValid = true;

        if(deliveryDetail != null) {
            if(deliveryDetail.recipientName == null || deliveryDetail.recipientName.equals(""))
                isValid = false;

            if(deliveryDetail.contactNumber == null || deliveryDetail.contactNumber.equals(""))
                isValid = false;

            if(deliveryDetail.address == null || deliveryDetail.address.equals(""))
                isValid = false;

            if(deliveryDetail.city == null || deliveryDetail.city.equals(""))
                isValid = false;

            if(deliveryDetail.state == null || deliveryDetail.state.equals(""))
                isValid = false;

            if(deliveryDetail.country == null)
                isValid = false;

            if(deliveryDetail.postalCode == null || deliveryDetail.postalCode.equals(""))
                isValid = false;

        }else {
            isValid = false;

        }
        return isValid;

    }

    /**
     * Calculate shipping and handling amount from the product
     * additional shipping cost, delivery area option and delivery
     * time option.
     * @param order
     * @return
     */
    public static Double calculateShippingAndHandling(Order order) {
        Double totalAmount = 0.0;
        if(order != null) {
            if(order.product != null) {
                if(order.product instanceof StockProduct) {
                    StockProduct stockProduct = (StockProduct) order.product;
                    totalAmount += stockProduct.additionalShippingCost;

                }
                if(order.deliveryAreaOption != null)
                    totalAmount += order.deliveryAreaOption.amount;

                if(order.deliveryTimeOption != null)
                    totalAmount += order.deliveryTimeOption.amount;

            }

        }
        return totalAmount ;

    }

    /**
     * Calculate initial amount from the price of product,
     * product additional shipping cost, delivery area option
     * and delivery time option.
     * @param order
     * @return
     */
    public static Double calculateInitialAmount(Order order) {
        Double initialAmount = 0.0;

        if(order != null) {
            if(order.product != null) {
                initialAmount += order.product.price;

            }
            initialAmount += Order.calculateShippingAndHandling(order);

        }
        return initialAmount;

    }

    /**
     * Create a new order.
     * @param order
     * @return Order
     */
    public static Order create(Order order) {

        order.initialAmount = Order.calculateInitialAmount(order);

        // Surprise order or send to self
        if(order.isSurprise || (order.senderEmail.equals(order.recipientEmail))) {
            // Order amount is immediately finalized
            order.finalAmount = order.initialAmount;
            if(!Order.hasValidDeliveryDetail(order.deliveryDetail)) {
                return null;

            }
            order.deliveryDetail.save();

        }
        order.save();
        Notification notification = new Notification(order, "Pending");
        return order;

    }

}
