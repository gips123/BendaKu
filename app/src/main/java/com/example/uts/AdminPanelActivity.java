package com.example.uts;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.bendaku.api.ApiClient;
import com.example.bendaku.api.ApiService;
import com.example.bendaku.model.Claim;
import com.example.bendaku.model.StrapiClaim;
import com.example.bendaku.model.StrapiResponse;
import com.example.bendaku.repository.ClaimRepository;
import com.example.bendaku.repository.ItemRepository;
import com.example.bendaku.utils.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminPanelActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private ClaimAdapter adapter;
    private SessionManager sessionManager;
    private ApiService apiService;
    private ClaimRepository claimRepository;
    private ItemRepository itemRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        initViews();
        initServices();
        checkAdminAccess();
        setupRecyclerView();
        setupSwipeRefresh();
        loadPendingClaims();
    }

    private void initViews() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        swipeRefresh = findViewById(R.id.swipeRefresh);
        recyclerView = findViewById(R.id.recyclerView);
    }

    private void initServices() {
        ApiClient.init(this);
        sessionManager = new SessionManager(this);
        apiService = ApiClient.getApiService();
        claimRepository = new ClaimRepository(this);
        itemRepository = new ItemRepository(this);
    }

    private void checkAdminAccess() {
        if (!sessionManager.getUser().isAdmin()) {
            Toast.makeText(this, "Akses ditolak. Hanya admin yang bisa mengakses halaman ini.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupRecyclerView() {
        adapter = new ClaimAdapter(new ArrayList<>(), new ClaimAdapter.OnClaimActionListener() {
            @Override
            public void onApprove(Claim claim) {
                approveClaim(claim);
            }

            @Override
            public void onReject(Claim claim) {
                rejectClaim(claim);
            }

            @Override
            public void onItemClick(Claim claim) {
                openClaimDetail(claim);
            }
        });
        // Default to CardView layout
        switchLayout(ClaimAdapter.VIEW_TYPE_CARDVIEW);
        recyclerView.setAdapter(adapter);
    }

    private void switchLayout(int layoutMode) {
        adapter.setLayoutMode(layoutMode);
        
        RecyclerView.LayoutManager layoutManager;
        if (layoutMode == ClaimAdapter.VIEW_TYPE_GRID) {
            // Grid layout with 2 columns
            layoutManager = new GridLayoutManager(this, 2);
        } else {
            // List or CardView layout (vertical linear)
            layoutManager = new LinearLayoutManager(this);
        }
        
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void setupSwipeRefresh() {
        swipeRefresh.setOnRefreshListener(this::loadPendingClaims);
    }

    private void loadPendingClaims() {
        swipeRefresh.setRefreshing(true);

        if (claimRepository != null) {
            claimRepository.getPendingClaims(new ClaimRepository.DataCallback() {
                @Override
                public void onDataLoaded(List<Claim> claims) {
                    swipeRefresh.setRefreshing(false);
                    if (claims != null && !claims.isEmpty()) {
                        adapter.updateClaims(claims);
                    } else {
                        adapter.updateClaims(new ArrayList<>());
                    }
                }

                @Override
                public void onError(String error) {
                    swipeRefresh.setRefreshing(false);
                    showError(error);
                }
            });
        } else {
            swipeRefresh.setRefreshing(false);
            showError("Repository tidak tersedia");
        }
    }

    private List<Claim> convertStrapiClaimsToClaims(List<StrapiClaim> strapiClaims) {
        List<Claim> claims = new ArrayList<>();
        for (StrapiClaim strapiClaim : strapiClaims) {
            // Get item statusItem from flat structure
            String itemStatusItem = null;
            if (strapiClaim.getFlatItem() != null) {
                itemStatusItem = strapiClaim.getFlatItem().getStatusItem();
            }
            
            // Only include claims where item statusItem is "open"
            if (itemStatusItem == null || !itemStatusItem.equals("open")) {
                continue; // Skip claims for items that are already claimed or resolved
            }
            
            Claim claim = new Claim();
            claim.setId(String.valueOf(strapiClaim.getId()));
            claim.setDocumentId(strapiClaim.getDocumentId());
            claim.setClaimerName(strapiClaim.getClaimerName());
            claim.setClaimerPhone(strapiClaim.getClaimerPhone());
            claim.setClaimerUsername(strapiClaim.getClaimerUsername());
            claim.setDescription(strapiClaim.getDescription());
            claim.setStatus(strapiClaim.getStatusClaim());
            claim.setAdminNotes(strapiClaim.getAdminNotes());
            claim.setItemStatusItem(itemStatusItem);
            
            Integer itemId = strapiClaim.getItemId();
            if (itemId != null) {
                claim.setItemId(String.valueOf(itemId));
            }
            
            String itemName = strapiClaim.getItemName();
            if (itemName == null && strapiClaim.getFlatItem() != null) {
                itemName = strapiClaim.getFlatItem().getName();
            }
            claim.setItemName(itemName);
            
            claim.setImageId(strapiClaim.getImageId());

            claim.setClaimerKtmId(strapiClaim.getClaimerKtmId());
            String ktmUrl = strapiClaim.getClaimerKtmUrl();
            if (ktmUrl != null && !ktmUrl.isEmpty() && !ktmUrl.startsWith("http")) {
                if (ktmUrl.startsWith("/")) {
                    ktmUrl = ktmUrl.substring(1);
                }
                ktmUrl = "http://10.0.2.2:1338/" + ktmUrl;
            }
            claim.setClaimerKtmUrl(ktmUrl);
            
            String imageUrl = strapiClaim.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty() && !imageUrl.startsWith("http")) {
                if (imageUrl.startsWith("/")) {
                    imageUrl = imageUrl.substring(1);
                }
                imageUrl = "http://10.0.2.2:1338/" + imageUrl;
            }
            claim.setProofImageUrl(imageUrl);
            
            claim.setCreatedAt(strapiClaim.getCreatedAt());
            claims.add(claim);
        }
        return claims;
    }

    private void approveClaim(Claim claim) {
        String claimDocumentId = claim.getDocumentId();
        if (claimDocumentId == null || claimDocumentId.isEmpty()) {
            showError("Document ID tidak tersedia");
            return;
        }
        
        final Integer itemIdInt;
        try {
            if (claim.getItemId() != null && !claim.getItemId().isEmpty()) {
                itemIdInt = Integer.parseInt(claim.getItemId());
            } else {
                showError("Item ID tidak tersedia");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Item ID tidak valid");
            return;
        }
        
        final String finalClaimDocumentId = claimDocumentId;
        final Claim finalClaim = claim;
        
        // First, fetch item to get current data
        Call<StrapiResponse<com.example.bendaku.model.StrapiItem>> itemCall = apiService.getItem(itemIdInt, "*");
        itemCall.enqueue(new Callback<StrapiResponse<com.example.bendaku.model.StrapiItem>>() {
            @Override
            public void onResponse(Call<StrapiResponse<com.example.bendaku.model.StrapiItem>> call, 
                                 Response<StrapiResponse<com.example.bendaku.model.StrapiItem>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    com.example.bendaku.model.StrapiItem item = response.body().getData();
                    if (item != null) {
                        updateItemStatusAndClaim(item, finalClaim, finalClaimDocumentId, itemIdInt);
                    } else {
                        showError("Item tidak ditemukan");
                    }
                } else {
                    showError("Gagal memuat data item");
                }
            }

            @Override
            public void onFailure(Call<StrapiResponse<com.example.bendaku.model.StrapiItem>> call, Throwable t) {
                showError("Error memuat item: " + t.getMessage());
            }
        });
    }
    
    private void updateItemStatusAndClaim(com.example.bendaku.model.StrapiItem item, Claim claim, 
                                         String claimDocumentId, Integer itemIdInt) {
        // Update item statusItem to "claimed"
        ApiService.ItemRequest.ItemData itemData = new ApiService.ItemRequest.ItemData(
                item.getName() != null ? item.getName() : "",
                item.getDescription() != null ? item.getDescription() : "",
                item.getLocation() != null ? item.getLocation() : "",
                item.getDateTime() != null ? item.getDateTime() : "",
                item.getType() != null ? item.getType() : "lost",
                "claimed",
                item.getReporterName() != null ? item.getReporterName() : "",
                item.getReporterPhone() != null ? item.getReporterPhone() : "",
                item.getImageId() != null ? item.getImageId() : null
        );
        
        ApiService.ItemRequest itemRequest = new ApiService.ItemRequest(itemData);
        
        // Update item first
        Call<StrapiResponse<com.example.bendaku.model.StrapiItem>> itemUpdateCall = 
                apiService.updateItem(itemIdInt, itemRequest);
        itemUpdateCall.enqueue(new Callback<StrapiResponse<com.example.bendaku.model.StrapiItem>>() {
            @Override
            public void onResponse(Call<StrapiResponse<com.example.bendaku.model.StrapiItem>> call,
                                 Response<StrapiResponse<com.example.bendaku.model.StrapiItem>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    // Then update claim status
                    updateClaimStatus(claim, claimDocumentId, itemIdInt);
                } else {
                    showError("Gagal update status item");
                }
            }

            @Override
            public void onFailure(Call<StrapiResponse<com.example.bendaku.model.StrapiItem>> call, Throwable t) {
                showError("Error update item: " + t.getMessage());
            }
        });
    }

    private void updateClaimStatus(Claim claim, String claimDocumentId, Integer itemIdInt) {
        ApiService.ClaimRequest.ClaimData claimData = new ApiService.ClaimRequest.ClaimData(
                claim.getClaimerName(),
                claim.getClaimerPhone(),
                claim.getDescription(),
                "approved",
                "Klaim disetujui oleh admin",
                claim.getImageId(),
                claim.getClaimerKtmId(),
                claim.getClaimerUsername(),
                itemIdInt,
                null
        );
        
        ApiService.ClaimRequest request = new ApiService.ClaimRequest(claimData);
        
        Call<StrapiResponse<StrapiClaim>> call = apiService.updateClaim(claimDocumentId, request);
        call.enqueue(new Callback<StrapiResponse<StrapiClaim>>() {
            @Override
            public void onResponse(Call<StrapiResponse<StrapiClaim>> call, Response<StrapiResponse<StrapiClaim>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    // Update database
                    if (itemRepository != null && claim.getItemId() != null) {
                        itemRepository.updateItemStatus(claim.getItemId(), "claimed");
                    }
                    if (claimRepository != null) {
                        claimRepository.updateClaimStatus(claimDocumentId, "approved", "Klaim disetujui oleh admin");
                        claimRepository.updateClaimsItemStatus(claim.getItemId(), "claimed");
                    }
                        Toast.makeText(AdminPanelActivity.this, "Klaim disetujui", Toast.LENGTH_SHORT).show();
                        loadPendingClaims();
                    } else {
                    String errorMsg = "Gagal menyetujui klaim";
                    if (response.body() != null && response.body().getError() != null) {
                        errorMsg = response.body().getError().getMessage();
                    }
                    showError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<StrapiResponse<StrapiClaim>> call, Throwable t) {
                showError("Error: " + t.getMessage());
            }
        });
    }

    private void rejectClaim(Claim claim) {
        // Update claim status to "rejected" - use documentId for Strapi v5
        String claimDocumentId = claim.getDocumentId();
        if (claimDocumentId == null || claimDocumentId.isEmpty()) {
            showError("Document ID tidak tersedia");
            return;
        }
        
        // Parse itemId to Integer
        Integer itemIdInt = null;
        try {
            if (claim.getItemId() != null && !claim.getItemId().isEmpty()) {
                itemIdInt = Integer.parseInt(claim.getItemId());
            }
        } catch (NumberFormatException e) {
            // Item ID not available, continue without it
        }
        
        // Create update request with rejected status, keeping existing imageUrl, claimer, and item
        ApiService.ClaimRequest.ClaimData claimData = new ApiService.ClaimRequest.ClaimData(
                claim.getClaimerName(),
                claim.getClaimerPhone(),
                claim.getDescription(),
                "rejected", // statusClaim
                "Klaim ditolak oleh admin", // adminNotes
                claim.getImageId(), // imageUrl - keep existing
                claim.getClaimerKtmId(),
                claim.getClaimerUsername(),
                itemIdInt, // item - keep existing
                null  // locale
        );
        
        ApiService.ClaimRequest request = new ApiService.ClaimRequest(claimData);
        
        Call<StrapiResponse<StrapiClaim>> call = apiService.updateClaim(claimDocumentId, request);
        call.enqueue(new Callback<StrapiResponse<StrapiClaim>>() {
            @Override
            public void onResponse(Call<StrapiResponse<StrapiClaim>> call, Response<StrapiResponse<StrapiClaim>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    // Update database
                    if (claimRepository != null) {
                        claimRepository.updateClaimStatus(claimDocumentId, "rejected", "Klaim ditolak oleh admin");
                    }
                        Toast.makeText(AdminPanelActivity.this, "Klaim ditolak", Toast.LENGTH_SHORT).show();
                        loadPendingClaims();
                    } else {
                    String errorMsg = "Gagal menolak klaim";
                    if (response.body() != null && response.body().getError() != null) {
                        errorMsg = response.body().getError().getMessage();
                    }
                    showError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<StrapiResponse<StrapiClaim>> call, Throwable t) {
                showError("Error: " + t.getMessage());
            }
        });
    }

    private void openClaimDetail(Claim claim) {
        Intent intent = new Intent(this, ClaimDetailActivity.class);
        intent.putExtra("claim_document_id", claim.getDocumentId()); // Use documentId for Strapi v5
        intent.putExtra("claim_item_id", claim.getItemId());
        intent.putExtra("claim_item_name", claim.getItemName());
        intent.putExtra("claim_claimer_name", claim.getClaimerName());
        intent.putExtra("claim_claimer_phone", claim.getClaimerPhone());
        intent.putExtra("claim_claimer_username", claim.getClaimerUsername());
        intent.putExtra("claim_description", claim.getDescription());
        intent.putExtra("claim_status", claim.getStatus());
        intent.putExtra("claim_admin_notes", claim.getAdminNotes());
        intent.putExtra("claim_proof_image_url", claim.getProofImageUrl());
        intent.putExtra("claim_ktm_image_url", claim.getClaimerKtmUrl());
        intent.putExtra("claim_created_at", claim.getCreatedAt());
        startActivity(intent);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_panel_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_list) {
            switchLayout(ClaimAdapter.VIEW_TYPE_LIST);
            return true;
        } else if (id == R.id.action_grid) {
            switchLayout(ClaimAdapter.VIEW_TYPE_GRID);
            return true;
        } else if (id == R.id.action_cardview) {
            switchLayout(ClaimAdapter.VIEW_TYPE_CARDVIEW);
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
