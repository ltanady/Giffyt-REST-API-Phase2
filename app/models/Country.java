package models;

import com.avaje.ebean.Expr;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.joda.time.DateTime;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ltanady
 * DateTime: 24/4/13
 * Time: 2:09 PM
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "GIFFYT_COUNTRY")
public class Country extends Model {

    @Id
    public Long id;

    @Constraints.Required
    @Column(name = "code", unique = true)
    public String code;

    @Constraints.Required
    @Column(name = "name", unique = true)
    public String name;

    @Constraints.Required
    @Column(name = "image_url")
    public String imageUrl;

    @Constraints.Required
    @Column(name = "currency")
    public String currency;

    @Constraints.Required
    @Column(name = "minimum_purchase_amount")
    public Double minimumPurchaseAmount;

    @Constraints.Required
    @Column(name = "subsidized_shipping_amount")
    public Double subsidizedShippingAmount;

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

    public static Finder<Long, Country> find = new Finder<Long, Country>(Long.class, Country.class);

    // Constructor
    public Country(String code, String name, String currency, Double minimumPurchaseAmount, Double subsidizedShippingAmount) {
        this.code = code;
        this.name = name;
        this.currency = currency;
        this.minimumPurchaseAmount = minimumPurchaseAmount;
        this.subsidizedShippingAmount = subsidizedShippingAmount;
        this.isActive = false;
        this.created = new DateTime();
        this.lastUpdate = this.created;

    }

    /**
     * Find Country by code.
     * @param countryCode
     * @return Country
     */
    public static Country findbyCode(String countryCode) {
        Country country = null;
        if(countryCode != null)
            country = Country.find.where().ieq("code", countryCode).findUnique();

        return country;

    }

    /**
     * Find Country by name.
     * @param countryName
     * @return Country
     */
    public static Country findByName(String countryName) {
        Country country = null;
        if(countryName != null)
            country = Country.find.where().ieq("name", countryName).findUnique();

        return country;

    }

}
