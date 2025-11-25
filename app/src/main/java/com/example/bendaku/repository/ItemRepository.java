package com.example.bendaku.repository;

import com.example.bendaku.api.ApiClient;
import com.example.bendaku.api.BendaKuApiService;
import com.example.bendaku.model.ApiResponse;
import com.example.bendaku.model.Item;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.google.gson.Gson;

public class ItemRepository {
    private BendaKuApiService apiService;
    private Gson gson;
    
    public ItemRepository() {
        apiService = ApiClient.getInstance().getApiService();
        gson = new Gson();
    }
    
    public void getItems(String type, ItemsCallback callback) {
        Call<ApiResponse<List<Item>>> call = apiService.getItems(type);
        call.enqueue(new Callback<ApiResponse<List<Item>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Item>>> call,
                                 Response<ApiResponse<List<Item>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Item>> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else {
                    callback.onError("Failed to load items");
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<List<Item>>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
    
    public void getItem(int id, ItemCallback callback) {
        Call<ApiResponse<Item>> call = apiService.getItem(id);
        call.enqueue(new Callback<ApiResponse<Item>>() {
            @Override
            public void onResponse(Call<ApiResponse<Item>> call,
                                 Response<ApiResponse<Item>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Item> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else {
                    callback.onError("Failed to load item");
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Item>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
    
    public void createItem(String name, String description, String location, String dateTime,
                          String type, Integer reporterId, String reporterName, String reporterPhone,
                          File imageFile, ItemCallback callback) {
        // Create JSON object for Strapi data field using Gson
        java.util.Map<String, Object> dataMap = new java.util.HashMap<>();
        dataMap.put("name", name);
        dataMap.put("description", description);
        dataMap.put("location", location);
        dataMap.put("dateTime", dateTime);
        dataMap.put("type", type);
        if (reporterId != null) {
            dataMap.put("reporterId", reporterId);
        }
        if (reporterName != null) {
            dataMap.put("reporterName", reporterName);
        }
        if (reporterPhone != null) {
            dataMap.put("reporterPhone", reporterPhone);
        }
        
        String dataJson = gson.toJson(dataMap);
        RequestBody itemBody = RequestBody.create(
            MediaType.parse("application/json"), dataJson);
        
        // Create MultipartBody.Part for image
        MultipartBody.Part imagePart = null;
        if (imageFile != null && imageFile.exists()) {
            RequestBody imageBody = RequestBody.create(
                MediaType.parse("image/*"), imageFile);
            imagePart = MultipartBody.Part.createFormData(
                "imageUrl", imageFile.getName(), imageBody);
        }
        
        Call<ApiResponse<Item>> call = apiService.createItem(itemBody, imagePart);
        call.enqueue(new Callback<ApiResponse<Item>>() {
            @Override
            public void onResponse(Call<ApiResponse<Item>> call,
                                 Response<ApiResponse<Item>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Item> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else {
                    callback.onError("Failed to create item");
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Item>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
    
    public void updateItem(int id, String name, String description, String location,
                          String dateTime, String type, String status, ItemCallback callback) {
        BendaKuApiService.UpdateItemRequest request = 
            new BendaKuApiService.UpdateItemRequest(name, description, location, dateTime, type, status);
        
        Call<ApiResponse<Item>> call = apiService.updateItem(id, request);
        call.enqueue(new Callback<ApiResponse<Item>>() {
            @Override
            public void onResponse(Call<ApiResponse<Item>> call,
                                 Response<ApiResponse<Item>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Item> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else {
                    callback.onError("Failed to update item");
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Item>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
    
    public void deleteItem(int id, DeleteCallback callback) {
        Call<ApiResponse<Void>> call = apiService.deleteItem(id);
        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call,
                                 Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Void> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        callback.onSuccess();
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else {
                    callback.onError("Failed to delete item");
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
    
    public interface ItemsCallback {
        void onSuccess(List<Item> items);
        void onError(String error);
    }
    
    public interface ItemCallback {
        void onSuccess(Item item);
        void onError(String error);
    }
    
    public interface DeleteCallback {
        void onSuccess();
        void onError(String error);
    }
}

