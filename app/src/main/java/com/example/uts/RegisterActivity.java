package com.example.uts;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bendaku.api.ApiClient;
import com.example.bendaku.api.ApiService;
import com.example.bendaku.model.ApiResponse;
import com.example.bendaku.model.User;
import com.example.bendaku.utils.SessionManager;
import com.example.bendaku.utils.DummyDataHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etFullName, etEmail, etStudentId, etPhone, etPassword, etConfirmPassword;
    private MaterialButton btnRegister;
    private SessionManager sessionManager;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        initServices();
        setupClickListeners();
    }

    private void initViews() {
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etStudentId = findViewById(R.id.etStudentId);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
    }

    private void initServices() {
        sessionManager = new SessionManager(this);
        apiService = ApiClient.getApiService();
    }

    private void setupClickListeners() {
        btnRegister.setOnClickListener(v -> performRegister());

        findViewById(R.id.tvLogin).setOnClickListener(v -> {
            finish();
        });
    }

    private void performRegister() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String studentId = etStudentId.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (!validateInput(fullName, email, studentId, phone, password, confirmPassword)) {
            return;
        }

        setLoading(true);

        // Menggunakan dummy data untuk testing (tanpa backend)
        // Jalankan di background thread untuk simulasi network call
        new Thread(() -> {
            ApiResponse<User> response = DummyDataHelper.simulateRegister(fullName, email, password, phone, studentId);

            // Kembali ke main thread untuk update UI
            runOnUiThread(() -> {
                setLoading(false);

                if (response.isSuccess()) {
                    Toast.makeText(RegisterActivity.this, "Registrasi berhasil! Silakan login dengan password: password123", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }).start();

        // Original API call code (commented out until backend is ready)
        /*
        ApiService.RegisterRequest request = new ApiService.RegisterRequest(fullName, email, password, phone, studentId);
        Call<ApiResponse<User>> call = apiService.register(request);

        call.enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                setLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<User> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        Toast.makeText(RegisterActivity.this, "Registrasi berhasil! Silakan login.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Registrasi gagal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                setLoading(false);
                Toast.makeText(RegisterActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        */
    }

    private boolean validateInput(String fullName, String email, String studentId, String phone, String password, String confirmPassword) {
        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Nama lengkap tidak boleh kosong");
            etFullName.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email tidak boleh kosong");
            etEmail.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Format email tidak valid");
            etEmail.requestFocus();
            return false;
        }

        if (!email.endsWith("@upnvj.ac.id")) {
            etEmail.setError(getString(R.string.email_validation));
            etEmail.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(studentId)) {
            etStudentId.setError("NIM/NIP tidak boleh kosong");
            etStudentId.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("No. telepon tidak boleh kosong");
            etPhone.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password tidak boleh kosong");
            etPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            etPassword.setError("Password minimal 6 karakter");
            etPassword.requestFocus();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Konfirmasi password tidak sama");
            etConfirmPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void setLoading(boolean loading) {
        btnRegister.setEnabled(!loading);
        btnRegister.setText(loading ? "Loading..." : getString(R.string.register));
    }
}
