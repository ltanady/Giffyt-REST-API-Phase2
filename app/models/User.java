package models;

import com.restfb.DefaultFacebookClient;
import com.restfb.Facebook;
import com.restfb.FacebookClient;
import com.restfb.exception.FacebookException;
import com.restfb.exception.FacebookGraphException;
import facebook.FacebookEvent;
import facebook.FacebookLocation;
import formatter.GiffytDateTimeFormatter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import play.cache.Cache;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ltanady
 * Date: 18/4/13
 * Time: 5:44 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "GIFFYT_USER")
public class User extends Model {

    @Id
    public Long id;

    @Facebook
    public String uid;

    @Facebook
    public String name;

    @Facebook
    public String email;

    @Facebook
    public String sex;

    @Facebook
    public String birthday;

    @Facebook
    public String birthday_date;

    @Facebook
    public FacebookLocation current_location;

    @JsonIgnore
    @Constraints.Required
    @Column(name = "created")
    public DateTime created;

    @JsonIgnore
    @Constraints.Required
    @Column(name = "last_update")
    public DateTime lastUpdate;

    public static Finder<String, User> find = new Finder<String, User>(String.class, User.class);

    /**
     * Get birthday date and month
     * @return
     */
    public String getBirthdateNoYear() {
        if(birthday_date != null) {
            String birthDateString = birthday_date.substring(0, 5);
            DateTimeFormatter usDateFormatter = GiffytDateTimeFormatter.getDateTimeFormatter(GiffytDateTimeFormatter.US_DATE_NO_YEAR);
            DateTime birthDate = usDateFormatter.parseDateTime(birthDateString);
            DateTimeFormatter ukDateFormatter = GiffytDateTimeFormatter.getDateTimeFormatter(GiffytDateTimeFormatter.UK_DATE_NO_YEAR);
            return ukDateFormatter.print(birthDate);

        }
        return null;

    }

    /**
     * Calculate user's age from birthday
     * @return
     */
    public Long getAge() {
        // Check if the birthday_date has year
        if(birthday_date != null && birthday_date.length() > 5) {
            DateTimeFormatter usDateFormatter = GiffytDateTimeFormatter.getDateTimeFormatter(GiffytDateTimeFormatter.US_DATE);
            LocalDateTime currentDate = new LocalDateTime();
            DateTime birthDate = usDateFormatter.parseDateTime(birthday_date);

            Years age = Years.yearsBetween(birthDate.toLocalDateTime(), currentDate);
            if(age != null)
                return new Long(age.getYears()+1);

        }
        return null;

    }

    /**
     * Get user's location.
     * @return
     */
    public Country getCountry() {
        if(current_location != null) {
            return Country.findByName(current_location.country);

        }
        return null;

    }

    /**
     * Get user's list of gifts with PENDING_RECIPIENT status.
     * @return List<Order>
     */
    public List<Order> getGifts() {
        return Order.find.where().eq("recipientFacebookId", this.uid).eq("orderStatus", Order.ORDER_STATUS.PENDING_RECIPIENT).findList();

    }

    /**
     * Find a Facebook user by facebook id
     * @param facebookId
     * @return
     */
    public static User findByFacebookId(String facebookId) {
        User user = User.find.where().eq("uid", facebookId).findUnique();

        return user;

    }

    /**
     * Login via Facebook.
     * @param facebookAccessToken
     * @return
     */
    public static User authenticateFacebook(String facebookAccessToken) {
        if(facebookAccessToken == null)
            return null;

        try {
            FacebookClient facebookClient = new DefaultFacebookClient(facebookAccessToken);
            String userQuery = "SELECT uid, name, email, birthday_date FROM user WHERE uid = me()";
            List<User> users = facebookClient.executeFqlQuery(userQuery, User.class);
            User currentFbUser = null;
            if(users.size() == 1)
                currentFbUser = users.get(0);

            // Firs time login
            if(findByFacebookId(currentFbUser.uid) == null) {
                currentFbUser.created = new DateTime();
                currentFbUser.lastUpdate = currentFbUser.created;

            }else {
                currentFbUser = findByFacebookId(currentFbUser.uid);
                currentFbUser.lastUpdate = new DateTime();

            }
            currentFbUser.save();
            return currentFbUser;

        }catch(FacebookGraphException e) {
            play.Logger.error("User.authenticateFacebook(): FacebookGraphException, " + e.getMessage());

        }catch(FacebookException e) {
            play.Logger.error("User.authenticateFacebook(): FacebookException, " + e.getMessage());

        }
        return null;

    }

