package models;

import org.codehaus.jackson.annotate.JsonBackReference;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.joda.time.DateTime;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: ltanady
 * DateTime: 11/3/13
 * Time: 1:17 PM
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "GIFFYT_DELIVERY_TIME_OPTION")
public class DeliveryTimeOption extends Model {

    @Id
    public Long id;

    @Constraints.Required
    @Column(name = "description")
    public String description;

    @Constraints.Required
    @Column(name = "amount")
    public Double amount;

    @ManyToOne
    @Constraints.Required
    public Merchant merchant;

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

    public static Model.Finder<Long, DeliveryTimeOption> find = new Model.Finder<Long, DeliveryTimeOption>(Long.class, DeliveryTimeOption.class);

    public DeliveryTimeOption(String description, Double amount, Merchant merchant) {
        this.description = description;
        this.amount = amount;
        this.merchant = merchant;
        this.isActive = false;
        this.created = new DateTime();
        this.lastUpdate = this.created;

    }


}
