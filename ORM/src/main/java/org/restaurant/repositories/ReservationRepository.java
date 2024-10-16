package org.restaurant.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.TypedQuery;
import org.restaurant.reservations.Reservation;

import java.util.Date;

public class ReservationRepository implements Repository <Reservation> {
    private EntityManager entityManager;

    public ReservationRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void add(Reservation reservation) {
        try {
            if (isElementReserved(reservation.getElement().getID(), reservation.getReservationDate())) {
                throw new IllegalArgumentException("Element is already reserved for the given date.");
            }
            entityManager.persist(reservation);
        } catch (Exception e) {
            throw e;
        }
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
        return entityManager.find(Reservation.class, ID, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
    }


    private boolean isElementReserved(int elementId, Date reservationDate) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(r) FROM Reservation r WHERE r.element.id = :elementId AND r.reservationDate = :reservationDate",
                Long.class
        );
        query.setParameter("elementId", elementId);
        query.setParameter("reservationDate", reservationDate);
        return query.getSingleResult() > 0;
    }
}