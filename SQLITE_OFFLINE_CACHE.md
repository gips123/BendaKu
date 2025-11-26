# SQLite Offline Cache Implementation

## Overview
Aplikasi BendaKu sekarang menggunakan SQLite database lokal (Room) untuk caching data dari Strapi API. Ini memungkinkan aplikasi untuk:
- Menampilkan data terakhir yang tersimpan ketika Strapi offline
- Memuat data lebih cepat dari database lokal
- Sync data dari API di background

## Architecture

### 1. Database Layer
- **AppDatabase**: Room database instance
- **LocalItem**: Entity untuk menyimpan data items
- **LocalClaim**: Entity untuk menyimpan data claims
- **ItemDao**: Data Access Object untuk items
- **ClaimDao**: Data Access Object untuk claims

### 2. Repository Layer
- **ItemRepository**: Mengelola sync antara API dan database untuk items
- **ClaimRepository**: Mengelola sync antara API dan database untuk claims

### 3. Flow Data
1. **Load Data**: 
   - Repository pertama kali memuat data dari database lokal (instant)
   - Kemudian sync dari API di background
   - Update database dengan data terbaru

2. **Offline Mode**:
   - Jika API tidak tersedia, aplikasi tetap menampilkan data dari database
   - User tidak akan melihat error, hanya data terakhir yang tersimpan

3. **Online Mode**:
   - Data dari API akan di-sync ke database
   - Database selalu ter-update dengan data terbaru

## Files Created

### Database Entities
- `app/src/main/java/com/example/bendaku/database/entity/LocalItem.java`
- `app/src/main/java/com/example/bendaku/database/entity/LocalClaim.java`

### DAO Interfaces
- `app/src/main/java/com/example/bendaku/database/dao/ItemDao.java`
- `app/src/main/java/com/example/bendaku/database/dao/ClaimDao.java`

### Database
- `app/src/main/java/com/example/bendaku/database/AppDatabase.java`

### Repositories
- `app/src/main/java/com/example/bendaku/repository/ItemRepository.java`
- `app/src/main/java/com/example/bendaku/repository/ClaimRepository.java`

## Updated Files

### Activities/Fragments
- `ItemListFragment.java`: Sekarang menggunakan `ItemRepository` untuk load data
- `AdminPanelActivity.java`: Sekarang menggunakan `ClaimRepository` untuk load data

### Build Configuration
- `app/build.gradle.kts`: Menambahkan Room database dependencies

## Dependencies Added

```kotlin
// Room Database
val roomVersion = "2.6.1"
implementation("androidx.room:room-runtime:$roomVersion")
annotationProcessor("androidx.room:room-compiler:$roomVersion")
```

## How It Works

### ItemListFragment
1. User membuka halaman home
2. `ItemRepository.getItems()` dipanggil
3. Data langsung dimuat dari database (instant display)
4. Background: Sync dari API dan update database
5. Jika API tersedia, data akan ter-update otomatis

### AdminPanelActivity
1. Admin membuka panel admin
2. `ClaimRepository.getPendingClaims()` dipanggil
3. Data langsung dimuat dari database (instant display)
4. Background: Sync dari API dan update database
5. Hanya menampilkan claims dengan status "pending" dan item statusItem "open"

## Benefits

1. **Offline Support**: User bisa melihat data terakhir meskipun Strapi offline
2. **Faster Loading**: Data dari database lebih cepat daripada API call
3. **Better UX**: Tidak ada loading yang lama, data langsung muncul
4. **Automatic Sync**: Data selalu ter-update ketika API tersedia

## Database Schema

### Items Table
- `id` (Primary Key, Auto Increment)
- `itemId` (Strapi ID, Unique)
- `documentId` (Strapi documentId)
- `name`, `description`, `location`, `dateTime`
- `type` (lost/found)
- `statusItem` (open/claimed/resolved)
- `reporterName`, `reporterPhone`
- `imageUrl`, `imageId`
- `createdAt`, `updatedAt`
- `lastSyncTime` (timestamp sync terakhir)

### Claims Table
- `id` (Primary Key, Auto Increment)
- `claimId` (Strapi ID, Unique)
- `documentId` (Strapi documentId)
- `itemId`, `itemName`, `itemStatusItem`
- `claimerName`, `claimerPhone`, `claimerUsername`
- `description`, `statusClaim`, `adminNotes`
- `proofImageUrl`, `imageId`
- `claimerKtmUrl`, `claimerKtmId`
- `createdAt`, `updatedAt`
- `lastSyncTime` (timestamp sync terakhir)

## Notes

- Database akan otomatis dibuat saat pertama kali aplikasi dijalankan
- Data akan ter-sync setiap kali user membuka halaman yang relevan
- Jika API offline, user akan melihat data terakhir yang tersimpan
- Database menggunakan `REPLACE` strategy untuk update data yang sudah ada

