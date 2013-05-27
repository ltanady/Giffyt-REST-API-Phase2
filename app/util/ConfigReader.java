package util;

import play.Play;

/**
 * Created with IntelliJ IDEA.
 * User: flukito
 * Date: 8/2/12
 * Time: 7:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConfigReader {

    public static String getValue(String parameterName){
        return Play.application().configuration().getString(parameterName);
    }
}
