# Broadcast Receiver Implementation - Offline/Online Mode

## Overview
Aplikasi BendaKu sekarang memiliki Broadcast Receiver untuk mendeteksi perubahan status koneksi internet dan menampilkan indikator offline mode. Ketika HP offline, aplikasi akan menampilkan banner "You are offline" dan menggunakan data dari SQLite lokal. Ketika HP online, aplikasi akan auto-sync dengan Strapi.

## Fitur yang Diimplementasikan

### 1. Network State Detection
- **NetworkStateReceiver**: Mendeteksi perubahan status koneksi internet
- **Auto-sync**: Otomatis sync data dari Strapi ketika koneksi kembali
- **Offline Indicator**: Menampilkan banner "You are offline" ketika tidak ada koneksi

### 2. Offline Mode
- Aplikasi menggunakan data dari SQLite/Room database
- User tetap bisa melihat data terakhir yang tersimpan
- Tidak ada error message, hanya indikator offline
- Status aplikasi berubah menjadi "Offline Mode"

### 3. Online Mode
- Auto-sync dengan Strapi ketika koneksi kembali
- Data terbaru disimpan ke Room database
- Offline indicator otomatis hilang

## Files Created

### 1. NetworkStateReceiver.java
**Path**: `app/src/main/java/com/example/bendaku/receiver/NetworkStateReceiver.java`

**Fungsi**:
- Mendeteksi perubahan status koneksi internet
- Trigger auto-sync ketika online
- Broadcast status ke komponen lain

**Methods**:
- `onReceive()`: Handle broadcast intent
- `isNetworkAvailable()`: Check apakah ada koneksi internet
- `syncDataFromStrapi()`: Sync data dari Strapi ke database lokal

## Files Updated

### 1. MainActivity.java
**Changes**:
- Menambahkan `NetworkStateReceiver` registration
- Method `setupNetworkReceiver()`: Register receiver
- Method `checkNetworkState()`: Check status koneksi saat activity start
- Method `updateOfflineIndicator()`: Show/hide offline banner
- Lifecycle: Register di `onCreate()`, unregister di `onDestroy()`

### 2. activity_main.xml
**Changes**:
- Menambahkan offline indicator card langsung di layout (antara header dan TabLayout)
- Posisi baru memastikan banner selalu terlihat di bawah header

### 3. AndroidManifest.xml
**Changes**:
- Menambahkan permission `ACCESS_NETWORK_STATE`

### 4. ItemRepository.java
**Changes**:
- Menambahkan method `syncAllItems()` untuk sync semua items

### 5. ClaimRepository.java
**Changes**:
- Menambahkan method `syncAllClaims()` untuk sync semua claims

## How It Works

### Flow Offline → Online
1. User kehilangan koneksi internet
2. `NetworkStateReceiver` mendeteksi perubahan
3. `MainActivity` menerima callback
4. Offline indicator muncul dengan banner merah "You are offline"
5. Aplikasi menggunakan data dari SQLite (tidak ada error)
6. User kembali ke area ber-sinyal
7. `NetworkStateReceiver` mendeteksi koneksi kembali
8. Auto-sync data dari Strapi ke database
9. Offline indicator hilang
10. Data terbaru tersedia di aplikasi

### Flow Online → Offline
1. User sedang online, melihat data dari Strapi
2. Koneksi internet terputus
3. `NetworkStateReceiver` mendeteksi perubahan
4. Offline indicator muncul
5. Aplikasi switch ke mode offline
6. Data dari SQLite ditampilkan (data terakhir sebelum offline)

## Technical Details

### Network Detection
- Menggunakan `ConnectivityManager` dengan `NetworkCapabilities` (Android 6.0+)
- Fallback ke `NetworkInfo` untuk Android versi lama
- Support WiFi, Cellular, dan Ethernet

### Auto-Sync
- Triggered ketika koneksi kembali
- Sync items dan claims dari Strapi
- Data disimpan ke Room database
- Background process (tidak blocking UI)

### Offline Indicator
- Banner merah di bagian atas aplikasi
- Text: "You are offline" + "Offline Mode"
- Icon warning
- Visibility: GONE ketika online, VISIBLE ketika offline

## Permissions Required

```xml
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.INTERNET" />
```

## Broadcast Actions

### CONNECTIVITY_ACTION
- System broadcast untuk perubahan koneksi
- Registered di `MainActivity`

### ACTION_NETWORK_STATE_CHANGED
- Custom broadcast dari `NetworkStateReceiver`
- Extra: `EXTRA_IS_CONNECTED` (boolean)

## Testing

### Test Offline Mode
1. Matikan WiFi dan Mobile Data
2. Buka aplikasi
3. Offline indicator harus muncul
4. Data dari database harus tetap ditampilkan

### Test Online Mode
1. Pastikan koneksi internet aktif
2. Buka aplikasi
3. Offline indicator tidak muncul
4. Data dari Strapi di-sync ke database

### Test Auto-Sync
1. Matikan koneksi internet
2. Buka aplikasi (offline mode)
3. Hidupkan kembali koneksi internet
4. Data harus otomatis ter-sync dari Strapi

## Benefits

1. **Better UX**: User tidak melihat error ketika offline
2. **Offline Support**: Aplikasi tetap berfungsi tanpa internet
3. **Auto-Sync**: Data selalu ter-update ketika online
4. **Visual Feedback**: User tahu status koneksi aplikasi
5. **Seamless Experience**: Transisi smooth antara online/offline

## Future Enhancements

1. **Notification**: Notifikasi ketika data ter-sync
2. **Sync Status**: Indicator sync progress
3. **Manual Sync**: Button untuk force sync
4. **Sync Queue**: Queue untuk data yang dibuat saat offline
5. **Conflict Resolution**: Handle conflict ketika data berubah saat offline

