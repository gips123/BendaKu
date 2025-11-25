# 🏗️ BendaKu - Architecture Diagram & File Structure

## 📊 Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                        UI Layer                             │
│  (Activities, Fragments, Adapters)                          │
│  - SplashActivity                                           │
│  - LoginActivity, RegisterActivity                          │
│  - MainActivity, ItemListFragment                           │
│  - ItemDetailActivity, AddReportActivity                     │
│  - ClaimFormActivity, AdminPanelActivity                    │
└───────────────────────┬─────────────────────────────────────┘
                        │ Observe LiveData
                        │ Call ViewModel methods
                        ▼
┌─────────────────────────────────────────────────────────────┐
│                    ViewModel Layer                           │
│  (MVVM - Business Logic)                                     │
│  - AuthViewModel                                             │
│  - ItemViewModel                                             │
│  - ClaimViewModel                                            │
└───────────────────────┬─────────────────────────────────────┘
                        │ Call Repository methods
                        │ Return LiveData
                        ▼
┌─────────────────────────────────────────────────────────────┐
│                   Repository Layer                           │
│  (Data Access & API Calls)                                   │
│  - AuthRepository                                            │
│  - ItemRepository                                            │
│  - ClaimRepository                                           │
└───────────────────────┬─────────────────────────────────────┘
                        │ Use ApiService
                        │ Handle callbacks
                        ▼
┌─────────────────────────────────────────────────────────────┐
│                    Network Layer                             │
│  - ApiClient (Retrofit Singleton)                           │
│  - BendaKuApiService (API Interface)                        │
│  - TokenManager (JWT Management)                            │
└───────────────────────┬─────────────────────────────────────┘
                        │ HTTP Requests
                        │ JWT Token in Header
                        ▼
┌─────────────────────────────────────────────────────────────┐
│                    Backend (Strapi)                          │
│  http://localhost:1337/api                                   │
└─────────────────────────────────────────────────────────────┘
```

---

## 📁 File Structure dengan Fungsi

```
app/src/main/java/com/example/
│
├── bendaku/                          # Core Package
│   │
│   ├── api/                          # Network Layer
│   │   ├── ApiClient.java          # Retrofit client dengan JWT interceptor
│   │   ├── ApiService.java         # [LEGACY] Old API service
│   │   └── BendaKuApiService.java  # ✅ New API service (Strapi endpoints)
│   │
│   ├── model/                      # Data Models
│   │   ├── ApiResponse.java        # Generic response wrapper
│   │   ├── AuthResponse.java       # Login/Register response (JWT + User)
│   │   ├── User.java               # User model
│   │   ├── Item.java               # Item model (lost/found)
│   │   └── Claim.java              # Claim model
│   │
│   ├── repository/                 # Repository Layer (Business Logic)
│   │   ├── AuthRepository.java     # Authentication operations
│   │   ├── ItemRepository.java     # Item CRUD operations
│   │   └── ClaimRepository.java    # Claim CRUD operations
│   │
│   ├── viewmodel/                  # ViewModel Layer (MVVM)
│   │   ├── AuthViewModel.java      # Auth ViewModel dengan LiveData
│   │   ├── ItemViewModel.java      # Item ViewModel dengan LiveData
│   │   └── ClaimViewModel.java     # Claim ViewModel dengan LiveData
│   │
│   └── utils/                      # Utilities
│       ├── TokenManager.java       # ✅ JWT token management (SharedPreferences)
│       ├── SessionManager.java     # [LEGACY] Old session manager
│       └── DummyDataHelper.java    # Dummy data untuk testing
│
└── uts/                            # UI Package (Activities & Fragments)
    ├── SplashActivity.java         # ✅ Entry point, init TokenManager
    ├── LoginActivity.java          # [NEEDS UPDATE] Use AuthViewModel
    ├── RegisterActivity.java       # [NEEDS UPDATE] Use AuthViewModel
    ├── MainActivity.java           # Main activity dengan ViewPager
    ├── ItemListFragment.java       # [NEEDS UPDATE] Use ItemViewModel
    ├── ItemDetailActivity.java     # [NEEDS UPDATE] Use ItemViewModel
    ├── AddReportActivity.java      # [NEEDS UPDATE] Use ItemViewModel
    ├── ClaimFormActivity.java      # [NEEDS UPDATE] Use ClaimViewModel
    ├── AdminPanelActivity.java     # [NEEDS UPDATE] Use ClaimViewModel
    └── utils/                      # UI Utilities
        ├── NotificationHelper.java
        └── SearchFilter.java
