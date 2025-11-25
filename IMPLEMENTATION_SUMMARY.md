# 📱 BendaKu Android - Summary Implementasi & Dokumentasi

## 🎯 Overview
Aplikasi BendaKu adalah sistem pelaporan barang hilang/ditemukan di kampus UPNVJ dengan fitur klaim. Aplikasi menggunakan arsitektur **MVVM (Model-View-ViewModel)** dan terintegrasi dengan backend Strapi.

---

## 🔄 Alur Aplikasi (Application Flow)

### 1. **Startup & Authentication Flow**
```
SplashActivity (Start)
    ↓
    ├─→ Initialize TokenManager
    ├─→ Check Login Status (TokenManager/SessionManager)
    ↓
    ├─→ Jika Sudah Login → MainActivity
    └─→ Jika Belum Login → LoginActivity
```

### 2. **Authentication Flow**
```
LoginActivity / RegisterActivity
    ↓
    ├─→ User Input (email, password, dll)
    ↓
    ├─→ AuthViewModel.register() / AuthViewModel.login()
    ↓
    ├─→ AuthRepository → API Call ke Strapi
    ↓
    ├─→ Success → TokenManager.saveToken(JWT)
    ├─→ Success → Navigate ke MainActivity
    └─→ Error → Show Error Message
```

### 3. **Main Application Flow**
```
MainActivity
    ↓
    ├─→ ViewPager dengan 2 Tabs:
    │   ├─→ Tab 1: Lost Items (ItemListFragment)
    │   └─→ Tab 2: Found Items (ItemListFragment)
    ↓
    ├─→ ItemListFragment
    │   ├─→ ItemViewModel.loadItems("lost" / "found")
    │   ├─→ ItemRepository.getItems()
    │   └─→ Display items di RecyclerView
    ↓
    ├─→ FAB Click → AddReportActivity
    │   ├─→ User input item details + image
    │   ├─→ ItemViewModel.createItem()
    │   └─→ ItemRepository.createItem() → API
    ↓
    └─→ Item Click → ItemDetailActivity
        ├─→ ItemViewModel.loadItem(id)
        ├─→ Show item details
        └─→ Claim Button → ClaimFormActivity
            ├─→ ClaimViewModel.createClaim()
            └─→ ClaimRepository.createClaim() → API
```

### 4. **Admin Flow**
```
AdminPanelActivity (jika user.isAdmin == true)
    ↓
    ├─→ ClaimViewModel.loadClaims("pending")
    ├─→ Display pending claims
    ↓
    ├─→ Approve Claim
    │   └─→ ClaimViewModel.approveClaim(id)
    └─→ Reject Claim
        └─→ ClaimViewModel.rejectClaim(id, notes)
```

---

## 📁 Struktur File & Fungsinya

### 🏗️ **Build Configuration**

#### `app/build.gradle.kts`
**Fungsi:** Konfigurasi build Android
- **BuildConfig:** Mengaktifkan `buildConfig = true` untuk akses `BuildConfig.API_BASE_URL`
- **Base URL Configuration:**
  - Debug: `http://10.0.2.2:1337/api/` (untuk Android Emulator)
  - Release: `http://[server-ip]:1337/api/` (untuk device real)
- **Dependencies:**
  - Retrofit 2.9.0 (HTTP client)
  - OkHttp 4.12.0 (HTTP interceptor)
  - Gson Converter (JSON parsing)
  - ViewModel & LiveData (MVVM architecture)
  - Glide (Image loading)

---

### 📦 **Models (Data Classes)**

#### `model/User.java`
**Fungsi:** Model data untuk user
- **Fields:**
  - `id` (int) - User ID
  - `email` (String) - Email user
  - `username` (String) - Username
  - `fullName` (String) - Nama lengkap
  - `phone` (String) - Nomor telepon
  - `studentId` (String) - NIM mahasiswa
  - `isAdmin` (boolean) - Status admin
- **Digunakan di:** AuthRepository, AuthViewModel, semua activity yang perlu user info

