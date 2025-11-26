package com.example.bendaku.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.bendaku.api.ApiClient;
import com.example.bendaku.api.ApiService;
import com.example.bendaku.database.AppDatabase;
import com.example.bendaku.database.dao.ItemDao;
import com.example.bendaku.database.entity.LocalItem;
import com.example.bendaku.model.Item;
import com.example.bendaku.model.StrapiItem;
import com.example.bendaku.model.StrapiResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemRepository {
    private ItemDao itemDao;
    private ApiService apiService;
    private ExecutorService executor;
    private Handler mainHandler;

    public ItemRepository(Context context) {
        AppDatabase database = AppDatabase.getDatabase(context);
        itemDao = database.itemDao();
        apiService = ApiClient.getApiService();
        executor = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public interface DataCallback {
        void onDataLoaded(List<Item> items);
        void onError(String error);
    }

    public void getItems(String type, String statusItem, DataCallback callback) {
        // First, load from database (offline support)
        executor.execute(() -> {
            List<LocalItem> localItems;
            if (type != null && !type.isEmpty()) {
                localItems = itemDao.getItemsByTypeAndStatus(type, statusItem);
            } else {
                localItems = itemDao.getItemsByStatus(statusItem);
            }

            // Convert to Item model
            List<Item> items = convertLocalItemsToItems(localItems);
            
            // Return cached data immediately
            mainHandler.post(() -> callback.onDataLoaded(items));

            // Then, try to sync from API in background
            syncItemsFromApi(type, statusItem);
        });
    }

    private void syncItemsFromApi(String type, String statusItem) {
        Call<StrapiResponse<List<StrapiItem>>> call;
        if (type != null && !type.isEmpty()) {
            call = apiService.getItemsByType("*", type);
        } else {
            call = apiService.getItems("*");
        }

        call.enqueue(new Callback<StrapiResponse<List<StrapiItem>>>() {
            @Override
            public void onResponse(Call<StrapiResponse<List<StrapiItem>>> call, 
                                 Response<StrapiResponse<List<StrapiItem>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<StrapiItem> strapiItems = response.body().getData();
                    if (strapiItems != null) {
                        saveItemsToDatabase(strapiItems, statusItem);
                    }
                }
            }

            @Override
            public void onFailure(Call<StrapiResponse<List<StrapiItem>>> call, Throwable t) {
                // Silent fail - user already has cached data
            }
        });
    }

    private void saveItemsToDatabase(List<StrapiItem> strapiItems, String filterStatus) {
        executor.execute(() -> {
            List<LocalItem> localItems = new ArrayList<>();
            long syncTime = System.currentTimeMillis();

            for (StrapiItem strapiItem : strapiItems) {
                if (strapiItem == null) continue;

                String itemStatus = strapiItem.getStatusItem();
                if (itemStatus == null) itemStatus = "open";

                // Only save items that match the filter status
                if (filterStatus != null && !filterStatus.equals(itemStatus)) {
                    continue;
                }

                LocalItem localItem = new LocalItem();
                localItem.itemId = strapiItem.getId() != null ? String.valueOf(strapiItem.getId()) : "";
                localItem.documentId = strapiItem.getDocumentId();
                localItem.name = strapiItem.getName();
                localItem.description = strapiItem.getDescription();
                localItem.location = strapiItem.getLocation();
                localItem.dateTime = strapiItem.getDateTime();
                localItem.type = strapiItem.getType();
                localItem.statusItem = itemStatus;
                localItem.reporterName = strapiItem.getReporterName();
                localItem.reporterPhone = strapiItem.getReporterPhone();
                localItem.imageId = strapiItem.getImageId();
                
                String imageUrl = strapiItem.getImageUrl();
                if (imageUrl != null && !imageUrl.isEmpty() && !imageUrl.startsWith("http")) {
                    if (imageUrl.startsWith("/")) {
                        imageUrl = imageUrl.substring(1);
                    }
                    imageUrl = "http://10.0.2.2:1338/" + imageUrl;
                }
                localItem.imageUrl = imageUrl;
                
                localItem.createdAt = strapiItem.getCreatedAt();
                localItem.updatedAt = strapiItem.getUpdatedAt();
                localItem.lastSyncTime = syncTime;

                localItems.add(localItem);
            }

            if (!localItems.isEmpty()) {
                itemDao.insertItems(localItems);
            }
        });
    }

    private List<Item> convertLocalItemsToItems(List<LocalItem> localItems) {
        List<Item> items = new ArrayList<>();
        for (LocalItem localItem : localItems) {
            Item item = new Item();
            item.setId(localItem.itemId);
            item.setName(localItem.name);
            item.setDescription(localItem.description);
            item.setLocation(localItem.location);
            item.setDateTime(localItem.dateTime);
            item.setType(localItem.type);
            item.setStatusItem(localItem.statusItem);
            item.setReporterName(localItem.reporterName);
            item.setReporterPhone(localItem.reporterPhone);
            item.setImageUrl(localItem.imageUrl);
            item.setCreatedAt(localItem.createdAt);
            items.add(item);
        }
        return items;
    }

    public void updateItemStatus(String itemId, String statusItem) {
        executor.execute(() -> {
            long syncTime = System.currentTimeMillis();
            String updatedAt = String.valueOf(syncTime);
            itemDao.updateItemStatus(itemId, statusItem, updatedAt, syncTime);
        });
    }
}