```

---

## 🔄 Complete Flow Diagram

### Flow 1: User Login
```
┌──────────────┐
│SplashActivity│
└──────┬───────┘
       │ Check TokenManager.isLoggedIn()
       │
       ▼
┌──────────────┐      No Token      ┌──────────────┐
│TokenManager  │ ──────────────────▶│LoginActivity │
└──────────────┘                    └──────┬───────┘
                                           │ User Input
                                           │ (email, password)
                                           ▼
                                    ┌──────────────┐
                                    │AuthViewModel │
                                    │  .login()    │
                                    └──────┬───────┘
                                           │
                                           ▼
                                    ┌──────────────┐
                                    │AuthRepository│
                                    │  .login()    │
                                    └──────┬───────┘
                                           │
                                           ▼
                                    ┌──────────────┐
                                    │ApiClient     │
                                    │ + JWT Header │
                                    └──────┬───────┘
                                           │
                                           ▼ POST /auth/local
                                    ┌──────────────┐
                                    │Strapi Backend│
                                    └──────┬───────┘
                                           │ Response: {jwt, user}
                                           ▼
                                    ┌──────────────┐
                                    │TokenManager  │
                                    │.saveToken()  │
                                    └──────┬───────┘
                                           │
                                           ▼
                                    ┌──────────────┐
                                    │MainActivity  │
                                    └──────────────┘
```

### Flow 2: Create Item (Lost/Found Report)
```
┌──────────────┐
│MainActivity  │
│  FAB Click   │
└──────┬───────┘
       │
       ▼
┌──────────────────┐
│AddReportActivity │
│  User Input:     │
│  - name          │
│  - description   │
│  - location      │
│  - type          │
│  - image         │
└──────┬───────────┘
       │
       ▼
┌──────────────┐
│ItemViewModel │
│.createItem() │
└──────┬───────┘
       │
       ▼
┌──────────────┐
│ItemRepository│
│.createItem() │
└──────┬───────┘
       │
       │ Create JSON data
       │ Convert image → MultipartBody.Part
       │
       ▼
┌──────────────┐
│ApiClient     │
│ + JWT Header │
└──────┬───────┘
       │
       ▼ POST /items (multipart)
┌──────────────┐
│Strapi Backend│
└──────┬───────┘
       │ Response: ApiResponse<Item>
       ▼
┌──────────────┐
│ItemViewModel │
│Update LiveData│
└──────┬───────┘
       │
       ▼
┌──────────────┐
│AddReportActivity│
│Observe LiveData│
│Show Success   │
│Navigate Back  │
└───────────────┘
```

### Flow 3: View Items List
```
┌──────────────┐
│MainActivity  │
│  Tab Selected│
└──────┬───────┘
       │
       ▼
┌──────────────────┐
│ItemListFragment  │
│  onCreateView()   │
└──────┬───────────┘
       │
       ▼
┌──────────────┐
│ItemViewModel │
│.loadItems()  │
│  ("lost" or  │
│   "found")   │
└──────┬───────┘
       │
       ▼
┌──────────────┐
│ItemRepository│
│.getItems()   │
└──────┬───────┘
       │
       ▼
┌──────────────┐
│ApiClient     │
│GET /items?   │
│filters[type] │
└──────┬───────┘
       │
       ▼
┌──────────────┐
│Strapi Backend│
└──────┬───────┘
       │ Response: ApiResponse<List<Item>>
       ▼
┌──────────────┐
│ItemViewModel │
│itemsLiveData │
│.setValue()   │
└──────┬───────┘
       │
       ▼
