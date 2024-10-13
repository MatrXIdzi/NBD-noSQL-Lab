package org.restaurant.repositories;

import jakarta.persistence.EntityManager;
import org.restaurant.reservations.Reservation;

public class ReservationRepository implements Repository <Reservation> {
    private EntityManager entityManager;

    public ReservationRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void add(Reservation reservation) {
        entityManager.persist(reservation);
    }

    @Override
    public void remove(Reservation reservation) {
        entityManager.remove(entityManager.contains(reservation) ? reservation : entityManager.merge(reservation));
    }

    @Override
    public int count() {
        return ((Number) entityManager.createQuery("SELECT COUNT(c) FROM Reservation c").getSingleResult()).intValue();
    }

    @Override
    public Reservation get(int ID) {
        return entityManager.find(Reservation.class, ID);
    }
}
