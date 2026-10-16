package com.example.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class MigrationExecutor {
    private static final Logger LOGGER = Logger.getLogger(MigrationExecutor.class.getName());
    private final String jdbcUrl;
    private final String username;
    private final String password;

    public MigrationExecutor(String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
    }

    public void executeMigration(String scriptPath) throws IOException, java.sql.SQLException {
        InputStream resource = getClass().getClassLoader().getResourceAsStream(scriptPath);
        if (resource == null) {
            throw new IOException("Migration introuvable dans le classpath : " + scriptPath);
        }
        StringBuilder sql = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.stripLeading().startsWith("--")) {
                    sql.append(line).append('\n');
                }
            }
        }

        List<String> statements = splitStatements(sql.toString());
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            connection.setAutoCommit(false);
            try (Statement statement = connection.createStatement()) {
                for (String command : statements) {
                    statement.execute(command);
                }
                connection.commit();
                LOGGER.info("Migration appliquée : " + scriptPath);
            } catch (java.sql.SQLException exception) {
                connection.rollback();
                throw exception;
            }
        }
    }

    private List<String> splitStatements(String script) {
        List<String> statements = new ArrayList<>();
        for (String command : script.split(";")) {
            if (!command.trim().isEmpty()) {
                statements.add(command.trim());
            }
        }
        return statements;
    }
}
