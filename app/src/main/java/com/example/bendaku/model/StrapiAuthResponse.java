package com.example.bendaku.model;

import com.google.gson.annotations.SerializedName;

public class StrapiAuthResponse {
    @SerializedName("jwt")
    private String jwt;

    @SerializedName("user")
    private StrapiUser user;

    @SerializedName("error")
    private StrapiError error;

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public StrapiUser getUser() {
        return user;
    }

    public void setUser(StrapiUser user) {
        this.user = user;
    }

    public StrapiError getError() {
        return error;
    }

    public void setError(StrapiError error) {
        this.error = error;
    }

    public boolean isSuccess() {
        return error == null && jwt != null;
    }

    public static class StrapiUser {
        @SerializedName("id")
        private Integer id;

        @SerializedName("username")
        private String username;

        @SerializedName("email")
        private String email;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    public static class StrapiError {
        @SerializedName("status")
        private int status;

        @SerializedName("message")
        private String message;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}

