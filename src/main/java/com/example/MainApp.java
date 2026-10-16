package com.example;

import com.example.model.Reservation;
import com.example.model.Salle;
import com.example.model.Utilisateur;
import com.example.service.ReservationService;
import com.example.util.DataInitializer;
import com.example.util.MigrationExecutor;
import com.example.util.PerformanceReport;
import com.example.util.ReservationQueries;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDateTime;
import java.util.List;

public class MainApp {
    private static final String JDBC_URL = "jdbc:h2:mem:reservation;DB_CLOSE_DELAY=-1";

    public static void main(String[] args) throws Exception {
        System.out.println("=== APPLICATION DE RÉSERVATION DE SALLES ===");
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("ReservationPU");
        try {
            new MigrationExecutor(JDBC_URL, "sa", "")
                    .executeMigration("db/migration/v2_0_add_description.sql");

            EntityManager em = emf.createEntityManager();
            try {
                new DataInitializer(em).initData();
                System.out.println("Données de démonstration : 3 utilisateurs, 3 salles, 3 réservations.");

                List<Reservation> upcoming = new ReservationQueries(em)
                        .upcomingInRoom("Salle A", LocalDateTime.now());
                System.out.println("JPQL — réservations à venir pour Salle A : " + upcoming.size());
                for (Reservation reservation : upcoming) {
                    System.out.println("  • " + reservation.getSalle().getNom() + " | "
                            + reservation.getUtilisateur().getNom() + " | " + reservation.getDateDebut());
                }
                int criteriaCount = new ReservationQueries(em)
                        .upcomingInRoomCriteria("Salle A", LocalDateTime.now()).size();
                System.out.println("Criteria API — même recherche : " + criteriaCount + " résultat(s).");

                Utilisateur user = em.createQuery(
                                "SELECT u FROM Utilisateur u WHERE u.email = :email", Utilisateur.class)
                        .setParameter("email", "alice.dupont@example.com")
                        .getSingleResult();
                Salle room = em.createQuery(
                                "SELECT s FROM Salle s WHERE s.nom = :nom", Salle.class)
                        .setParameter("nom", "Salle A")
                        .getSingleResult();
                LocalDateTime start = LocalDateTime.now().plusDays(3);
                new ReservationService(em).create(new Reservation(user, room, start, start.plusHours(1)));
                System.out.println("Transaction RESOURCE_LOCAL : nouvelle réservation enregistrée.");

                showSecondLevelCache(emf, room.getId());
                new PerformanceReport(em).printReport();
            } finally {
                em.close();
            }
        } finally {
            emf.close();
        }
        System.out.println();
        System.out.println("Exécution terminée avec succès.");
    }

    private static void showSecondLevelCache(EntityManagerFactory emf, Long roomId) {
        EntityManager first = emf.createEntityManager();
        try {
            first.find(Salle.class, roomId);
        } finally {
            first.close();
        }
        EntityManager second = emf.createEntityManager();
        try {
            second.find(Salle.class, roomId);
        } finally {
            second.close();
        }
        System.out.println("Cache de second niveau : Salle A relue dans une seconde session.");
    }
}
