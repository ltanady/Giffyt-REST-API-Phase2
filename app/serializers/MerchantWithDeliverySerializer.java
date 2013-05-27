package serializers;

import models.Merchant;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: ltanady
 * Date: 9/5/13
 * Time: 2:04 AM
 * To change this template use File | Settings | File Templates.
 */
public class MerchantWithDeliverySerializer extends SerializerBase<Merchant> {

    public MerchantWithDeliverySerializer() {
        super(Merchant.class, true);

    }

    @Override
    public void serialize(Merchant merchant, JsonGenerator jg, SerializerProvider sp) throws IOException, JsonProcessingException {
        try {
            if(merchant != null) {
                jg.writeStartObject();

                jg.writeNumberField("id", merchant.id);
                jg.writeStringField("name", merchant.name);
                jg.writeObjectField("country", merchant.country);
                jg.writeObjectField("deliveryAreaOptions", merchant.deliveryAreaOptions);
                jg.writeObjectField("deliveryTimeOptions", merchant.deliveryTimeOptions);

                jg.writeEndObject();

            }

        }catch(JsonProcessingException e) {
            play.Logger.error("MerchantSerializer: Json Format Exception");
            play.Logger.error("MerchantSerializer: " + e.getMessage());

        }catch(IOException e) {
            play.Logger.error("MerchantSerializer: IOException");
            play.Logger.error("MerchantSerializer: " + e.getMessage());

        }

    }

}
