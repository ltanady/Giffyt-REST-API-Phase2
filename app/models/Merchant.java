package models;

import com.avaje.ebean.Expr;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.joda.time.DateTime;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ltanady
 * DateTime: 24/4/13
 * Time: 2:17 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "GIFFYT_MERCHANT", uniqueConstraints = {  @UniqueConstraint(columnNames = { "name", "country_id" }) })
public class Merchant extends Model {

    public enum MERCHANT_TYPE {
        DISTRIBUTOR(0),
        RETAILER(1);

        private final int value;
        MERCHANT_TYPE(int v) {
            value = v;
        }

        @org.codehaus.jackson.annotate.JsonValue
        public int value() {
            return value;
        }
        @org.codehaus.jackson.annotate.JsonCreator
        public static MERCHANT_TYPE fromValue(int typeCode) {
            for (MERCHANT_TYPE c: MERCHANT_TYPE.values()) {
                if (c.value == typeCode) {
                    return c;
                }
            }
            throw new IllegalArgumentException("Invalid Status type code: " + typeCode);

        }

    }

    @Id
    public Long id;

    @Constraints.Required
    @Column(name = "name")
    public String name;

    @Constraints.Required
    @Column(name = "type")
    public MERCHANT_TYPE type;

    @Constraints.Required
    @Column(name = "address_1")
    public String address1;

    @Column(name = "address_2")
    public String address2;

    @Constraints.Required
    @Column(name = "city")
    public String city;

    @Constraints.Required
    @Column(name = "state")
    public String state;

    @OneToOne
    @Constraints.Required
    @Column(name = "country_id")
    public Country country;

    @Constraints.Required
    @Column(name = "postal_code")
    public String postalCode;

    @Constraints.Required
    @Column(name = "contact_person")
    public String contactPerson;

    @Constraints.Required
    @Column(name = "phone")
    public String phone;

    @Column(name = "fax")
    public String fax;

    @Constraints.Required
    @Column(name = "email")
    @Constraints.Email
    public String email;

    @Constraints.Required
    @Column(name = "is_active")
    public boolean isActive;

    @OneToMany(targetEntity = DeliveryAreaOption.class, mappedBy = "merchant", cascade = CascadeType.ALL)
    public List<DeliveryAreaOption> deliveryAreaOptions;

    @OneToMany(targetEntity = DeliveryTimeOption.class, mappedBy = "merchant", cascade = CascadeType.ALL)
    public List<DeliveryTimeOption> deliveryTimeOptions;

    @JsonIgnore
    @Constraints.Required
    @Column(name = "created")
    public DateTime created;

    @JsonIgnore
    @Constraints.Required
    @Column(name = "last_update")
    public DateTime lastUpdate;


    public static Finder<Long, Merchant> find = new Finder<Long, Merchant>(Long.class, Merchant.class);

    public Merchant(String name, MERCHANT_TYPE type, String address1, String address2, String city, String state, Country country, String postalCode,
                    String contactPerson, String phone, String fax, String email) {
        this.name = name;
        this.type = type;
        this.address1 = address1;
        this.address2 = address2;
        this.city = city;
        this.state = state;
        this.country = country;
        this.postalCode = postalCode;
        this.contactPerson = contactPerson;
        this.phone = phone;
        this.fax = fax;
        this.email = email;
        this.isActive = false;
        this.created = new DateTime();
        this.lastUpdate = this.created;

    }

    public String toString() {
        return this.name + " " + this.country.name;

    }


}
