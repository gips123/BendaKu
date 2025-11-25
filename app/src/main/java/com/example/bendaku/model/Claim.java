package com.example.bendaku.model;

public class Claim {
    private int id;
    private Integer itemId;
    private Item item;
    private Integer claimerId;
    private String claimerName;
    private String claimerPhone;
    private User claimer;
    private String description;
    private String proofImageUrl;
    private String status; // "pending", "approved", "rejected"
    private String adminNotes;
    private String createdAt;
    private String updatedAt;
    
    public Claim() {}
    
    public Claim(int id, Integer itemId, Integer claimerId, String claimerName,
                 String claimerPhone, String description, String proofImageUrl, String status) {
        this.id = id;
        this.itemId = itemId;
        this.claimerId = claimerId;
        this.claimerName = claimerName;
        this.claimerPhone = claimerPhone;
        this.description = description;
        this.proofImageUrl = proofImageUrl;
        this.status = status;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Integer getItemId() { return itemId; }
    public void setItemId(Integer itemId) { this.itemId = itemId; }

    public Item getItem() { return item; }
    public void setItem(Item item) { this.item = item; }

    public Integer getClaimerId() { return claimerId; }
    public void setClaimerId(Integer claimerId) { this.claimerId = claimerId; }

    public String getClaimerName() { return claimerName; }
    public void setClaimerName(String claimerName) { this.claimerName = claimerName; }

    public String getClaimerPhone() { return claimerPhone; }
    public void setClaimerPhone(String claimerPhone) { this.claimerPhone = claimerPhone; }

    public User getClaimer() { return claimer; }
    public void setClaimer(User claimer) { this.claimer = claimer; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getProofImageUrl() { return proofImageUrl; }
    public void setProofImageUrl(String proofImageUrl) { this.proofImageUrl = proofImageUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAdminNotes() { return adminNotes; }
    public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
