package com.example.bendaku.model;

import java.io.Serializable;

public class Item implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String description;
    private String location;
    private String dateTime; // Format: "03 Okt 2024, 10:30"
    private String imageUrl;
    private String type; // "lost" or "found"
    private String status; // "open", "claimed", "resolved"
    private Integer reporterId;
    private String reporterName;
    private String reporterPhone;
    private User reporter;
    private String createdAt;
    private String updatedAt;
    
    public Item() {}
    
    public Item(int id, String name, String description, String location, String dateTime,
                String imageUrl, String type, String status, Integer reporterId,
                String reporterName, String reporterPhone) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.dateTime = dateTime;
        this.imageUrl = imageUrl;
        this.type = type;
        this.status = status;
        this.reporterId = reporterId;
        this.reporterName = reporterName;
        this.reporterPhone = reporterPhone;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

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

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getReporterId() { return reporterId; }
    public void setReporterId(Integer reporterId) { this.reporterId = reporterId; }

    public String getReporterName() { return reporterName; }
    public void setReporterName(String reporterName) { this.reporterName = reporterName; }

    public String getReporterPhone() { return reporterPhone; }
    public void setReporterPhone(String reporterPhone) { this.reporterPhone = reporterPhone; }

    public User getReporter() { return reporter; }
    public void setReporter(User reporter) { this.reporter = reporter; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
