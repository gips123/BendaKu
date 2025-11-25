package com.example.bendaku.repository;

import com.example.bendaku.api.ApiClient;
import com.example.bendaku.api.BendaKuApiService;
import com.example.bendaku.model.ApiResponse;
import com.example.bendaku.model.Claim;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.google.gson.Gson;

public class ClaimRepository {
    private BendaKuApiService apiService;
    private Gson gson;
    
    public ClaimRepository() {
        apiService = ApiClient.getInstance().getApiService();
        gson = new Gson();
    }
    
    public void getClaims(String status, ClaimsCallback callback) {
        Call<ApiResponse<List<Claim>>> call = apiService.getClaims(status);
        call.enqueue(new Callback<ApiResponse<List<Claim>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Claim>>> call,
                                 Response<ApiResponse<List<Claim>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Claim>> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else {
                    callback.onError("Failed to load claims");
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<List<Claim>>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
    
    public void getClaim(int id, ClaimCallback callback) {
        Call<ApiResponse<Claim>> call = apiService.getClaim(id);
        call.enqueue(new Callback<ApiResponse<Claim>>() {
            @Override
            public void onResponse(Call<ApiResponse<Claim>> call,
                                 Response<ApiResponse<Claim>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Claim> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else {
                    callback.onError("Failed to load claim");
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Claim>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
    
    public void createClaim(Integer itemId, Integer claimerId, String claimerName,
                           String claimerPhone, String description, File proofImageFile,
                           ClaimCallback callback) {
        // Create JSON object for Strapi data field using Gson
        java.util.Map<String, Object> dataMap = new java.util.HashMap<>();
        if (itemId != null) {
            dataMap.put("itemId", itemId);
        }
        if (claimerId != null) {
            dataMap.put("claimerId", claimerId);
        }
        if (claimerName != null) {
            dataMap.put("claimerName", claimerName);
        }
        if (claimerPhone != null) {
            dataMap.put("claimerPhone", claimerPhone);
        }
        if (description != null) {
            dataMap.put("description", description);
        }
        
        String dataJson = gson.toJson(dataMap);
        RequestBody claimBody = RequestBody.create(
            MediaType.parse("application/json"), dataJson);
        
        // Create MultipartBody.Part for proof image
        MultipartBody.Part proofImagePart = null;
        if (proofImageFile != null && proofImageFile.exists()) {
            RequestBody imageBody = RequestBody.create(
                MediaType.parse("image/*"), proofImageFile);
            proofImagePart = MultipartBody.Part.createFormData(
                "proofImageUrl", proofImageFile.getName(), imageBody);
        }
        
        Call<ApiResponse<Claim>> call = apiService.createClaim(claimBody, proofImagePart);
        call.enqueue(new Callback<ApiResponse<Claim>>() {
            @Override
            public void onResponse(Call<ApiResponse<Claim>> call,
                                 Response<ApiResponse<Claim>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Claim> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else {
                    callback.onError("Failed to create claim");
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Claim>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
    
    public void approveClaim(int id, ClaimCallback callback) {
        Call<ApiResponse<Claim>> call = apiService.approveClaim(id);
        call.enqueue(new Callback<ApiResponse<Claim>>() {
            @Override
            public void onResponse(Call<ApiResponse<Claim>> call,
                                 Response<ApiResponse<Claim>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Claim> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else {
                    callback.onError("Failed to approve claim");
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Claim>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
    
    public void rejectClaim(int id, String adminNotes, ClaimCallback callback) {
        BendaKuApiService.RejectRequest request = new BendaKuApiService.RejectRequest(adminNotes);
        
        Call<ApiResponse<Claim>> call = apiService.rejectClaim(id, request);
        call.enqueue(new Callback<ApiResponse<Claim>>() {
            @Override
            public void onResponse(Call<ApiResponse<Claim>> call,
                                 Response<ApiResponse<Claim>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Claim> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else {
                    callback.onError("Failed to reject claim");
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Claim>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
    
    public interface ClaimsCallback {
        void onSuccess(List<Claim> claims);
        void onError(String error);
    }
    
    public interface ClaimCallback {
        void onSuccess(Claim claim);
        void onError(String error);
    }
}

