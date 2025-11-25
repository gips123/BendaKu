package com.example.bendaku.api;

import com.example.bendaku.model.StrapiAuthResponse;
import com.example.bendaku.model.StrapiClaim;
import com.example.bendaku.model.StrapiItem;
import com.example.bendaku.model.StrapiResponse;
import com.example.bendaku.model.StrapiUploadResponse;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // ========== Auth Endpoints ==========
    /**
     * Login to Strapi
     * POST /api/auth/local
     * Body: {"identifier": "email or username", "password": "****"}
     */
    @POST("api/auth/local")
    Call<StrapiAuthResponse> login(@Body LoginRequest request);

    /**
     * Register new user
     * POST /api/auth/local/register
     */
    @POST("api/auth/local/register")
    Call<StrapiAuthResponse> register(@Body RegisterRequest request);

    // ========== Upload Endpoint ==========
    /**
     * Upload file/image
     * POST /api/upload
     * Multipart form-data with key "files" and Authorization header
     * Returns array of uploaded files
     */
    @Multipart
    @POST("api/upload")
    Call<List<StrapiUploadResponse>> uploadFile(@Part MultipartBody.Part file);

    // ========== Items Endpoints ==========
    /**
     * Get all items with populated relations
     * GET /api/items?populate=*
     */
    @GET("api/items")
    Call<StrapiResponse<List<StrapiItem>>> getItems(@Query("populate") String populate);

    /**
     * Get items filtered by type
     * GET /api/items?populate=*&filters[type][$eq]=lost
     */
    @GET("api/items")
    Call<StrapiResponse<List<StrapiItem>>> getItemsByType(
            @Query("populate") String populate,
            @Query("filters[type][$eq]") String type
    );

    /**
     * Get single item by ID
     * GET /api/items/{id}?populate=*
     */
    @GET("api/items/{id}")
    Call<StrapiResponse<StrapiItem>> getItem(
            @Path("id") Integer id,
            @Query("populate") String populate
    );

    /**
     * Create new item
     * POST /api/items
     * Body: {"data": {...}}
     */
    @POST("api/items")
    Call<StrapiResponse<StrapiItem>> createItem(@Body ItemRequest request);

    /**
     * Update item
     * PUT /api/items/{id}
     */
    @PUT("api/items/{id}")
    Call<StrapiResponse<StrapiItem>> updateItem(
            @Path("id") Integer id,
            @Body ItemRequest request
    );

    /**
     * Delete item
     * DELETE /api/items/{id}
     */
    @DELETE("api/items/{id}")
    Call<StrapiResponse<StrapiItem>> deleteItem(@Path("id") Integer id);

    // ========== Claims Endpoints ==========
    /**
     * Get all claims with populated relations
     * GET /api/claims?populate=*
     */
    @GET("api/claims")
    Call<StrapiResponse<List<StrapiClaim>>> getClaims(@Query("populate") String populate);

    /**
     * Get claims filtered by status
     * GET /api/claims?populate=*&filters[statusClaim][$eq]=pending
     */
    @GET("api/claims")
    Call<StrapiResponse<List<StrapiClaim>>> getClaimsByStatus(
            @Query("populate") String populate,
            @Query("filters[statusClaim][$eq]") String status
    );

    /**
     * Get single claim by ID
     * GET /api/claims/{id}?populate=*
     */
    @GET("api/claims/{id}")
    Call<StrapiResponse<StrapiClaim>> getClaim(
            @Path("id") Integer id,
            @Query("populate") String populate
    );

    /**
     * Create new claim
     * POST /api/claims
     * Body: {"data": {...}}
     */
    @POST("api/claims")
    Call<StrapiResponse<StrapiClaim>> createClaim(@Body ClaimRequest request);

    /**
     * Update claim
     * PUT /api/claims/{id}
     */
    @PUT("api/claims/{id}")
    Call<StrapiResponse<StrapiClaim>> updateClaim(
            @Path("id") Integer id,
            @Body ClaimRequest request
    );

    /**
     * Delete claim
     * DELETE /api/claims/{id}
     */
    @DELETE("api/claims/{id}")
    Call<StrapiResponse<StrapiClaim>> deleteClaim(@Path("id") Integer id);

    // ========== Request Classes ==========
    class LoginRequest {
        public String identifier; // email or username
        public String password;

        public LoginRequest(String identifier, String password) {
            this.identifier = identifier;
            this.password = password;
        }
    }

    class RegisterRequest {
        public String username;
        public String email;
        public String password;

        public RegisterRequest(String username, String email, String password) {
            this.username = username;
            this.email = email;
            this.password = password;
        }
    }

    class ItemRequest {
        public ItemData data;

        public ItemRequest(ItemData data) {
            this.data = data;
        }

        public static class ItemData {
            public String name;
            public String description;
            public String location;
            public String dateTime;
            public String type; // "lost" or "found"
            public String statusItem; // "open", "claimed", "resolved"
            public String reporterName;
            public String reporterPhone;
            public Integer imageUrl; // ID of uploaded file

            public ItemData(String name, String description, String location, String dateTime,
                           String type, String statusItem, String reporterName, String reporterPhone,
                           Integer imageUrl) {
                this.name = name;
                this.description = description;
                this.location = location;
                this.dateTime = dateTime;
                this.type = type;
                this.statusItem = statusItem;
                this.reporterName = reporterName;
                this.reporterPhone = reporterPhone;
                this.imageUrl = imageUrl;
            }
        }
    }

    class ClaimRequest {
        public ClaimData data;

        public ClaimRequest(ClaimData data) {
            this.data = data;
        }

        public static class ClaimData {
            public String claimerName;
            public String claimerPhone;
            public String claimerUsername;
            public String description;
            public String statusClaim; // "pending", "approved", "rejected"
            public String adminNotes;
            public Integer imageUrl; // ID of uploaded file
            @SerializedName("claimerktm")
            public Integer claimerKtm; // ID foto KTM/identitas
            public String locale; // Optional: for multi-language

            public ClaimData(String claimerName, String claimerPhone, String description,
                           String statusClaim, String adminNotes, Integer imageUrl,
                           Integer claimerKtm, String claimerUsername,
                           String locale) {
                this.claimerName = claimerName;
                this.claimerPhone = claimerPhone;
                this.claimerUsername = claimerUsername;
                this.description = description;
                this.statusClaim = statusClaim;
                this.adminNotes = adminNotes;
                this.imageUrl = imageUrl;
                this.claimerKtm = claimerKtm;
                this.locale = locale;
            }
        }
    }
}
