package com.example.bendaku.model;

public class Claim {
    private String id;
    private String documentId; // Strapi v5 documentId
    private String itemId;
    private String itemName;
    private String claimerId;
    private String claimerName;
    private String claimerPhone;
    private String description;
    private String proofImageUrl;
    private Integer imageId; // ID foto bukti
    private Integer claimerKtmId; // ID foto KTM/identitas
    private String claimerKtmUrl;
    private String claimerUsername;
    private String status; // "pending_verification", "approved", "rejected"
    private String itemStatusItem; // "open", "claimed", "resolved" - status dari item yang di-claim
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

    public String getDocumentId() { return documentId; }
    public void setDocumentId(String documentId) { this.documentId = documentId; }

    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

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

    public Integer getImageId() { return imageId; }
    public void setImageId(Integer imageId) { this.imageId = imageId; }

    public Integer getClaimerKtmId() { return claimerKtmId; }
    public void setClaimerKtmId(Integer claimerKtmId) { this.claimerKtmId = claimerKtmId; }

    public String getClaimerKtmUrl() { return claimerKtmUrl; }
    public void setClaimerKtmUrl(String claimerKtmUrl) { this.claimerKtmUrl = claimerKtmUrl; }

    public String getClaimerUsername() { return claimerUsername; }
    public void setClaimerUsername(String claimerUsername) { this.claimerUsername = claimerUsername; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getItemStatusItem() { return itemStatusItem; }
    public void setItemStatusItem(String itemStatusItem) { this.itemStatusItem = itemStatusItem; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getAdminNotes() { return adminNotes; }
    public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }
}
