package serializers;

import models.Country;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: ltanady
 * Date: 26/4/13
 * Time: 6:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class CountrySerializer extends SerializerBase<Country> {

    public CountrySerializer() {
        super(Country.class, true);

    }

    @Override
    public void serialize(Country country, JsonGenerator jg, SerializerProvider sp) throws IOException, JsonProcessingException {
        try {
            if(country != null) {
                jg.writeStartObject();

                jg.writeNumberField("id", country.id);
                jg.writeStringField("code", country.code);
                jg.writeStringField("name", country.name);
                jg.writeStringField("imageUrl", country.imageUrl);
                jg.writeStringField("currency", country.currency);
                jg.writeNumberField("minimumPurchaseAmount", country.minimumPurchaseAmount);
                jg.writeNumberField("subsidizedShippingAmount", country.subsidizedShippingAmount);
                jg.writeBooleanField("isActive", country.isActive);

                jg.writeEndObject();

            }

        }catch(JsonProcessingException e) {
            play.Logger.error("CountrySerializer: Json Format Exception");
            play.Logger.error("CountrySerializer: " + e.getMessage());

        }catch(IOException e) {
            play.Logger.error("CountrySerializer: IOException");
            play.Logger.error("CountrySerializer: " + e.getMessage());

        }

    }

}