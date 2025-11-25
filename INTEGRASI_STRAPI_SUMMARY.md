# Ringkasan Integrasi Strapi Backend

## Perubahan yang Telah Dilakukan

### 1. **ApiClient** (`app/src/main/java/com/example/bendaku/api/ApiClient.java`)
- ✅ Base URL diubah ke `http://10.0.2.2:1338/` (untuk Android emulator)
- ✅ Menambahkan Authorization interceptor untuk otomatis menambahkan JWT token ke header
- ✅ Menambahkan method `init(Context)` untuk inisialisasi context

### 2. **SessionManager** (`app/src/main/java/com/example/bendaku/utils/SessionManager.java`)
- ✅ Menambahkan penyimpanan JWT token
- ✅ Menambahkan penyimpanan User ID
- ✅ Method `isLoggedIn()` sekarang juga mengecek keberadaan JWT token

### 3. **Model Strapi** (Baru)
- ✅ `StrapiResponse.java` - Wrapper response Strapi dengan struktur `{data, meta, error}`
- ✅ `StrapiItem.java` - Model untuk Item dengan struktur Strapi (id, attributes)
- ✅ `StrapiClaim.java` - Model untuk Claim dengan struktur Strapi
- ✅ `StrapiAuthResponse.java` - Response untuk login/register (jwt, user)
- ✅ `StrapiUploadResponse.java` - Response untuk upload file

### 4. **ApiService** (`app/src/main/java/com/example/bendaku/api/ApiService.java`)
- ✅ Endpoint Auth:
  - `POST /api/auth/local` - Login
  - `POST /api/auth/local/register` - Register
- ✅ Endpoint Upload:
  - `POST /api/upload` - Upload file/image
- ✅ Endpoint Items:
  - `GET /api/items?populate=*` - Get all items
  - `GET /api/items?populate=*&filters[type][$eq]=lost` - Get items by type
  - `GET /api/items/{id}?populate=*` - Get item by ID
  - `POST /api/items` - Create item
  - `PUT /api/items/{id}` - Update item
  - `DELETE /api/items/{id}` - Delete item
- ✅ Endpoint Claims:
  - `GET /api/claims?populate=*` - Get all claims
  - `GET /api/claims?populate=*&filters[statusClaim][$eq]=pending` - Get claims by status
  - `GET /api/claims/{id}?populate=*` - Get claim by ID
  - `POST /api/claims` - Create claim
  - `PUT /api/claims/{id}` - Update claim
  - `DELETE /api/claims/{id}` - Delete claim

### 5. **LoginActivity** (`app/src/main/java/com/example/uts/LoginActivity.java`)
- ✅ Menggunakan API Strapi untuk login
- ✅ Menyimpan JWT token setelah login berhasil
- ✅ Error handling untuk koneksi dan autentikasi

### 6. **AddReportActivity** (`app/src/main/java/com/example/uts/AddReportActivity.java`)
- ✅ Upload gambar terlebih dahulu ke `/api/upload`
- ✅ Menggunakan ID file yang diupload untuk membuat item
- ✅ Flow: Upload Image → Get Image ID → Create Item dengan Image ID
- ✅ Error handling untuk upload dan create item

### 7. **ItemListFragment** (`app/src/main/java/com/example/uts/ItemListFragment.java`)
- ✅ Menggunakan API Strapi untuk mengambil list items
- ✅ Support filter by type (lost/found)
- ✅ Konversi StrapiItem ke Item untuk kompatibilitas dengan adapter
- ✅ Menangani URL gambar dari Strapi (menambahkan base URL jika relatif)

### 8. **Model Item** (`app/src/main/java/com/example/bendaku/model/Item.java`)
- ✅ Menambahkan field `statusItem` untuk kompatibilitas dengan Strapi

## Alur Integrasi

### Login Flow
1. User memasukkan email dan password
2. App mengirim `POST /api/auth/local` dengan `{identifier, password}`
3. Strapi mengembalikan `{jwt, user}`
4. App menyimpan JWT token dan user info ke SessionManager

