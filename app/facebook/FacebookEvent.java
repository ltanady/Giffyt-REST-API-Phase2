package facebook;

import com.restfb.Facebook;
import models.User;
import models.User;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ltanady
 * Date: 18/4/13
 * Time: 6:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class FacebookEvent {
    @Facebook
    public List<User> today;

    @Facebook
    public List<User> tomorrow;

    @Facebook
    public List<User> thisMonth;

    @Facebook
    public List<User> nextMonth;

}