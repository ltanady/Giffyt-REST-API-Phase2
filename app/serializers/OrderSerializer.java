package serializers;

import models.Country;
import models.Order;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: ltanady
 * Date: 12/5/13
 * Time: 7:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class OrderSerializer extends SerializerBase<Order> {

    public OrderSerializer() {
        super(Order.class, true);

    }

    @Override
    public void serialize(Order order, JsonGenerator jg, SerializerProvider sp) throws IOException, JsonProcessingException {
        try {
            if(order != null) {
                jg.writeStartObject();

                jg.writeNumberField("id", order.id);
                jg.writeStringField("message", order.message);
                jg.writeObjectField("product", order.product);
                jg.writeStringField("senderFacebookId", order.senderFacebookId);
                jg.writeStringField("senderName", order.senderName);
                jg.writeStringField("senderEmail", order.senderEmail);
                jg.writeStringField("recipientFacebookId", order.recipientFacebookId);
                jg.writeStringField("recipientName", order.recipientName);
                jg.writeStringField("recipientEmail", order.recipientEmail);
                jg.writeStringField("temporaryToken", order.temporaryToken);
                jg.writeBooleanField("isSurprise", order.isSurprise);
                jg.writeObjectField("orderStatus", order.orderStatus);
                jg.writeObjectField("deliveryDetail", order.deliveryDetail);

                jg.writeEndObject();

            }

        }catch(JsonProcessingException e) {
            play.Logger.error("OrderSerializer: Json Format Exception");
            play.Logger.error("OrderSerializer: " + e.getMessage());

        }catch(IOException e) {
            play.Logger.error("OrderSerializer: IOException");
            play.Logger.error("OrderSerializer: " + e.getMessage());

        }

    }

}