package com.example;

import com.example.model.Reservation;
import com.example.model.Salle;
import com.example.model.Utilisateur;
import com.example.service.ReservationService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReservationTest {
    private static EntityManagerFactory emf;
    private EntityManager em;

    @BeforeAll
    static void init() {
        emf = Persistence.createEntityManagerFactory("ReservationPU");
    }

    @BeforeEach
    void setUp() {
        em = emf.createEntityManager();
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Reservation").executeUpdate();
        em.createQuery("DELETE FROM Salle").executeUpdate();
        em.createQuery("DELETE FROM Utilisateur").executeUpdate();
        em.getTransaction().commit();
    }

    @AfterAll
    static void close() {
        if (emf != null) {
            emf.close();
        }
    }

    @Test
    void createsReservation() {
        Utilisateur user = new Utilisateur("Test User", "test.user@example.com");
        Salle room = new Salle("Test room", 10, "JUnit fixture");
        em.getTransaction().begin();
        em.persist(user);
        em.persist(room);
        em.getTransaction().commit();

        LocalDateTime start = LocalDateTime.now().plusDays(2);
        Reservation saved = new ReservationService(em)
                .create(new Reservation(user, room, start, start.plusHours(1)));
        assertNotNull(saved.getId());
    }

    @Test
    void rejectsOverlappingReservation() {
        Utilisateur user = new Utilisateur("Test User", "test.user@example.com");
        Salle room = new Salle("Test room", 10, "JUnit fixture");
        em.getTransaction().begin();
        em.persist(user);
        em.persist(room);
        em.getTransaction().commit();

        LocalDateTime start = LocalDateTime.now().plusDays(2);
        ReservationService service = new ReservationService(em);
        service.create(new Reservation(user, room, start, start.plusHours(1)));
        assertThrows(IllegalStateException.class,
                () -> service.create(new Reservation(user, room, start.plusMinutes(30), start.plusHours(2))));
    }
}
