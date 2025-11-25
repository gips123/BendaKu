package com.example.uts;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.bendaku.api.ApiClient;
import com.example.bendaku.api.ApiService;
import com.example.bendaku.model.Claim;
import com.example.bendaku.model.StrapiClaim;
import com.example.bendaku.model.StrapiResponse;
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

        swipeRefresh = findViewById(R.id.swipeRefresh);
        recyclerView = findViewById(R.id.recyclerView);
    }

    private void initServices() {
        ApiClient.init(this);
        sessionManager = new SessionManager(this);
        apiService = ApiClient.getApiService();
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
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupSwipeRefresh() {
        swipeRefresh.setOnRefreshListener(this::loadPendingClaims);
    }

    private void loadPendingClaims() {
        swipeRefresh.setRefreshing(true);

        // Get claims with status "pending"
        Call<StrapiResponse<List<StrapiClaim>>> call = apiService.getClaimsByStatus("*", "pending");
        call.enqueue(new Callback<StrapiResponse<List<StrapiClaim>>>() {
            @Override
            public void onResponse(Call<StrapiResponse<List<StrapiClaim>>> call, Response<StrapiResponse<List<StrapiClaim>>> response) {
                swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<StrapiClaim> strapiClaims = response.body().getData();
                    if (strapiClaims != null && !strapiClaims.isEmpty()) {
                        // Convert StrapiClaim to Claim for adapter
                        List<Claim> claims = convertStrapiClaimsToClaims(strapiClaims);
                        adapter.updateClaims(claims);
                    } else {
                        adapter.updateClaims(new ArrayList<>());
                    }
                } else {
                    String errorMsg = "Gagal memuat klaim";
                    if (response.body() != null && response.body().getError() != null) {
                        errorMsg = response.body().getError().getMessage();
                    }
                    showError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<StrapiResponse<List<StrapiClaim>>> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
                String errorMsg = "Error: " + t.getMessage();
                if (t.getMessage() != null && t.getMessage().contains("Failed to connect")) {
                    errorMsg = "Tidak dapat terhubung ke server. Pastikan backend Strapi berjalan.";
                }
                showError(errorMsg);
            }
        });
    }

    private List<Claim> convertStrapiClaimsToClaims(List<StrapiClaim> strapiClaims) {
        List<Claim> claims = new ArrayList<>();
        for (StrapiClaim strapiClaim : strapiClaims) {
            Claim claim = new Claim();
            claim.setId(String.valueOf(strapiClaim.getId()));
            claim.setClaimerName(strapiClaim.getClaimerName());
            claim.setClaimerPhone(strapiClaim.getClaimerPhone());
            claim.setClaimerUsername(strapiClaim.getClaimerUsername());
            claim.setDescription(strapiClaim.getDescription());
            claim.setStatus(strapiClaim.getStatusClaim()); // Map statusClaim to status
            claim.setAdminNotes(strapiClaim.getAdminNotes());
            
            // Save image ID for update operations
            claim.setImageId(strapiClaim.getImageId());

            // Claimer KTM info
            claim.setClaimerKtmId(strapiClaim.getClaimerKtmId());
            String ktmUrl = strapiClaim.getClaimerKtmUrl();
            if (ktmUrl != null && !ktmUrl.isEmpty() && !ktmUrl.startsWith("http")) {
                if (ktmUrl.startsWith("/")) {
                    ktmUrl = ktmUrl.substring(1);
                }
                ktmUrl = "http://10.0.2.2:1338/" + ktmUrl;
            }
            claim.setClaimerKtmUrl(ktmUrl);
            
            // Handle image URL
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
        // Update claim status to "approved"
        Integer claimId = Integer.parseInt(claim.getId());
        
        // Create update request with approved status, keeping existing imageUrl and claimer
        ApiService.ClaimRequest.ClaimData claimData = new ApiService.ClaimRequest.ClaimData(
                claim.getClaimerName(),
                claim.getClaimerPhone(),
                claim.getDescription(),
                "approved", // statusClaim
                "Klaim disetujui oleh admin", // adminNotes
                claim.getImageId(), // imageUrl - keep existing
                claim.getClaimerKtmId(),
                claim.getClaimerUsername(),
                null  // locale
        );
        
        ApiService.ClaimRequest request = new ApiService.ClaimRequest(claimData);
        
        Call<StrapiResponse<StrapiClaim>> call = apiService.updateClaim(claimId, request);
        call.enqueue(new Callback<StrapiResponse<StrapiClaim>>() {
            @Override
            public void onResponse(Call<StrapiResponse<StrapiClaim>> call, Response<StrapiResponse<StrapiClaim>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
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
        // Update claim status to "rejected"
        Integer claimId = Integer.parseInt(claim.getId());
        
        // Create update request with rejected status, keeping existing imageUrl and claimer
        ApiService.ClaimRequest.ClaimData claimData = new ApiService.ClaimRequest.ClaimData(
                claim.getClaimerName(),
                claim.getClaimerPhone(),
                claim.getDescription(),
                "rejected", // statusClaim
                "Klaim ditolak oleh admin", // adminNotes
                claim.getImageId(), // imageUrl - keep existing
                claim.getClaimerKtmId(),
                claim.getClaimerUsername(),
                null  // locale
        );
        
        ApiService.ClaimRequest request = new ApiService.ClaimRequest(claimData);
        
        Call<StrapiResponse<StrapiClaim>> call = apiService.updateClaim(claimId, request);
        call.enqueue(new Callback<StrapiResponse<StrapiClaim>>() {
            @Override
            public void onResponse(Call<StrapiResponse<StrapiClaim>> call, Response<StrapiResponse<StrapiClaim>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
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

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