#### `model/Item.java`
**Fungsi:** Model data untuk barang hilang/ditemukan
- **Fields:**
  - `id` (int) - Item ID
  - `name` (String) - Nama barang
  - `description` (String) - Deskripsi
  - `location` (String) - Lokasi ditemukan/hilang
  - `dateTime` (String) - Waktu (format: "03 Okt 2024, 10:30")
  - `imageUrl` (String) - URL gambar
  - `type` (String) - "lost" atau "found"
  - `status` (String) - "open", "claimed", "resolved"
  - `reporterId`, `reporterName`, `reporterPhone` - Info pelapor
  - `createdAt`, `updatedAt` - Timestamps
- **Digunakan di:** ItemRepository, ItemViewModel, ItemListFragment, ItemDetailActivity

#### `model/Claim.java`
**Fungsi:** Model data untuk klaim barang
- **Fields:**
  - `id` (int) - Claim ID
  - `itemId` (Integer) - ID item yang diklaim
  - `claimerId`, `claimerName`, `claimerPhone` - Info pengklaim
  - `description` (String) - Deskripsi klaim
  - `proofImageUrl` (String) - URL bukti gambar
  - `status` (String) - "pending", "approved", "rejected"
  - `adminNotes` (String) - Catatan admin
  - `createdAt`, `updatedAt` - Timestamps
- **Digunakan di:** ClaimRepository, ClaimViewModel, ClaimFormActivity, AdminPanelActivity

#### `model/AuthResponse.java`
**Fungsi:** Model response untuk login/register
- **Fields:**
  - `jwt` (String) - JWT token untuk authentication
  - `user` (User) - Data user yang login
- **Digunakan di:** AuthRepository untuk menangani response login/register

#### `model/ApiResponse.java`
**Fungsi:** Wrapper generic untuk semua API response
- **Fields:**
  - `success` (boolean) - Status sukses/gagal
  - `message` (String) - Pesan response
  - `data` (T) - Data response (generic type)
- **Digunakan di:** Semua API calls untuk standarisasi response format

---

### 🔌 **Network Layer**

#### `api/ApiClient.java`
**Fungsi:** Singleton Retrofit client dengan konfigurasi
- **Fitur:**
  - Singleton pattern (satu instance untuk seluruh app)
  - JWT Token Interceptor: Otomatis menambahkan `Authorization: Bearer <token>` ke setiap request
  - HTTP Logging Interceptor: Log semua request/response untuk debugging
  - Base URL dari `BuildConfig.API_BASE_URL`
- **Method:**
  - `getInstance()` - Get singleton instance
  - `getApiService()` - Get BendaKuApiService interface
- **Digunakan di:** Semua Repository classes

#### `api/BendaKuApiService.java`
**Fungsi:** Interface Retrofit untuk semua API endpoints
- **Authentication Endpoints:**
  - `POST auth/local/register` - Registrasi user baru
  - `POST auth/local` - Login user
  - `GET users/me` - Get current user profile
- **Items Endpoints:**
  - `GET items` - Get semua items (dengan filter type)
  - `GET items/{id}` - Get detail item
  - `POST items` - Create item (multipart dengan image)
  - `PUT items/{id}` - Update item
  - `DELETE items/{id}` - Delete item
- **Claims Endpoints:**
  - `GET claims` - Get semua claims (dengan filter status)
  - `GET claims/{id}` - Get detail claim
  - `POST claims` - Create claim (multipart dengan proof image)
  - `PATCH claims/{id}/approve` - Approve claim (admin only)
  - `PATCH claims/{id}/reject` - Reject claim (admin only)
- **Admin Endpoints:**
  - `GET admin/users` - Get semua users
  - `GET admin/statistics` - Get statistics
- **Digunakan di:** Semua Repository classes untuk melakukan API calls

---

### 🗄️ **Repository Layer (Business Logic)**

#### `repository/AuthRepository.java`
**Fungsi:** Repository untuk authentication operations
- **Methods:**
  - `register()` - Registrasi user baru, otomatis save JWT token
  - `login()` - Login user, otomatis save JWT token
  - `getCurrentUser()` - Get current user profile (menggunakan JWT dari TokenManager)
  - `logout()` - Clear JWT token
