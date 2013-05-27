package models;

import com.avaje.ebean.*;
import controllers.Products;
import org.codehaus.jackson.annotate.JsonBackReference;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.joda.time.DateTime;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import javax.validation.Constraint;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ltanady
 * Date: 24/4/13
 * Time: 3:19 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "GIFFYT_PRODUCT")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value=StockProduct.class, name="StockProduct")
})
public abstract class Product extends Model {

    @Id
    public Long id;

    @Constraints.Required
    @Column(name = "code")
    public String code;

    @Constraints.Required
    @Column(name = "price")
    public Double price;

    @Constraints.Required
    @Column(name = "brand")
    public String brand;

    @OneToOne
    @Constraints.Required
    @Column(name = "merchant_id")
    public Merchant merchant;

    //@JsonBackReference
    @ManyToOne
    public Campaign campaign;

    @Constraints.Required
    @Column(name = "exchangeable")
    public Boolean exchangeable;

    @Constraints.Required
    @Column(name = "minimum_order_days")
    public Long minimumOrderDays;

    @Constraints.Required
    @Column(name = "is_active")
    public Boolean isActive;

    @JsonIgnore
    @Constraints.Required
    @Column(name = "created")
    public DateTime created;

    @JsonIgnore
    @Constraints.Required
    @Column(name = "last_update")
    public DateTime lastUpdate;

    @OneToMany(targetEntity = ProductAttribute.class, mappedBy = "product", cascade = CascadeType.ALL)
    public List<ProductAttribute> productAttributes;

    @OneToMany(targetEntity = ProductImage.class, mappedBy = "product", cascade = CascadeType.ALL)
    public List<ProductImage> productImages;

    public static Finder<Long, Product> find = new Finder<Long, Product>(Long.class, Product.class);

    /**
     * Get country of a product.
     * @return Country
     */
    public Country getCountry(){
        Country country = null;
        if(this.merchant != null) {
            country = this.merchant.country;
        }
        return country;

    }

    /**
     * Find list of active product attributes.
     * @return List<ProductAttribute>
     */
    public List<ProductAttribute> getProductAttributes() {
        List<ProductAttribute> productAttributes = ProductAttribute.find.where().and(Expr.eq("product", this), Expr.eq("isActive", true)).findList();

        return productAttributes;

    }

    /**
     * Find list of active product images.
     * @return
     */
    public List<ProductImage> getProductImages() {
        List<ProductImage> productImages = ProductImage.find.where().and(Expr.eq("product", this), Expr.eq("isActive", true)).findList();

        return productImages;

    }

    /**
     * Find list of active delivery area options.
     * @return List<DeliveryAreaOption>
     */
    public List<DeliveryAreaOption> getDeliveryAreaOptions() {
        List<DeliveryAreaOption> deliveryAreaOptions = DeliveryAreaOption.find.where().and(Expr.eq("merchant", this.merchant), Expr.eq("isActive", true)).findList();

        return deliveryAreaOptions;

    }

    /**
     * Find list of active delivery time options.
     * @return List<DeliveryTimeOption>
     */
    public List<DeliveryTimeOption> getDeliveryTimeOptions() {
        List<DeliveryTimeOption> deliveryTimeOptions = DeliveryTimeOption.find.where().and(Expr.eq("merchant", this.merchant), Expr.eq("isActive", true)).findList();

        return deliveryTimeOptions;

    }

    /**
     * Find active product by id
     * @param id
     * @return Product
     */
    public static Product findById(Long id) {
        Product product = null;
        if(id != null) {
            product = Product.find.where().and(Expr.eq("id", id), Expr.eq("isActive", true)).findUnique();

        }
        return product;

    }

    /**
     * Find list of active products given the country.
     * @param country
     * @return List<Product>
     */
    public static List<Product> findByCountry(Country country) {
        List<Product> products = null;
        if(country != null && country.isActive) {
            products = Product.find.where().and(Expr.eq("merchant.country", country), Expr.and(Expr.eq("isActive", true), Expr.isNull("campaign"))).orderBy("price, brand asc").findList();

        }
        return products;

    }

