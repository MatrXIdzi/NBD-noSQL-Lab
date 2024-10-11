package org.restaurant.repositories;

import org.restaurant.reservations.Reservation;

import java.util.ArrayList;
import java.util.List;

public class ReservationRepository implements Repository <Reservation> {
    private List<Reservation> reservations = new ArrayList<Reservation>();
    @Override
    public void add(Reservation reservation) {

    }

    @Override
    public void remove(Reservation reservation) {

    }

    @Override
    public int count() {
        return 0;
    }

    @Override
    public Reservation get(int ID) {
        return null;
    }
}
