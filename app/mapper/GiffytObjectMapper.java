package mapper;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.module.SimpleModule;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: ltanady
 * Date: 3/8/12
 * Time: 11:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class GiffytObjectMapper extends ObjectMapper {

    private SimpleModule module;

    public GiffytObjectMapper() {
        this.module = new SimpleModule("Giffyt Object Module", new Version(1, 1, 0, "SNAPSHOT"));
        this.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
        this.configure(SerializationConfig.Feature.WRITE_NULL_MAP_VALUES, true);
        this.configure(SerializationConfig.Feature.WRITE_ENUMS_USING_INDEX, true);

    }

    /**
     * Add Serializer to the mapper
     * @param jsonSerializer
     */
    public void addSerializer(JsonSerializer jsonSerializer) {
        this.module.addSerializer(jsonSerializer);

    }

    /**
     * Add Deserializer to the mapper
     * @param jsonDeserializer
     */
    public void addDeserializer(JsonDeserializer jsonDeserializer, Class objectClass) {
        this.module.addDeserializer(objectClass, jsonDeserializer);

    }

    /**
     * Register module before using
     */
    public void registerModule() {
        this.registerModule(this.module);

    }

    /**
     * Overrides parent's method to include default pretty printing
     * @param object
     * @return
     * @throws java.io.IOException
     */
    public String writeValueAsString(Object object) throws IOException{
        String result = null;
        try {
            result = this.writerWithDefaultPrettyPrinter().writeValueAsString(object);
    
        }catch(IOException e) {
            e.printStackTrace();
            play.Logger.error("GiffytObjectMapper.writeValueAsString(): IOException");
            play.Logger.error("GiffytObjectMapper.writeValueAsString(): " + e.getMessage());
            
        }
        return result;
        
    }

}
