package com.example.bendaku.api;

import com.example.bendaku.model.StrapiAuthResponse;
import com.example.bendaku.model.StrapiClaim;
import com.example.bendaku.model.StrapiItem;
import com.example.bendaku.model.StrapiResponse;
import com.example.bendaku.model.StrapiUploadResponse;
import com.example.bendaku.model.StrapiUserDetail;
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

    @POST("api/auth/local")
    Call<StrapiAuthResponse> login(@Body LoginRequest request);

    @POST("api/auth/local/register")
    Call<StrapiAuthResponse> register(@Body RegisterRequest request);

    @Multipart
    @POST("api/upload")
    Call<List<StrapiUploadResponse>> uploadFile(@Part MultipartBody.Part file);

    @GET("api/items")
    Call<StrapiResponse<List<StrapiItem>>> getItems(@Query("populate") String populate);

    @GET("api/items")
    Call<StrapiResponse<List<StrapiItem>>> getItemsByType(
            @Query("populate") String populate,
            @Query("filters[type][$eq]") String type
    );

    @GET("api/items/{id}")
    Call<StrapiResponse<StrapiItem>> getItem(
            @Path("id") Integer id,
            @Query("populate") String populate
    );

    @GET("api/items/{documentId}")
    Call<StrapiResponse<StrapiItem>> getItemByDocumentId(
            @Path("documentId") String documentId,
            @Query("populate") String populate
    );

    @POST("api/items")
    Call<StrapiResponse<StrapiItem>> createItem(@Body ItemRequest request);

    @PUT("api/items/{id}")
    Call<StrapiResponse<StrapiItem>> updateItem(
            @Path("id") Integer id,
            @Body ItemRequest request
    );

    @DELETE("api/items/{id}")
    Call<StrapiResponse<StrapiItem>> deleteItem(@Path("id") Integer id);

    @GET("api/claims")
    Call<StrapiResponse<List<StrapiClaim>>> getClaims(@Query("populate") String populate);

    @GET("api/claims")
    Call<StrapiResponse<List<StrapiClaim>>> getClaimsByStatus(
            @Query("populate") String populate,
            @Query("filters[statusClaim][$eq]") String status
    );

    @GET("api/claims/{documentId}")
    Call<StrapiResponse<StrapiClaim>> getClaim(
            @Path("documentId") String documentId,
            @Query("populate") String populate
    );

    @POST("api/claims")
    Call<StrapiResponse<StrapiClaim>> createClaim(@Body ClaimRequest request);

    @PUT("api/claims/{documentId}")
    Call<StrapiResponse<StrapiClaim>> updateClaim(
            @Path("documentId") String documentId,
            @Body ClaimRequest request
    );

    @DELETE("api/claims/{id}")
    Call<StrapiResponse<StrapiClaim>> deleteClaim(@Path("id") Integer id);

    @GET("api/users/me")
    Call<StrapiUserDetail> getCurrentUser(@Query("populate") String populate);

    class LoginRequest {
        public String identifier;
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
            public String type;
            public String statusItem;
            public String reporterName;
            public String reporterPhone;
            public Integer imageUrl;

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
            public String statusClaim;
            public String adminNotes;
            public Integer imageUrl;
            @SerializedName("claimerktm")
            public Integer claimerKtm;
            public Integer item;
            public String locale;

            public ClaimData(String claimerName, String claimerPhone, String description,
                           String statusClaim, String adminNotes, Integer imageUrl,
                           Integer claimerKtm, String claimerUsername,
                           Integer item, String locale) {
                this.claimerName = claimerName;
                this.claimerPhone = claimerPhone;
                this.claimerUsername = claimerUsername;
                this.description = description;
                this.statusClaim = statusClaim;
                this.adminNotes = adminNotes;
                this.imageUrl = imageUrl;
                this.claimerKtm = claimerKtm;
                this.item = item;
                this.locale = locale;
            }
        }
    }
}
