package com.example.socialnetwork;

import com.example.socialnetwork.db.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;

public class TestDB {
    public static void main(String[] args) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            System.out.println("✅ Підключення успішне!");
            System.out.println("База даних: " + conn.getCatalog());
            conn.close();
        } catch (SQLException e) {
            System.err.println("❌ Помилка SQL: " + e.getMessage());
            e.printStackTrace();
        }
    }
}