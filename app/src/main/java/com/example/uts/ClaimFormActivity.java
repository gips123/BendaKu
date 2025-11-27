package com.example.uts;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bendaku.api.ApiClient;
import com.example.bendaku.api.ApiService;
import com.example.bendaku.model.StrapiClaim;
import com.example.bendaku.model.StrapiResponse;
import com.example.bendaku.model.StrapiUploadResponse;
import com.example.bendaku.utils.SessionManager;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClaimFormActivity extends AppCompatActivity {

    private TextView tvItemName;
    private View tvProofImageHint, tvKtmImageHint;
    private ImageView ivProofPreview, ivKtmPreview;
    private TextInputEditText etClaimDescription;
    private TextInputEditText etClaimerPhone;
    private TextInputEditText etClaimerUsername;
    private MaterialButton btnSubmitClaim;

    private String itemId, itemName;
    private Uri selectedProofImageUri;
    private Uri selectedKtmImageUri;
    private SessionManager sessionManager;
    private ApiService apiService;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ActivityResultLauncher<Intent> ktmImagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claim_form);

        initViews();
        initServices();
        getIntentData();
        setupImagePicker();
        setupClickListeners();
    }

    private void initViews() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tvItemName = findViewById(R.id.tvItemName);
        tvProofImageHint = findViewById(R.id.tvProofImageHint);
        ivProofPreview = findViewById(R.id.ivProofPreview);
        ivKtmPreview = findViewById(R.id.ivKtmPreview);
        tvKtmImageHint = findViewById(R.id.tvKtmImageHint);
        etClaimDescription = findViewById(R.id.etClaimDescription);
        etClaimerPhone = findViewById(R.id.etClaimerPhone);
        etClaimerUsername = findViewById(R.id.etClaimerUsername);
        btnSubmitClaim = findViewById(R.id.btnSubmitClaim);
    }

    private void initServices() {
        ApiClient.init(this);
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
        findViewById(R.id.cardKtmImage).setOnClickListener(v -> selectKtmImage());
        btnSubmitClaim.setOnClickListener(v -> submitClaim());
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedProofImageUri = result.getData().getData();
                        if (selectedProofImageUri != null) {
                            ivProofPreview.setImageURI(selectedProofImageUri);
                            ivProofPreview.setVisibility(View.VISIBLE);
                            tvProofImageHint.setVisibility(View.GONE);
                        }
                    }
                }
        );

        ktmImagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedKtmImageUri = result.getData().getData();
                        if (selectedKtmImageUri != null) {
                            ivKtmPreview.setImageURI(selectedKtmImageUri);
                            ivKtmPreview.setVisibility(View.VISIBLE);
                            tvKtmImageHint.setVisibility(View.GONE);
                        }
                    }
                }
        );
    }

    private void selectProofImage() {
        ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .createIntent(intent -> {
                    imagePickerLauncher.launch(intent);
                    return null;
                });
    }

    private void selectKtmImage() {
        ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .createIntent(intent -> {
                    ktmImagePickerLauncher.launch(intent);
                    return null;
                });
    }

    private void submitClaim() {
        String description = etClaimDescription.getText().toString().trim();
        String claimerPhone = etClaimerPhone.getText().toString().trim();
        String claimerUsername = etClaimerUsername.getText().toString().trim();

        if (!validateInput(description, claimerPhone, claimerUsername)) {
            return;
        }

        if (selectedProofImageUri == null) {
            Toast.makeText(this, "Silakan pilih foto bukti", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedKtmImageUri == null) {
            Toast.makeText(this, "Silakan upload foto KTM/identitas", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);
        uploadProofImage(description, claimerPhone, claimerUsername);
    }

    private void uploadProofImage(String description, String claimerPhone, String claimerUsername) {
        uploadImage(selectedProofImageUri, "proof_image.jpg", "Gagal mengupload foto bukti", proofImageId ->
                uploadKtmImage(description, claimerPhone, claimerUsername, proofImageId)
        );
    }

    private void uploadKtmImage(String description, String claimerPhone, String claimerUsername, Integer proofImageId) {
        uploadImage(selectedKtmImageUri, "ktm_image.jpg", "Gagal mengupload foto KTM", ktmImageId ->
                createClaim(description, claimerPhone, claimerUsername, proofImageId, ktmImageId)
        );
    }

    private interface UploadCallback {
        void onSuccess(Integer imageId);
    }

    private void uploadImage(Uri uri, String defaultFileName, String errorContext, UploadCallback callback) {
        if (uri == null) {
            setLoading(false);
            Toast.makeText(this, errorContext, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String fileName = defaultFileName;
            String scheme = uri.getScheme();
            if ("content".equals(scheme)) {
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        fileName = cursor.getString(nameIndex);
                    }
                    cursor.close();
                }
            } else if ("file".equals(scheme) && uri.getPath() != null) {
                fileName = new File(uri.getPath()).getName();
            }

            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) {
                setLoading(false);
                Toast.makeText(this, "Tidak dapat membaca file " + fileName, Toast.LENGTH_SHORT).show();
                return;
            }

            File tempFile = new File(getCacheDir(), fileName);
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.close();
            inputStream.close();

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), tempFile);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("files", fileName, requestFile);

            Call<List<StrapiUploadResponse>> uploadCall = apiService.uploadFile(filePart);
            uploadCall.enqueue(new Callback<List<StrapiUploadResponse>>() {
                @Override
                public void onResponse(Call<List<StrapiUploadResponse>> call, Response<List<StrapiUploadResponse>> response) {
                    if (tempFile.exists()) {
                        tempFile.delete();
                    }

                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                        Integer imageId = response.body().get(0).getId();
                        callback.onSuccess(imageId);
                    } else {
                        setLoading(false);
                        String errorMsg = errorContext;
                        if (response.code() == 401) {
                            errorMsg = "Sesi login telah berakhir. Silakan login ulang.";
                        }
                        Toast.makeText(ClaimFormActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<StrapiUploadResponse>> call, Throwable t) {
                    if (tempFile.exists()) {
                        tempFile.delete();
                    }

                    setLoading(false);
                    String errorMsg = errorContext + ": " + t.getMessage();
                    if (t.getMessage() != null && t.getMessage().contains("Failed to connect")) {
                        errorMsg = "Tidak dapat terhubung ke server. Pastikan backend Strapi berjalan.";
                    }
                    Toast.makeText(ClaimFormActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            setLoading(false);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void createClaim(String description, String claimerPhone, String claimerUsername,
                             Integer proofImageId, Integer ktmImageId) {
        String claimerName = sessionManager.getUser() != null ? sessionManager.getUser().getFullName() : "";
        if (TextUtils.isEmpty(claimerName) && sessionManager.getUser() != null) {
            claimerName = sessionManager.getUser().getFullName();
        }

        Integer itemIdInt = null;
        try {
            if (itemId != null && !itemId.isEmpty()) {
                itemIdInt = Integer.parseInt(itemId);
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Item ID tidak valid", Toast.LENGTH_SHORT).show();
            setLoading(false);
            return;
        }

        ApiService.ClaimRequest.ClaimData claimData = new ApiService.ClaimRequest.ClaimData(
                claimerName,
                claimerPhone,
                description,
                "pending",
                null,
                proofImageId,
                ktmImageId,
                claimerUsername,
                itemIdInt,
                null
        );

        ApiService.ClaimRequest request = new ApiService.ClaimRequest(claimData);

        Call<StrapiResponse<StrapiClaim>> call = apiService.createClaim(request);
        call.enqueue(new Callback<StrapiResponse<StrapiClaim>>() {
            @Override
            public void onResponse(Call<StrapiResponse<StrapiClaim>> call, Response<StrapiResponse<StrapiClaim>> response) {
                setLoading(false);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(ClaimFormActivity.this, "Klaim berhasil dikirim", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                    String errorMsg = "Gagal mengirim klaim";
                    if (response.body() != null && response.body().getError() != null) {
                        errorMsg = response.body().getError().getMessage();
                    }
                    Toast.makeText(ClaimFormActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<StrapiResponse<StrapiClaim>> call, Throwable t) {
                setLoading(false);
                String errorMsg = "Error: " + t.getMessage();
                if (t.getMessage() != null && t.getMessage().contains("Failed to connect")) {
                    errorMsg = "Tidak dapat terhubung ke server. Pastikan backend Strapi berjalan.";
                }
                Toast.makeText(ClaimFormActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean validateInput(String description, String claimerPhone, String claimerUsername) {
        if (TextUtils.isEmpty(description)) {
            etClaimDescription.setError("Deskripsi klaim tidak boleh kosong");
            etClaimDescription.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(claimerPhone)) {
            etClaimerPhone.setError("Nomor telepon klaimer harus diisi");
            etClaimerPhone.requestFocus();
            return false;
        }

        if (claimerPhone.length() < 8) {
            etClaimerPhone.setError("Nomor telepon tidak valid");
            etClaimerPhone.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(claimerUsername)) {
            etClaimerUsername.setError("Username klaimer harus diisi");
            etClaimerUsername.requestFocus();
            return false;
        }

        return true;
    }

    private void setLoading(boolean loading) {
        btnSubmitClaim.setEnabled(!loading);
        btnSubmitClaim.setText(loading ? "Mengirim..." : getString(R.string.submit_claim));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