┌──────────────┐
│ItemListFragment│
│Observe LiveData│
│Update RecyclerView│
└───────────────┘
```

---

## 🔐 JWT Token Flow

```
┌─────────────────────────────────────────────────────────┐
│                    JWT Token Lifecycle                   │
└─────────────────────────────────────────────────────────┘

1. Login/Register
   ┌──────────────┐
   │Strapi Response│ → {jwt: "eyJhbGc...", user: {...}}
   └──────┬───────┘
          │
          ▼
   ┌──────────────┐
   │TokenManager  │ → saveToken(jwt)
   │SharedPreferences│ → Key: "jwt_token"
   └──────┬───────┘
          │
          ▼
2. Every API Call
   ┌──────────────┐
   │ApiClient      │
   │Interceptor    │ → getToken() dari TokenManager
   └──────┬───────┘
          │
          ▼
   ┌──────────────┐
   │Request Header│ → "Authorization: Bearer eyJhbGc..."
   └──────┬───────┘
          │
          ▼
   ┌──────────────┐
   │Strapi Backend│ → Validate JWT → Process Request
   └──────────────┘
          │
          ▼
3. Token Expired (401)
   ┌──────────────┐
   │Response 401   │ → Unauthorized
   └──────┬───────┘
          │
          ▼
   ┌──────────────┐
   │AuthRepository│ → clearToken()
   │TokenManager  │ → Remove from SharedPreferences
   └──────┬───────┘
          │
          ▼
   ┌──────────────┐
   │Redirect      │ → LoginActivity
   └──────────────┘
```

---

## 📦 Data Models Relationship

```
┌──────────┐
│   User   │
│  (id,    │
│  email,  │
│  name)   │
└────┬─────┘
     │
     │ 1
     │
     │ N
┌────▼─────┐      ┌──────────┐
│   Item   │      │  Claim   │
│  (id,    │◄─────│  (id,    │
│  name,   │  N   │  itemId, │
│  type,   │      │  status) │
│  image)  │      └──────────┘
└──────────┘
     │
     │ N
     │
     │ 1
┌────▼─────┐
│ Reporter │
│  (User)  │
└──────────┘
```

---

## 🎯 Key Components Summary

| Component | File | Purpose |
|-----------|------|---------|
| **Entry Point** | `SplashActivity.java` | Initialize app, check login, navigate |
| **Token Storage** | `TokenManager.java` | JWT token management (SharedPreferences) |
| **API Client** | `ApiClient.java` | Retrofit singleton dengan JWT interceptor |
| **API Interface** | `BendaKuApiService.java` | Semua Strapi endpoints |
| **Auth Logic** | `AuthRepository.java` | Login, register, get user |
| **Item Logic** | `ItemRepository.java` | CRUD items dengan image upload |
| **Claim Logic** | `ClaimRepository.java` | CRUD claims dengan proof upload |
| **Auth UI Logic** | `AuthViewModel.java` | LiveData untuk auth operations |
| **Item UI Logic** | `ItemViewModel.java` | LiveData untuk item operations |
| **Claim UI Logic** | `ClaimViewModel.java` | LiveData untuk claim operations |

---

## ✅ Implementation Status

### ✅ Completed
- [x] BuildConfig dengan API_BASE_URL
- [x] Models (User, Item, Claim, AuthResponse, ApiResponse)
- [x] TokenManager untuk JWT
- [x] ApiClient dengan JWT interceptor
- [x] BendaKuApiService dengan semua endpoints
- [x] Repositories (Auth, Item, Claim)
- [x] ViewModels (Auth, Item, Claim)
- [x] TokenManager initialization di SplashActivity

### 🔄 Needs Update
- [ ] LoginActivity → Use AuthViewModel
- [ ] RegisterActivity → Use AuthViewModel
- [ ] ItemListFragment → Use ItemViewModel
- [ ] ItemDetailActivity → Use ItemViewModel
- [ ] AddReportActivity → Use ItemViewModel
- [ ] ClaimFormActivity → Use ClaimViewModel
- [ ] AdminPanelActivity → Use ClaimViewModel

---

**Last Updated:** 2024  
**Architecture:** MVVM Pattern  
**Status:** Core implementation complete, UI integration pending

