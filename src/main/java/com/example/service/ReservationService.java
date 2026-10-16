package com.example.service;

import com.example.model.Reservation;
import org.hibernate.OptimisticLockException;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReservationService {
    private static final Logger LOGGER = Logger.getLogger(ReservationService.class.getName());
    private final EntityManager em;

    public ReservationService(EntityManager em) {
        this.em = em;
    }

    public Reservation create(Reservation reservation) {
        validatePeriod(reservation.getDateDebut(), reservation.getDateFin());
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            List<Reservation> conflicts = em.createQuery(
                    "SELECT r FROM Reservation r WHERE r.salle.id = :salleId "
                            + "AND r.dateDebut < :fin AND r.dateFin > :debut", Reservation.class)
                    .setParameter("salleId", reservation.getSalle().getId())
                    .setParameter("debut", reservation.getDateDebut())
                    .setParameter("fin", reservation.getDateFin())
                    .getResultList();
            if (!conflicts.isEmpty()) {
                throw new IllegalStateException("La salle est déjà réservée sur cette période.");
            }
            em.persist(reservation);
            tx.commit();
            LOGGER.info("Réservation créée pour " + reservation.getUtilisateur().getEmail());
            return reservation;
        } catch (RuntimeException exception) {
            if (tx.isActive()) {
                tx.rollback();
            }
            if (exception instanceof OptimisticLockException) {
                LOGGER.log(Level.WARNING, "Conflit de mise à jour détecté.", exception);
            } else if (exception instanceof IllegalStateException) {
                LOGGER.warning(exception.getMessage());
            } else {
                LOGGER.log(Level.SEVERE, "Échec de création de réservation.", exception);
            }
            throw exception;
        }
    }

    private void validatePeriod(LocalDateTime debut, LocalDateTime fin) {
        if (debut == null || fin == null || !fin.isAfter(debut)) {
            throw new IllegalArgumentException("La date de fin doit être postérieure à la date de début.");
        }
    }
}
