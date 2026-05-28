package com.example.socialnetwork.controller;

import com.example.socialnetwork.db.DatabaseConnection;
import com.example.socialnetwork.model.*;
import com.example.socialnetwork.service.SocialNetworkService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.util.List;

public class MainController {

    @FXML private TableView<Student> studentsTable;
    @FXML private TableColumn<Student, Integer> colId;
    @FXML private TableColumn<Student, String> colFirstName;
    @FXML private TableColumn<Student, String> colLastName;
    @FXML private TableColumn<Student, String> colEmail;

    @FXML private TextField searchLastNameField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;

    @FXML private TableView<Group> groupsTable;
    @FXML private TableColumn<Group, Integer> colGroupId;
    @FXML private TableColumn<Group, String> colGroupName;
    @FXML private TableColumn<Group, Integer> colHeadmanId;

    @FXML private TextField groupNameField;
    @FXML private TextField headmanIdField;

    @FXML private ComboBox<Student> student1Combo;
    @FXML private ComboBox<Student> student2Combo;
    @FXML private ComboBox<Student> selectedStudentCombo;
    @FXML private ListView<Student> friendsListView;

    @FXML private TextArea queryResultArea;

    private SocialNetworkService service;
    private ObservableList<Student> studentList;
    private ObservableList<Group> groupList;

    @FXML
    public void initialize() {
        // Автоматичне створення таблиць
        DatabaseConnection.createTablesIfNotExist();

        service = new SocialNetworkService();
        studentList = FXCollections.observableArrayList();
        groupList = FXCollections.observableArrayList();

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        colGroupId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colGroupName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colHeadmanId.setCellValueFactory(new PropertyValueFactory<>("headmanId"));

        studentsTable.setItems(studentList);
        groupsTable.setItems(groupList);

        loadStudents();
        loadGroups();
        loadStudentCombos();
    }

    private void loadStudents() {
        try {
            studentList.setAll(service.getAllStudents());
        } catch (SQLException e) {
            showError("Помилка завантаження студентів", e);
        }
    }

    private void loadGroups() {
        try {
            groupList.setAll(service.getAllGroups());
        } catch (SQLException e) {
            showError("Помилка завантаження груп", e);
        }
    }

    private void loadStudentCombos() {
        try {
            ObservableList<Student> students = FXCollections.observableArrayList(service.getAllStudents());
            student1Combo.setItems(students);
            student2Combo.setItems(students);
            selectedStudentCombo.setItems(students);
        } catch (SQLException e) {
            showError("Помилка завантаження списків", e);
        }
    }

    @FXML
    private void searchStudentsByLastName() {
        String lastName = searchLastNameField.getText().trim();
        if (lastName.isEmpty()) {
            loadStudents();
            return;
        }
        try {
            studentList.setAll(service.searchStudentsByLastName(lastName));
        } catch (SQLException e) {
            showError("Помилка пошуку", e);
        }
    }

    @FXML
    private void clearStudentSearch() {
        searchLastNameField.clear();
        loadStudents();
    }

