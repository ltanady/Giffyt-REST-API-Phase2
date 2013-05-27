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
 * DateTime: 28/1/13
 * Time: 3:01 PM
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "GIFFYT_PRODUCT_ATTRIBUTE")
public class ProductAttribute extends Model {

    @Id
    public Long id;

    @Constraints.Required
    @Column(name = "name")
    public String name;

    @Constraints.Required
    @Column(name = "value")
    public String value;

    @JsonBackReference
    @ManyToOne
    @Constraints.Required
    public Product product;

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

    public static Finder<Long, ProductAttribute> find = new Finder<Long, ProductAttribute>(Long.class, ProductAttribute.class);

    public ProductAttribute(String name, String value) {
        this.name = name;
        this.value = value;
        this.isActive = true;
        this.created = new DateTime();
        this.lastUpdate = this.created;

    }


}