package org.restaurant;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.restaurant.model.Element;
import org.restaurant.model.Hall;
import org.restaurant.model.Table;
import redis.clients.jedis.json.JsonObjectMapper;

public class RedisJsonObjectMapper implements JsonObjectMapper {
    private final ObjectMapper mapper = new ObjectMapper();
    private final Gson gson = new Gson();

    @Override
    public <T> T fromJson(String s, Class<T> aClass) {
        try {
            if (aClass == Element.class) {
                if (mapper.readTree(s).has("premium")) {
                    return (T) gson.fromJson(s, Table.class);
                }
                else {
                    return (T) gson.fromJson(s, Hall.class);
                }
            }
            return gson.fromJson(s, aClass);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toJson(Object o) {
        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
