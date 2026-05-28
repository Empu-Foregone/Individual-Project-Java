package com.example.socialnetwork.db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseConnection {
    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            Properties props = new Properties();
            String configFile = "db.properties";
            InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream(configFile);
            if (input == null) {
                throw new SQLException("Файл " + configFile + " не знайдено! Переконайтеся, що він лежить у src/main/resources/");
            }
            try {
                props.load(input);
                input.close();
            } catch (Exception e) {
                throw new SQLException("Помилка читання " + configFile + ": " + e.getMessage());
            }

            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");

            if (url == null || user == null || password == null) {
                throw new SQLException("Неповні параметри в db.properties. Потрібні: db.url, db.user, db.password");
            }

            System.out.println("Підключення до: " + url);
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Підключення до БД успішне!");
        }
        return connection;
    }

    public static void createTablesIfNotExist() {
        String[] createStatements = {
            "CREATE TABLE IF NOT EXISTS groups (id SERIAL PRIMARY KEY, name VARCHAR(50) NOT NULL UNIQUE, headman_id INTEGER NULL)",
            "CREATE TABLE IF NOT EXISTS students (id SERIAL PRIMARY KEY, first_name VARCHAR(50) NOT NULL, last_name VARCHAR(50) NOT NULL, email VARCHAR(100) NOT NULL UNIQUE)",
            "CREATE TABLE IF NOT EXISTS student_groups (student_id INTEGER NOT NULL REFERENCES students(id) ON DELETE CASCADE, group_id INTEGER NOT NULL REFERENCES groups(id) ON DELETE CASCADE, PRIMARY KEY (student_id, group_id))",
            "CREATE TABLE IF NOT EXISTS friendships (id SERIAL PRIMARY KEY, student1_id INTEGER NOT NULL REFERENCES students(id) ON DELETE CASCADE, student2_id INTEGER NOT NULL REFERENCES students(id) ON DELETE CASCADE, CHECK (student1_id < student2_id), UNIQUE (student1_id, student2_id))",
            "ALTER TABLE groups ADD CONSTRAINT IF NOT EXISTS fk_headman FOREIGN KEY (headman_id) REFERENCES students(id) ON DELETE SET NULL"
        };
        try (Connection conn = getConnection()) {
            for (String sql : createStatements) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(sql);
                }
            }
            System.out.println("Таблиці перевірено/створено.");
        } catch (SQLException e) {
            System.err.println("Помилка створення таблиць: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("З'єднання з БД закрито.");
        }
    }
}