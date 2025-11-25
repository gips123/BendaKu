package com.example.bendaku.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class StrapiClaim implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("id")
    private Integer id;

    @SerializedName("attributes")
    private Attributes attributes;

    public StrapiClaim() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    // Helper methods
    public String getClaimerName() {
        return attributes != null ? attributes.claimerName : null;
    }

    public String getClaimerPhone() {
        return attributes != null ? attributes.claimerPhone : null;
    }

    public String getClaimerUsername() {
        return attributes != null ? attributes.claimerUsername : null;
    }

    public String getDescription() {
        return attributes != null ? attributes.description : null;
    }

    public String getStatusClaim() {
        return attributes != null ? attributes.statusClaim : null;
    }

    public String getAdminNotes() {
        return attributes != null ? attributes.adminNotes : null;
    }

    public String getImageUrl() {
        if (attributes != null && attributes.imageUrl != null && attributes.imageUrl.data != null) {
            return attributes.imageUrl.data.attributes != null ? attributes.imageUrl.data.attributes.url : null;
        }
        return null;
    }

    public String getClaimerKtmUrl() {
        if (attributes != null && attributes.claimerKtm != null) {
            if (attributes.claimerKtm.data != null && attributes.claimerKtm.data.attributes != null) {
                return attributes.claimerKtm.data.attributes.url;
            }
            if (attributes.claimerKtm.directUrl != null) {
                return attributes.claimerKtm.directUrl;
            }
        }
        return null;
    }

    public Integer getClaimerKtmId() {
        if (attributes != null && attributes.claimerKtm != null) {
            if (attributes.claimerKtm.data != null) {
                return attributes.claimerKtm.data.id;
            }
            if (attributes.claimerKtm.directId != null) {
                return attributes.claimerKtm.directId;
            }
        }
        return null;
    }

    public Integer getImageId() {
        if (attributes != null && attributes.imageUrl != null && attributes.imageUrl.data != null) {
            return attributes.imageUrl.data.id;
        }
        return null;
    }

    public String getCreatedAt() {
        return attributes != null ? attributes.createdAt : null;
    }

    public static class Attributes {
        @SerializedName("claimerName")
        private String claimerName;

        @SerializedName("claimerPhone")
        private String claimerPhone;

        @SerializedName("claimerUsername")
        private String claimerUsername;

        @SerializedName("description")
        private String description;

        @SerializedName("statusClaim")
        private String statusClaim; // "pending", "approved", "rejected"

        @SerializedName("adminNotes")
        private String adminNotes;

        @SerializedName("imageUrl")
        private MediaRelation imageUrl;

        @SerializedName("claimerktm")
        private MediaRelation claimerKtm;

        @SerializedName("claimer")
        private UserRelation claimer;

        @SerializedName("createdAt")
        private String createdAt;

        @SerializedName("updatedAt")
        private String updatedAt;

        // Getters and setters
        public String getClaimerName() { return claimerName; }
        public void setClaimerName(String claimerName) { this.claimerName = claimerName; }

        public String getClaimerPhone() { return claimerPhone; }
        public void setClaimerPhone(String claimerPhone) { this.claimerPhone = claimerPhone; }

        public String getClaimerUsername() { return claimerUsername; }
        public void setClaimerUsername(String claimerUsername) { this.claimerUsername = claimerUsername; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getStatusClaim() { return statusClaim; }
        public void setStatusClaim(String statusClaim) { this.statusClaim = statusClaim; }

        public String getAdminNotes() { return adminNotes; }
        public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }

        public MediaRelation getImageUrl() { return imageUrl; }
        public void setImageUrl(MediaRelation imageUrl) { this.imageUrl = imageUrl; }

        public MediaRelation getClaimerKtm() { return claimerKtm; }
        public void setClaimerKtm(MediaRelation claimerKtm) { this.claimerKtm = claimerKtm; }

        public UserRelation getClaimer() { return claimer; }
        public void setClaimer(UserRelation claimer) { this.claimer = claimer; }

        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

        public String getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    }

    public static class MediaRelation {
        @SerializedName("data")
        private MediaData data;

        @SerializedName("id")
        private Integer directId;

        @SerializedName("url")
        private String directUrl;

        public MediaData getData() { return data; }
        public void setData(MediaData data) { this.data = data; }

        public Integer getDirectId() { return directId; }
        public String getDirectUrl() { return directUrl; }
    }

    public static class MediaData {
        @SerializedName("id")
        private Integer id;

        @SerializedName("attributes")
        private MediaAttributes attributes;

        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }

        public MediaAttributes getAttributes() { return attributes; }
        public void setAttributes(MediaAttributes attributes) { this.attributes = attributes; }
    }

    public static class MediaAttributes {
        @SerializedName("url")
        private String url;

        @SerializedName("name")
        private String name;

        @SerializedName("mime")
        private String mime;

        @SerializedName("size")
        private Double size;

        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getMime() { return mime; }
        public void setMime(String mime) { this.mime = mime; }

        public Double getSize() { return size; }
        public void setSize(Double size) { this.size = size; }
    }

    public static class UserRelation {
        @SerializedName("data")
        private UserData data;

        public UserData getData() { return data; }
        public void setData(UserData data) { this.data = data; }
    }

    public static class UserData {
        @SerializedName("id")
        private Integer id;

        @SerializedName("attributes")
        private UserAttributes attributes;

        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }

        public UserAttributes getAttributes() { return attributes; }
        public void setAttributes(UserAttributes attributes) { this.attributes = attributes; }
    }

    public static class UserAttributes {
        @SerializedName("username")
        private String username;

        @SerializedName("email")
        private String email;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
}