- **Callback Interfaces:**
  - `AuthCallback` - Callback untuk register/login
  - `UserCallback` - Callback untuk get user
- **Error Handling:**
  - Auto-logout jika token expired (401 response)
  - Network error handling
- **Digunakan di:** AuthViewModel

#### `repository/ItemRepository.java`
**Fungsi:** Repository untuk item operations
- **Methods:**
  - `getItems(type)` - Get items dengan filter type ("lost" atau "found")
  - `getItem(id)` - Get detail item by ID
  - `createItem()` - Create item baru dengan multipart upload (image + data JSON)
  - `updateItem()` - Update item existing
  - `deleteItem()` - Delete item
- **Callback Interfaces:**
  - `ItemsCallback` - Callback untuk get items list
  - `ItemCallback` - Callback untuk single item operation
  - `DeleteCallback` - Callback untuk delete operation
- **Multipart Upload:**
  - Membuat JSON data untuk field "data"
  - Upload image sebagai MultipartBody.Part
- **Digunakan di:** ItemViewModel

#### `repository/ClaimRepository.java`
**Fungsi:** Repository untuk claim operations
- **Methods:**
  - `getClaims(status)` - Get claims dengan filter status ("pending", "approved", "rejected")
  - `getClaim(id)` - Get detail claim by ID
  - `createClaim()` - Create claim baru dengan multipart upload (proof image + data JSON)
  - `approveClaim(id)` - Approve claim (admin only)
  - `rejectClaim(id, adminNotes)` - Reject claim dengan catatan (admin only)
- **Callback Interfaces:**
  - `ClaimsCallback` - Callback untuk get claims list
  - `ClaimCallback` - Callback untuk single claim operation
- **Digunakan di:** ClaimViewModel

---

### 🎨 **ViewModel Layer (MVVM)**

#### `viewmodel/AuthViewModel.java`
**Fungsi:** ViewModel untuk authentication, menghubungkan UI dengan Repository
- **LiveData:**
  - `userLiveData` - User data (untuk update UI setelah login)
  - `errorLiveData` - Error messages
  - `isLoadingLiveData` - Loading state
- **Methods:**
  - `register()` - Panggil AuthRepository.register()
  - `login()` - Panggil AuthRepository.login()
  - `getCurrentUser()` - Panggil AuthRepository.getCurrentUser()
  - `logout()` - Clear token dan reset user data
- **Digunakan di:** LoginActivity, RegisterActivity

#### `viewmodel/ItemViewModel.java`
**Fungsi:** ViewModel untuk item operations
- **LiveData:**
  - `itemsLiveData` - List of items
  - `itemLiveData` - Single item (untuk detail atau created item)
  - `errorLiveData` - Error messages
  - `isLoadingLiveData` - Loading state
- **Methods:**
  - `loadItems(type)` - Load items list
  - `loadItem(id)` - Load single item detail
  - `createItem()` - Create new item
  - `updateItem()` - Update existing item
  - `deleteItem()` - Delete item
- **Digunakan di:** ItemListFragment, ItemDetailActivity, AddReportActivity

#### `viewmodel/ClaimViewModel.java`
**Fungsi:** ViewModel untuk claim operations
- **LiveData:**
  - `claimsLiveData` - List of claims
  - `claimLiveData` - Single claim
  - `errorLiveData` - Error messages
  - `isLoadingLiveData` - Loading state
- **Methods:**
  - `loadClaims(status)` - Load claims list dengan filter
  - `loadClaim(id)` - Load single claim detail
  - `createClaim()` - Create new claim
  - `approveClaim(id)` - Approve claim (admin)
  - `rejectClaim(id, notes)` - Reject claim (admin)
- **Digunakan di:** ClaimFormActivity, AdminPanelActivity

---

### 🛠️ **Utilities**

#### `utils/TokenManager.java`
**Fungsi:** Manager untuk JWT token storage menggunakan SharedPreferences
- **Methods:**
  - `init(Context)` - Initialize TokenManager (dipanggil di SplashActivity)
  - `saveToken(String)` - Save JWT token
  - `getToken()` - Get JWT token (untuk API interceptor)
  - `clearToken()` - Clear token (untuk logout)
  - `isLoggedIn()` - Check apakah user sudah login
