package serializers.facebook;

import facebook.FacebookEvent;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * Event: ltanady
 * Date: 26/4/13
 * Time: 4:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class FacebookEventSerializer extends SerializerBase<FacebookEvent> {

    public FacebookEventSerializer() {
        super(FacebookEvent.class, true);

    }

    @Override
    public void serialize(FacebookEvent facebookEvent, JsonGenerator jg, SerializerProvider sp)  throws IOException, JsonProcessingException {
        try {
            if(facebookEvent != null) {
                jg.writeStartObject();

                jg.writeObjectField("today", facebookEvent.today);
                jg.writeObjectField("tomorrow", facebookEvent.tomorrow);
                jg.writeObjectField("thisMonth", facebookEvent.thisMonth);
                jg.writeObjectField("nextMonth", facebookEvent.nextMonth);

                jg.writeEndObject();

            }

        }catch(JsonProcessingException e) {
            play.Logger.error("FacebookEventSerializer.serialize(): Json Format Exception");
            play.Logger.error("FacebookEventSerializer.serialize(): " + e.getMessage());

        }catch(IOException e) {
            play.Logger.error("FacebookEventSerializer.serialize(): IOException");
            play.Logger.error("FacebookEventSerializer.serialize(): " + e.getMessage());

        }

    }

}