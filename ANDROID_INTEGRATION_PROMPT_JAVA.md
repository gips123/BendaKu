# Prompt untuk Integrasi Android (Java) - BendaKu Backend Strapi

## 📱 Konteks Aplikasi

Saya memiliki backend Strapi untuk aplikasi Android "BendaKu" - sistem pelaporan barang hilang/ditemukan di kampus UPNVJ dengan fitur klaim. Backend sudah siap dan berjalan di `http://localhost:1337` (development) atau `http://[server-ip]:1337` (production).

## 🔗 Base URL & Endpoints

**Base URL:** `http://localhost:1337/api` (development) atau `http://[server-ip]:1337/api` (production)

### Authentication Endpoints
- `POST /api/auth/local/register` - Registrasi user baru
- `POST /api/auth/local` - Login
- `GET /api/users/me` - Get current user profile

### Items Endpoints
- `GET /api/items` - Get semua items (filter: `?filters[type][$eq]=lost|found`)
- `GET /api/items/:id` - Get detail item
- `POST /api/items` - Create item (authenticated)
- `PUT /api/items/:id` - Update item (owner/admin)
- `DELETE /api/items/:id` - Delete item (owner/admin)

### Claims Endpoints
- `GET /api/claims` - Get semua claims (filter: `?filters[status][$eq]=pending|approved|rejected`)
- `GET /api/claims/:id` - Get detail claim
- `POST /api/claims` - Create claim (authenticated)
- `PATCH /api/claims/:id/approve` - Approve claim (admin only)
- `PATCH /api/claims/:id/reject` - Reject claim (admin only)

### Admin Endpoints
- `GET /api/admin/users` - Get semua users (admin only)
- `GET /api/admin/statistics` - Get statistics (admin only)

## 📋 Response Format

### Success Response
```json
{
  "success": true,
  "message": "Operasi berhasil",
  "data": { ... }
}
```

### Error Response
```json
{
  "success": false,
  "message": "Error message",
  "data": null
}
```

## 🔐 Authentication

### Register Request
```json
POST /api/auth/local/register
Content-Type: application/json

{
  "email": "budi.santoso@upnvj.ac.id",
  "password": "password123",
  "username": "budi.santoso",
  "fullName": "Budi Santoso",
  "phone": "081234567891",
  "studentId": "2021001"
}
```

### Register Response
```json
{
  "success": true,
  "message": "Registration successful",
  "data": {
    "jwt": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "email": "budi.santoso@upnvj.ac.id",
      "username": "budi.santoso",
      "fullName": "Budi Santoso",
      "phone": "081234567891",
      "studentId": "2021001",
      "isAdmin": false
    }
  }
}
```

### Login Request
```json
POST /api/auth/local
Content-Type: application/json

{
  "identifier": "budi.santoso@upnvj.ac.id",
  "password": "password123"
}
```

### Login Response
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "jwt": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "email": "budi.santoso@upnvj.ac.id",
      "username": "budi.santoso",
      "fullName": "Budi Santoso",
      "phone": "081234567891",
      "studentId": "2021001",
      "isAdmin": false
    }
  }
}
```

### Authorization Header
Setelah login, semua authenticated requests harus include:
```
Authorization: Bearer <JWT_TOKEN>
```

**JWT Token expires dalam 7 hari.**

## 📦 Data Models (Java)

### User Model
```java
public class User {
    private int id;
    private String email;
    private String username;
    private String fullName;
    private String phone;
    private String studentId;
    private boolean isAdmin;
    
    // Constructors, Getters, Setters
    public User() {}
    
    public User(int id, String email, String username, String fullName, 
                String phone, String studentId, boolean isAdmin) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.fullName = fullName;
        this.phone = phone;
        this.studentId = studentId;
        this.isAdmin = isAdmin;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    // ... (other getters/setters)
}
```

### Item Model
```java
public class Item {
    private int id;
    private String name;
    private String description;
    private String location;
    private String dateTime; // Format: "03 Okt 2024, 10:30"
    private String imageUrl;
    private String type; // "lost" or "found"
    private String status; // "open", "claimed", "resolved"
    private Integer reporterId;
    private String reporterName;
    private String reporterPhone;
    private User reporter;
    private String createdAt;
    private String updatedAt;
    
