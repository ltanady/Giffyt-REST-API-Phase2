package serializers;

import models.DeliveryTimeOption;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: ltanady
 * Date: 9/5/13
 * Time: 1:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class DeliveryTimeOptionSerializer extends SerializerBase<DeliveryTimeOption> {

    public DeliveryTimeOptionSerializer() {
        super(DeliveryTimeOption.class, true);

    }

    @Override
    public void serialize(DeliveryTimeOption deliveryAreaOption, JsonGenerator jg, SerializerProvider sp) throws IOException, JsonProcessingException {
        try {
            if(deliveryAreaOption != null) {
                jg.writeStartObject();

                jg.writeNumberField("id", deliveryAreaOption.id);
                jg.writeStringField("description", deliveryAreaOption.description);
                jg.writeNumberField("amount", deliveryAreaOption.amount);

                jg.writeEndObject();

            }

        }catch(JsonProcessingException e) {
            play.Logger.error("DeliveryTimeOptionSerializer: Json Format Exception");
            play.Logger.error("DeliveryTimeOptionSerializer: " + e.getMessage());

        }catch(IOException e) {
            play.Logger.error("DeliveryTimeOptionSerializer: IOException");
            play.Logger.error("DeliveryTimeOptionSerializer: " + e.getMessage());

        }

    }
}