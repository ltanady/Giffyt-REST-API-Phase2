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
 * Date: 8/5/13
 * Time: 11:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class MerchantSerializer extends SerializerBase<Merchant> {

    public MerchantSerializer() {
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
