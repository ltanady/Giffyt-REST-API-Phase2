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
 * Date: 26/4/13
 * Time: 6:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProductSerializer extends SerializerBase<Product> {

    public ProductSerializer() {
        super(Product.class, true);

    }

    @Override
    public void serialize(Product product, JsonGenerator jg, SerializerProvider sp)  throws IOException, JsonProcessingException {
        try {
            if(product != null) {
                jg.writeStartObject();

                jg.writeNumberField("id", product.id);
                jg.writeStringField("code", product.code);
                jg.writeObjectField("country", product.merchant.country);
                jg.writeNumberField("price", product.price);
                jg.writeStringField("brand", product.brand);
                jg.writeObjectField("merchant", product.merchant);
                jg.writeObjectField("campaign", product.campaign);

                if(product instanceof StockProduct) {
                    StockProduct stockProduct = (StockProduct) product;
                    jg.writeStringField("name", stockProduct.name);
                    jg.writeStringField("description", stockProduct.description);
                    jg.writeObjectField("additionalShippingCost", stockProduct.additionalShippingCost);
                    jg.writeStringField("estimatedDelivery", stockProduct.estimatedDelivery);
                    jg.writeStringField("type", StockProduct.class.getSimpleName());
                    jg.writeObjectField("productAttributes", stockProduct.getProductAttributes());
                    jg.writeObjectField("productImages", stockProduct.getProductImages());

                }
                jg.writeEndObject();

            }

        }catch(JsonProcessingException e) {
            play.Logger.error("ProductSerializer: Json Format Exception");
            play.Logger.error("ProductSerializer: " + e.getMessage());

        }catch(IOException e) {
            play.Logger.error("ProductSerializer: IOException");
            play.Logger.error("ProductSerializer: " + e.getMessage());

        }

    }
}