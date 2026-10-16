package com.example.util;

import com.example.model.Reservation;
import com.example.model.Salle;
import com.example.model.Utilisateur;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class DataInitializer {
    private final EntityManager em;

    public DataInitializer(EntityManager em) {
        this.em = em;
    }

    public void initData() {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try {
            Utilisateur alice = new Utilisateur("Alice Dupont", "alice.dupont@example.com");
            Utilisateur karim = new Utilisateur("Karim Benali", "karim.benali@example.com");
            Utilisateur sara = new Utilisateur("Sara Amrani", "sara.amrani@example.com");
            em.persist(alice);
            em.persist(karim);
            em.persist(sara);

            Salle salleA = new Salle("Salle A", 20, "Écran et tableau blanc");
            Salle salleB = new Salle("Salle B", 12, "Visioconférence");
            Salle salleC = new Salle("Salle C", 35, "Configuration conférence");
            em.persist(salleA);
            em.persist(salleB);
            em.persist(salleC);
            em.flush();

            LocalDateTime demain = LocalDateTime.now().plusDays(1).withMinute(0).withSecond(0).withNano(0);
            em.persist(new Reservation(alice, salleA, demain, demain.plusHours(2)));
            em.persist(new Reservation(karim, salleB, demain.plusHours(1), demain.plusHours(3)));
            em.persist(new Reservation(sara, salleC, demain.plusDays(1), demain.plusDays(1).plusHours(2)));
            tx.commit();
        } catch (RuntimeException exception) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw exception;
        }
    }
}
