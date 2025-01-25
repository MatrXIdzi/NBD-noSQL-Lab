package org.example;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import redis.clients.jedis.json.JsonObjectMapper;

import java.util.Date;
import java.util.UUID;

public class RedisJsonObjectMapper implements JsonObjectMapper {
    @Override
    public <T> T fromJson(String s, Class<T> aClass) {
        if (aClass == Reservation.class) {
            JsonObject obj = JsonParser.parseString(s).getAsJsonObject();

            UUID reservationId = UUID.fromString(obj.get("reservationId").getAsString());
            UUID clientId = UUID.fromString(obj.get("clientId").getAsString());
            UUID elementId = UUID.fromString(obj.get("elementId").getAsString());
            Date reservationDate = new Date(obj.get("reservationDate").getAsLong());
            String restaurantName = obj.get("restaurantName").getAsString();

            Reservation reservation = new Reservation(reservationId, reservationDate, clientId, elementId, restaurantName);

            return (T) reservation;
        }
        throw new RuntimeException("Can't convert object from json");
    }

    @Override
    public String toJson(Object o) {
        if (!(o instanceof Reservation)) throw new RuntimeException("Can't convert object to json");

        JsonObject obj = new JsonObject();
        Reservation reservation = (Reservation) o;
        obj.addProperty("reservationId", reservation.getId().toString());
        obj.addProperty("reservationDate", reservation.getReservationDate().getTime());
        obj.addProperty("clientId", reservation.getClientId().toString());
        obj.addProperty("elementId", reservation.getElementId().toString());
        obj.addProperty("restaurantName", reservation.getRestaurantName());
        return obj.toString();
    }
}
