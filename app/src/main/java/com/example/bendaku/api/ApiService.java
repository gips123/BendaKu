package com.example.bendaku.api;

import com.example.bendaku.model.ApiResponse;
import com.example.bendaku.model.User;
import com.example.bendaku.model.Item;
import com.example.bendaku.model.Claim;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    // Auth endpoints
    @POST("auth/login")
    Call<ApiResponse<User>> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<ApiResponse<User>> register(@Body RegisterRequest request);

    // Items endpoints
    @GET("items")
    Call<ApiResponse<List<Item>>> getItems(@Query("type") String type);

    @GET("items/{id}")
    Call<ApiResponse<Item>> getItem(@Path("id") String id);

    @Multipart
    @POST("items")
    Call<ApiResponse<Item>> createItem(
            @Part("name") RequestBody name,
            @Part("description") RequestBody description,
            @Part("location") RequestBody location,
            @Part("dateTime") RequestBody dateTime,
            @Part("type") RequestBody type,
            @Part("reporterId") RequestBody reporterId,
            @Part("reporterName") RequestBody reporterName,
            @Part("reporterPhone") RequestBody reporterPhone,
            @Part MultipartBody.Part image
    );

    // Claims endpoints
    @GET("claims")
    Call<ApiResponse<List<Claim>>> getClaims(@Query("status") String status);

    @Multipart
    @POST("claims")
    Call<ApiResponse<Claim>> createClaim(
            @Part("itemId") RequestBody itemId,
            @Part("claimerId") RequestBody claimerId,
            @Part("claimerName") RequestBody claimerName,
            @Part("claimerPhone") RequestBody claimerPhone,
            @Part("description") RequestBody description,
            @Part MultipartBody.Part proofImage
    );

    @PUT("claims/{id}/approve")
    Call<ApiResponse<Claim>> approveClaim(@Path("id") String id);

    @PUT("claims/{id}/reject")
    Call<ApiResponse<Claim>> rejectClaim(@Path("id") String id, @Body RejectRequest request);

    // Request classes
    class LoginRequest {
        public String email;
        public String password;

        public LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }

    class RegisterRequest {
        public String fullName;
        public String email;
        public String password;
        public String phone;
        public String studentId;

        public RegisterRequest(String fullName, String email, String password, String phone, String studentId) {
            this.fullName = fullName;
            this.email = email;
            this.password = password;
            this.phone = phone;
            this.studentId = studentId;
        }
    }

    class RejectRequest {
        public String reason;

        public RejectRequest(String reason) {
            this.reason = reason;
        }
    }
}
