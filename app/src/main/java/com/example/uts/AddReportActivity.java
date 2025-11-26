package com.example.uts;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bendaku.api.ApiClient;
import com.example.bendaku.api.ApiService;
import com.example.bendaku.model.StrapiItem;
import com.example.bendaku.model.StrapiResponse;
import com.example.bendaku.model.StrapiUploadResponse;
import com.example.bendaku.utils.SessionManager;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;

import android.database.Cursor;
import android.provider.OpenableColumns;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddReportActivity extends AppCompatActivity {

    private MaterialButtonToggleGroup toggleGroupType;
    private MaterialButton btnLost, btnFound, btnSubmit;
    private ImageView ivPreview;
    private View uploadPlaceholder, photoUploadArea;
    private TextInputEditText etItemName, etDescription, etLocation, etName, etPhone;

    private String reportType = "lost";
    private Uri selectedImageUri;
    private SessionManager sessionManager;
    private ApiService apiService;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_report);

        initViews();
        initServices();
        setupClickListeners();
        setupImagePicker();
        setReportType("lost");
    }

    private void initViews() {
        // Toolbar setup
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Modern toggle group and buttons
        toggleGroupType = findViewById(R.id.toggleGroupType);
        btnLost = findViewById(R.id.btnLost);
        btnFound = findViewById(R.id.btnFound);
        btnSubmit = findViewById(R.id.btnSubmit);

        // Photo upload elements
        ivPreview = findViewById(R.id.ivPreview);
        uploadPlaceholder = findViewById(R.id.uploadPlaceholder);
        photoUploadArea = findViewById(R.id.photoUploadArea);

        // Form inputs - updated for new layout
        etItemName = findViewById(R.id.etItemName);
        etDescription = findViewById(R.id.etDescription);
        etLocation = findViewById(R.id.etLocation);
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
    }

    private void initServices() {
        ApiClient.init(this);
        sessionManager = new SessionManager(this);
        apiService = ApiClient.getApiService();
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            ivPreview.setImageURI(selectedImageUri);
                            ivPreview.setVisibility(View.VISIBLE);
                            uploadPlaceholder.setVisibility(View.GONE);
                        }
                    }
                }
        );
    }

    private void setupClickListeners() {
        // Toggle group listener for modern type selection
        toggleGroupType.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btnLost) {
                    setReportType("lost");
                } else if (checkedId == R.id.btnFound) {
                    setReportType("found");
                }
            }
        });

        // Photo upload area click
        photoUploadArea.setOnClickListener(v -> selectImage());

        // Submit button
        btnSubmit.setOnClickListener(v -> submitReport());

        // Toolbar back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setReportType(String type) {
        this.reportType = type;

        // Update button selection
        if ("lost".equals(type)) {
            toggleGroupType.check(R.id.btnLost);
        } else {
            toggleGroupType.check(R.id.btnFound);
        }
    }

    private void selectImage() {
        ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .createIntent(intent -> {
                    imagePickerLauncher.launch(intent);
                    return null;
                });
    }

    private void submitReport() {
        String itemName = etItemName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(itemName)) {
            etItemName.setError("Nama barang harus diisi");
            etItemName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(description)) {
            etDescription.setError("Deskripsi harus diisi");
            etDescription.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(location)) {
            etLocation.setError("Lokasi harus diisi");
            etLocation.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(name)) {
            etName.setError("Nama lengkap harus diisi");
            etName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Nomor telepon harus diisi");
            etPhone.requestFocus();
            return;
        }

        // Show loading state
        btnSubmit.setEnabled(false);
        btnSubmit.setText("Mengirim...");

        // Check if user is logged in
        if (!sessionManager.isLoggedIn()) {
            btnSubmit.setEnabled(true);
            btnSubmit.setText("Kirim Laporan");
            Toast.makeText(this, "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get current date and time in ISO 8601 format
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        String dateTime = sdf.format(calendar.getTime());

        // If image is selected, upload it first, then create item
        if (selectedImageUri != null) {
            uploadImageAndCreateItem(itemName, description, location, dateTime, name, phone);
        } else {
            // Create item without image
            createItem(itemName, description, location, dateTime, name, phone, null);
        }
    }

    private void uploadImageAndCreateItem(String itemName, String description, String location,
                                         String dateTime, String name, String phone) {
        try {
            // Get file name from URI
            String fileName = "image.jpg";
            if (selectedImageUri.getScheme().equals("content")) {
                Cursor cursor = getContentResolver().query(selectedImageUri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        fileName = cursor.getString(nameIndex);
                    }
                    cursor.close();
                }
            } else if (selectedImageUri.getScheme().equals("file")) {
                fileName = new File(selectedImageUri.getPath()).getName();
            }

            // Read file from URI
            InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
            if (inputStream == null) {
                btnSubmit.setEnabled(true);
                btnSubmit.setText("Kirim Laporan");
                Toast.makeText(this, "Tidak dapat membaca file gambar", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create temporary file
            File tempFile = new File(getCacheDir(), fileName);
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.close();
            inputStream.close();

            // Create request body for file
            RequestBody requestFile = RequestBody.create(
                    MediaType.parse("image/*"),
                    tempFile
            );

            // Create multipart part - Strapi expects "files" as the key
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("files", fileName, requestFile);

            // Upload file
            Call<List<StrapiUploadResponse>> uploadCall = apiService.uploadFile(filePart);
            uploadCall.enqueue(new Callback<List<StrapiUploadResponse>>() {
                @Override
                public void onResponse(Call<List<StrapiUploadResponse>> call, Response<List<StrapiUploadResponse>> response) {
                    // Clean up temp file
                    if (tempFile.exists()) {
                        tempFile.delete();
                    }

                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                        // Get uploaded file ID
                        StrapiUploadResponse uploadResponse = response.body().get(0);
                        Integer imageId = uploadResponse.getId();

                        // Create item with image ID
                        createItem(itemName, description, location, dateTime, name, phone, imageId);
                    } else {
                        btnSubmit.setEnabled(true);
                        btnSubmit.setText("Kirim Laporan");
                        String errorMsg = "Gagal mengupload gambar";
                        if (response.code() == 401) {
                            errorMsg = "Sesi login telah berakhir. Silakan login ulang.";
                        }
                        Toast.makeText(AddReportActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<StrapiUploadResponse>> call, Throwable t) {
                    // Clean up temp file
                    if (tempFile.exists()) {
                        tempFile.delete();
                    }

                    btnSubmit.setEnabled(true);
                    btnSubmit.setText("Kirim Laporan");
                    String errorMsg = "Error upload: " + t.getMessage();
                    if (t.getMessage() != null && t.getMessage().contains("Failed to connect")) {
                        errorMsg = "Tidak dapat terhubung ke server. Pastikan backend Strapi berjalan.";
                    }
                    Toast.makeText(AddReportActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            btnSubmit.setEnabled(true);
            btnSubmit.setText("Kirim Laporan");
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            }
    }

    private void createItem(String itemName, String description, String location,
                           String dateTime, String name, String phone, Integer imageId) {
        // Create item request
        ApiService.ItemRequest.ItemData itemData = new ApiService.ItemRequest.ItemData(
                itemName,
                description,
                location,
                dateTime,
                reportType, // "lost" or "found"
                "open", // statusItem: "open", "claimed", "resolved"
                name,
                phone,
                imageId // ID dari upload
        );

        ApiService.ItemRequest request = new ApiService.ItemRequest(itemData);

        Call<StrapiResponse<StrapiItem>> call = apiService.createItem(request);
        call.enqueue(new Callback<StrapiResponse<StrapiItem>>() {
            @Override
            public void onResponse(Call<StrapiResponse<StrapiItem>> call, Response<StrapiResponse<StrapiItem>> response) {
                btnSubmit.setEnabled(true);
                btnSubmit.setText("Kirim Laporan");

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                String reportTypeText = reportType.equals("lost") ? "barang hilang" : "barang ditemukan";
                Toast.makeText(AddReportActivity.this,
                    "Laporan " + reportTypeText + " berhasil dikirim!\n" + itemName,
                    Toast.LENGTH_LONG).show();

                // Close activity and return to main
                setResult(RESULT_OK);
                finish();
                } else {
                    String errorMsg = "Gagal mengirim laporan";
                    if (response.body() != null && response.body().getError() != null) {
                        errorMsg = response.body().getError().getMessage();
                    }
                    Toast.makeText(AddReportActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<StrapiResponse<StrapiItem>> call, Throwable t) {
                btnSubmit.setEnabled(true);
                btnSubmit.setText("Kirim Laporan");
                String errorMsg = "Error: " + t.getMessage();
                if (t.getMessage() != null && t.getMessage().contains("Failed to connect")) {
                    errorMsg = "Tidak dapat terhubung ke server. Pastikan backend Strapi berjalan.";
                }
                Toast.makeText(AddReportActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }
}
