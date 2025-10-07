package com.example.bendaku.model;

public class Claim {
    private String id;
    private String itemId;
    private String claimerId;
    private String claimerName;
    private String claimerPhone;
    private String description;
    private String proofImageUrl;
    private String status; // "pending_verification", "approved", "rejected"
    private String createdAt;
    private String adminNotes;

    public Claim() {}

    public Claim(String itemId, String claimerId, String claimerName, String claimerPhone,
                 String description, String proofImageUrl) {
        this.itemId = itemId;
        this.claimerId = claimerId;
        this.claimerName = claimerName;
        this.claimerPhone = claimerPhone;
        this.description = description;
        this.proofImageUrl = proofImageUrl;
        this.status = "pending_verification";
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    public String getClaimerId() { return claimerId; }
    public void setClaimerId(String claimerId) { this.claimerId = claimerId; }

    public String getClaimerName() { return claimerName; }
    public void setClaimerName(String claimerName) { this.claimerName = claimerName; }

    public String getClaimerPhone() { return claimerPhone; }
    public void setClaimerPhone(String claimerPhone) { this.claimerPhone = claimerPhone; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getProofImageUrl() { return proofImageUrl; }
    public void setProofImageUrl(String proofImageUrl) { this.proofImageUrl = proofImageUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getAdminNotes() { return adminNotes; }
    public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }
}
