package facebook;

import com.restfb.Facebook;

/**
 * Created with IntelliJ IDEA.
 * User: ltanady
 * Date: 26/4/13
 * Time: 5:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class FacebookLocation {

    @Facebook
    public String city;

    @Facebook
    public String state;

    @Facebook
    public String country;

    @Facebook
    public String zip;

}