    // Constructors, Getters, Setters
}
```

### Claim Model
```java
public class Claim {
    private int id;
    private Integer itemId;
    private Item item;
    private Integer claimerId;
    private String claimerName;
    private String claimerPhone;
    private User claimer;
    private String description;
    private String proofImageUrl;
    private String status; // "pending", "approved", "rejected"
    private String adminNotes;
    private String createdAt;
    private String updatedAt;
    
    // Constructors, Getters, Setters
}
```

### ApiResponse Wrapper
```java
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    
    public ApiResponse() {}
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}
```

## 🛠️ Implementation Requirements (Java)

### 1. Retrofit Setup
- Gunakan Retrofit untuk HTTP client
- Base URL di-configure di `BuildConfig.API_BASE_URL`
- Support untuk multipart/form-data (untuk file upload)
- Interceptor untuk JWT token di Authorization header

### 2. Dependencies (build.gradle)
```gradle
dependencies {
    // Networking
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'com.squareup.okhttp3:okhttp:4.12.0'
    implementation 'com.squareup.okhttp3:logging-interceptor:4.12.0'
    
    // Image Loading
    implementation 'com.github.bumptech.glide:glide:4.16.0'
    
    // ViewModel & LiveData
    implementation 'androidx.lifecycle:lifecycle-viewmodel:2.7.0'
    implementation 'androidx.lifecycle:lifecycle-livedata:2.7.0'
}
```

### 3. Network Layer
- API Service interface dengan semua endpoints
- Repository pattern untuk business logic
- Error handling dengan try-catch dan proper error messages

### 4. Authentication
- SharedPreferences untuk menyimpan JWT token
- Auto-refresh token jika expired (atau redirect ke login)
- Logout functionality yang clear token

### 5. File Upload
- Support image upload untuk items (imageUrl) dan claims (proofImageUrl)
- Maximum file size: 5MB
- Image compression sebelum upload (optional, untuk optimize)

### 6. Date Format
- Format: "dd MMM yyyy, HH:mm" (contoh: "03 Okt 2024, 10:30")
- Gunakan SimpleDateFormat untuk parsing

### 7. Permissions
- Public: Read items (GET /api/items)
- Authenticated: Create item, create claim, read own profile
- Admin: Approve/reject claims, manage users

### 8. Error Handling
- Handle network errors (no internet, timeout)
- Handle API errors (400, 401, 403, 404, 500)
- Show user-friendly error messages
- Auto-logout jika token invalid (401)

## 📝 Contoh Implementasi (Java)

### Retrofit API Service
```java
public interface BendaKuApiService {
    @POST("auth/local/register")
    Call<ApiResponse<AuthResponse>> register(@Body RegisterRequest request);
    
    @POST("auth/local")
    Call<ApiResponse<AuthResponse>> login(@Body LoginRequest request);
    
    @GET("users/me")
    Call<ApiResponse<User>> getCurrentUser();
    
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
    
    @POST("claims")
    Call<ApiResponse<Claim>> createClaim(@Body CreateClaimRequest request);
}
```

### Request Models
```java
public class RegisterRequest {
    private String email;
    private String password;
    private String username;
    private String fullName;
    private String phone;
    private String studentId;
    
    // Constructors, Getters, Setters
}

public class LoginRequest {
    private String identifier;
    private String password;
    
    // Constructors, Getters, Setters
}

public class AuthResponse {
    private String jwt;
    private User user;
    
    // Constructors, Getters, Setters
}
```

### RetrofitClient Setup
```java
public class RetrofitClient {
    private static final String BASE_URL = BuildConfig.API_BASE_URL;
    private static RetrofitClient instance;
    private BendaKuApiService apiService;
    
    private RetrofitClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    String token = TokenManager.getToken();
                    
                    Request.Builder requestBuilder = original.newBuilder()
                        .header("Authorization", "Bearer " + token);
                    
                    return chain.proceed(requestBuilder.build());
                }
            })
            .addInterceptor(new HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY))
            .build();
        
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        
        apiService = retrofit.create(BendaKuApiService.class);
    }
    
    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }
    
    public BendaKuApiService getApiService() {
        return apiService;
    }
}
```

### Token Manager
```java
public class TokenManager {
    private static final String PREFS_NAME = "bendaku_prefs";
    private static final String KEY_TOKEN = "jwt_token";
    private static SharedPreferences prefs;
    
    public static void init(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    
    public static void saveToken(String token) {
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }
    
    public static String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }
    
    public static void clearToken() {
        prefs.edit().remove(KEY_TOKEN).apply();
    }
    
