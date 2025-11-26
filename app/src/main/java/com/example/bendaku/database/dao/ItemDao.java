package com.example.bendaku.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.bendaku.database.entity.LocalItem;

import java.util.List;

@Dao
public interface ItemDao {
    @Query("SELECT * FROM items WHERE type = :type AND statusItem = :statusItem ORDER BY createdAt DESC")
    List<LocalItem> getItemsByTypeAndStatus(String type, String statusItem);

    @Query("SELECT * FROM items WHERE statusItem = :statusItem ORDER BY createdAt DESC")
    List<LocalItem> getItemsByStatus(String statusItem);

    @Query("SELECT * FROM items WHERE itemId = :itemId")
    LocalItem getItemById(String itemId);

    @Query("SELECT * FROM items WHERE documentId = :documentId")
    LocalItem getItemByDocumentId(String documentId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertItem(LocalItem item);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertItems(List<LocalItem> items);

    @Update
    void updateItem(LocalItem item);

    @Query("DELETE FROM items WHERE itemId = :itemId")
    void deleteItem(String itemId);

    @Query("DELETE FROM items")
    void deleteAllItems();

    @Query("UPDATE items SET statusItem = :statusItem, updatedAt = :updatedAt, lastSyncTime = :syncTime WHERE itemId = :itemId")
    void updateItemStatus(String itemId, String statusItem, String updatedAt, long syncTime);

    @Query("DELETE FROM items WHERE (:type IS NULL OR type = :type) AND statusItem = :statusItem AND documentId NOT IN (:documentIds)")
    void deleteItemsNotInDocumentIds(String type, String statusItem, List<String> documentIds);

    @Query("DELETE FROM items WHERE (:type IS NULL OR type = :type) AND statusItem = :statusItem")
    void deleteItemsByTypeAndStatus(String type, String statusItem);
}

