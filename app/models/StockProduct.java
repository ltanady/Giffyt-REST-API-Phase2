package models;

import org.joda.time.DateTime;
import play.data.validation.Constraints;

import javax.persistence.*;
import javax.validation.Constraint;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ltanady
 * Date: 1/8/12
 * Time: 2:39 PM
 * To change this template use File | Settings | File Templates.
 */

@Entity
@DiscriminatorValue("StockProduct")
public class StockProduct extends Product {

    @Constraints.Required
    @Column(name = "name")
    public String name;

    @Constraints.Required
    @Column(name = "sku")
    public String sku;

    @Constraints.Required
    @Column(name = "description")
    public String description;

    @Constraints.Required
    @Column(name = "estimated_delivery")
    public String estimatedDelivery;

    @Constraints.Required
    @Column(name = "additional_shipping_cost")
    public Double additionalShippingCost;

    public StockProduct() {
        this.isActive = false;
        this.exchangeable = true;
        this.additionalShippingCost = 0.0;
        this.created = new DateTime();
        this.lastUpdate = this.created;

    }

    public StockProduct(String code, String name, String sku, String description, Double price,
                        String brand, Merchant merchant, Boolean exchangeable, String estimatedDelivery, Double additionalShippingCost) {
        this.code = code;
        this.name = name;
        this.sku = sku;
        this.description = description;
        this.price = price;
        this.brand = brand;
        this.merchant = merchant;
        this.isActive = false;
        this.exchangeable = exchangeable;
        this.estimatedDelivery = estimatedDelivery;
        this.additionalShippingCost = additionalShippingCost;
        this.created = new DateTime();
        this.lastUpdate = this.created;

    }

    public String toString() {
        return "Stock Product " + this.id;

    }

}
