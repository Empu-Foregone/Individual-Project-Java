package com.example.socialnetwork.dao;

import com.example.socialnetwork.db.DatabaseConnection;
import com.example.socialnetwork.model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FriendshipDAO {

    public void addFriendship(int student1Id, int student2Id) throws SQLException {
        int a = Math.min(student1Id, student2Id);
        int b = Math.max(student1Id, student2Id);
        String sql = "INSERT INTO friendships (student1_id, student2_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, a);
            pstmt.setInt(2, b);
            pstmt.executeUpdate();
        }
    }

    public void removeFriendship(int student1Id, int student2Id) throws SQLException {
        int a = Math.min(student1Id, student2Id);
        int b = Math.max(student1Id, student2Id);
        String sql = "DELETE FROM friendships WHERE student1_id = ? AND student2_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, a);
            pstmt.setInt(2, b);
            pstmt.executeUpdate();
        }
    }

    public List<Student> getFriends(int studentId) throws SQLException {
        List<Student> friends = new ArrayList<>();
        String sql = "SELECT s.id, s.first_name, s.last_name, s.email FROM students s " +
                     "WHERE s.id IN (SELECT student1_id FROM friendships WHERE student2_id = ? " +
                     "               UNION SELECT student2_id FROM friendships WHERE student1_id = ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    friends.add(new Student(
                            rs.getInt("id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("email")
                    ));
                }
            }
        }
        return friends;
    }

    public int countFriends(int studentId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM friendships WHERE student1_id = ? OR student2_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    public List<Student> getTopStudentsByFriendsCount(int limit) throws SQLException {
        List<Student> top = new ArrayList<>();
        String sql = "SELECT s.id, s.first_name, s.last_name, s.email, " +
                     "(SELECT COUNT(*) FROM friendships WHERE student1_id = s.id OR student2_id = s.id) AS friends_cnt " +
                     "FROM students s ORDER BY friends_cnt DESC LIMIT ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Student st = new Student(
                            rs.getInt("id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("email")
                    );
                    top.add(st);
                }
            }
        }
        return top;
    }

    public List<String> getMostFriendlyGroupPairs() throws SQLException {
        List<String> result = new ArrayList<>();
        String sql = "SELECT g1.name AS group1, g2.name AS group2, COUNT(*) AS friendship_count " +
                     "FROM friendships f " +
                     "JOIN student_groups sg1 ON f.student1_id = sg1.student_id " +
                     "JOIN student_groups sg2 ON f.student2_id = sg2.student_id " +
                     "JOIN groups g1 ON sg1.group_id = g1.id " +
                     "JOIN groups g2 ON sg2.group_id = g2.id " +
                     "WHERE g1.id < g2.id " +
                     "GROUP BY g1.name, g2.name " +
                     "ORDER BY friendship_count DESC LIMIT 5";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                result.add(rs.getString("group1") + " — " + rs.getString("group2") +
                           " : " + rs.getInt("friendship_count") + " дружб");
            }
        }
        return result;
    }

    public List<Student> getStudentsNotFriendsWithOwnHeadman() throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT DISTINCT s.id, s.first_name, s.last_name, s.email " +
                     "FROM students s " +
                     "JOIN student_groups sg ON s.id = sg.student_id " +
                     "JOIN groups g ON sg.group_id = g.id " +
                     "WHERE g.headman_id IS NOT NULL " +
                     "AND s.id != g.headman_id " +
                     "AND NOT EXISTS (SELECT 1 FROM friendships f " +
                     "WHERE (f.student1_id = s.id AND f.student2_id = g.headman_id) " +
                     "OR (f.student1_id = g.headman_id AND f.student2_id = s.id))";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Student(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email")
                ));
            }
        }
        return list;
    }
}