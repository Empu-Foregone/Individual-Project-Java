package com.example.socialnetwork.service;

import com.example.socialnetwork.dao.*;
import com.example.socialnetwork.model.*;

import java.sql.SQLException;
import java.util.List;

public class SocialNetworkService {
    private final StudentDAO studentDAO;
    private final GroupDAO groupDAO;
    private final FriendshipDAO friendshipDAO;
    private final StudentGroupDAO studentGroupDAO;

    public SocialNetworkService() {
        this.studentDAO = new StudentDAO();
        this.groupDAO = new GroupDAO();
        this.friendshipDAO = new FriendshipDAO();
        this.studentGroupDAO = new StudentGroupDAO();
    }

    public void addStudent(Student student) throws SQLException {
        studentDAO.add(student);
    }
    public void updateStudent(Student student) throws SQLException {
        studentDAO.update(student);
    }
    public void deleteStudent(int id) throws SQLException {
        studentGroupDAO.deleteAllGroupsForStudent(id);
        studentDAO.delete(id);
    }
    public List<Student> getAllStudents() throws SQLException {
        return studentDAO.getAll();
    }
    public List<Student> searchStudentsByLastName(String lastName) throws SQLException {
        return studentDAO.searchByLastName(lastName);
    }
    public List<Student> getStudentsByGroupId(int groupId) throws SQLException {
        return studentDAO.searchByGroupId(groupId);
    }
    public List<Group> getGroupsOfStudent(int studentId) throws SQLException {
        return studentDAO.getGroupsOfStudent(studentId);
    }


    public void addGroup(Group group) throws SQLException {
        groupDAO.add(group);
    }
    public void updateGroup(Group group) throws SQLException {
        groupDAO.update(group);
    }
    public void deleteGroup(int id) throws SQLException {
        groupDAO.delete(id);
    }
    public List<Group> getAllGroups() throws SQLException {
        return groupDAO.getAll();
    }
    public Group findGroupByName(String name) throws SQLException {
        return groupDAO.findByName(name);
    }
    public List<Student> getStudentsInGroup(int groupId) throws SQLException {
        return groupDAO.getStudentsOfGroup(groupId);
    }


    public void addStudentToGroup(int studentId, int groupId) throws SQLException {
        studentGroupDAO.addStudentToGroup(studentId, groupId);
    }
    public void removeStudentFromGroup(int studentId, int groupId) throws SQLException {
        studentGroupDAO.removeStudentFromGroup(studentId, groupId);
    }


    public void addFriendship(int student1Id, int student2Id) throws SQLException {
        friendshipDAO.addFriendship(student1Id, student2Id);
    }
    public void removeFriendship(int student1Id, int student2Id) throws SQLException {
        friendshipDAO.removeFriendship(student1Id, student2Id);
    }
    public List<Student> getFriendsOfStudent(int studentId) throws SQLException {
        return friendshipDAO.getFriends(studentId);
    }
    public int countFriends(int studentId) throws SQLException {
        return friendshipDAO.countFriends(studentId);
    }


    public List<Student> getTop5StudentsByFriendsCount() throws SQLException {
        return friendshipDAO.getTopStudentsByFriendsCount(5);
    }
    public List<String> getMostFriendlyGroupPairs() throws SQLException {
        return friendshipDAO.getMostFriendlyGroupPairs();
    }
    public List<Student> getStudentsNotFriendsWithOwnHeadman() throws SQLException {
        return friendshipDAO.getStudentsNotFriendsWithOwnHeadman();
    }
}