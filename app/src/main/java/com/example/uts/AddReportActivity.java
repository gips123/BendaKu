package com.example.uts;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bendaku.api.ApiClient;
import com.example.bendaku.api.ApiService;
import com.example.bendaku.model.ApiResponse;
import com.example.bendaku.model.Item;
import com.example.bendaku.utils.SessionManager;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
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

    private MaterialButton btnLost, btnFound, btnSubmit;
    private ImageView ivPreview;
    private View uploadPlaceholder;
    private TextInputEditText etItemName, etDescription, etLocation, etDateTime;

    private String reportType = "lost";
    private Uri selectedImageUri;
    private SessionManager sessionManager;
    private ApiService apiService;
    private Calendar selectedDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_report);

        initViews();
        initServices();
        setupClickListeners();
        setReportType("lost");
    }

    private void initViews() {
        btnLost = findViewById(R.id.btnLost);
        btnFound = findViewById(R.id.btnFound);
        btnSubmit = findViewById(R.id.btnSubmit);
        ivPreview = findViewById(R.id.ivPreview);
        uploadPlaceholder = findViewById(R.id.uploadPlaceholder);
        etItemName = findViewById(R.id.etItemName);
        etDescription = findViewById(R.id.etDescription);
        etLocation = findViewById(R.id.etLocation);
        etDateTime = findViewById(R.id.etDateTime);

        selectedDateTime = Calendar.getInstance();
    }

    private void initServices() {
        sessionManager = new SessionManager(this);
        apiService = ApiClient.getApiService();
    }

    private void setupClickListeners() {
        btnLost.setOnClickListener(v -> setReportType("lost"));
        btnFound.setOnClickListener(v -> setReportType("found"));

        findViewById(R.id.cardImage).setOnClickListener(v -> selectImage());

        etDateTime.setOnClickListener(v -> showDateTimePicker());

        btnSubmit.setOnClickListener(v -> submitReport());
    }

    private void setReportType(String type) {
        this.reportType = type;

        if ("lost".equals(type)) {
            btnLost.setStrokeWidth(4);
            btnFound.setStrokeWidth(1);
        } else {
            btnLost.setStrokeWidth(1);
            btnFound.setStrokeWidth(4);
        }
    }

    private void selectImage() {
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
            selectedImageUri = data.getData();
            ivPreview.setImageURI(selectedImageUri);
            uploadPlaceholder.setVisibility(View.GONE);
        }
    }

    private void showDateTimePicker() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDateTime.set(Calendar.YEAR, year);
                    selectedDateTime.set(Calendar.MONTH, month);
                    selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    showTimePicker();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedDateTime.set(Calendar.MINUTE, minute);

                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
                    etDateTime.setText(sdf.format(selectedDateTime.getTime()));
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        );

        timePickerDialog.show();
    }

    private void submitReport() {
        String itemName = etItemName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String dateTime = etDateTime.getText().toString().trim();

        if (!validateInput(itemName, description, location, dateTime)) {
            return;
        }

        if (selectedImageUri == null) {
            Toast.makeText(this, "Silakan pilih foto barang", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        // Prepare multipart data
        File imageFile = new File(selectedImageUri.getPath());
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", imageFile.getName(), requestFile);

        RequestBody nameBody = RequestBody.create(MediaType.parse("text/plain"), itemName);
        RequestBody descBody = RequestBody.create(MediaType.parse("text/plain"), description);
        RequestBody locationBody = RequestBody.create(MediaType.parse("text/plain"), location);
        RequestBody dateTimeBody = RequestBody.create(MediaType.parse("text/plain"), dateTime);
        RequestBody typeBody = RequestBody.create(MediaType.parse("text/plain"), reportType);
        RequestBody reporterIdBody = RequestBody.create(MediaType.parse("text/plain"), sessionManager.getUser().getId());
        RequestBody reporterNameBody = RequestBody.create(MediaType.parse("text/plain"), sessionManager.getUser().getFullName());
        RequestBody reporterPhoneBody = RequestBody.create(MediaType.parse("text/plain"), sessionManager.getUser().getPhone());

        Call<ApiResponse<Item>> call = apiService.createItem(
                nameBody, descBody, locationBody, dateTimeBody, typeBody,
                reporterIdBody, reporterNameBody, reporterPhoneBody, imagePart
        );

        call.enqueue(new Callback<ApiResponse<Item>>() {
            @Override
            public void onResponse(Call<ApiResponse<Item>> call, Response<ApiResponse<Item>> response) {
                setLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Item> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        Toast.makeText(AddReportActivity.this, getString(R.string.report_success), Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddReportActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddReportActivity.this, "Gagal mengirim laporan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Item>> call, Throwable t) {
                setLoading(false);
                Toast.makeText(AddReportActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInput(String itemName, String description, String location, String dateTime) {
        if (TextUtils.isEmpty(itemName)) {
            etItemName.setError("Nama barang tidak boleh kosong");
            etItemName.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(description)) {
            etDescription.setError("Deskripsi tidak boleh kosong");
            etDescription.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(location)) {
            etLocation.setError("Lokasi tidak boleh kosong");
            etLocation.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(dateTime)) {
            etDateTime.setError("Tanggal & waktu tidak boleh kosong");
            etDateTime.requestFocus();
            return false;
        }

        return true;
    }

    private void setLoading(boolean loading) {
        btnSubmit.setEnabled(!loading);
        btnSubmit.setText(loading ? "Mengirim..." : getString(R.string.submit));
    }
}
