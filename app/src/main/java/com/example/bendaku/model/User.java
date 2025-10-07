package com.example.bendaku.model;

public class User {
    private String id;
    private String fullName;
    private String email;
    private String phone;
    private String studentId;
    private boolean isAdmin;

    public User() {}

    public User(String fullName, String email, String phone, String studentId) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.studentId = studentId;
        this.isAdmin = false;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public boolean isAdmin() { return isAdmin; }
    public void setAdmin(boolean admin) { isAdmin = admin; }
}