- **Storage:** SharedPreferences dengan key "jwt_token"
- **Digunakan di:** 
  - ApiClient (untuk interceptor)
  - AuthRepository (untuk save/clear token)
  - SplashActivity (untuk check login status)

#### `utils/SessionManager.java`
**Fungsi:** Manager untuk session management (legacy, masih digunakan untuk backward compatibility)
- **Methods:**
  - `createSession(User)` - Save user session
  - `getUser()` - Get current user
  - `isLoggedIn()` - Check login status
  - `logout()` - Clear session
- **Digunakan di:** Beberapa Activity yang masih menggunakan old implementation

---

### 🎬 **Activities & Fragments**

#### `uts/SplashActivity.java`
**Fungsi:** Splash screen dan app initialization
- **Flow:**
  1. Initialize `TokenManager.init(this)` - **PENTING!**
  2. Check login status (TokenManager atau SessionManager)
  3. Navigate ke MainActivity (jika logged in) atau LoginActivity (jika belum)
- **Delay:** 2 detik splash screen
- **Digunakan:** Entry point aplikasi

#### `uts/LoginActivity.java`
**Fungsi:** Activity untuk login user
- **Flow:**
  1. User input email/identifier dan password
  2. Panggil `AuthViewModel.login()`
  3. Jika success → Navigate ke MainActivity
  4. Jika error → Show error message
- **Seharusnya menggunakan:** AuthViewModel (perlu diupdate dari old ApiService)

#### `uts/RegisterActivity.java`
**Fungsi:** Activity untuk registrasi user baru
- **Flow:**
  1. User input: email, password, username, fullName, phone, studentId
  2. Panggil `AuthViewModel.register()`
  3. Jika success → Navigate ke MainActivity
  4. Jika error → Show error message
- **Seharusnya menggunakan:** AuthViewModel (perlu diupdate dari old ApiService)

#### `uts/MainActivity.java`
**Fungsi:** Main activity dengan ViewPager untuk Lost/Found items
- **Components:**
  - ViewPager2 dengan 2 tabs (Lost Items, Found Items)
  - FAB untuk add new report
  - Search functionality
- **Fragments:**
  - ItemListFragment (untuk Lost items)
  - ItemListFragment (untuk Found items)
- **Seharusnya menggunakan:** ItemViewModel (perlu diupdate)

#### `uts/ItemListFragment.java`
**Fungsi:** Fragment untuk menampilkan list items
- **Flow:**
  1. Observe `ItemViewModel.itemsLiveData`
  2. Panggil `ItemViewModel.loadItems("lost" / "found")`
  3. Display items di RecyclerView
  4. Handle pull-to-refresh
- **Seharusnya menggunakan:** ItemViewModel (perlu diupdate)

#### `uts/ItemDetailActivity.java`
**Fungsi:** Activity untuk menampilkan detail item
- **Flow:**
  1. Receive item ID dari intent
  2. Panggil `ItemViewModel.loadItem(id)`
  3. Display item details
  4. Button "Claim" → Navigate ke ClaimFormActivity
- **Seharusnya menggunakan:** ItemViewModel (perlu diupdate)

#### `uts/AddReportActivity.java`
**Fungsi:** Activity untuk membuat laporan item baru
- **Flow:**
  1. User input: name, description, location, dateTime, type, image
  2. Panggil `ItemViewModel.createItem()`
  3. Jika success → Navigate back ke MainActivity
  4. Jika error → Show error message
- **Seharusnya menggunakan:** ItemViewModel (perlu diupdate)

#### `uts/ClaimFormActivity.java`
**Fungsi:** Activity untuk membuat klaim
- **Flow:**
  1. Receive item ID dari intent
  2. User input: description, proof image
  3. Panggil `ClaimViewModel.createClaim()`
  4. Jika success → Show success message
- **Seharusnya menggunakan:** ClaimViewModel (perlu diupdate)

