package com.example.uts;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.bendaku.model.Item;
import com.example.bendaku.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

public class ItemDetailActivity extends AppCompatActivity {

    private ImageView ivItemImage;
    private TextView tvItemType, tvItemName, tvItemDescription, tvItemLocation, tvItemDate;
    private TextView tvReporter;
    private MaterialButton btnClaim, btnContact;

    private Item currentItem;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        initViews();
        initServices();
        getItemId();
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
    }

    private void getItemId() {
        currentItem = (Item) getIntent().getSerializableExtra("item");

        if (currentItem != null) {
            displayItemDetails();
        } else {
            Toast.makeText(this, "Item tidak ditemukan", Toast.LENGTH_SHORT).show();
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

        if ("lost".equals(currentItem.getType())) {
            tvItemType.setText("HILANG");
            tvItemType.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
        } else {
            tvItemType.setText("DITEMUKAN");
            tvItemType.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
        }

        if (currentItem.getImageUrl() != null && !currentItem.getImageUrl().isEmpty()) {
            String imageUrl = currentItem.getImageUrl();

            if (imageUrl.startsWith("drawable://")) {
                String drawableName = imageUrl.replace("drawable://", "");
                int drawableResId = getDrawableResourceId(drawableName);

                if (drawableResId != 0) {
                    ivItemImage.setImageResource(drawableResId);
                } else {
                    ivItemImage.setImageResource(R.drawable.ic_item);
                }
            } else {
                Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_item)
                        .error(R.drawable.ic_item)
                        .into(ivItemImage);
            }
        } else {
            ivItemImage.setImageResource(R.drawable.ic_item);
        }

        if (sessionManager.getUser() != null && sessionManager.getUser().getId().equals(currentItem.getReporterId())) {
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
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(android.net.Uri.parse("https://wa.me/" + currentItem.getReporterPhone()));
                startActivity(intent);
            }
        });
    }

    private int getDrawableResourceId(String drawableName) {
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
                return R.drawable.ic_item;
        }
    }
}
