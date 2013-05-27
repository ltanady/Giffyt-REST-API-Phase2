package exception;

import response.APIResponse;

/**
 * Created with IntelliJ IDEA.
 * User: ltanady
 * Date: 9/5/13
 * Time: 10:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class GiffytRESTAPIException extends Exception {

    public APIResponse apiResponse;
    public Exception exception;

    public GiffytRESTAPIException(APIResponse apiResponse, Exception exception) {
        this.apiResponse = apiResponse;
        this.exception = exception;

    }

}