    @FXML
    private void addStudent() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
            showAlert("Помилка", "Заповніть всі поля");
            return;
        }
        try {
            Student s = new Student(firstName, lastName, email);
            service.addStudent(s);
            loadStudents();
            loadStudentCombos();
            firstNameField.clear();
            lastNameField.clear();
            emailField.clear();
        } catch (SQLException e) {
            showError("Помилка додавання", e);
        }
    }

    @FXML
    private void updateStudent() {
        Student selected = studentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Помилка", "Виберіть студента в таблиці");
            return;
        }
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
            showAlert("Помилка", "Заповніть всі поля");
            return;
        }
        selected.setFirstName(firstName);
        selected.setLastName(lastName);
        selected.setEmail(email);
        try {
            service.updateStudent(selected);
            loadStudents();
            loadStudentCombos();
        } catch (SQLException e) {
            showError("Помилка оновлення", e);
        }
    }

    @FXML
    private void deleteStudent() {
        Student selected = studentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Помилка", "Виберіть студента");
            return;
        }
        try {
            service.deleteStudent(selected.getId());
            loadStudents();
            loadStudentCombos();
        } catch (SQLException e) {
            showError("Помилка видалення", e);
        }
    }

    @FXML
    private void addGroup() {
        String name = groupNameField.getText().trim();
        if (name.isEmpty()) {
            showAlert("Помилка", "Введіть назву групи");
            return;
        }
        Integer headmanId = null;
        if (!headmanIdField.getText().trim().isEmpty()) {
            try {
                headmanId = Integer.parseInt(headmanIdField.getText().trim());
            } catch (NumberFormatException e) {
                showAlert("Помилка", "ID старости має бути числом");
                return;
            }
        }
        Group group = new Group(name, headmanId);
        try {
            service.addGroup(group);
            loadGroups();
            groupNameField.clear();
            headmanIdField.clear();
        } catch (SQLException e) {
            showError("Помилка додавання групи", e);
        }
    }

    @FXML
    private void updateGroup() {
        Group selected = groupsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Помилка", "Виберіть групу");
            return;
        }
        String name = groupNameField.getText().trim();
        if (name.isEmpty()) name = selected.getName();
        Integer headmanId = selected.getHeadmanId();
        if (!headmanIdField.getText().trim().isEmpty()) {
            try {
                headmanId = Integer.parseInt(headmanIdField.getText().trim());
            } catch (NumberFormatException e) { }
        }
        selected.setName(name);
        selected.setHeadmanId(headmanId);
        try {
            service.updateGroup(selected);
            loadGroups();
        } catch (SQLException e) {
            showError("Помилка оновлення групи", e);
        }
    }

    @FXML
    private void deleteGroup() {
        Group selected = groupsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Помилка", "Виберіть групу");
            return;
        }
        try {
            service.deleteGroup(selected.getId());
            loadGroups();
        } catch (SQLException e) {
            showError("Помилка видалення групи", e);
        }
    }

    @FXML
    private void addFriendship() {
        Student s1 = student1Combo.getValue();
        Student s2 = student2Combo.getValue();
        if (s1 == null || s2 == null || s1.getId() == s2.getId()) {
            showAlert("Помилка", "Оберіть двох різних студентів");
            return;
        }
        try {
            service.addFriendship(s1.getId(), s2.getId());
            showAlert("Успіх", "Дружбу додано");
        } catch (SQLException e) {
            showError("Помилка додавання дружби", e);
        }
    }

    @FXML
    private void removeFriendship() {
        Student s1 = student1Combo.getValue();
        Student s2 = student2Combo.getValue();
        if (s1 == null || s2 == null) {
            showAlert("Помилка", "Оберіть студентів");
            return;
        }
        try {
            service.removeFriendship(s1.getId(), s2.getId());
            showAlert("Успіх", "Дружбу видалено");
        } catch (SQLException e) {
            showError("Помилка видалення дружби", e);
        }
    }

    @FXML
    private void showFriends() {
        Student student = selectedStudentCombo.getValue();
        if (student == null) return;
        try {
            List<Student> friends = service.getFriendsOfStudent(student.getId());
            friendsListView.getItems().setAll(friends);
        } catch (SQLException e) {
            showError("Помилка завантаження друзів", e);
        }
    }

    @FXML
    private void showTop5Friends() {
        try {
            List<Student> top = service.getTop5StudentsByFriendsCount();
            StringBuilder sb = new StringBuilder("Топ-5 студентів за кількістю друзів:\n");
            for (Student s : top) {
                int cnt = service.countFriends(s.getId());
                sb.append(s.getFirstName()).append(" ").append(s.getLastName())
                  .append(" (").append(cnt).append(" друзів)\n");
            }
            queryResultArea.setText(sb.toString());
        } catch (SQLException e) {
            showError("Помилка запиту", e);
        }
    }

    @FXML
    private void showMostFriendlyGroups() {
        try {
            List<String> pairs = service.getMostFriendlyGroupPairs();
            StringBuilder sb = new StringBuilder("Найбільше дружніх зв'язків між групами:\n");
            for (String line : pairs) sb.append(line).append("\n");
            queryResultArea.setText(sb.toString());
        } catch (SQLException e) {
            showError("Помилка запиту", e);
        }
    }

    @FXML
    private void showNotFriendsWithHeadman() {
        try {
            List<Student> list = service.getStudentsNotFriendsWithOwnHeadman();
            StringBuilder sb = new StringBuilder("Студенти, які не дружать зі старостою своєї групи:\n");
            for (Student s : list) sb.append(s.getFirstName()).append(" ").append(s.getLastName()).append("\n");
            queryResultArea.setText(sb.toString());
        } catch (SQLException e) {
            showError("Помилка запиту", e);
        }
    }

    private void showError(String title, Exception e) {
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}