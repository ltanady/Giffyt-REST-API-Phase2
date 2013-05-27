package models;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.joda.time.DateTime;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: ltanady
 * Date: 10/5/13
 * Time: 1:15 AM
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "GIFFYT_DELIVERY_DETAIL")
public class DeliveryDetail extends Model {

    @Id
    public Long id;

    @Constraints.Required
    @Column(name="recipient_name")
    public String recipientName;

    @Constraints.Required
    @Column(name = "address")
    public String address;

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
    @Column(name = "contact_number")
    public String contactNumber;

    @Column(name = "shipment_date")
    public DateTime shipmentDate;

    @JsonIgnore
    @Constraints.Required
    @Column(name = "created")
    public DateTime created;

    @JsonIgnore
    @Constraints.Required
    @Column(name = "last_update")
    public DateTime lastUpdate;

    public DeliveryDetail() {
        this.shipmentDate = null;
        this.created = new DateTime();
        this.lastUpdate = this.created;

    }

    public DeliveryDetail(String recipientName, String address, String city, String state, Country country, String postalCode, String contactNumber, DateTime shipmentDate) {
        this.recipientName = recipientName;
        this.address = address;
        this.city = city;
        this.state = state;
        this.country = country;
        this.postalCode = postalCode;
        this.contactNumber = contactNumber;
        this.shipmentDate = null;
        this.created = new DateTime();
        this.lastUpdate = this.created;

    }

}
