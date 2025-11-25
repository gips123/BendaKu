package com.example.uts;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bendaku.api.ApiClient;
import com.example.bendaku.api.ApiService;
import com.example.bendaku.model.StrapiAuthResponse;
import com.example.bendaku.model.User;
import com.example.bendaku.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private SessionManager sessionManager;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        initServices();
        checkSession();
        setupClickListeners();
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
    }

    private void initServices() {
        ApiClient.init(this);
        sessionManager = new SessionManager(this);
        apiService = ApiClient.getApiService();
    }

    private void checkSession() {
        if (sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> performLogin());

        findViewById(R.id.tvRegister).setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    private void performLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (!validateInput(email, password)) {
            return;
        }

        setLoading(true);

        // Strapi login API call
        ApiService.LoginRequest request = new ApiService.LoginRequest(email, password);
        Call<StrapiAuthResponse> call = apiService.login(request);

        call.enqueue(new Callback<StrapiAuthResponse>() {
            @Override
            public void onResponse(Call<StrapiAuthResponse> call, Response<StrapiAuthResponse> response) {
                setLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    StrapiAuthResponse authResponse = response.body();
                    if (authResponse.isSuccess()) {
                        // Save JWT token and user info
                        String jwt = authResponse.getJwt();
                        Integer userId = authResponse.getUser() != null ? authResponse.getUser().getId() : null;
                        sessionManager.saveJwtToken(jwt, userId);

                        // Create User object and save to session
                        User user = new User();
                        if (authResponse.getUser() != null) {
                            user.setId(String.valueOf(authResponse.getUser().getId()));
                            user.setEmail(authResponse.getUser().getEmail());
                            user.setFullName(authResponse.getUser().getUsername());
                        }
                        sessionManager.createSession(user);

                        Toast.makeText(LoginActivity.this, "Login berhasil", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        String errorMsg = authResponse.getError() != null 
                                ? authResponse.getError().getMessage() 
                                : "Login gagal";
                        Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle HTTP error response
                    String errorMsg = "Login gagal";
                    if (response.errorBody() != null) {
                        try {
                            // Try to parse error message from response
                            errorMsg = "Error: " + response.code();
                        } catch (Exception e) {
                            errorMsg = "Login gagal. Silakan coba lagi.";
                        }
                    }
                    Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<StrapiAuthResponse> call, Throwable t) {
                setLoading(false);
                String errorMsg = "Koneksi gagal: " + t.getMessage();
                if (t.getMessage() != null && t.getMessage().contains("Failed to connect")) {
                    errorMsg = "Tidak dapat terhubung ke server. Pastikan backend Strapi berjalan di http://localhost:1338";
                }
                Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean validateInput(String email, String password) {
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

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password tidak boleh kosong");
            etPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void setLoading(boolean loading) {
        btnLogin.setEnabled(!loading);
        btnLogin.setText(loading ? "Loading..." : getString(R.string.login));
    }
}
