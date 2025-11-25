# Panduan Integrasi Item API

## 1. Alur Lengkap

1. **Login**  
   - Endpoint `POST /api/auth/local`  
   - Body `{"identifier":"email atau username","password":"****"}`  
   - Simpan `jwt` dari response untuk header Authorization.

2. **Upload Gambar (opsional, sebelum membuat item/claim)**  
   - Endpoint `POST /api/upload`  
   - Multipart form-data dengan key `files` dan header `Authorization: Bearer <jwt>`  
   - Response berupa array; ambil `id` file (`imageId`).

3. **Kirim Item / Claim**  
   - Endpoint `POST /api/items` atau `POST /api/claims`  
   - Header `Content-Type: application/json` + `Authorization`  
   - Body mengikuti struktur pada bagian 3 di bawah; masukkan `imageUrl: <imageId>` jika form punya upload bukti.

4. **Ambil Data (list/detail)**  
   - Endpoint `GET /api/items?populate=*`  
   - `populate=*` otomatis memuat semua relasi termasuk `imageUrl`, `reporter`, dll.

### 1.1 Alur Form Item / Claim dengan Upload

1. User isi field teks (name, description, claimerName, dll.) dan pilih file bukti.
2. Frontend kirim file ke `/api/upload`, tunggu respons ID (mis. `uploadedFileId`).
3. Jika upload berhasil, frontend baru mengirim request utama:
   - Item: `POST /api/items` dengan body `{"data": {..., "imageUrl": uploadedFileId}}`
   - Claim: `POST /api/claims` dengan body `{"data": {..., "imageUrl": uploadedFileId}}`
4. Jika upload gagal, tampilkan error dan jangan lanjut membuat item/claim.
5. Saat menampilkan data, gunakan `?populate=*` agar relasi media muncul lengkap.

## 2. Ringkasan Endpoint

### 2.1 Auth & Upload

| Tujuan        | Method | URL (Base `http://localhost:1338`) | Auth |
|---------------|--------|------------------------------------|------|
| Register user | POST   | `/api/auth/local/register`         | No   |
| Login         | POST   | `/api/auth/local`                  | No   |
| Upload file   | POST   | `/api/upload`                      | Yes  |

### 2.2 Item

| Tujuan        | Method | URL | Auth |
|---------------|--------|-----|------|
| Create item   | POST   | `/api/items` | Yes |
| List items    | GET    | `/api/items?populate=*` | Opsional |
| Detail item   | GET    | `/api/items/{id}?populate=*` | Opsional |
| Update item   | PUT    | `/api/items/{id}` | Yes |
| Delete item   | DELETE | `/api/items/{id}` | Yes |

### 2.3 Claim

| Tujuan          | Method | URL | Auth |
|-----------------|--------|-----|------|
| Create claim    | POST   | `/api/claims` | Yes |
| List claims     | GET    | `/api/claims?populate=*` | Opsional |
| Detail claim    | GET    | `/api/claims/{id}?populate=*` | Opsional |
| Update claim    | PUT    | `/api/claims/{id}` | Yes |
| Delete claim    | DELETE | `/api/claims/{id}` | Yes |

Catatan:
- Semua endpoint memakai port 1338 sesuai `config/server.ts`.
- Role **Public** sebaiknya tidak punya izin `create`, sehingga POST wajib memakai Bearer token dari user login.

## 3. Struktur Request & Response

### 3.1 Body POST `/api/items`

```json
{
  "data": {
    "name": "Dompet Kulit Hitam",
    "description": "Dompet kulit hitam dengan kartu ATM",
    "location": "Gedung A, Lantai 2",
    "dateTime": "2025-11-25T10:30:00",
    "type": "lost",
    "statusItem": "open",
    "reporterName": "John Doe",
    "reporterPhone": "081234567890",
    "imageUrl": 3,
    "reporter": 1
  }
}
```

### 3.2 Field Reference

| Field          | Tipe                | Enum/Contoh                | Keterangan |
|----------------|---------------------|----------------------------|------------|
| `name`         | string              | `"Dompet"`                 | Nama item |
| `description`  | text                | `"Dompet kulit hitam..."`  | Deskripsi |
| `location`     | string              | `"Gedung A, Lt. 2"`        | Lokasi |
| `dateTime`     | string (ISO)        | `"2025-11-25T10:30:00"`    | Waktu kejadian |
| `type`         | enum                | `lost` / `found`           | Jenis item |
| `statusItem`   | enum                | `open` / `claimed` / `resolved` | Status |
| `reporterName` | string              | `"John Doe"`               | Nama pelapor |
| `reporterPhone`| string              | `"08123..."`               | Kontak pelapor |
| `imageUrl`     | media relation ID   | `3`                        | ID file upload |
| `reporter`     | user relation ID    | `1`                        | ID user (opsional) |

