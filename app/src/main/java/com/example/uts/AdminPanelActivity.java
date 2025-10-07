package com.example.uts;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.bendaku.api.ApiClient;
import com.example.bendaku.api.ApiService;
import com.example.bendaku.model.ApiResponse;
import com.example.bendaku.model.Claim;
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

        Call<ApiResponse<List<Claim>>> call = apiService.getClaims("pending_verification");
        call.enqueue(new Callback<ApiResponse<List<Claim>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Claim>>> call, Response<ApiResponse<List<Claim>>> response) {
                swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Claim>> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        List<Claim> claims = apiResponse.getData();
                        if (claims != null) {
                            adapter.updateClaims(claims);
                        }
                    } else {
                        showError(apiResponse.getMessage());
                    }
                } else {
                    showError("Gagal memuat klaim");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Claim>>> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
                showError("Error: " + t.getMessage());
            }
        });
    }

    private void approveClaim(Claim claim) {
        Call<ApiResponse<Claim>> call = apiService.approveClaim(claim.getId());
        call.enqueue(new Callback<ApiResponse<Claim>>() {
            @Override
            public void onResponse(Call<ApiResponse<Claim>> call, Response<ApiResponse<Claim>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Claim> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        Toast.makeText(AdminPanelActivity.this, "Klaim disetujui", Toast.LENGTH_SHORT).show();
                        loadPendingClaims();
                    } else {
                        showError(apiResponse.getMessage());
                    }
                } else {
                    showError("Gagal menyetujui klaim");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Claim>> call, Throwable t) {
                showError("Error: " + t.getMessage());
            }
        });
    }

    private void rejectClaim(Claim claim) {
        ApiService.RejectRequest request = new ApiService.RejectRequest("Klaim ditolak oleh admin");
        Call<ApiResponse<Claim>> call = apiService.rejectClaim(claim.getId(), request);
        call.enqueue(new Callback<ApiResponse<Claim>>() {
            @Override
            public void onResponse(Call<ApiResponse<Claim>> call, Response<ApiResponse<Claim>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Claim> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        Toast.makeText(AdminPanelActivity.this, "Klaim ditolak", Toast.LENGTH_SHORT).show();
                        loadPendingClaims();
                    } else {
                        showError(apiResponse.getMessage());
                    }
                } else {
                    showError("Gagal menolak klaim");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Claim>> call, Throwable t) {
                showError("Error: " + t.getMessage());
            }
        });
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
