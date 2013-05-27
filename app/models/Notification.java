package models;

import org.codehaus.jackson.annotate.JsonBackReference;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: flukito
 * Date: 8/9/12
 * Time: 11:12 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "GIFFYT_NOTIFICATION")
public class Notification extends Model {

    @Id
    public Long id;

    @OneToOne
    @Column(name = "order_id")
    public Order order;

    @Constraints.Required
    @Column(name = "status")
    public String status;

    @Constraints.Required
    @Column(name = "created")
    public Date created;

    @Constraints.Required
    @Column(name = "last_update")
    public Date lastUpdate;

    public static Finder<Long, Notification> find = new Finder<Long, Notification>(Long.class, Notification.class);

    public Notification(Order order, String status){
        this.order = order;
        this.status = status;
        this.created = new Date();
        this.lastUpdate = this.created;

    }

    //To get list of orders that have been ordered by senders to be sent to recipients
    public static List<Notification> getPendingNotifications(){
        return Notification.find.where().eq("status", "Pending").findList();
    }


}
