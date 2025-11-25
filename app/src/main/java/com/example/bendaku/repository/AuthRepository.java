package com.example.bendaku.repository;

import com.example.bendaku.api.ApiClient;
import com.example.bendaku.api.BendaKuApiService;
import com.example.bendaku.model.ApiResponse;
import com.example.bendaku.model.AuthResponse;
import com.example.bendaku.model.User;
import com.example.bendaku.utils.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {
    private BendaKuApiService apiService;
    
    public AuthRepository() {
        apiService = ApiClient.getInstance().getApiService();
    }
    
    public void register(String email, String password, String username, String fullName,
                       String phone, String studentId, AuthCallback callback) {
        BendaKuApiService.RegisterRequest request = new BendaKuApiService.RegisterRequest(
            email, password, username, fullName, phone, studentId
        );
        
        Call<AuthResponse> call = apiService.register(request);
        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call,
                                 Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    if (authResponse.getJwt() != null && authResponse.getUser() != null) {
                        TokenManager.saveToken(authResponse.getJwt());
                        callback.onSuccess(authResponse.getUser());
                    } else {
                        callback.onError("Invalid response data");
                    }
                } else {
                    String errorMsg = "Registration failed";
                    if (response.code() == 400) {
                        errorMsg = "Email sudah terdaftar atau data tidak valid";
                    }
                    callback.onError(errorMsg);
                }
            }
            
            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
    
    public void login(String identifier, String password, AuthCallback callback) {
        BendaKuApiService.LoginRequest request = new BendaKuApiService.LoginRequest(
            identifier, password
        );
        
        Call<AuthResponse> call = apiService.login(request);
        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call,
                                 Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    if (authResponse.getJwt() != null && authResponse.getUser() != null) {
                        TokenManager.saveToken(authResponse.getJwt());
                        callback.onSuccess(authResponse.getUser());
                    } else {
                        callback.onError("Invalid response data");
                    }
                } else {
                    String errorMsg = "Login failed";
                    if (response.code() == 400 || response.code() == 401) {
                        errorMsg = "Email atau password salah";
                    }
                    callback.onError(errorMsg);
                }
            }
            
            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
    
    public void getCurrentUser(UserCallback callback) {
        Call<ApiResponse<User>> call = apiService.getCurrentUser();
        call.enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call,
                                 Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<User> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else {
                    if (response.code() == 401) {
                        TokenManager.clearToken();
                        callback.onError("Session expired. Please login again.");
                    } else {
                        callback.onError("Failed to get user profile");
                    }
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
    
    public void logout() {
        TokenManager.clearToken();
    }
    
    public interface AuthCallback {
        void onSuccess(User user);
        void onError(String error);
    }
    
    public interface UserCallback {
        void onSuccess(User user);
        void onError(String error);
    }
}