### Create Item Flow
1. User mengisi form dan memilih gambar
2. App upload gambar ke `POST /api/upload` (multipart dengan key "files")
3. Strapi mengembalikan array dengan ID file
4. App membuat item dengan `POST /api/items` dengan body:
   ```json
   {
     "data": {
       "name": "...",
       "description": "...",
       "location": "...",
       "dateTime": "2025-11-25T10:30:00",
       "type": "lost",
       "statusItem": "open",
       "reporterName": "...",
       "reporterPhone": "...",
       "imageUrl": <imageId>,
       "reporter": <userId>
     }
   }
   ```

### Get Items Flow
1. App mengirim `GET /api/items?populate=*`
2. Strapi mengembalikan `{data: [{id, attributes: {...}}], meta: {}}`
3. App mengkonversi StrapiItem ke Item untuk ditampilkan

## Konfigurasi

### Base URL
- **Emulator**: `http://10.0.2.2:1338/` (sudah dikonfigurasi)
- **Real Device**: Ganti ke IP komputer Anda, contoh: `http://192.168.1.xxx:1338/`

Untuk mengubah base URL, edit `ApiClient.java`:
```java
private static final String BASE_URL = "http://10.0.2.2:1338/";
```

### Authorization
Semua request yang memerlukan autentikasi otomatis menambahkan header:
```
Authorization: Bearer <jwt_token>
```

Token JWT disimpan di SessionManager dan otomatis ditambahkan oleh interceptor.

## Testing

### Prasyarat
1. Strapi backend harus berjalan di `http://localhost:1338`
2. Pastikan permission untuk Public role sudah dikonfigurasi:
   - Items: GET (public), POST/PUT/DELETE (authenticated)
   - Claims: GET (public), POST/PUT/DELETE (authenticated)
   - Upload: POST (authenticated)

### Test Cases
1. ✅ Login dengan email dan password
2. ✅ Upload gambar dan dapatkan ID
3. ✅ Create item dengan gambar
4. ✅ Get list items dengan populate
5. ✅ Filter items by type

## Catatan Penting

1. **Network Security**: Pastikan `AndroidManifest.xml` memiliki `android:usesCleartextTraffic="true"` untuk HTTP (sudah ada)

2. **Image URL**: Strapi mengembalikan URL relatif untuk gambar. App otomatis menambahkan base URL jika diperlukan.

3. **Error Handling**: Semua API call memiliki error handling untuk:
   - Koneksi gagal
   - Autentikasi gagal (401)
   - Server error (500)
   - Response parsing error

4. **Date Format**: Menggunakan ISO 8601 format (`yyyy-MM-dd'T'HH:mm:ss`) untuk `dateTime` field.

## File yang Diubah/Dibuat

### Diubah:
- `app/src/main/java/com/example/bendaku/api/ApiClient.java`
- `app/src/main/java/com/example/bendaku/api/ApiService.java`
- `app/src/main/java/com/example/bendaku/utils/SessionManager.java`
- `app/src/main/java/com/example/bendaku/model/Item.java`
- `app/src/main/java/com/example/uts/LoginActivity.java`
- `app/src/main/java/com/example/uts/AddReportActivity.java`
- `app/src/main/java/com/example/uts/ItemListFragment.java`

### Dibuat:
- `app/src/main/java/com/example/bendaku/model/StrapiResponse.java`
- `app/src/main/java/com/example/bendaku/model/StrapiItem.java`
- `app/src/main/java/com/example/bendaku/model/StrapiClaim.java`
- `app/src/main/java/com/example/bendaku/model/StrapiAuthResponse.java`
- `app/src/main/java/com/example/bendaku/model/StrapiUploadResponse.java`

## Next Steps (Opsional)

1. Update `RegisterActivity` untuk menggunakan API Strapi
2. Update `ClaimFormActivity` untuk create claim dengan upload gambar
3. Update `ItemDetailActivity` untuk menampilkan detail item dari Strapi
4. Update `AdminPanelActivity` untuk manage claims
5. Implementasi refresh token jika diperlukan
6. Tambahkan retry mechanism untuk network failures

