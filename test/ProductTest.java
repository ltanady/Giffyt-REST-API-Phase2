import models.*;
import org.junit.Test;

import java.util.List;

import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;
/**
 * Created with IntelliJ IDEA.
 * User: ltanady
 * Date: 9/5/13
 * Time: 9:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProductTest {

    @Test
    public void testGetProduct() {

        running(fakeApplication(), new Runnable() {
            public void run() {
                Product product = Product.findById(1L);
                assertThat(product).isNotNull();

            }
        });

    }

    @Test
    public void testGetProductAttributes() {

        running(fakeApplication(), new Runnable() {
            public void run() {
                Product product = Product.findById(1L);
                assertThat(product).isNotNull();
                List<ProductAttribute> productAttributes = product.getProductAttributes();
                assertThat(productAttributes.size()).isEqualTo(1);

            }
        });

    }

    @Test
    public void testGetProductImages() {

        running(fakeApplication(), new Runnable() {
            public void run() {
                Product product = Product.findById(1L);
                assertThat(product).isNotNull();
                List<ProductImage> productImages = product.getProductImages();
                assertThat(productImages.size()).isEqualTo(1);

            }
        });

    }

    @Test
    public void testGetProductDeliveryAreaOptions() {

        running(fakeApplication(), new Runnable() {
            public void run() {
                Product product = Product.findById(1L);
                assertThat(product).isNotNull();
                List<DeliveryAreaOption> deliveryAreaOptions = product.getDeliveryAreaOptions();
                assertThat(deliveryAreaOptions.size()).isEqualTo(7);

            }
        });

    }

    @Test
    public void testGetProductDeliveryTimeOptions() {

        running(fakeApplication(), new Runnable() {
            public void run() {
                Product product = Product.findById(1L);
                assertThat(product).isNotNull();
                List<DeliveryTimeOption> deliveryTimeOptions = product.getDeliveryTimeOptions();
                assertThat(deliveryTimeOptions.size()).isEqualTo(5);

            }
        });

    }

}
