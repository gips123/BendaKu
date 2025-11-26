package com.example.bendaku.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.bendaku.api.ApiClient;
import com.example.bendaku.api.ApiService;
import com.example.bendaku.database.AppDatabase;
import com.example.bendaku.database.dao.ClaimDao;
import com.example.bendaku.database.entity.LocalClaim;
import com.example.bendaku.model.Claim;
import com.example.bendaku.model.StrapiClaim;
import com.example.bendaku.model.StrapiResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClaimRepository {
    private ClaimDao claimDao;
    private ApiService apiService;
    private ExecutorService executor;
    private Handler mainHandler;

    public ClaimRepository(Context context) {
        AppDatabase database = AppDatabase.getDatabase(context);
        claimDao = database.claimDao();
        apiService = ApiClient.getApiService();
        executor = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public interface DataCallback {
        void onDataLoaded(List<Claim> claims);
        void onError(String error);
    }

    public void getPendingClaims(DataCallback callback) {
        // First, load from database (offline support)
        executor.execute(() -> {
            // Only get claims where item statusItem is "open"
            List<LocalClaim> localClaims = claimDao.getClaimsByStatusAndItemStatus("pending", "open");

            // Convert to Claim model
            List<Claim> claims = convertLocalClaimsToClaims(localClaims);
            
            // Return cached data immediately
            mainHandler.post(() -> callback.onDataLoaded(claims));

            // Then, try to sync from API in background
            syncClaimsFromApi();
        });
    }

    private void syncClaimsFromApi() {
        Call<StrapiResponse<List<StrapiClaim>>> call = apiService.getClaimsByStatus("*", "pending");
        call.enqueue(new Callback<StrapiResponse<List<StrapiClaim>>>() {
            @Override
            public void onResponse(Call<StrapiResponse<List<StrapiClaim>>> call, 
                                 Response<StrapiResponse<List<StrapiClaim>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<StrapiClaim> strapiClaims = response.body().getData();
                    if (strapiClaims != null) {
                        saveClaimsToDatabase(strapiClaims);
                    }
                }
            }

            @Override
            public void onFailure(Call<StrapiResponse<List<StrapiClaim>>> call, Throwable t) {
                // Silent fail - user already has cached data
            }
        });
    }

    private void saveClaimsToDatabase(List<StrapiClaim> strapiClaims) {
        executor.execute(() -> {
            List<LocalClaim> localClaims = new ArrayList<>();
            List<String> syncedDocumentIds = new ArrayList<>();
            long syncTime = System.currentTimeMillis();

            for (StrapiClaim strapiClaim : strapiClaims) {
                if (strapiClaim == null) continue;

                // Get item statusItem from flat structure
                String itemStatusItem = null;
                if (strapiClaim.getFlatItem() != null) {
                    itemStatusItem = strapiClaim.getFlatItem().getStatusItem();
                }

                // Only save claims where item statusItem is "open"
                if (itemStatusItem == null || !itemStatusItem.equals("open")) {
                    continue;
                }

                LocalClaim localClaim = new LocalClaim();
                localClaim.claimId = strapiClaim.getId() != null ? String.valueOf(strapiClaim.getId()) : "";
                localClaim.documentId = strapiClaim.getDocumentId();
                localClaim.claimerName = strapiClaim.getClaimerName();
                localClaim.claimerPhone = strapiClaim.getClaimerPhone();
                localClaim.claimerUsername = strapiClaim.getClaimerUsername();
                localClaim.description = strapiClaim.getDescription();
                localClaim.statusClaim = strapiClaim.getStatusClaim();
                localClaim.adminNotes = strapiClaim.getAdminNotes();
                localClaim.itemStatusItem = itemStatusItem;
                
                Integer itemId = strapiClaim.getItemId();
                if (itemId != null) {
                    localClaim.itemId = String.valueOf(itemId);
                }
                
                String itemName = strapiClaim.getItemName();
                if (itemName == null && strapiClaim.getFlatItem() != null) {
                    itemName = strapiClaim.getFlatItem().getName();
                }
                localClaim.itemName = itemName;
                
                localClaim.imageId = strapiClaim.getImageId();
                localClaim.claimerKtmId = strapiClaim.getClaimerKtmId();
                
                String ktmUrl = strapiClaim.getClaimerKtmUrl();
                if (ktmUrl != null && !ktmUrl.isEmpty() && !ktmUrl.startsWith("http")) {
                    if (ktmUrl.startsWith("/")) {
                        ktmUrl = ktmUrl.substring(1);
                    }
                    ktmUrl = "http://10.0.2.2:1338/" + ktmUrl;
                }
                localClaim.claimerKtmUrl = ktmUrl;
                
                String imageUrl = strapiClaim.getImageUrl();
                if (imageUrl != null && !imageUrl.isEmpty() && !imageUrl.startsWith("http")) {
                    if (imageUrl.startsWith("/")) {
                        imageUrl = imageUrl.substring(1);
                    }
                    imageUrl = "http://10.0.2.2:1338/" + imageUrl;
                }
                localClaim.proofImageUrl = imageUrl;
                
                localClaim.createdAt = strapiClaim.getCreatedAt();
                localClaim.updatedAt = strapiClaim.getUpdatedAt();
                localClaim.lastSyncTime = syncTime;

                localClaims.add(localClaim);
                if (localClaim.documentId != null && !localClaim.documentId.isEmpty()) {
                    syncedDocumentIds.add(localClaim.documentId);
                }
            }

            if (!localClaims.isEmpty()) {
                claimDao.insertClaims(localClaims);
            }

            if (!syncedDocumentIds.isEmpty()) {
                claimDao.deleteClaimsNotInDocumentIds("pending", "open", syncedDocumentIds);
            } else {
                claimDao.deleteClaimsByStatusAndItemStatus("pending", "open");
            }
        });
    }

    private List<Claim> convertLocalClaimsToClaims(List<LocalClaim> localClaims) {
        List<Claim> claims = new ArrayList<>();
        for (LocalClaim localClaim : localClaims) {
            Claim claim = new Claim();
            claim.setId(localClaim.claimId);
            claim.setDocumentId(localClaim.documentId);
            claim.setItemId(localClaim.itemId);
            claim.setItemName(localClaim.itemName);
            claim.setItemStatusItem(localClaim.itemStatusItem);
            claim.setClaimerName(localClaim.claimerName);
            claim.setClaimerPhone(localClaim.claimerPhone);
            claim.setClaimerUsername(localClaim.claimerUsername);
            claim.setDescription(localClaim.description);
            claim.setStatus(localClaim.statusClaim);
            claim.setAdminNotes(localClaim.adminNotes);
            claim.setImageId(localClaim.imageId);
            claim.setClaimerKtmId(localClaim.claimerKtmId);
            claim.setClaimerKtmUrl(localClaim.claimerKtmUrl);
            claim.setProofImageUrl(localClaim.proofImageUrl);
            claim.setCreatedAt(localClaim.createdAt);
            claims.add(claim);
        }
        return claims;
    }

    public void updateClaimStatus(String documentId, String status, String adminNotes) {
        executor.execute(() -> {
            long syncTime = System.currentTimeMillis();
            String updatedAt = String.valueOf(syncTime);
            claimDao.updateClaimStatus(documentId, status, adminNotes, updatedAt, syncTime);
        });
    }

    public void updateClaimsItemStatus(String itemId, String itemStatus) {
        executor.execute(() -> {
            claimDao.updateClaimsItemStatus(itemId, itemStatus);
        });
    }

    public void syncAllClaims() {
        syncClaimsFromApi();
    }

    public void removeClaimByDocumentId(String documentId) {
        if (documentId == null || documentId.isEmpty()) return;
        executor.execute(() -> claimDao.deleteClaimByDocumentId(documentId));
    }
}

