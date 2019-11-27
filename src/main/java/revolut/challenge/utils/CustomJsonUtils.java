package revolut.challenge.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import spark.ResponseTransformer;

public class CustomJsonUtils {

    public CustomJsonUtils() throws InstantiationException {
        throw new InstantiationException("Instances of this type are forbidden.");
    }

    public static String toJson(Object object) throws JsonProcessingException {
        ObjectMapper Obj = new ObjectMapper();
        String jsonStr = Obj.writeValueAsString(object);
        return jsonStr;
    }

    public static ResponseTransformer json() {
        return CustomJsonUtils::toJson;
    }
}