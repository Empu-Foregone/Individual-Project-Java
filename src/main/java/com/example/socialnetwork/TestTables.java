package com.example.socialnetwork;

import com.example.socialnetwork.db.DatabaseConnection;
import java.sql.*;

public class TestTables {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("Підключено до: " + conn.getCatalog());
            String[] tables = {"students", "groups", "student_groups", "friendships"};
            for (String table : tables) {
                try (ResultSet rs = conn.getMetaData().getTables(null, null, table, null)) {
                    if (rs.next()) {
                        System.out.println("✅ Таблиця " + table + " існує");
                    } else {
                        System.out.println("❌ Таблиця " + table + " НЕ існує");
                    }
                }
            }
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM students");
                if (rs.next()) {
                    System.out.println("Кількість студентів: " + rs.getInt(1));
                }
            }
            
        } catch (Exception e) {
            System.err.println("Помилка: " + e.getMessage());
            e.printStackTrace();
        }
    }
}