#### `uts/AdminPanelActivity.java`
**Fungsi:** Activity untuk admin mengelola claims
- **Flow:**
  1. Check jika user isAdmin
  2. Panggil `ClaimViewModel.loadClaims("pending")`
  3. Display pending claims
  4. Approve/Reject buttons → `ClaimViewModel.approveClaim()` / `rejectClaim()`
- **Seharusnya menggunakan:** ClaimViewModel (perlu diupdate)

---

## 🔐 Authentication Flow Detail

```
1. User Login/Register
   ↓
2. AuthRepository → API Call ke Strapi
   ↓
3. Response berisi JWT token + User data
   ↓
4. TokenManager.saveToken(JWT) → Simpan di SharedPreferences
   ↓
5. Setiap API call berikutnya:
   ApiClient Interceptor → Ambil token dari TokenManager
   → Tambahkan header: "Authorization: Bearer <JWT>"
   ↓
6. Jika token expired (401):
   → Auto clear token
   → Redirect ke LoginActivity
```

---

## 📤 Data Flow (Contoh: Create Item)

```
1. User di AddReportActivity
   Input: name, description, location, image, dll
   ↓
2. AddReportActivity → ItemViewModel.createItem()
   ↓
3. ItemViewModel → ItemRepository.createItem()
   ↓
4. ItemRepository:
   - Buat JSON data dari fields
   - Convert image File → MultipartBody.Part
   - Panggil BendaKuApiService.createItem(data, image)
   ↓
5. ApiClient:
   - Interceptor tambahkan JWT token
   - Send request ke Strapi
   ↓
6. Strapi Response → ApiResponse<Item>
   ↓
7. ItemRepository callback → ItemViewModel
   ↓
8. ItemViewModel update LiveData:
   - itemLiveData.setValue(item) → Success
   - errorLiveData.setValue(error) → Error
   ↓
9. AddReportActivity observe LiveData:
   - Jika success → Show success, navigate back
   - Jika error → Show error message
```

---

## ⚠️ Catatan Penting

### 1. **Initialization**
- **TokenManager.init()** HARUS dipanggil di SplashActivity.onCreate()
- Tanpa ini, JWT token tidak akan tersimpan/dibaca

### 2. **Base URL Configuration**
- Debug: `http://10.0.2.2:1337/api/` (untuk emulator)
- Release: Ganti `[server-ip]` dengan IP server sebenarnya
- Untuk device real, gunakan IP komputer (bukan localhost)

### 3. **Activities Update Required**
- Activities masih menggunakan old `ApiService`
- Perlu diupdate untuk menggunakan ViewModels:
  - LoginActivity → AuthViewModel
  - RegisterActivity → AuthViewModel
  - ItemListFragment → ItemViewModel
  - ItemDetailActivity → ItemViewModel
  - AddReportActivity → ItemViewModel
  - ClaimFormActivity → ClaimViewModel
  - AdminPanelActivity → ClaimViewModel

### 4. **Error Handling**
- Semua Repository sudah handle:
  - Network errors
  - API errors (400, 401, 403, 404, 500)
  - Auto-logout jika token expired (401)

### 5. **Date Format**
- Format: `"dd MMM yyyy, HH:mm"` (contoh: "03 Okt 2024, 10:30")
- Gunakan SimpleDateFormat untuk parsing/formatting

---

## 🚀 Next Steps

1. **Update Activities** untuk menggunakan ViewModels
2. **Test API Integration** dengan backend Strapi
3. **Handle Edge Cases:**
   - No internet connection
   - Server timeout
   - Image upload failure
4. **Add Loading Indicators** di UI
5. **Add Error Dialogs** untuk user feedback
6. **Implement Pull-to-Refresh** di ItemListFragment
7. **Add Image Compression** sebelum upload (optional)

---

## 📚 Resources

- **Backend API Documentation:** `ANDROID_INTEGRATION_PROMPT_JAVA.md`
- **Base URL:** Configure di `app/build.gradle.kts` → `buildConfigField`
- **Testing Credentials:** Lihat di `ANDROID_INTEGRATION_PROMPT_JAVA.md`

---

**Dibuat:** 2024  
**Arsitektur:** MVVM (Model-View-ViewModel)  
**Networking:** Retrofit + OkHttp  
**Backend:** Strapi CMS

