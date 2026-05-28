package com.example.socialnetwork.dao;

import com.example.socialnetwork.db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StudentGroupDAO {

    public void addStudentToGroup(int studentId, int groupId) throws SQLException {
        String sql = "INSERT INTO student_groups (student_id, group_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, groupId);
            pstmt.executeUpdate();
        }
    }

    public void removeStudentFromGroup(int studentId, int groupId) throws SQLException {
        String sql = "DELETE FROM student_groups WHERE student_id = ? AND group_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, groupId);
            pstmt.executeUpdate();
        }
    }

    public void deleteAllGroupsForStudent(int studentId) throws SQLException {
        String sql = "DELETE FROM student_groups WHERE student_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.executeUpdate();
        }
    }
}