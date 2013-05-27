package models;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.joda.time.DateTime;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ltanady
 * Date: 18/1/13
 * Time: 4:08 PM
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "GIFFYT_CAMPAIGN")
public class Campaign extends Model {

    @Id
    public Long id;

    @OneToOne
    @Constraints.Required
    @Column(name = "country_id")
    public Country country;

    @Column(name = "name")
    public String name;

    @Column(name = "description")
    public String description;

    //@JsonManagedReference
    @JsonIgnore
    @OneToMany(targetEntity = Product.class, mappedBy = "campaign", cascade = CascadeType.ALL)
    public List<Product> products;

    @Column(name = "campaign_limit")
    public Long campaignLimit;

    @Column(name = "start_date")
    public DateTime startDate;

    @Column(name = "end_date")
    public DateTime endDate;

    @Constraints.Required
    @Column(name = "is_active")
    public Boolean isActive;

    //@JsonManagedReference
    @JsonIgnore
    @ManyToMany(targetEntity = User.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "GIFFYT_CAMPAIGN_USER", joinColumns = { @JoinColumn(name = "campaign_id") }, inverseJoinColumns = { @JoinColumn(name = "facebook_user_id") }, uniqueConstraints = {  @UniqueConstraint(columnNames = { "campaign_id", "facebook_user_id" }) })
    public List<User> users;

    @JsonIgnore
    @Constraints.Required
    @Column(name = "created")
    public DateTime created;

    @JsonIgnore
    @Constraints.Required
    @Column(name = "last_update")
    public DateTime lastUpdate;

    public static Finder<Long, Campaign> find = new Finder<Long, Campaign>(Long.class, Campaign.class);

    public Campaign(Country country, String name, String description, Long campaignLimit, DateTime startDate, DateTime endDate) {
        this.country = country;
        this.name = name;
        this.description = description;
        this.products = new ArrayList<Product>();
        this.campaignLimit = campaignLimit;
        this.startDate = startDate;
        this.endDate = endDate;
        this.created = new DateTime();
        this.lastUpdate = this.created;
        this.isActive = false;

    }

}
