package org.restaurant;

import com.google.gson.*;
import org.restaurant.model.Reservation;

import java.lang.reflect.Type;

public class ReservationGsonSerializer implements JsonSerializer<Reservation> {
    private static final String RESTAURANT_NAME = "Kafka Na Wynos";

    @Override
    public JsonElement serialize(Reservation reservation, Type type, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        obj.addProperty("reservationId", reservation.getId().toString());
        obj.addProperty("reservationDate", reservation.getReservationDate().getTime());
        obj.addProperty("clientId", reservation.getClient().getId().toString());
        obj.addProperty("elementId", reservation.getElement().getId().toString());
        obj.addProperty("restaurantName", RESTAURANT_NAME);
        return obj;
    }
}