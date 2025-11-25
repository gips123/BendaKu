package com.example.uts;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.bendaku.api.ApiClient;
import com.example.bendaku.api.BendaKuApiService;
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
    private BendaKuApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        initViews();
        initServices();
        getItemId();
        //loadItemDetail();
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
        apiService = ApiClient.getInstance().getApiService();
    }

    private void getItemId() {
        // Try to get the item object first
        currentItem = (Item) getIntent().getSerializableExtra("item");

        if (currentItem != null) {
            // We have the item data, display it directly
            displayItemDetails();
        } else {
            // Fallback to old method with item_id for API call
            itemId = getIntent().getStringExtra("item_id");
            if (itemId == null) {
                Toast.makeText(this, "Item tidak ditemukan", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                loadItemDetail();
            }
        }
    }

    private void loadItemDetail() {
        if (itemId == null) {
            Toast.makeText(this, "Item ID tidak valid", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        try {
            int id = Integer.parseInt(itemId);
            Call<ApiResponse<Item>> call = apiService.getItem(id);
            
            call.enqueue(new Callback<ApiResponse<Item>>() {
                @Override
                public void onResponse(Call<ApiResponse<Item>> call, Response<ApiResponse<Item>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse<Item> apiResponse = response.body();
                        if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                            currentItem = apiResponse.getData();
                            displayItemDetails();
                        } else {
                            showError(apiResponse.getMessage());
                            finish();
                        }
                    } else {
                        showError("Gagal memuat detail item");
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Item>> call, Throwable t) {
                    showError("Error: " + t.getMessage());
                    finish();
                }
            });
        } catch (NumberFormatException e) {
            showError("Item ID tidak valid");
            finish();
        }
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
            tvItemType.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
        } else {
            tvItemType.setText("DITEMUKAN");
            tvItemType.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
        }

        // Load image
        if (currentItem.getImageUrl() != null && !currentItem.getImageUrl().isEmpty()) {
            String imageUrl = currentItem.getImageUrl();

            // Check if it's a drawable resource reference
            if (imageUrl.startsWith("drawable://")) {
                String drawableName = imageUrl.replace("drawable://", "");
                int drawableResId = getDrawableResourceId(drawableName);

                if (drawableResId != 0) {
                    ivItemImage.setImageResource(drawableResId);
                } else {
                    // Fallback to default image
                    ivItemImage.setImageResource(R.drawable.ic_item);
                }
            } else {
                // Load from URL using Glide (for future API integration)
                Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_item)
                        .error(R.drawable.ic_item)
                        .into(ivItemImage);
            }
        } else {
            // Default image when no image is available
            ivItemImage.setImageResource(R.drawable.ic_item);
        }

        // Hide claim button if user is the reporter
        if (currentItem.getReporterId() != null && 
            sessionManager.getUser().getId() == currentItem.getReporterId()) {
            btnClaim.setText("Ini adalah laporan Anda");
            btnClaim.setEnabled(false);
        }
    }

    private void setupClickListeners() {
        btnClaim.setOnClickListener(v -> {
            if (currentItem != null) {
                Intent intent = new Intent(this, ClaimFormActivity.class);
                intent.putExtra("item_id", String.valueOf(currentItem.getId()));
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

    private int getDrawableResourceId(String drawableName) {
        // Map drawable names to resource IDs
        switch (drawableName) {
            case "dompet":
                return R.drawable.dompet;
            case "hoodie":
                return R.drawable.hoodie;
            case "kunci":
                return R.drawable.kunci;
            case "powerbank":
                return R.drawable.powerbank;
            case "samsung":
                return R.drawable.samsung;
            case "sus":
                return R.drawable.sus;
            default:
                return R.drawable.ic_item; // Default fallback
        }
    }
}
