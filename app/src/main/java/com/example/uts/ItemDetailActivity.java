package com.example.uts;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.bendaku.api.ApiClient;
import com.example.bendaku.api.ApiService;
import com.example.bendaku.model.ApiResponse;
import com.example.bendaku.model.Item;
import com.example.bendaku.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemDetailActivity extends AppCompatActivity {

    private ImageView ivItemImage;
    private TextView tvItemType, tvItemName, tvItemDescription, tvItemLocation, tvItemDate;
    private TextView tvReporter;
    private MaterialButton btnClaim, btnContact;

    private String itemId;
    private Item currentItem;
    private SessionManager sessionManager;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        initViews();
        initServices();
        getItemId();
        loadItemDetail();
        setupClickListeners();
    }

    private void initViews() {
        ivItemImage = findViewById(R.id.ivItemImage);
        tvItemType = findViewById(R.id.tvItemType);
        tvItemName = findViewById(R.id.tvItemName);
        tvItemDescription = findViewById(R.id.tvItemDescription);
        tvItemLocation = findViewById(R.id.tvItemLocation);
        tvItemDate = findViewById(R.id.tvItemDate);
        tvReporter = findViewById(R.id.tvReporter);
        btnClaim = findViewById(R.id.btnClaim);
        btnContact = findViewById(R.id.btnContact);
    }

    private void initServices() {
        sessionManager = new SessionManager(this);
        apiService = ApiClient.getApiService();
    }

    private void getItemId() {
        itemId = getIntent().getStringExtra("item_id");
        if (itemId == null) {
            Toast.makeText(this, "Item tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadItemDetail() {
        Call<ApiResponse<Item>> call = apiService.getItem(itemId);
        call.enqueue(new Callback<ApiResponse<Item>>() {
            @Override
            public void onResponse(Call<ApiResponse<Item>> call, Response<ApiResponse<Item>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Item> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        currentItem = apiResponse.getData();
                        displayItemDetails();
                    } else {
                        showError(apiResponse.getMessage());
                    }
                } else {
                    showError("Gagal memuat detail barang");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Item>> call, Throwable t) {
                showError("Error: " + t.getMessage());
            }
        });
    }

    private void displayItemDetails() {
        if (currentItem == null) return;

        tvItemName.setText(currentItem.getName());
        tvItemDescription.setText(currentItem.getDescription());
        tvItemLocation.setText(currentItem.getLocation());
        tvItemDate.setText(currentItem.getDateTime());
        tvReporter.setText(currentItem.getReporterName());

        // Set type with color
        if ("lost".equals(currentItem.getType())) {
            tvItemType.setText("HILANG");
            tvItemType.setBackgroundColor(getColor(android.R.color.holo_red_dark));
        } else {
            tvItemType.setText("DITEMUKAN");
            tvItemType.setBackgroundColor(getColor(android.R.color.holo_green_dark));
        }

        // Load image
        if (currentItem.getImageUrl() != null && !currentItem.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(currentItem.getImageUrl())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_gallery)
                    .into(ivItemImage);
        } else {
            ivItemImage.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        // Hide claim button if user is the reporter
        if (sessionManager.getUser().getId().equals(currentItem.getReporterId())) {
            btnClaim.setText("Ini adalah laporan Anda");
            btnClaim.setEnabled(false);
        }
    }

    private void setupClickListeners() {
        btnClaim.setOnClickListener(v -> {
            if (currentItem != null) {
                Intent intent = new Intent(this, ClaimFormActivity.class);
                intent.putExtra("item_id", currentItem.getId());
                intent.putExtra("item_name", currentItem.getName());
                startActivity(intent);
            }
        });

        btnContact.setOnClickListener(v -> {
            if (currentItem != null && currentItem.getReporterPhone() != null) {
                // Open WhatsApp or phone dialer
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(android.net.Uri.parse("https://wa.me/" + currentItem.getReporterPhone()));
                startActivity(intent);
            }
        });
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
