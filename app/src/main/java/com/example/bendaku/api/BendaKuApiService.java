package com.example.bendaku.api;

import com.example.bendaku.model.ApiResponse;
import com.example.bendaku.model.AuthResponse;
import com.example.bendaku.model.User;
import com.example.bendaku.model.Item;
import com.example.bendaku.model.Claim;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface BendaKuApiService {

    // Authentication Endpoints
    // Note: Strapi returns {jwt, user} directly, not wrapped in ApiResponse
    @POST("auth/local/register")
    Call<AuthResponse> register(@Body RegisterRequest request);
    
    @POST("auth/local")
    Call<AuthResponse> login(@Body LoginRequest request);
    
    @GET("users/me")
    Call<ApiResponse<User>> getCurrentUser();
    
    // Items Endpoints
    @GET("items")
    Call<ApiResponse<List<Item>>> getItems(@Query("filters[type][$eq]") String type);
    
    @GET("items/{id}")
    Call<ApiResponse<Item>> getItem(@Path("id") int id);
    
    @Multipart
    @POST("items")
    Call<ApiResponse<Item>> createItem(
        @Part("data") RequestBody itemData,
        @Part MultipartBody.Part image
    );
    
    @PUT("items/{id}")
    Call<ApiResponse<Item>> updateItem(
        @Path("id") int id,
        @Body UpdateItemRequest request
    );
    
    @DELETE("items/{id}")
    Call<ApiResponse<Void>> deleteItem(@Path("id") int id);
    
    // Claims Endpoints
    @GET("claims")
    Call<ApiResponse<List<Claim>>> getClaims(@Query("filters[status][$eq]") String status);
    
    @GET("claims/{id}")
    Call<ApiResponse<Claim>> getClaim(@Path("id") int id);
    
    @Multipart
    @POST("claims")
    Call<ApiResponse<Claim>> createClaim(
        @Part("data") RequestBody claimData,
        @Part MultipartBody.Part proofImage
    );
    
    @PATCH("claims/{id}/approve")
    Call<ApiResponse<Claim>> approveClaim(@Path("id") int id);
    
    @PATCH("claims/{id}/reject")
    Call<ApiResponse<Claim>> rejectClaim(@Path("id") int id, @Body RejectRequest request);
    
    // Admin Endpoints
    @GET("admin/users")
    Call<ApiResponse<List<User>>> getAllUsers();
    
    @GET("admin/statistics")
    Call<ApiResponse<StatisticsResponse>> getStatistics();
    
    // Request Models
    class RegisterRequest {
        public String email;
        public String password;
        public String username;
        public String fullName;
        public String phone;
        public String studentId;
        
        public RegisterRequest(String email, String password, String username,
                              String fullName, String phone, String studentId) {
            this.email = email;
            this.password = password;
            this.username = username;
            this.fullName = fullName;
            this.phone = phone;
            this.studentId = studentId;
        }
    }
    
    class LoginRequest {
        public String identifier;
        public String password;
        
        public LoginRequest(String identifier, String password) {
            this.identifier = identifier;
            this.password = password;
        }
    }
    
    class UpdateItemRequest {
        public String name;
        public String description;
        public String location;
        public String dateTime;
        public String type;
        public String status;
        
        public UpdateItemRequest(String name, String description, String location,
                                String dateTime, String type, String status) {
            this.name = name;
            this.description = description;
            this.location = location;
            this.dateTime = dateTime;
            this.type = type;
            this.status = status;
        }
    }
    
    class RejectRequest {
        public String adminNotes;
        
        public RejectRequest(String adminNotes) {
            this.adminNotes = adminNotes;
        }
    }
    
    class StatisticsResponse {
        public int totalItems;
        public int totalClaims;
        public int pendingClaims;
        public int approvedClaims;
        public int rejectedClaims;
    }
}

