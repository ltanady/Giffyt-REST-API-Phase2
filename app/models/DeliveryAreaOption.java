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
 * Date: 8/5/13
 * Time: 10:54 PM
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "GIFFYT_DELIVERY_AREA_OPTION")
public class DeliveryAreaOption {

    @Id
    public Long id;

    @Constraints.Required
    @Column(name = "area")
    public String area;

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

    public static Model.Finder<Long, DeliveryAreaOption> find = new Model.Finder<Long, DeliveryAreaOption>(Long.class, DeliveryAreaOption.class);

    public DeliveryAreaOption(String area, Double amount, Merchant merchant) {
        this.area = area;
        this.amount = amount;
        this.merchant = merchant;
        this.isActive = false;
        this.created = new DateTime();
        this.lastUpdate = this.created;

    }

}
