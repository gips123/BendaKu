package com.example.bendaku.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Index;

@Entity(tableName = "claims", indices = {@Index(value = {"claimId"}, unique = true)})
public class LocalClaim {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public String claimId; // Strapi ID
    public String documentId; // Strapi documentId
    public String itemId; // Related item ID
    public String itemName;
    public String itemStatusItem; // Status from related item
    public String claimerName;
    public String claimerPhone;
    public String claimerUsername;
    public String description;
    public String statusClaim; // "pending", "approved", "rejected"
    public String adminNotes;
    public String proofImageUrl;
    public Integer imageId;
    public String claimerKtmUrl;
    public Integer claimerKtmId;
    public String createdAt;
    public String updatedAt;
    public long lastSyncTime; // Timestamp when data was last synced from API
}