    /**
     * Get Facebook Events.
     * @param facebookAccessToken
     * @return
     */
    public static FacebookEvent getFacebookEvents(String facebookAccessToken) {
        if(facebookAccessToken == null)
            return null;

        try {
            FacebookClient facebookClient = new DefaultFacebookClient(facebookAccessToken);

            Map<String, String> queries = new HashMap<String, String>();
            DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd");

            //DateTime today = DateTimeFormat.forPattern("MM/dd/yyyy").parseDateTime("11/30/2013");
            DateTime today = DateTime.now();
            String todayString = formatter.print(today);
            String todayQuery = "SELECT uid, name, sex, birthday, birthday_date FROM user WHERE uid in (SELECT uid2 FROM friend WHERE uid1 = me()) " +
                    "AND birthday_date != 'null' AND substr(birthday_date,0,5) = '" + todayString + "' ORDER BY name ASC";

            DateTime tomorrow = today.plusDays(1);
            String tomorrowString = formatter.print(tomorrow);
            String tomorrowQuery = "SELECT uid, name, sex, birthday, birthday_date FROM user WHERE uid in (SELECT uid2 FROM friend WHERE uid1 = me()) " +
                    "AND birthday_date != 'null' AND substr(birthday_date,0,5) = '" + tomorrowString + "' ORDER BY name ASC";

            DateTime endOfMonth = today.dayOfMonth().withMaximumValue();
            String endOfMonthString = formatter.print(endOfMonth);
            String thisMonthQuery = "SELECT uid, name, sex, birthday, birthday_date FROM user WHERE uid in (SELECT uid2 FROM friend WHERE uid1 = me()) " +
                    "AND birthday_date != 'null' AND substr(birthday_date,0,5) > '" + tomorrowString +
                    "' AND substr(birthday_date,0,5) <= '" + endOfMonthString + "' ORDER BY birthday_date ASC";

            DateTime startNextMonth = endOfMonth.plusDays(1);
            DateTime endNextMonth = startNextMonth.dayOfMonth().withMaximumValue();
            String startNextMonthString = formatter.print(startNextMonth);
            String endNextMonthString = formatter.print(endNextMonth);
            String nextMonthQuery = "SELECT uid, name, sex, birthday, birthday_date FROM user WHERE uid in (SELECT uid2 FROM friend WHERE uid1 = me()) " +
                    "AND birthday_date != 'null' AND substr(birthday_date,0,5) >= '" + startNextMonthString + "' AND substr(birthday_date,0,5) <= '" + endNextMonthString + "' ORDER BY birthday_date ASC";

            queries.put("today", todayQuery);
            queries.put("tomorrow", tomorrowQuery);
            queries.put("thisMonth", thisMonthQuery);
            queries.put("nextMonth", nextMonthQuery);

            FacebookEvent facebookEvent = facebookClient.executeFqlMultiquery(queries, FacebookEvent.class);
            return facebookEvent;

        }catch(FacebookGraphException e) {
            play.Logger.error("User.getFacebookEvents(), " + e.getMessage());

        }catch(FacebookException e) {
            play.Logger.error("User.getFacebookEvents(), " + e.getMessage());

        }
        return null;

    }

    /**
     * Get Facebook Friends.
     * @param facebookAccessToken
     * @return
     */
    public static List<User> getFacebookFriends(String facebookAccessToken) {
        if(facebookAccessToken == null)
            return null;

        try {
            FacebookClient facebookClient = new DefaultFacebookClient(facebookAccessToken);
            List<User> friends = null;
            if(Cache.get(facebookAccessToken + ".friends") != null) {
                if(Cache.get(facebookAccessToken + ".friends") instanceof List) {
                    friends = (List) Cache.get(facebookAccessToken + ".friends");

                }

            }else {
                String friendsQuery = "SELECT uid, name, sex, birthday, birthday_date, current_location FROM user WHERE uid in (SELECT uid2 FROM friend WHERE uid1 = me()) ORDER BY name ASC";
                friends = facebookClient.executeFqlQuery(friendsQuery, User.class);
                Cache.set(facebookAccessToken + ".friends", friends, 60*60*60);

            }
            return new ArrayList<User>(friends);

        }catch(FacebookGraphException e) {
            play.Logger.error("User.getFacebookFriends(): FacebookGraphException, " + e.getMessage());

        }catch(FacebookException e) {
            play.Logger.error("User.getFacebookFriends(): FacebookException, " + e.getMessage());

        }
        return null;

    }

    /**
     * Get Facebook friend info
     * @param facebookAccessToken
     * @param friendFacebookId
     * @return
     */
    public static User getFacebookFriendInfo(String facebookAccessToken, String friendFacebookId) {
        if(facebookAccessToken == null || friendFacebookId == null)
            return null;

        try {
            FacebookClient facebookClient = new DefaultFacebookClient(facebookAccessToken);
            Object facebookFriendObject = Cache.get(facebookAccessToken + "." + friendFacebookId);
            // Get from cache
            if(facebookFriendObject != null) {
                if(facebookFriendObject instanceof User) {
                    return (User) facebookFriendObject;
                }

            }else {
                String friendQuery = "SELECT uid, name, sex, birthday, birthday_date, current_location FROM user WHERE uid in (SELECT uid2 FROM friend WHERE uid1 = me() AND uid2 = " + friendFacebookId + ") or uid = " + friendFacebookId;
                List<User> users = facebookClient.executeFqlQuery(friendQuery, User.class);

                if(users.size() == 1) {
                    User friend = users.get(0);
                    Cache.set(facebookAccessToken + "." + friend.uid, friend);

                    return friend;

                }

            }

        }catch(FacebookGraphException e) {
            play.Logger.error("User.getFacebookFriends(): FacebookGraphException, " + e.getMessage());

        }catch(FacebookException e) {
            play.Logger.error("User.getFacebookFriends(): FacebookException, " + e.getMessage());

        }
        return null;

    }

    @Override
    public String toString() {
        return String.format("%s, %s, %s, %s", uid, name, email, birthday_date);

    }

}
