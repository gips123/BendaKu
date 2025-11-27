package com.example.bendaku.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class StrapiItem implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("id")
    private Integer id;

    @SerializedName("documentId")
    private String documentId;

    @SerializedName("attributes")
    private Attributes attributes;

    @SerializedName("name")
    private String flatName;

    @SerializedName("description")
    private String flatDescription;

    @SerializedName("location")
    private String flatLocation;

    @SerializedName("dateTime")
    private String flatDateTime;

    @SerializedName("type")
    private String flatType;

    @SerializedName("statusItem")
    private String flatStatusItem;

    @SerializedName("reporterName")
    private String flatReporterName;

    @SerializedName("reporterPhone")
    private String flatReporterPhone;

    @SerializedName("imageUrl")
    private FlatImage flatImage;

    @SerializedName("claims")
    private List<StrapiClaim> flatClaims;

    @SerializedName("createdAt")
    private String flatCreatedAt;

    @SerializedName("updatedAt")
    private String flatUpdatedAt;

    public StrapiItem() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    public String getName() {
        if (attributes != null && attributes.name != null) {
            return attributes.name;
        }
        return flatName;
    }

    public String getDescription() {
        if (attributes != null && attributes.description != null) {
            return attributes.description;
        }
        return flatDescription;
    }

    public String getLocation() {
        if (attributes != null && attributes.location != null) {
            return attributes.location;
        }
        return flatLocation;
    }

    public String getDateTime() {
        if (attributes != null && attributes.dateTime != null) {
            return attributes.dateTime;
        }
        return flatDateTime;
    }

    public String getType() {
        if (attributes != null && attributes.type != null) {
            return attributes.type;
        }
        return flatType;
    }

    public String getStatusItem() {
        if (attributes != null && attributes.statusItem != null) {
            return attributes.statusItem;
        }
        return flatStatusItem;
    }

    public String getReporterName() {
        if (attributes != null && attributes.reporterName != null) {
            return attributes.reporterName;
        }
        return flatReporterName;
    }

    public String getReporterPhone() {
        if (attributes != null && attributes.reporterPhone != null) {
            return attributes.reporterPhone;
        }
        return flatReporterPhone;
    }

    public String getImageUrl() {
        if (attributes != null && attributes.imageUrl != null) {
            String url = attributes.imageUrl.getUrl();
            if (url != null) {
                return url;
            }
        }
        if (flatImage != null) {
            return flatImage.getUrl();
        }
        return null;
    }

    public Integer getImageId() {
        if (attributes != null && attributes.imageUrl != null && attributes.imageUrl.data != null) {
            return attributes.imageUrl.data.id;
        }
        if (flatImage != null && flatImage.id != null) {
            return flatImage.id;
        }
        return null;
    }

    public String getCreatedAt() {
        if (attributes != null && attributes.createdAt != null) {
            return attributes.createdAt;
        }
        return flatCreatedAt;
    }

    public String getUpdatedAt() {
        if (attributes != null && attributes.updatedAt != null) {
            return attributes.updatedAt;
        }
        return flatUpdatedAt;
    }

    public List<StrapiClaim> getClaims() {
        if (attributes != null && attributes.claims != null && attributes.claims.data != null) {
            return attributes.claims.data;
        }
        return flatClaims;
    }

    public static class Attributes {
        @SerializedName("name")
        private String name;

        @SerializedName("description")
        private String description;

        @SerializedName("location")
        private String location;

        @SerializedName("dateTime")
        private String dateTime;

        @SerializedName("type")
        private String type; // "lost" or "found"

        @SerializedName("statusItem")
        private String statusItem; // "open", "claimed", "resolved"

        @SerializedName("reporterName")
        private String reporterName;

        @SerializedName("reporterPhone")
        private String reporterPhone;

        @SerializedName("imageUrl")
        private MediaRelation imageUrl;

        @SerializedName("reporter")
        private UserRelation reporter;

        @SerializedName("claims")
        private ClaimsRelation claims;

        @SerializedName("createdAt")
        private String createdAt;

        @SerializedName("updatedAt")
        private String updatedAt;

        @SerializedName("publishedAt")
        private String publishedAt;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }

        public String getDateTime() { return dateTime; }
        public void setDateTime(String dateTime) { this.dateTime = dateTime; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getStatusItem() { return statusItem; }
        public void setStatusItem(String statusItem) { this.statusItem = statusItem; }

        public String getReporterName() { return reporterName; }
        public void setReporterName(String reporterName) { this.reporterName = reporterName; }

        public String getReporterPhone() { return reporterPhone; }
        public void setReporterPhone(String reporterPhone) { this.reporterPhone = reporterPhone; }

        public MediaRelation getImageUrl() { return imageUrl; }
        public void setImageUrl(MediaRelation imageUrl) { this.imageUrl = imageUrl; }

        public UserRelation getReporter() { return reporter; }
        public void setReporter(UserRelation reporter) { this.reporter = reporter; }

        public ClaimsRelation getClaims() { return claims; }
        public void setClaims(ClaimsRelation claims) { this.claims = claims; }

        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

        public String getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

        public String getPublishedAt() { return publishedAt; }
        public void setPublishedAt(String publishedAt) { this.publishedAt = publishedAt; }
    }

    public static class MediaRelation {
        @SerializedName("data")
        private MediaData data;

        @SerializedName("id")
        private Integer directId;

        @SerializedName("url")
        private String directUrl;

        @SerializedName("formats")
        private MediaFormats directFormats;

        public MediaData getData() { return data; }
        public void setData(MediaData data) { this.data = data; }

        public Integer getId() {
            if (data != null) {
                return data.id;
            }
            return directId;
        }

        public String getUrl() {
            if (data != null && data.attributes != null) {
                return data.attributes.url;
            }
            return directUrl;
        }
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

    public static class FlatImage {
        @SerializedName("id")
        private Integer id;

        @SerializedName("url")
        private String url;

        @SerializedName("formats")
        private MediaFormats formats;

        public Integer getId() { return id; }
        public String getUrl() { return url; }
        public MediaFormats getFormats() { return formats; }
    }

    public static class MediaFormats {
        @SerializedName("thumbnail")
        private MediaAttributes thumbnail;
        @SerializedName("small")
        private MediaAttributes small;
        @SerializedName("medium")
        private MediaAttributes medium;
        @SerializedName("large")
        private MediaAttributes large;

        public MediaAttributes getThumbnail() { return thumbnail; }
        public MediaAttributes getSmall() { return small; }
        public MediaAttributes getMedium() { return medium; }
        public MediaAttributes getLarge() { return large; }
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

    public static class ClaimsRelation {
        @SerializedName("data")
        private List<StrapiClaim> data;

        public List<StrapiClaim> getData() { return data; }
        public void setData(List<StrapiClaim> data) { this.data = data; }
    }
}

