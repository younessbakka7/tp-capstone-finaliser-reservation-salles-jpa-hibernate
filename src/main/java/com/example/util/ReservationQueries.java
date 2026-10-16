package com.example.util;

import com.example.model.Reservation;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.List;

public class ReservationQueries {
    private final EntityManager em;

    public ReservationQueries(EntityManager em) {
        this.em = em;
    }

    public List<Reservation> upcomingInRoom(String roomName, LocalDateTime from) {
        String jpql = "SELECT r FROM Reservation r WHERE r.salle.nom = :nomSalle AND r.dateDebut > :date";
        return em.createQuery(jpql, Reservation.class)
                .setParameter("nomSalle", roomName)
                .setParameter("date", from)
                .getResultList();
    }

    public List<Reservation> upcomingInRoomCriteria(String roomName, LocalDateTime from) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Reservation> query = cb.createQuery(Reservation.class);
        Root<Reservation> root = query.from(Reservation.class);
        Predicate sameRoom = cb.equal(root.get("salle").get("nom"), roomName);
        Predicate upcoming = cb.greaterThan(root.get("dateDebut"), from);
        query.select(root).where(cb.and(sameRoom, upcoming));
        return em.createQuery(query).getResultList();
    }
}
