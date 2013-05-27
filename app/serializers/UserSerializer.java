package serializers;

import models.User;
import models.Country;
import models.User;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: ltanady
 * Date: 26/4/13
 * Time: 4:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserSerializer extends SerializerBase<User> {

    public UserSerializer() {
        super(User.class, true);

    }

    @Override
    public void serialize(User user, JsonGenerator jg, SerializerProvider sp)  throws IOException, JsonProcessingException {
        try {
            if(user != null) {
                jg.writeStartObject();

                jg.writeStringField("facebookId", user.uid);
                jg.writeStringField("name", user.name);
                jg.writeStringField("email", user.email);
                jg.writeStringField("gender", user.sex);
                jg.writeStringField("birthday", user.getBirthdateNoYear());
                jg.writeObjectField("age", user.getAge());

                if(user.current_location != null && user.current_location.country != null) {
                    jg.writeObjectField("country", Country.findByName(user.current_location.country));

                }else {
                    jg.writeObjectField("country", null);

                }
                jg.writeEndObject();

            }

        }catch(JsonProcessingException e) {
            play.Logger.error("UserSerializer.serialize(): Json Format Exception");
            play.Logger.error("UserSerializer.serialize(): " + e.getMessage());

        }catch(IOException e) {
            play.Logger.error("UserSerializer.serialize(): IOException");
            play.Logger.error("UserSerializer.serialize(): " + e.getMessage());

        }

    }

}