    public static boolean isLoggedIn() {
        return getToken() != null;
    }
}
```

### Repository Example
```java
public class ItemRepository {
    private BendaKuApiService apiService;
    
    public ItemRepository() {
        apiService = RetrofitClient.getInstance().getApiService();
    }
    
    public void getItems(String type, Callback<ApiResponse<List<Item>>> callback) {
        Call<ApiResponse<List<Item>>> call = apiService.getItems(type);
        call.enqueue(callback);
    }
    
    public void createItem(Item item, File imageFile, Callback<ApiResponse<Item>> callback) {
        // Create RequestBody for item data
        Gson gson = new Gson();
        String itemJson = gson.toJson(item);
        RequestBody itemBody = RequestBody.create(
            MediaType.parse("application/json"), itemJson);
        
        // Create MultipartBody.Part for image
        MultipartBody.Part imagePart = null;
        if (imageFile != null) {
            RequestBody imageBody = RequestBody.create(
                MediaType.parse("image/*"), imageFile);
            imagePart = MultipartBody.Part.createFormData(
                "imageUrl", imageFile.getName(), imageBody);
        }
        
        Call<ApiResponse<Item>> call = apiService.createItem(itemBody, imagePart);
        call.enqueue(callback);
    }
}
```

### ViewModel Example
```java
public class ItemViewModel extends ViewModel {
    private ItemRepository repository;
    private MutableLiveData<List<Item>> itemsLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    
    public ItemViewModel() {
        repository = new ItemRepository();
    }
    
    public LiveData<List<Item>> getItems() {
        return itemsLiveData;
    }
    
    public LiveData<String> getError() {
        return errorLiveData;
    }
    
    public void loadItems(String type) {
        repository.getItems(type, new Callback<ApiResponse<List<Item>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Item>>> call, 
                                 Response<ApiResponse<List<Item>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Item>> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        itemsLiveData.setValue(apiResponse.getData());
                    } else {
                        errorLiveData.setValue(apiResponse.getMessage());
                    }
                } else {
                    errorLiveData.setValue("Failed to load items");
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<List<Item>>> call, Throwable t) {
                errorLiveData.setValue("Network error: " + t.getMessage());
            }
        });
    }
}
```

## 🧪 Testing Credentials

Setelah menjalankan `npm run seed`, gunakan credentials berikut:

**Admin:**
- Email: `admin@upnvj.ac.id`
- Password: `password123`

**Regular Users:**
- `budi.santoso@upnvj.ac.id` / `password123`
- `siti.nurhaliza@upnvj.ac.id` / `password123`
- `ahmad.fadli@upnvj.ac.id` / `password123`
- `rina.kartika@upnvj.ac.id` / `password123`

## ⚠️ Important Notes

1. **Email Validation**: Hanya email `@upnvj.ac.id` yang bisa register
2. **Password**: Minimum 8 karakter
3. **CORS**: Backend sudah dikonfigurasi untuk allow Android app
4. **Image URLs**: Media fields return full URLs otomatis
5. **Date Format**: Gunakan format "dd MMM yyyy, HH:mm" (contoh: "03 Okt 2024, 10:30")
6. **JWT Token**: Simpan di SharedPreferences, include di setiap authenticated request
7. **File Upload**: Gunakan MultipartBody.Part untuk image upload
8. **Base URL**: 
   - Emulator: `http://10.0.2.2:1337/api`
   - Real Device: `http://192.168.x.x:1337/api`

#
## 🔧 Tech Stack Preferences

- **Language**: Java
- **Architecture**: MVVM
- **Networking**: Retrofit + OkHttp
- **Image Loading**: Glide
- **Async**: Callbacks atau RxJava
- **UI**: XML Layouts dengan ViewBinding
- **Navigation**: Navigation Component
- **Storage**: SharedPreferences

## 📚 Additional Resources

- Backend API Documentation: `API_DOCUMENTATION.md`
- Backend Setup: `README.md`
- Environment Setup: `ENV_SETUP.md`

---

**Catatan Penting:**
- Aplikasi Android harus menggunakan Retrofit untuk HTTP client
- Base URL akan di-configure di `BuildConfig.API_BASE_URL`
- Format response harus match dengan model `ApiResponse<T>` di Java
- Semua date format: "dd MMM yyyy, HH:mm" (contoh: "03 Okt 2024, 10:30")
- Pastikan handle semua error cases dengan proper user feedback

Tolong buatkan implementasi Android (Java) yang lengkap dan production-ready dengan semua requirements di atas!

