package formatter;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created with IntelliJ IDEA.
 * User: ltanady
 * Date: 23/4/13
 * Time: 3:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class GiffytDateTimeFormatter {

    public static final String US_DATE = "MM/dd/yyyy";
    public static final String US_DATE_NO_YEAR = "MM/dd";
    public static final String UK_DATE = "dd/MM/yyyy";
    public static final String UK_DATE_NO_YEAR = "dd/MM";
    public static final String LONG_DATE = "dd MMMM yyyy";

    public static DateTimeFormatter getDateTimeFormatter(String format) {
        return DateTimeFormat.forPattern(format);

    }

}
