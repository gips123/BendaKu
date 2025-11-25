package com.example.uts;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bendaku.api.ApiClient;
import com.example.bendaku.api.BendaKuApiService;
import com.example.bendaku.model.ApiResponse;
import com.example.bendaku.model.AuthResponse;
import com.example.bendaku.model.User;
import com.example.bendaku.utils.SessionManager;
import com.example.bendaku.utils.TokenManager;
import com.example.uts.BuildConfig;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private SessionManager sessionManager;
    private BendaKuApiService apiService;

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
        // Initialize TokenManager first
        TokenManager.init(this);
        sessionManager = new SessionManager(this);
        apiService = ApiClient.getInstance().getApiService();
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

        BendaKuApiService.LoginRequest request = new BendaKuApiService.LoginRequest(email, password);
        Call<AuthResponse> call = apiService.login(request);

        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                // Check if activity is still valid
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                
                setLoading(false);

                try {
                    int responseCode = response.code();
                    Log.d(TAG, "=== LOGIN RESPONSE ===");
                    Log.d(TAG, "Response code: " + responseCode);
                    Log.d(TAG, "Response isSuccessful: " + response.isSuccessful());
                    Log.d(TAG, "Response body is null: " + (response.body() == null));
                    
                    if (response.isSuccessful() && response.body() != null) {
                        AuthResponse authResponse = response.body();
                        
                        Log.d(TAG, "AuthResponse JWT is null: " + (authResponse.getJwt() == null));
                        Log.d(TAG, "AuthResponse User is null: " + (authResponse.getUser() == null));
                        
                        // Validate auth response
                        if (authResponse.getJwt() == null || authResponse.getJwt().isEmpty()) {
                            Log.e(TAG, "JWT token is null or empty");
                            showToast("Error: JWT token tidak ditemukan");
                            return;
                        }
                        
                        if (authResponse.getUser() == null) {
                            Log.e(TAG, "User data is null");
                            showToast("Error: User data tidak ditemukan");
                            return;
                        }
                        
                        Log.d(TAG, "Saving JWT token and user session");
                        // Save JWT token
                        TokenManager.saveToken(authResponse.getJwt());
                        
                        // Save user session
                        sessionManager.createSession(authResponse.getUser());
                        
                        showToast("Login berhasil");
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        // Handle error response
                        String errorMsg = "Login gagal";
                        String errorBodyText = "";
                        
                        if (response.errorBody() != null) {
                            try {
                                errorBodyText = response.errorBody().string();
                                Log.e(TAG, "Error body: " + errorBodyText);
                            } catch (Exception e) {
                                Log.e(TAG, "Error reading error body", e);
                            }
                        }
                        
                        if (responseCode == 400) {
                            errorMsg = "Email atau password salah";
                        } else if (responseCode == 401) {
                            errorMsg = "Unauthorized - Email atau password salah";
                        } else if (responseCode == 404) {
                            errorMsg = "Endpoint tidak ditemukan. Pastikan backend Strapi berjalan di " + BuildConfig.API_BASE_URL;
                        } else if (responseCode == 500) {
                            errorMsg = "Server error";
                        } else {
                            errorMsg = "Login gagal (Code: " + responseCode + ")";
                            if (!errorBodyText.isEmpty()) {
                                errorMsg += "\n" + errorBodyText;
                            }
                        }
                        
                        Log.e(TAG, "Response error: " + responseCode + " - " + errorMsg);
                        showToast(errorMsg);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Exception in onResponse", e);
                    e.printStackTrace();
                    String errorMsg = e.getMessage() != null ? e.getMessage() : "Terjadi kesalahan";
                    Log.e(TAG, "Exception message: " + errorMsg);
                    showToast("Error: " + errorMsg);
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                // Check if activity is still valid
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                
                setLoading(false);
                String errorMsg = "Network error";
                if (t != null && t.getMessage() != null) {
                    if (t.getMessage().contains("Failed to connect")) {
                        errorMsg = "Tidak dapat terhubung ke server. Pastikan backend Strapi berjalan.";
                    } else {
                        errorMsg = "Error: " + t.getMessage();
                    }
                }
                showToast(errorMsg);
                if (t != null) {
                    t.printStackTrace();
                }
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
        if (btnLogin != null) {
            btnLogin.setEnabled(!loading);
            btnLogin.setText(loading ? "Loading..." : getString(R.string.login));
        }
    }
    
    private void showToast(String message) {
        if (message == null || message.isEmpty()) {
            return;
        }
        
        runOnUiThread(() -> {
            if (!isFinishing() && !isDestroyed()) {
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
