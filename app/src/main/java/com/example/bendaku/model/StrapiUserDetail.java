package com.example.bendaku.model;

import com.google.gson.annotations.SerializedName;

public class StrapiUserDetail {
    private Integer id;
    private String username;
    private String email;

    @SerializedName("fullname")
    private String fullName;

    private String phone;

    @SerializedName("studentID")
    private String studentId;
    @SerializedName("isAdmin")
    private Boolean isAdmin;

    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public String getStudentId() {
        return studentId;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }
}

