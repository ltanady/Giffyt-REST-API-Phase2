package models;

import org.codehaus.jackson.annotate.JsonBackReference;
import org.codehaus.jackson.annotate.JsonIgnore;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: ltanady
 * Date: 23/12/12
 * Time: 2:42 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "GIFFYT_PRODUCT_IMAGE")
public class ProductImage extends Model {

    @Id
    public Long id;

    @Constraints.Required
    @Column(name = "image_url")
    public String imageUrl;

    @JsonBackReference
    @ManyToOne
    @Constraints.Required
    public Product product;

    @Constraints.Required
    @Column(name = "number")
    public Long number;

    @Constraints.Required
    @Column(name = "is_active")
    public Boolean isActive;

    @JsonIgnore
    @Constraints.Required
    @Column(name = "created")
    public Date created;

    @JsonIgnore
    @Constraints.Required
    @Column(name = "last_update")
    public Date lastUpdate;

    public static Finder<Long, ProductImage> find = new Finder<Long, ProductImage>(Long.class, ProductImage.class);

    public ProductImage(Product product, Long number, String imageUrl) {
        this.product = product;
        this.number = number;
        this.imageUrl = imageUrl;
        this.isActive = true;
        this.created = new Date();
        this.lastUpdate = this.created;

    }




}
