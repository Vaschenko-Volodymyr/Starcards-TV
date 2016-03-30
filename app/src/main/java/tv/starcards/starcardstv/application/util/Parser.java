package tv.starcards.starcardstv.application.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Parser {

    public String parse(String json, String key) {
        JsonParser parser = new JsonParser();
        JsonObject mainObject = parser.parse(json).getAsJsonObject();
        String result = mainObject.get(key).toString();
        return result.substring(1, result.length() - 1);
    }
}
