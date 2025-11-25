package com.example.bendaku.model;

public class User {
    private int id;
    private String email;
    private String username;
    private String fullName;
    private String phone;
    private String studentId;
    private boolean isAdmin;
    
    public User() {}
    
    public User(int id, String email, String username, String fullName, 
                String phone, String studentId, boolean isAdmin) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.fullName = fullName;
        this.phone = phone;
        this.studentId = studentId;
        this.isAdmin = isAdmin;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    
    public boolean isAdmin() { return isAdmin; }
    public void setAdmin(boolean admin) { this.isAdmin = admin; }
}
