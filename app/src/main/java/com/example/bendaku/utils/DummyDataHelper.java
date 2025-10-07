package com.example.bendaku.utils;

import com.example.bendaku.model.ApiResponse;
import com.example.bendaku.model.User;
import com.example.bendaku.model.Item;
import com.example.bendaku.model.Claim;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DummyDataHelper {

    private static final Gson gson = new Gson();

    // Dummy users untuk testing
    private static List<User> dummyUsers = new ArrayList<>();
    private static List<Item> dummyItems = new ArrayList<>();
    private static List<Claim> dummyClaims = new ArrayList<>();

    static {
        initializeDummyUsers();
        initializeDummyItems();
        initializeDummyClaims();
    }

    private static void initializeDummyUsers() {
        // Admin user
        User admin = new User("Admin BendaKu", "admin@upnvj.ac.id", "081234567890", "ADM001");
        admin.setId("1");
        admin.setAdmin(true);
        dummyUsers.add(admin);

        // Regular users
        User user1 = new User("Budi Santoso", "budi.santoso@upnvj.ac.id", "081234567891", "2021001");
        user1.setId("2");
        dummyUsers.add(user1);

        User user2 = new User("Siti Nurhaliza", "siti.nurhaliza@upnvj.ac.id", "081234567892", "2021002");
        user2.setId("3");
        dummyUsers.add(user2);

        User user3 = new User("Ahmad Fadli", "ahmad.fadli@upnvj.ac.id", "081234567893", "2021003");
        user3.setId("4");
        dummyUsers.add(user3);

        User user4 = new User("Rina Kartika", "rina.kartika@upnvj.ac.id", "081234567894", "2021004");
        user4.setId("5");
        dummyUsers.add(user4);
    }

    private static void initializeDummyItems() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        // Lost items
        Item item1 = new Item("Dompet Kulit Coklat", "Dompet kulit warna coklat berisi KTP dan kartu ATM",
                             "Gedung Rektorat Lt. 2", "03 Okt 2024, 10:30",
                             "https://via.placeholder.com/300x200?text=Dompet", "lost", "2", "Budi Santoso", "081234567891");
        item1.setId("1");
        item1.setCreatedAt(currentDate);
        dummyItems.add(item1);

        Item item2 = new Item("Laptop ASUS Vivobook", "Laptop ASUS Vivobook 14 warna silver dengan stiker UPNVJ",
                             "Perpustakaan Lt. 3", "02 Okt 2024, 14:15",
                             "https://via.placeholder.com/300x200?text=Laptop", "lost", "3", "Siti Nurhaliza", "081234567892");
        item2.setId("2");
        item2.setCreatedAt(currentDate);
        dummyItems.add(item2);

        Item item3 = new Item("Kunci Motor Honda", "Kunci motor Honda Beat warna putih dengan gantungan Hello Kitty",
                             "Parkiran Gedung B", "04 Okt 2024, 16:45",
                             "https://via.placeholder.com/300x200?text=Kunci+Motor", "lost", "4", "Ahmad Fadli", "081234567893");
        item3.setId("3");
        item3.setCreatedAt(currentDate);
        dummyItems.add(item3);

        // Found items
        Item item4 = new Item("Handphone Samsung", "Samsung Galaxy A23 warna biru dengan case transparan",
                             "Kantin Fakultas Teknik", "05 Okt 2024, 12:20",
                             "https://via.placeholder.com/300x200?text=Samsung+HP", "found", "5", "Rina Kartika", "081234567894");
        item4.setId("4");
        item4.setCreatedAt(currentDate);
        dummyItems.add(item4);

        Item item5 = new Item("Jaket Hoodie Hitam", "Jaket hoodie warna hitam ukuran L merk Uniqlo",
                             "Aula Serba Guna", "06 Okt 2024, 09:15",
                             "https://via.placeholder.com/300x200?text=Jaket+Hoodie", "found", "2", "Budi Santoso", "081234567891");
        item5.setId("5");
        item5.setCreatedAt(currentDate);
        dummyItems.add(item5);

        Item item6 = new Item("Powerbank Xiaomi", "Powerbank Xiaomi 10000mAh warna putih dengan kabel micro USB",
                             "Lab Komputer Gedung C", "07 Okt 2024, 11:30",
                             "https://via.placeholder.com/300x200?text=Powerbank", "found", "3", "Siti Nurhaliza", "081234567892");
        item6.setId("6");
        item6.setCreatedAt(currentDate);
        dummyItems.add(item6);
    }

    private static void initializeDummyClaims() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        // Pending claims
        Claim claim1 = new Claim("4", "2", "Budi Santoso", "081234567891",
                                "Saya yakin ini HP saya karena ada wallpaper foto keluarga dan aplikasi yang saya install",
                                "https://via.placeholder.com/300x200?text=Bukti+HP");
        claim1.setId("1");
        claim1.setCreatedAt(currentDate);
        dummyClaims.add(claim1);

        Claim claim2 = new Claim("5", "4", "Ahmad Fadli", "081234567893",
                                "Jaket ini milik saya, ada nama saya di label dalam jaket",
                                "https://via.placeholder.com/300x200?text=Bukti+Jaket");
        claim2.setId("2");
        claim2.setCreatedAt(currentDate);
        dummyClaims.add(claim2);
    }

    // Simulasi login API
    public static ApiResponse<User> simulateLogin(String email, String password) {
        ApiResponse<User> response = new ApiResponse<>();

        // Simulate network delay
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Find user by email
        User foundUser = null;
        for (User user : dummyUsers) {
            if (user.getEmail().equals(email)) {
                foundUser = user;
                break;
            }
        }

        if (foundUser != null) {
            // Untuk demo, password yang benar adalah "password123"
            if ("password123".equals(password)) {
                response.setSuccess(true);
                response.setMessage("Login berhasil");
                response.setData(foundUser);
            } else {
                response.setSuccess(false);
                response.setMessage("Password salah");
                response.setData(null);
            }
        } else {
            response.setSuccess(false);
            response.setMessage("Email tidak ditemukan. Pastikan menggunakan email @upnvj.ac.id");
            response.setData(null);
        }

        return response;
    }

    // Simulasi register API
    public static ApiResponse<User> simulateRegister(String fullName, String email, String password, String phone, String studentId) {
        ApiResponse<User> response = new ApiResponse<>();

        // Simulate network delay
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Check if email already exists
        for (User user : dummyUsers) {
            if (user.getEmail().equals(email)) {
                response.setSuccess(false);
                response.setMessage("Email sudah terdaftar");
                response.setData(null);
                return response;
            }
        }

        // Create new user
        User newUser = new User(fullName, email, phone, studentId);
        newUser.setId(String.valueOf(dummyUsers.size() + 1));
        dummyUsers.add(newUser);

        response.setSuccess(true);
        response.setMessage("Registrasi berhasil");
        response.setData(newUser);

        return response;
    }

    // Simulasi get items API
    public static ApiResponse<List<Item>> simulateGetItems(String type) {
        ApiResponse<List<Item>> response = new ApiResponse<>();

        // Simulate network delay
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        List<Item> filteredItems = new ArrayList<>();
        for (Item item : dummyItems) {
            if (type == null || type.equals(item.getType())) {
                filteredItems.add(item);
            }
        }

        response.setSuccess(true);
        response.setMessage("Data berhasil dimuat");
        response.setData(filteredItems);

        return response;
    }

    // Simulasi get item detail API
    public static ApiResponse<Item> simulateGetItem(String itemId) {
        ApiResponse<Item> response = new ApiResponse<>();

        // Simulate network delay
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Item foundItem = null;
        for (Item item : dummyItems) {
            if (item.getId().equals(itemId)) {
                foundItem = item;
                break;
            }
        }

        if (foundItem != null) {
            response.setSuccess(true);
            response.setMessage("Detail berhasil dimuat");
            response.setData(foundItem);
        } else {
            response.setSuccess(false);
            response.setMessage("Barang tidak ditemukan");
            response.setData(null);
        }

        return response;
    }

    // Simulasi get claims API
    public static ApiResponse<List<Claim>> simulateGetClaims(String status) {
        ApiResponse<List<Claim>> response = new ApiResponse<>();

        // Simulate network delay
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        List<Claim> filteredClaims = new ArrayList<>();
        for (Claim claim : dummyClaims) {
            if (status == null || status.equals(claim.getStatus())) {
                filteredClaims.add(claim);
            }
        }

        response.setSuccess(true);
        response.setMessage("Data klaim berhasil dimuat");
        response.setData(filteredClaims);

        return response;
    }

    // Simulasi create item API
    public static ApiResponse<Item> simulateCreateItem(String name, String description, String location,
                                                      String dateTime, String type, String reporterId,
                                                      String reporterName, String reporterPhone) {
        ApiResponse<Item> response = new ApiResponse<>();

        // Simulate network delay
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Item newItem = new Item(name, description, location, dateTime,
                               "https://via.placeholder.com/300x200?text=New+Item",
                               type, reporterId, reporterName, reporterPhone);
        newItem.setId(String.valueOf(dummyItems.size() + 1));
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
        newItem.setCreatedAt(sdf.format(new Date()));

        dummyItems.add(newItem);

        response.setSuccess(true);
        response.setMessage("Laporan berhasil dikirim");
        response.setData(newItem);

        return response;
    }

    // Simulasi create claim API
    public static ApiResponse<Claim> simulateCreateClaim(String itemId, String claimerId, String claimerName,
                                                        String claimerPhone, String description) {
        ApiResponse<Claim> response = new ApiResponse<>();

        // Simulate network delay
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Claim newClaim = new Claim(itemId, claimerId, claimerName, claimerPhone, description,
                                  "https://via.placeholder.com/300x200?text=Proof+Image");
        newClaim.setId(String.valueOf(dummyClaims.size() + 1));
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
        newClaim.setCreatedAt(sdf.format(new Date()));

        dummyClaims.add(newClaim);

        response.setSuccess(true);
        response.setMessage("Klaim berhasil dikirim dan menunggu verifikasi admin");
        response.setData(newClaim);

        return response;
    }

    // Simulasi approve claim API
    public static ApiResponse<Claim> simulateApproveClaim(String claimId) {
        ApiResponse<Claim> response = new ApiResponse<>();

        // Simulate network delay
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Claim foundClaim = null;
        for (Claim claim : dummyClaims) {
            if (claim.getId().equals(claimId)) {
                foundClaim = claim;
                claim.setStatus("approved");
                break;
            }
        }

        if (foundClaim != null) {
            response.setSuccess(true);
            response.setMessage("Klaim berhasil disetujui");
            response.setData(foundClaim);
        } else {
            response.setSuccess(false);
            response.setMessage("Klaim tidak ditemukan");
            response.setData(null);
        }

        return response;
    }

    // Simulasi reject claim API
    public static ApiResponse<Claim> simulateRejectClaim(String claimId, String reason) {
        ApiResponse<Claim> response = new ApiResponse<>();

        // Simulate network delay
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Claim foundClaim = null;
        for (Claim claim : dummyClaims) {
            if (claim.getId().equals(claimId)) {
                foundClaim = claim;
                claim.setStatus("rejected");
                claim.setAdminNotes(reason);
                break;
            }
        }

        if (foundClaim != null) {
            response.setSuccess(true);
            response.setMessage("Klaim berhasil ditolak");
            response.setData(foundClaim);
        } else {
            response.setSuccess(false);
            response.setMessage("Klaim tidak ditemukan");
            response.setData(null);
        }

        return response;
    }

    // Get dummy users for admin panel
    public static List<User> getAllUsers() {
        return new ArrayList<>(dummyUsers);
    }

    // Login credentials untuk testing:
    public static String getLoginInstructions() {
        return "=== DUMMY LOGIN CREDENTIALS ===\n\n" +
               "Admin:\n" +
               "Email: admin@upnvj.ac.id\n" +
               "Password: password123\n\n" +
               "User Regular:\n" +
               "Email: budi.santoso@upnvj.ac.id\n" +
               "Password: password123\n\n" +
               "Email: siti.nurhaliza@upnvj.ac.id\n" +
               "Password: password123\n\n" +
               "Email: ahmad.fadli@upnvj.ac.id\n" +
               "Password: password123\n\n" +
               "Email: rina.kartika@upnvj.ac.id\n" +
               "Password: password123\n\n" +
               "Semua user menggunakan password yang sama: password123\n\n" +
               "=== DUMMY DATA TERSEDIA ===\n" +
               "- 3 Barang Hilang\n" +
               "- 3 Barang Ditemukan\n" +
               "- 2 Klaim Pending untuk Admin";
    }
}
