package serializers;

import models.Product;
import models.StockProduct;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: ltanady
 * Date: 12/5/13
 * Time: 8:15 AM
 * To change this template use File | Settings | File Templates.
 */
public class ProductSimpleSerializer extends SerializerBase<Product> {

    public ProductSimpleSerializer() {
        super(Product.class, true);

    }

    @Override
    public void serialize(Product product, JsonGenerator jg, SerializerProvider sp)  throws IOException, JsonProcessingException {
        try {
            if(product != null) {
                jg.writeStartObject();

                jg.writeNumberField("id", product.id);
                jg.writeStringField("code", product.code);
                jg.writeNumberField("price", product.price);
                jg.writeStringField("brand", product.brand);
                jg.writeObjectField("campaign", product.campaign);

                if(product instanceof StockProduct) {
                    StockProduct stockProductSimple = (StockProduct) product;
                    jg.writeStringField("name",stockProductSimple.name);
                    jg.writeStringField("description",stockProductSimple.description);
                    jg.writeStringField("estimatedDelivery", stockProductSimple.estimatedDelivery);
                    jg.writeStringField("type", StockProduct.class.getSimpleName());

                }
                jg.writeEndObject();

            }

        }catch(JsonProcessingException e) {
            play.Logger.error("ProductSimpleSerializer: Json Format Exception");
            play.Logger.error("ProductSimpleSerializer: " + e.getMessage());

        }catch(IOException e) {
            play.Logger.error("ProductSimpleSerializer: IOException");
            play.Logger.error("ProductSimpleSerializer: " + e.getMessage());

        }

    }
}