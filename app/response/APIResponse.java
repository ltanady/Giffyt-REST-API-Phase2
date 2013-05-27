package response;

/**
 * Created with IntelliJ IDEA.
 * User: ltanady
 * Date: 26/4/13
 * Time: 7:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class APIResponse {

    public String action;

    public Object data;

    public String message;

    public APIResponse(String action, Object data, String message) {
        this.action = action;
        this.data = data;
        this.message = message;

    }

}
