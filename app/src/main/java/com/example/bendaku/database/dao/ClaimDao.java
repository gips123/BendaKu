package com.example.bendaku.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.bendaku.database.entity.LocalClaim;

import java.util.List;

@Dao
public interface ClaimDao {
    @Query("SELECT * FROM claims WHERE statusClaim = :status AND itemStatusItem = :itemStatus ORDER BY createdAt DESC")
    List<LocalClaim> getClaimsByStatusAndItemStatus(String status, String itemStatus);

    @Query("SELECT * FROM claims WHERE statusClaim = :status ORDER BY createdAt DESC")
    List<LocalClaim> getClaimsByStatus(String status);

    @Query("SELECT * FROM claims WHERE claimId = :claimId")
    LocalClaim getClaimById(String claimId);

    @Query("SELECT * FROM claims WHERE documentId = :documentId")
    LocalClaim getClaimByDocumentId(String documentId);

    @Query("SELECT * FROM claims WHERE itemId = :itemId")
    List<LocalClaim> getClaimsByItemId(String itemId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertClaim(LocalClaim claim);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertClaims(List<LocalClaim> claims);

    @Update
    void updateClaim(LocalClaim claim);

    @Query("DELETE FROM claims WHERE claimId = :claimId")
    void deleteClaim(String claimId);

    @Query("DELETE FROM claims WHERE documentId = :documentId")
    void deleteClaimByDocumentId(String documentId);

    @Query("DELETE FROM claims")
    void deleteAllClaims();

    @Query("UPDATE claims SET statusClaim = :status, adminNotes = :adminNotes, updatedAt = :updatedAt, lastSyncTime = :syncTime WHERE documentId = :documentId")
    void updateClaimStatus(String documentId, String status, String adminNotes, String updatedAt, long syncTime);

    @Query("UPDATE claims SET itemStatusItem = :itemStatus WHERE itemId = :itemId")
    void updateClaimsItemStatus(String itemId, String itemStatus);

    @Query("DELETE FROM claims WHERE statusClaim = :status AND itemStatusItem = :itemStatus AND documentId NOT IN (:documentIds)")
    void deleteClaimsNotInDocumentIds(String status, String itemStatus, List<String> documentIds);

    @Query("DELETE FROM claims WHERE statusClaim = :status AND itemStatusItem = :itemStatus")
    void deleteClaimsByStatusAndItemStatus(String status, String itemStatus);
}

