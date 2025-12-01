package com.marlodev.app_android.data.network.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class GsonProvider {

    public static Gson getGson() {
        return new GsonBuilder()
                // LocalDateTime deserializer
                .registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
                    @Override
                    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        String str = json.getAsString();
                        if (str == null || str.isEmpty()) return null;
                        try {
                            return LocalDateTime.parse(str, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                        } catch (Exception e) {
                            throw new JsonParseException("Error parseando LocalDateTime: " + str, e);
                        }
                    }
                })
                // ZonedDateTime deserializer
                .registerTypeAdapter(ZonedDateTime.class, new JsonDeserializer<ZonedDateTime>() {
                    @Override
                    public ZonedDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        String str = json.getAsString();
                        if (str == null || str.isEmpty()) return null;

                        try {
                            // Intenta parsear con ISO_ZONED_DATE_TIME (2025-11-30T18:02:28.755598-05:00 o Z)
                            return ZonedDateTime.parse(str, DateTimeFormatter.ISO_ZONED_DATE_TIME);
                        } catch (Exception e1) {
                            try {
                                // Fallback: si viene como LocalDateTime sin zona, lo asume UTC
                                return ZonedDateTime.of(LocalDateTime.parse(str, DateTimeFormatter.ISO_LOCAL_DATE_TIME), ZoneOffset.UTC);
                            } catch (Exception e2) {
                                throw new JsonParseException("Error parseando ZonedDateTime: " + str, e2);
                            }
                        }
                    }
                })
                .create();
    }
}