### 3.2 Body POST `/api/claims`

```json
{
  "data": {
    "claimerName": "Sandi",
    "claimerPhone": "089878764534",
    "description": "Mengklaim handphone samsung",
    "statusClaim": "pending",
    "adminNotes": "Menunggu verifikasi",
    "imageUrl": 5,
    "claimer": 2,          // optional: ID user claimer
    "locale": "en"         // bila multi-bahasa
  }
}
```

Field referensi claim:

| Field         | Tipe      | Enum/Contoh                | Keterangan |
|---------------|-----------|----------------------------|------------|
| `claimerName` | string    | `"Sandi"`                  | Nama pengklaim |
| `claimerPhone`| string    | `"0898..."`                | Kontak pengklaim |
| `description` | text      | `"Saya kehilangan ..."`    | Deskripsi klaim |
| `statusClaim` | enum      | `pending` / `approved` / `rejected` | Status klaim |
| `adminNotes`  | text      | `"Verifikasi foto KTP"`    | Catatan admin |
| `claimer`     | relation  | `2`                        | ID user (opsional) |
| `imageUrl`    | media ID  | `5`                        | Bukti foto klaim |

### 3.3 Response Contoh Item (POST berhasil)

```json
{
  "data": {
    "id": 2,
    "attributes": {
      "name": "Dompet Kulit Hitam",
      "description": "Dompet kulit hitam dengan kartu ATM",
      "location": "Gedung A, Lantai 2",
      "dateTime": "2025-11-25T10:30:00",
      "type": "lost",
      "statusItem": "open",
      "reporterName": "John Doe",
      "reporterPhone": "081234567890",
      "createdAt": "2025-11-25T10:35:00.000Z",
      "updatedAt": "2025-11-25T10:35:00.000Z",
      "publishedAt": "2025-11-25T10:35:00.000Z"
    }
  },
  "meta": {}
}
```

Karena `populate=*`, blok `attributes.imageUrl.data.attributes` berisi detail file (url, mime, size, dll.). Hal yang sama berlaku untuk klaim (misal relasi `claimer`).

## 4. Testing Cepat di Postman

1. **Login**  
   - Request: `POST {{base_url}}/api/auth/local`  
   - Body: `{"identifier":"user@example.com","password":"secret"}`  
   - Simpan `jwt` dari response ke environment variable `token`.

2. **Upload Gambar**  
   - Request: `POST {{base_url}}/api/upload`  
   - Headers: `Authorization: Bearer {{token}}`  
   - Body: form-data `files` (type File). Simpan `{{imageId}}` dari response.

3. **Create Item / Claim**  
   - Request: `POST {{base_url}}/api/items` **atau** `POST {{base_url}}/api/claims`  
   - Headers: `Content-Type: application/json`, `Authorization: Bearer {{token}}`  
   - Body: JSON dengan `imageUrl: {{imageId}}` sesuai kebutuhan.

4. **GET Items / Claims**  
   - Request: `GET {{base_url}}/api/items?populate=*` atau `GET {{base_url}}/api/claims?populate=*`  
   - Pastikan data + gambar tampil.

Gunakan environment Postman:
- `base_url = http://localhost:1338`
- `token = <hasil login>`
- `imageId = <hasil upload>`

## 5. Prompt Untuk Tim Android

```
Bangun fitur Lost & Found ke backend Strapi (http://localhost:1338). Flow: 
1) Login via POST /api/auth/local, simpan jwt. 
2) Upload gambar via POST /api/upload (multipart field files + Authorization Bearer). Ambil id file. 
3) Create item via POST /api/items dengan body Strapi {"data":{...,"imageUrl":<id upload>}}. 
4) Tampilkan list dengan GET /api/items?populate=*. 
Implementasi Retrofit + OkHttp, interceptor untuk header Authorization, tangani error login/upload/token. Enum type=lost|found, status=open|claimed|resolved, dateTime format ISO 8601.
```

Prompt ini memastikan aplikasi mengikuti alur login → upload → create → fetch sesuai backend.

