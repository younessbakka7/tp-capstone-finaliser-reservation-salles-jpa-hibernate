package com.example.util;

import org.hibernate.Session;
import org.hibernate.stat.Statistics;

import javax.persistence.EntityManager;

public class PerformanceReport {
    private final EntityManager em;

    public PerformanceReport(EntityManager em) {
        this.em = em;
    }

    public void printReport() {
        Session session = em.unwrap(Session.class);
        Statistics stats = session.getSessionFactory().getStatistics();
        System.out.println();
        System.out.println("=== RAPPORT DE PERFORMANCE HIBERNATE ===");
        System.out.println("Requêtes JPQL exécutées       : " + stats.getQueryExecutionCount());
        System.out.println("Cache L2 — hits               : " + stats.getSecondLevelCacheHitCount());
        System.out.println("Cache L2 — misses             : " + stats.getSecondLevelCacheMissCount());
        System.out.println("Cache L2 — mises en cache     : " + stats.getSecondLevelCachePutCount());
        System.out.println("Transactions                  : " + stats.getTransactionCount());
        System.out.println("Durée maximale d'une requête (ms): " + stats.getQueryExecutionMaxTime());
    }
}
