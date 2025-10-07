package com.example.uts;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bendaku.api.ApiClient;
import com.example.bendaku.api.ApiService;
import com.example.bendaku.model.ApiResponse;
import com.example.bendaku.model.Claim;
import com.example.bendaku.utils.SessionManager;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClaimFormActivity extends AppCompatActivity {

    private TextView tvItemName, tvProofImageHint;
    private ImageView ivProofPreview;
    private TextInputEditText etClaimDescription;
    private MaterialButton btnSubmitClaim;

    private String itemId, itemName;
    private Uri selectedProofImageUri;
    private SessionManager sessionManager;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claim_form);

        initViews();
        initServices();
        getIntentData();
        setupClickListeners();
    }

    private void initViews() {
        tvItemName = findViewById(R.id.tvItemName);
        tvProofImageHint = findViewById(R.id.tvProofImageHint);
        ivProofPreview = findViewById(R.id.ivProofPreview);
        etClaimDescription = findViewById(R.id.etClaimDescription);
        btnSubmitClaim = findViewById(R.id.btnSubmitClaim);
    }

    private void initServices() {
        sessionManager = new SessionManager(this);
        apiService = ApiClient.getApiService();
    }

    private void getIntentData() {
        itemId = getIntent().getStringExtra("item_id");
        itemName = getIntent().getStringExtra("item_name");

        if (itemId == null || itemName == null) {
            Toast.makeText(this, "Data item tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvItemName.setText(itemName);
    }

    private void setupClickListeners() {
        findViewById(R.id.cardProofImage).setOnClickListener(v -> selectProofImage());
        btnSubmitClaim.setOnClickListener(v -> submitClaim());
    }

    private void selectProofImage() {
        ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            selectedProofImageUri = data.getData();
            ivProofPreview.setImageURI(selectedProofImageUri);
            tvProofImageHint.setVisibility(View.GONE);
        }
    }

    private void submitClaim() {
        String description = etClaimDescription.getText().toString().trim();

        if (!validateInput(description)) {
            return;
        }

        if (selectedProofImageUri == null) {
            Toast.makeText(this, "Silakan pilih foto bukti", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        // Prepare multipart data
        File imageFile = new File(selectedProofImageUri.getPath());
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("proofImage", imageFile.getName(), requestFile);

        RequestBody itemIdBody = RequestBody.create(MediaType.parse("text/plain"), itemId);
        RequestBody claimerIdBody = RequestBody.create(MediaType.parse("text/plain"), sessionManager.getUser().getId());
        RequestBody claimerNameBody = RequestBody.create(MediaType.parse("text/plain"), sessionManager.getUser().getFullName());
        RequestBody claimerPhoneBody = RequestBody.create(MediaType.parse("text/plain"), sessionManager.getUser().getPhone());
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"), description);

        Call<ApiResponse<Claim>> call = apiService.createClaim(
                itemIdBody, claimerIdBody, claimerNameBody, claimerPhoneBody, descriptionBody, imagePart
        );

        call.enqueue(new Callback<ApiResponse<Claim>>() {
            @Override
            public void onResponse(Call<ApiResponse<Claim>> call, Response<ApiResponse<Claim>> response) {
                setLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Claim> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        Toast.makeText(ClaimFormActivity.this, getString(R.string.claim_success), Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(ClaimFormActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ClaimFormActivity.this, "Gagal mengirim klaim", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Claim>> call, Throwable t) {
                setLoading(false);
                Toast.makeText(ClaimFormActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInput(String description) {
        if (TextUtils.isEmpty(description)) {
            etClaimDescription.setError("Deskripsi klaim tidak boleh kosong");
            etClaimDescription.requestFocus();
            return false;
        }

        return true;
    }

    private void setLoading(boolean loading) {
        btnSubmitClaim.setEnabled(!loading);
        btnSubmitClaim.setText(loading ? "Mengirim..." : getString(R.string.submit_claim));
    }
}
