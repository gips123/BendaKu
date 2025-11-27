package com.example.uts;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.bendaku.api.ApiClient;
import com.example.bendaku.api.ApiService;
import com.example.bendaku.model.StrapiClaim;
import com.example.bendaku.model.StrapiResponse;
import com.example.bendaku.model.StrapiItem;
import com.example.bendaku.model.Claim;
import com.example.bendaku.utils.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClaimDetailActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextView tvItemName, tvItemLocation, tvItemDescription, tvItemType;
    private TextView tvClaimerName, tvClaimerPhone, tvClaimerUsername;
    private TextView tvClaimDescription, tvClaimStatus, tvAdminNotes, tvCreatedAt;
    private ImageView ivProofImage, ivKtmImage, ivItemImage;
    private MaterialButton btnApprove, btnReject;

    private String claimDocumentId;
    private ApiService apiService;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claim_detail);

        claimDocumentId = getIntent().getStringExtra("claim_document_id");
        if (claimDocumentId == null || claimDocumentId.isEmpty()) {
            Toast.makeText(this, "Claim Document ID tidak valid", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        initServices();
        setupToolbar();
        loadClaimDetail();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvItemName = findViewById(R.id.tvItemName);
        tvItemLocation = findViewById(R.id.tvItemLocation);
        tvItemDescription = findViewById(R.id.tvItemDescription);
        tvItemType = findViewById(R.id.tvItemType);
        tvClaimerName = findViewById(R.id.tvClaimerName);
        tvClaimerPhone = findViewById(R.id.tvClaimerPhone);
        tvClaimerUsername = findViewById(R.id.tvClaimerUsername);
        tvClaimDescription = findViewById(R.id.tvClaimDescription);
        tvClaimStatus = findViewById(R.id.tvClaimStatus);
        tvAdminNotes = findViewById(R.id.tvAdminNotes);
        tvCreatedAt = findViewById(R.id.tvCreatedAt);
        ivProofImage = findViewById(R.id.ivProofImage);
        ivKtmImage = findViewById(R.id.ivKtmImage);
        ivItemImage = findViewById(R.id.ivItemImage);
        btnApprove = findViewById(R.id.btnApprove);
        btnReject = findViewById(R.id.btnReject);
    }

    private void initServices() {
        ApiClient.init(this);
        apiService = ApiClient.getApiService();
        sessionManager = new SessionManager(this);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Detail Klaim");
        }
    }

    private void loadClaimDetail() {
        Call<StrapiResponse<StrapiClaim>> call = apiService.getClaim(claimDocumentId, "*");
        call.enqueue(new Callback<StrapiResponse<StrapiClaim>>() {
            @Override
            public void onResponse(Call<StrapiResponse<StrapiClaim>> call, Response<StrapiResponse<StrapiClaim>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    StrapiClaim claim = response.body().getData();
                    if (claim != null) {
                        String itemDocumentId = getItemDocumentId(claim);
                        if (itemDocumentId != null && !itemDocumentId.isEmpty()) {
                            loadItemDetail(itemDocumentId, claim);
                        } else {
                            displayClaimDetail(claim, null);
                        }
                    } else {
                        Toast.makeText(ClaimDetailActivity.this, "Data klaim tidak ditemukan", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    String errorMsg = "Gagal memuat detail klaim";
                    if (response.body() != null && response.body().getError() != null) {
                        errorMsg = response.body().getError().getMessage();
                    }
                    Toast.makeText(ClaimDetailActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<StrapiResponse<StrapiClaim>> call, Throwable t) {
                Toast.makeText(ClaimDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private String getItemDocumentId(StrapiClaim claim) {
        if (claim.getFlatItem() != null && claim.getFlatItem().getDocumentId() != null) {
            return claim.getFlatItem().getDocumentId();
        }
        if (claim.getAttributes() != null && claim.getAttributes().getItem() != null) {
            if (claim.getAttributes().getItem().getFlatDocumentId() != null) {
                return claim.getAttributes().getItem().getFlatDocumentId();
            }
            if (claim.getAttributes().getItem().getData() != null && 
                claim.getAttributes().getItem().getData().getFlatDocumentId() != null) {
                return claim.getAttributes().getItem().getData().getFlatDocumentId();
            }
        }
        return null;
    }

    private void loadItemDetail(String itemDocumentId, StrapiClaim claim) {
        Call<StrapiResponse<StrapiItem>> itemCall = 
            apiService.getItemByDocumentId(itemDocumentId, "*");
        itemCall.enqueue(new Callback<StrapiResponse<StrapiItem>>() {
            @Override
            public void onResponse(
                    Call<StrapiResponse<StrapiItem>> call,
                    Response<StrapiResponse<StrapiItem>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    StrapiItem item = response.body().getData();
                    displayClaimDetail(claim, item);
                } else {
                    displayClaimDetail(claim, null);
                }
            }

            @Override
            public void onFailure(
                    Call<StrapiResponse<StrapiItem>> call,
                    Throwable t) {
                displayClaimDetail(claim, null);
            }
        });
    }

    private void displayClaimDetail(StrapiClaim claim, StrapiItem item) {
        String itemName = claim.getItemName();
        if (itemName == null && claim.getFlatItem() != null) {
            itemName = claim.getFlatItem().getName();
        }
        tvItemName.setText(itemName != null ? itemName : "N/A");

        if (claim.getFlatItem() != null) {
            tvItemLocation.setText(claim.getFlatItem().getLocation() != null ? claim.getFlatItem().getLocation() : "N/A");
            tvItemDescription.setText(claim.getFlatItem().getDescription() != null ? claim.getFlatItem().getDescription() : "N/A");
            String type = claim.getFlatItem().getType();
            tvItemType.setText(type != null ? type.toUpperCase() : "N/A");
        } else {
            tvItemLocation.setText("N/A");
            tvItemDescription.setText("N/A");
            tvItemType.setText("N/A");
        }

        if (item != null) {
            String itemImageUrl = item.getImageUrl();
            if (itemImageUrl != null && !itemImageUrl.isEmpty()) {
                if (!itemImageUrl.startsWith("http")) {
                    if (itemImageUrl.startsWith("/")) {
                        itemImageUrl = itemImageUrl.substring(1);
                    }
                    itemImageUrl = "http://10.0.2.2:1338/" + itemImageUrl;
                }
                Glide.with(this).load(itemImageUrl).into(ivItemImage);
            }
        } else {
            if (claim.getFlatItem() != null) {
                StrapiClaim.FlatImage itemImage = claim.getFlatItem().getImageUrl();
                if (itemImage != null && itemImage.getUrl() != null && !itemImage.getUrl().isEmpty()) {
                    String itemImageUrl = itemImage.getUrl();
                    if (!itemImageUrl.startsWith("http")) {
                        if (itemImageUrl.startsWith("/")) {
                            itemImageUrl = itemImageUrl.substring(1);
                        }
                        itemImageUrl = "http://10.0.2.2:1338/" + itemImageUrl;
                    }
                    Glide.with(this).load(itemImageUrl).into(ivItemImage);
                }
            }
        }

        tvClaimerName.setText(claim.getClaimerName() != null ? claim.getClaimerName() : "N/A");
        tvClaimerPhone.setText(claim.getClaimerPhone() != null ? claim.getClaimerPhone() : "N/A");
        tvClaimerUsername.setText(claim.getClaimerUsername() != null ? claim.getClaimerUsername() : "N/A");

        tvClaimDescription.setText(claim.getDescription() != null ? claim.getDescription() : "N/A");
        String status = claim.getStatusClaim();
        tvClaimStatus.setText(status != null ? status.toUpperCase() : "N/A");
        tvAdminNotes.setText(claim.getAdminNotes() != null ? claim.getAdminNotes() : "Belum ada catatan");
        tvCreatedAt.setText(claim.getCreatedAt() != null ? claim.getCreatedAt() : "N/A");

        String proofImageUrl = claim.getImageUrl();
        if (proofImageUrl != null && !proofImageUrl.isEmpty()) {
            if (!proofImageUrl.startsWith("http")) {
                if (proofImageUrl.startsWith("/")) {
                    proofImageUrl = proofImageUrl.substring(1);
                }
                proofImageUrl = "http://10.0.2.2:1338/" + proofImageUrl;
            }
            Glide.with(this).load(proofImageUrl).into(ivProofImage);
        }

        String ktmImageUrl = claim.getClaimerKtmUrl();
        if (ktmImageUrl != null && !ktmImageUrl.isEmpty()) {
            if (!ktmImageUrl.startsWith("http")) {
                if (ktmImageUrl.startsWith("/")) {
                    ktmImageUrl = ktmImageUrl.substring(1);
                }
                ktmImageUrl = "http://10.0.2.2:1338/" + ktmImageUrl;
            }
            Glide.with(this).load(ktmImageUrl).into(ivKtmImage);
        }

        if (status != null && status.equals("pending")) {
            btnApprove.setVisibility(android.view.View.VISIBLE);
            btnReject.setVisibility(android.view.View.VISIBLE);
            btnApprove.setOnClickListener(v -> approveClaim(claim));
            btnReject.setOnClickListener(v -> rejectClaim(claim));
        } else {
            btnApprove.setVisibility(android.view.View.GONE);
            btnReject.setVisibility(android.view.View.GONE);
        }
    }

    private void approveClaim(StrapiClaim strapiClaim) {
        Claim claim = new Claim();
        claim.setId(String.valueOf(strapiClaim.getId()));
        claim.setClaimerName(strapiClaim.getClaimerName());
        claim.setClaimerPhone(strapiClaim.getClaimerPhone());
        claim.setClaimerUsername(strapiClaim.getClaimerUsername());
        claim.setDescription(strapiClaim.getDescription());
        claim.setImageId(strapiClaim.getImageId());
        claim.setClaimerKtmId(strapiClaim.getClaimerKtmId());
        Integer itemId = strapiClaim.getItemId();
        if (itemId != null) {
            claim.setItemId(String.valueOf(itemId));
        }

        Integer claimIdInt = Integer.parseInt(claim.getId());
        Integer itemIdInt = null;
        try {
            if (claim.getItemId() != null && !claim.getItemId().isEmpty()) {
                itemIdInt = Integer.parseInt(claim.getItemId());
            }
        } catch (NumberFormatException e) {
        }

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
                    Toast.makeText(ClaimDetailActivity.this, "Klaim disetujui", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String errorMsg = "Gagal menyetujui klaim";
                    if (response.body() != null && response.body().getError() != null) {
                        errorMsg = response.body().getError().getMessage();
                    }
                    Toast.makeText(ClaimDetailActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<StrapiResponse<StrapiClaim>> call, Throwable t) {
                Toast.makeText(ClaimDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void rejectClaim(StrapiClaim strapiClaim) {
        String claimDocumentId = strapiClaim.getDocumentId();
        if (claimDocumentId == null || claimDocumentId.isEmpty()) {
            Toast.makeText(this, "Document ID tidak tersedia", Toast.LENGTH_SHORT).show();
            return;
        }

        Claim claim = new Claim();
        claim.setId(String.valueOf(strapiClaim.getId()));
        claim.setDocumentId(claimDocumentId);
        claim.setClaimerName(strapiClaim.getClaimerName());
        claim.setClaimerPhone(strapiClaim.getClaimerPhone());
        claim.setClaimerUsername(strapiClaim.getClaimerUsername());
        claim.setDescription(strapiClaim.getDescription());
        claim.setImageId(strapiClaim.getImageId());
        claim.setClaimerKtmId(strapiClaim.getClaimerKtmId());
        Integer itemId = strapiClaim.getItemId();
        if (itemId != null) {
            claim.setItemId(String.valueOf(itemId));
        }
        try {
            if (claim.getItemId() != null && !claim.getItemId().isEmpty()) {
                itemIdInt = Integer.parseInt(claim.getItemId());
            }
        } catch (NumberFormatException e) {
        }

        ApiService.ClaimRequest.ClaimData claimData = new ApiService.ClaimRequest.ClaimData(
                claim.getClaimerName(),
                claim.getClaimerPhone(),
                claim.getDescription(),
                "rejected",
                "Klaim ditolak oleh admin",
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
                    Toast.makeText(ClaimDetailActivity.this, "Klaim ditolak", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String errorMsg = "Gagal menolak klaim";
                    if (response.body() != null && response.body().getError() != null) {
                        errorMsg = response.body().getError().getMessage();
                    }
                    Toast.makeText(ClaimDetailActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<StrapiResponse<StrapiClaim>> call, Throwable t) {
                Toast.makeText(ClaimDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