    /**
     * Find list of active products given the country
     * @param country
     * @param budget
     * @return
     */
    public static List<Product> findByCountryAndBudget(Country country, Double budget) {
        List<Product> products = null;

        if(country != null && country.isActive && budget != null) {
            String sqlString = "SELECT PRODUCT.id as product_id FROM GIFFYT_PRODUCT PRODUCT INNER JOIN GIFFYT_MERCHANT MERCHANT ON PRODUCT.merchant_id = MERCHANT.id INNER JOIN GIFFYT_COUNTRY COUNTRY ON MERCHANT.country_id = COUNTRY.id " +
                    "INNER JOIN " +
                    "(SELECT sum(max_amount) AS shipping_and_handling, merchant_id FROM ( " +
                    "SELECT max(amount) AS max_amount, merchant_id FROM GIFFYT_DELIVERY_AREA_OPTION GROUP BY merchant_id " +
                    "UNION " +
                    "SELECT max(amount) AS max_amount, merchant_id FROM GIFFYT_DELIVERY_TIME_OPTION GROUP BY merchant_id) A GROUP BY merchant_id) MERCHANT_SHIPPING_AND_HANDLING ON PRODUCT.merchant_id = MERCHANT_SHIPPING_AND_HANDLING.merchant_id " +
                    "WHERE COUNTRY.id = :countryId AND PRODUCT.is_active = 1 AND PRODUCT.exchangeable = 1 AND (price + MERCHANT_SHIPPING_AND_HANDLING.shipping_and_handling) < :budget";

            SqlQuery productQuery = Ebean.createSqlQuery(sqlString);
            productQuery.setParameter("countryId", country.id);
            productQuery.setParameter("budget", budget);

            List<SqlRow> productRows = productQuery.findList();
            List<Long> productIds = new ArrayList<Long>();
            for(SqlRow row: productRows)
                productIds.add(row.getLong("product_id"));

            products = Product.find.where().in("id", productIds).findList();

        }
        return products;

    }


    /**
     * Get valid delivery area option, returns null if not found.
     * @param deliveryAreaOptionId
     * @return DeliveryAreaOption
     */
    public DeliveryAreaOption getValidDeliveryAreaOption(Long deliveryAreaOptionId) {
        DeliveryAreaOption deliveryAreaOption = DeliveryAreaOption.find.where().eq("merchant", this.merchant).eq("id", deliveryAreaOptionId).eq("isActive", true).findUnique();

        return deliveryAreaOption;

    }

    /**
     * Get valid delivery time option, returns null if not found.
     * @param deliveryTimeOptionId
     * @return deliveryTimeOption
     */
    public DeliveryTimeOption getValidDeliveryTimeOption(Long deliveryTimeOptionId) {
        DeliveryTimeOption deliveryTimeOption = DeliveryTimeOption.find.where().eq("merchant", this.merchant).eq("id", deliveryTimeOptionId).eq("isActive", true).findUnique();

        return deliveryTimeOption;

    }

    /**
     * Group Products according to the Product code
     * @param products
     * @return HashMap<String, List<Product>>
     */
    public static LinkedHashMap<String, List<Product>> groupProducts(List<Product> products) {
        LinkedHashMap<String, List<Product>> groupedProductsHashMap= new LinkedHashMap<String, List<Product>>();
        if(products == null)
            return groupedProductsHashMap;

        List<Product> groupedProducts = null;

        for(Product product: products) {
            groupedProducts = groupedProductsHashMap.get(product.code);
            if(groupedProducts != null) {
                groupedProducts.add(product);

            }else {
                groupedProducts = new ArrayList<Product>();
                groupedProducts.add(product);
                groupedProductsHashMap.put(product.code, groupedProducts);

            }

        }
        return groupedProductsHashMap;

    }

    public abstract String toString();

}
