package com.example.socialnetwork.dao;

import com.example.socialnetwork.db.DatabaseConnection;
import com.example.socialnetwork.model.Student;
import com.example.socialnetwork.model.Group;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public void add(Student student) throws SQLException {
        String sql = "INSERT INTO students (first_name, last_name, email) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, student.getFirstName());
            pstmt.setString(2, student.getLastName());
            pstmt.setString(3, student.getEmail());
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    student.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new SQLException("Помилка підключення до БД: " + e.getMessage(), e);
        }
    }

    public Student getById(int id) throws SQLException {
        String sql = "SELECT id, first_name, last_name, email FROM students WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Student(
                            rs.getInt("id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("email")
                    );
                }
            }
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new SQLException("Помилка підключення до БД: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Student> getAll() throws SQLException {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT id, first_name, last_name, email FROM students ORDER BY id";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                students.add(new Student(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email")
                ));
            }
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new SQLException("Помилка підключення до БД: " + e.getMessage(), e);
        }
        return students;
    }

    public void update(Student student) throws SQLException {
        String sql = "UPDATE students SET first_name = ?, last_name = ?, email = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, student.getFirstName());
            pstmt.setString(2, student.getLastName());
            pstmt.setString(3, student.getEmail());
            pstmt.setInt(4, student.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new SQLException("Помилка підключення до БД: " + e.getMessage(), e);
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM students WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new SQLException("Помилка підключення до БД: " + e.getMessage(), e);
        }
    }

    public List<Student> searchByLastName(String lastName) throws SQLException {
        List<Student> result = new ArrayList<>();
        String sql = "SELECT id, first_name, last_name, email FROM students WHERE last_name ILIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + lastName + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    result.add(new Student(
                            rs.getInt("id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("email")
                    ));
                }
            }
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new SQLException("Помилка підключення до БД: " + e.getMessage(), e);
        }
        return result;
    }

    public List<Student> searchByGroupId(int groupId) throws SQLException {
        List<Student> result = new ArrayList<>();
        String sql = "SELECT s.id, s.first_name, s.last_name, s.email " +
                     "FROM students s " +
                     "JOIN student_groups sg ON s.id = sg.student_id " +
                     "WHERE sg.group_id = ? " +
                     "ORDER BY s.last_name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, groupId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    result.add(new Student(
                            rs.getInt("id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("email")
                    ));
                }
            }
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new SQLException("Помилка підключення до БД: " + e.getMessage(), e);
        }
        return result;
    }

    public List<Group> getGroupsOfStudent(int studentId) throws SQLException {
        List<Group> groups = new ArrayList<>();
        String sql = "SELECT g.id, g.name, g.headman_id FROM groups g " +
                     "JOIN student_groups sg ON g.id = sg.group_id " +
                     "WHERE sg.student_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Integer headmanId = rs.getObject("headman_id", Integer.class);
                    groups.add(new Group(rs.getInt("id"), rs.getString("name"), headmanId));
                }
            }
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new SQLException("Помилка підключення до БД: " + e.getMessage(), e);
        }
        return groups;
    }
}