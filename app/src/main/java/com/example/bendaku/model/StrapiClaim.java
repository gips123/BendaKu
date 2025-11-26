package com.example.bendaku.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class StrapiClaim implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("id")
    private Integer id;

    @SerializedName("attributes")
    private Attributes attributes;

    // Flat structure support (when Strapi response is sanitized without attributes wrapper)
    @SerializedName("documentId")
    private String documentId;

    @SerializedName("claimerName")
    private String flatClaimerName;

    @SerializedName("claimerPhone")
    private String flatClaimerPhone;

    @SerializedName("claimerUsername")
    private String flatClaimerUsername;

    @SerializedName("description")
    private String flatDescription;

    @SerializedName("statusClaim")
    private String flatStatusClaim;

    @SerializedName("adminNotes")
    private String flatAdminNotes;

    @SerializedName("imageUrl")
    private FlatImage flatImageUrl;

    @SerializedName("claimerktm")
    private FlatImage flatClaimerKtm;

    @SerializedName("item")
    private FlatItem flatItem;

    @SerializedName("createdAt")
    private String flatCreatedAt;

    @SerializedName("updatedAt")
    private String flatUpdatedAt;

    @SerializedName("publishedAt")
    private String flatPublishedAt;

    public StrapiClaim() {}

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

    // Helper methods
    public String getClaimerName() {
        if (attributes != null && attributes.claimerName != null) {
            return attributes.claimerName;
        }
        return flatClaimerName;
    }

    public String getClaimerPhone() {
        if (attributes != null && attributes.claimerPhone != null) {
            return attributes.claimerPhone;
        }
        return flatClaimerPhone;
    }

    public String getClaimerUsername() {
        if (attributes != null && attributes.claimerUsername != null) {
            return attributes.claimerUsername;
        }
        return flatClaimerUsername;
    }

    public String getDescription() {
        if (attributes != null && attributes.description != null) {
            return attributes.description;
        }
        return flatDescription;
    }

    public String getStatusClaim() {
        if (attributes != null && attributes.statusClaim != null) {
            return attributes.statusClaim;
        }
        return flatStatusClaim;
    }

    public String getAdminNotes() {
        if (attributes != null && attributes.adminNotes != null) {
            return attributes.adminNotes;
        }
        return flatAdminNotes;
    }

    public String getImageUrl() {
        // Check nested structure first
        if (attributes != null && attributes.imageUrl != null && attributes.imageUrl.data != null) {
            if (attributes.imageUrl.data.attributes != null) {
                return attributes.imageUrl.data.attributes.url;
            }
        }
        // Check flat structure
        if (flatImageUrl != null) {
            return flatImageUrl.getUrl();
        }
        return null;
    }

    public String getClaimerKtmUrl() {
        // Check nested structure first
        if (attributes != null && attributes.claimerKtm != null) {
            if (attributes.claimerKtm.data != null && attributes.claimerKtm.data.attributes != null) {
                return attributes.claimerKtm.data.attributes.url;
            }
            if (attributes.claimerKtm.directUrl != null) {
                return attributes.claimerKtm.directUrl;
            }
        }
        // Check flat structure
        if (flatClaimerKtm != null) {
            return flatClaimerKtm.getUrl();
        }
        return null;
    }

    public Integer getClaimerKtmId() {
        // Check nested structure first
        if (attributes != null && attributes.claimerKtm != null) {
            if (attributes.claimerKtm.data != null) {
                return attributes.claimerKtm.data.id;
            }
            if (attributes.claimerKtm.directId != null) {
                return attributes.claimerKtm.directId;
            }
        }
        // Check flat structure
        if (flatClaimerKtm != null) {
            return flatClaimerKtm.getId();
        }
        return null;
    }

    public Integer getImageId() {
        // Check nested structure first
        if (attributes != null && attributes.imageUrl != null && attributes.imageUrl.data != null) {
            return attributes.imageUrl.data.id;
        }
        // Check flat structure
        if (flatImageUrl != null) {
            return flatImageUrl.getId();
        }
        return null;
    }

    public String getCreatedAt() {
        if (attributes != null && attributes.createdAt != null) {
            return attributes.createdAt;
        }
        return flatCreatedAt;
    }

    public Integer getItemId() {
        // Check nested structure first
        if (attributes != null && attributes.item != null) {
            // Handle flat structure (item is direct ID)
            if (attributes.item.directId != null) {
                return attributes.item.directId;
            }
            // Handle nested structure (item.data.id)
            if (attributes.item.data != null && attributes.item.data.id != null) {
                return attributes.item.data.id;
            }
        }
        // Check flat structure
        if (flatItem != null) {
            return flatItem.getId();
        }
        return null;
    }

    public StrapiItem getItem() {
        // Check nested structure first
        if (attributes != null && attributes.item != null) {
            // Handle nested structure (item.data.attributes)
            if (attributes.item.data != null && attributes.item.data.item != null) {
                return attributes.item.data.item;
            }
        }
        // Flat structure returns null for full item object (only ID available)
        return null;
    }

    public String getItemName() {
        // Check nested structure first
        if (attributes != null && attributes.item != null) {
            // Handle nested structure
            if (attributes.item.data != null) {
                if (attributes.item.data.item != null) {
                    return attributes.item.data.item.getName();
                }
                if (attributes.item.data.flatName != null) {
                    return attributes.item.data.flatName;
                }
            }
            // Handle flat structure
            if (attributes.item.flatName != null) {
                return attributes.item.flatName;
            }
        }
        // Check flat structure
        if (flatItem != null) {
            return flatItem.getName();
        }
        return null;
    }

    public FlatItem getFlatItem() {
        return flatItem;
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

        @SerializedName("item")
        private ItemRelation item;

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

        public ItemRelation getItem() { return item; }
        public void setItem(ItemRelation item) { this.item = item; }

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

    public static class ItemRelation {
        @SerializedName("data")
        private ItemData data;

        @SerializedName("id")
        private Integer directId;

        // Flat structure support (when item is directly an object, not nested in data)
        @SerializedName("name")
        private String flatName;

        @SerializedName("documentId")
        private String flatDocumentId;

        public ItemData getData() { return data; }
        public void setData(ItemData data) { this.data = data; }

        public Integer getDirectId() { return directId; }

        public String getFlatName() { return flatName; }
        public String getFlatDocumentId() { return flatDocumentId; }
    }

    public static class ItemData {
        @SerializedName("id")
        private Integer id;

        @SerializedName("attributes")
        private StrapiItem item;

        // Flat structure support (when item is directly an object)
        @SerializedName("name")
        private String flatName;

        @SerializedName("documentId")
        private String flatDocumentId;

        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }

        public StrapiItem getItem() { return item; }
        public void setItem(StrapiItem item) { this.item = item; }

        public String getFlatName() { return flatName; }
        public String getFlatDocumentId() { return flatDocumentId; }
    }

    // Flat structure classes
    public static class FlatImage {
        @SerializedName("id")
        private Integer id;

        @SerializedName("url")
        private String url;

        @SerializedName("name")
        private String name;

        @SerializedName("documentId")
        private String documentId;

        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }

        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDocumentId() { return documentId; }
        public void setDocumentId(String documentId) { this.documentId = documentId; }
    }

    public static class FlatItem {
        @SerializedName("id")
        private Integer id;

        @SerializedName("documentId")
        private String documentId;

        @SerializedName("name")
        private String name;

        @SerializedName("location")
        private String location;

        @SerializedName("description")
        private String description;

        @SerializedName("dateTime")
        private String dateTime;

        @SerializedName("type")
        private String type;

        @SerializedName("statusItem")
        private String statusItem;

        @SerializedName("reporterName")
        private String reporterName;

        @SerializedName("reporterPhone")
        private String reporterPhone;

        @SerializedName("imageUrl")
        private FlatImage imageUrl;

        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }

        public String getDocumentId() { return documentId; }
        public void setDocumentId(String documentId) { this.documentId = documentId; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

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

        public FlatImage getImageUrl() { return imageUrl; }
        public void setImageUrl(FlatImage imageUrl) { this.imageUrl = imageUrl; }
    }
}

