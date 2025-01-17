package org.project.cursexchange.util;

import com.google.gson.Gson;

public class JsonConverter {
    private static final Gson gson = new Gson();

    public static String convertToJson(Object object) {
        return gson.toJson(object);
    }
}
