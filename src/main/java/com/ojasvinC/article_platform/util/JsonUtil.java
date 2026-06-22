package com.ojasvinC.article_platform.util;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static String toJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj); // convert object → JSON string
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz); // convert JSON → object
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}