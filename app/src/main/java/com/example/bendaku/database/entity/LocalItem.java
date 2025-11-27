package com.example.bendaku.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Index;

@Entity(tableName = "items", indices = {@Index(value = {"itemId"}, unique = true)})
public class LocalItem {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public String itemId;
    public String documentId;
    public String name;
    public String description;
    public String location;
    public String dateTime;
    public String type;
    public String statusItem;
    public String reporterName;
    public String reporterPhone;
    public String imageUrl;
    public Integer imageId;
    public String createdAt;
    public String updatedAt;
    public long lastSyncTime;
}

