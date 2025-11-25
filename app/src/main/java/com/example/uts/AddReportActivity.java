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
import com.example.bendaku.api.BendaKuApiService;
import com.example.bendaku.model.ApiResponse;
import com.example.bendaku.model.Item;
import com.example.bendaku.model.User;
import com.example.bendaku.repository.ItemRepository;
import com.example.bendaku.utils.SessionManager;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
    private ItemRepository itemRepository;

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
        sessionManager = new SessionManager(this);
        itemRepository = new ItemRepository();
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

        // Get current date time
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
        String dateTime = sdf.format(Calendar.getInstance().getTime());

        // Get user info
        User currentUser = sessionManager.getUser();
        Integer reporterId = currentUser != null ? currentUser.getId() : null;
        String reporterName = name;
        String reporterPhone = phone;

        // Prepare image file
        File imageFile = null;
        if (selectedImageUri != null) {
            try {
                // Get file from URI
                InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                if (inputStream != null) {
                    // Create temporary file
                    File tempFile = new File(getCacheDir(), "temp_image_" + System.currentTimeMillis() + ".jpg");
                    FileOutputStream outputStream = new FileOutputStream(tempFile);
                    
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    
                    inputStream.close();
                    outputStream.close();
                    imageFile = tempFile;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error loading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        // Create item via API
        itemRepository.createItem(itemName, description, location, dateTime, reportType,
            reporterId, reporterName, reporterPhone, imageFile,
            new ItemRepository.ItemCallback() {
                @Override
                public void onSuccess(Item item) {
                    btnSubmit.setEnabled(true);
                    btnSubmit.setText("Kirim Laporan");
                    
                    String reportTypeText = reportType.equals("lost") ? "barang hilang" : "barang ditemukan";
                    Toast.makeText(AddReportActivity.this,
                        "Laporan " + reportTypeText + " berhasil dikirim!",
                        Toast.LENGTH_LONG).show();

                    setResult(RESULT_OK);
                    finish();
                }

                @Override
                public void onError(String error) {
                    btnSubmit.setEnabled(true);
                    btnSubmit.setText("Kirim Laporan");
                    Toast.makeText(AddReportActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                }
            });
    }
}
