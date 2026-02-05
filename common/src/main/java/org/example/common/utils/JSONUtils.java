package org.example.common.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONUtils {

    private static ObjectMapper mapper = new ObjectMapper();

    public static String toJSONString(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T parseObject(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JsonNode toJsonNode(String json) {
        try {
            return mapper.readTree(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JsonNode toJsonNode(Object o) {
        try {
            return mapper.readTree(mapper.writeValueAsBytes(o));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
