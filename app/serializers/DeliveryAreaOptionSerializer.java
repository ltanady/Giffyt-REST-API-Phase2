package serializers;

import models.DeliveryAreaOption;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: ltanady
 * Date: 9/5/13
 * Time: 1:41 AM
 * To change this template use File | Settings | File Templates.
 */
public class DeliveryAreaOptionSerializer extends SerializerBase<DeliveryAreaOption> {

    public DeliveryAreaOptionSerializer() {
        super(DeliveryAreaOption.class, true);

    }

    @Override
    public void serialize(DeliveryAreaOption deliveryAreaOption, JsonGenerator jg, SerializerProvider sp) throws IOException, JsonProcessingException {
        try {
            if(deliveryAreaOption != null) {
                jg.writeStartObject();

                jg.writeNumberField("id", deliveryAreaOption.id);
                jg.writeStringField("area", deliveryAreaOption.area);
                jg.writeNumberField("amount", deliveryAreaOption.amount);

                jg.writeEndObject();

            }

        }catch(JsonProcessingException e) {
            play.Logger.error("DeliveryAreaOptionSerializer: Json Format Exception");
            play.Logger.error("DeliveryAreaOptionSerializer: " + e.getMessage());

        }catch(IOException e) {
            play.Logger.error("DeliveryAreaOptionSerializer: IOException");
            play.Logger.error("DeliveryAreaOptionSerializer: " + e.getMessage());

        }

    }
}