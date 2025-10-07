package com.example.bendaku.model;

public class Item {
    private String id;
    private String name;
    private String description;
    private String location;
    private String dateTime;
    private String imageUrl;
    private String type; // "lost" or "found"
    private String reporterId;
    private String reporterName;
    private String reporterPhone;
    private String status; // "active", "claimed", "resolved"
    private String createdAt;

    public Item() {}

    public Item(String name, String description, String location, String dateTime,
                String imageUrl, String type, String reporterId, String reporterName, String reporterPhone) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.dateTime = dateTime;
        this.imageUrl = imageUrl;
        this.type = type;
        this.reporterId = reporterId;
        this.reporterName = reporterName;
        this.reporterPhone = reporterPhone;
        this.status = "active";
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDateTime() { return dateTime; }
    public void setDateTime(String dateTime) { this.dateTime = dateTime; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getReporterId() { return reporterId; }
    public void setReporterId(String reporterId) { this.reporterId = reporterId; }

    public String getReporterName() { return reporterName; }
    public void setReporterName(String reporterName) { this.reporterName = reporterName; }

    public String getReporterPhone() { return reporterPhone; }
    public void setReporterPhone(String reporterPhone) { this.reporterPhone = reporterPhone; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
