package com.example.socialnetwork.dao;

import com.example.socialnetwork.db.DatabaseConnection;
import com.example.socialnetwork.model.Group;
import com.example.socialnetwork.model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GroupDAO {

    public void add(Group group) throws SQLException {
        String sql = "INSERT INTO groups (name, headman_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, group.getName());
            if (group.getHeadmanId() != null) {
                pstmt.setInt(2, group.getHeadmanId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    group.setId(rs.getInt(1));
                }
            }
        }
    }

    public Group getById(int id) throws SQLException {
        String sql = "SELECT id, name, headman_id FROM groups WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Integer hid = rs.getObject("headman_id", Integer.class);
                    return new Group(rs.getInt("id"), rs.getString("name"), hid);
                }
            }
        }
        return null;
    }

    public List<Group> getAll() throws SQLException {
        List<Group> groups = new ArrayList<>();
        String sql = "SELECT id, name, headman_id FROM groups ORDER BY name";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Integer hid = rs.getObject("headman_id", Integer.class);
                groups.add(new Group(rs.getInt("id"), rs.getString("name"), hid));
            }
        }
        return groups;
    }

    public void update(Group group) throws SQLException {
        String sql = "UPDATE groups SET name = ?, headman_id = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, group.getName());
            if (group.getHeadmanId() != null) {
                pstmt.setInt(2, group.getHeadmanId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            pstmt.setInt(3, group.getId());
            pstmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM groups WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    public List<Student> getStudentsOfGroup(int groupId) throws SQLException {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT s.id, s.first_name, s.last_name, s.email " +
                     "FROM students s JOIN student_groups sg ON s.id = sg.student_id " +
                     "WHERE sg.group_id = ? ORDER BY s.last_name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, groupId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    students.add(new Student(
                            rs.getInt("id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("email")
                    ));
                }
            }
        }
        return students;
    }

    public Group findByName(String name) throws SQLException {
        String sql = "SELECT id, name, headman_id FROM groups WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Integer hid = rs.getObject("headman_id", Integer.class);
                    return new Group(rs.getInt("id"), rs.getString("name"), hid);
                }
            }
        }
        return null;
    }
}