package com.example.uts;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.bendaku.api.ApiClient;
import com.example.bendaku.api.ApiService;
import com.example.bendaku.model.Item;
import com.example.bendaku.model.StrapiItem;
import com.example.bendaku.model.StrapiResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemListFragment extends Fragment {

    private static final String ARG_TYPE = "type";

    private String itemType;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private LinearLayout emptyState;
    private LinearLayout loadingState;
    private ItemAdapter adapter;
    private ApiService apiService;
    private List<Item> allItems = new ArrayList<>();
    private String currentSearchQuery = "";

    public static ItemListFragment newInstance(String type) {
        ItemListFragment fragment = new ItemListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            itemType = getArguments().getString(ARG_TYPE);
        }
        if (getContext() != null) {
            ApiClient.init(getContext());
        }
        apiService = ApiClient.getApiService();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        initViews(view);
        setupRecyclerView();
        setupSwipeRefresh();
        loadItems();

        return view;
    }

    private void initViews(View view) {
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        recyclerView = view.findViewById(R.id.recyclerView);
        emptyState = view.findViewById(R.id.emptyState);
        loadingState = view.findViewById(R.id.loadingState);
    }

    private void setupRecyclerView() {
        adapter = new ItemAdapter(new ArrayList<>(), this::onItemClick);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupSwipeRefresh() {
        swipeRefresh.setOnRefreshListener(this::loadItems);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh data whenever fragment becomes visible again to capture latest posts
        loadItems();
    }

    private void loadItems() {
        showLoading();
        swipeRefresh.setRefreshing(true);

        // Call Strapi API to get items
        Call<StrapiResponse<List<StrapiItem>>> call;
        if (itemType != null && !itemType.isEmpty()) {
            // Get items filtered by type
            call = apiService.getItemsByType("*", itemType);
        } else {
            // Get all items
            call = apiService.getItems("*");
        }

        call.enqueue(new Callback<StrapiResponse<List<StrapiItem>>>() {
            @Override
            public void onResponse(Call<StrapiResponse<List<StrapiItem>>> call, Response<StrapiResponse<List<StrapiItem>>> response) {
                swipeRefresh.setRefreshing(false);
                hideLoading();

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<StrapiItem> strapiItems = response.body().getData();
                    if (strapiItems != null && !strapiItems.isEmpty()) {
                        List<Item> items = convertStrapiItemsToItems(strapiItems);
                        List<Item> openItems = filterOpenItems(items);
                        allItems = new ArrayList<>(openItems);
                        performSearch(currentSearchQuery);
                    } else {
                        allItems.clear();
                        showEmpty();
                    }
                } else {
                    String errorMsg = "Gagal memuat data";
                    if (response.body() != null && response.body().getError() != null) {
                        errorMsg = response.body().getError().getMessage();
                    }
                    showError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<StrapiResponse<List<StrapiItem>>> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
                hideLoading();
                String errorMsg = "Error: " + t.getMessage();
                if (t.getMessage() != null && t.getMessage().contains("Failed to connect")) {
                    errorMsg = "Tidak dapat terhubung ke server. Pastikan backend Strapi berjalan.";
                }
                showError(errorMsg);
            }
        });
    }

    private List<Item> convertStrapiItemsToItems(List<StrapiItem> strapiItems) {
        List<Item> items = new ArrayList<>();
        for (StrapiItem strapiItem : strapiItems) {
            if (strapiItem == null) continue;
            
            Item item = new Item();
            item.setId(strapiItem.getId() != null ? String.valueOf(strapiItem.getId()) : "");
            item.setName(strapiItem.getName() != null ? strapiItem.getName() : "");
            item.setDescription(strapiItem.getDescription() != null ? strapiItem.getDescription() : "");
            item.setLocation(strapiItem.getLocation() != null ? strapiItem.getLocation() : "");
            item.setDateTime(strapiItem.getDateTime() != null ? strapiItem.getDateTime() : "");
            item.setType(strapiItem.getType() != null ? strapiItem.getType() : "lost");
            item.setStatusItem(strapiItem.getStatusItem() != null ? strapiItem.getStatusItem() : "open");
            item.setReporterName(strapiItem.getReporterName() != null ? strapiItem.getReporterName() : "");
            item.setReporterPhone(strapiItem.getReporterPhone() != null ? strapiItem.getReporterPhone() : "");
            
            // Handle image URL - Strapi returns relative URL, need to prepend base URL
            String imageUrl = strapiItem.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                // If URL doesn't start with http, prepend base URL
                if (!imageUrl.startsWith("http")) {
                    // Remove leading slash if present
                    if (imageUrl.startsWith("/")) {
                        imageUrl = imageUrl.substring(1);
                    }
                    imageUrl = "http://10.0.2.2:1338/" + imageUrl;
                }
            }
            item.setImageUrl(imageUrl);
            
            item.setCreatedAt(strapiItem.getCreatedAt() != null ? strapiItem.getCreatedAt() : "");
            items.add(item);
        }
        return items;
    }

    private List<Item> filterOpenItems(List<Item> items) {
        List<Item> filtered = new ArrayList<>();
        for (Item item : items) {
            String statusItem = item.getStatusItem();
            if (statusItem == null || statusItem.isEmpty() || "open".equalsIgnoreCase(statusItem)) {
                filtered.add(item);
            }
        }
        return filtered;
    }

    // Add the missing performSearch method
    public void performSearch(String query) {
        currentSearchQuery = query;

        if (allItems.isEmpty()) {
            return;
        }

        List<Item> filteredItems;

        if (query == null || query.trim().isEmpty()) {
            // Show all items if search is empty
            filteredItems = new ArrayList<>(allItems);
        } else {
            // Filter items based on search query
            String searchQuery = query.toLowerCase().trim();
            filteredItems = new ArrayList<>();
            for (Item item : allItems) {
                if (item.getName().toLowerCase().contains(searchQuery) ||
                    item.getDescription().toLowerCase().contains(searchQuery) ||
                    item.getLocation().toLowerCase().contains(searchQuery)) {
                    filteredItems.add(item);
                }
            }
        }

        // Update adapter with filtered results
        adapter.updateItems(filteredItems);

        if (filteredItems.isEmpty()) {
            showEmpty();
        } else {
            showContent();
        }
    }

    private void showContent() {
        recyclerView.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);
        loadingState.setVisibility(View.GONE);
    }

    private void showEmpty() {
        recyclerView.setVisibility(View.GONE);
        emptyState.setVisibility(View.VISIBLE);
        loadingState.setVisibility(View.GONE);
    }

    private void showLoading() {
        recyclerView.setVisibility(View.GONE);
        emptyState.setVisibility(View.GONE);
        loadingState.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        loadingState.setVisibility(View.GONE);
    }

    private void showError(String message) {
        showEmpty(); // Show empty state for errors
        if (getContext() != null) {
            Toast.makeText(getContext(), "Error: " + message, Toast.LENGTH_SHORT).show();
        }
    }

    private void onItemClick(Item item) {
        Intent intent = new Intent(getContext(), ItemDetailActivity.class);
        intent.putExtra("item", item);
        startActivity(intent);
    }
}
