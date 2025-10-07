package com.example.uts;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.bendaku.api.ApiClient;
import com.example.bendaku.api.ApiService;
import com.example.bendaku.model.ApiResponse;
import com.example.bendaku.model.Item;
import com.example.bendaku.utils.DummyDataHelper;

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
    private TextView tvEmpty;
    private ItemAdapter adapter;
    private ApiService apiService;

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
        tvEmpty = view.findViewById(R.id.tvEmpty);
    }

    private void setupRecyclerView() {
        adapter = new ItemAdapter(new ArrayList<>(), this::onItemClick);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupSwipeRefresh() {
        swipeRefresh.setOnRefreshListener(this::loadItems);
    }

    private void loadItems() {
        swipeRefresh.setRefreshing(true);

        // Menggunakan dummy data untuk testing (tanpa backend)
        // Jalankan di background thread untuk simulasi network call
        new Thread(() -> {
            ApiResponse<List<Item>> response = DummyDataHelper.simulateGetItems(itemType);

            // Kembali ke main thread untuk update UI
            getActivity().runOnUiThread(() -> {
                swipeRefresh.setRefreshing(false);

                if (response.isSuccess()) {
                    List<Item> items = response.getData();
                    if (items != null && !items.isEmpty()) {
                        adapter.updateItems(items);
                        showContent();
                    } else {
                        showEmpty();
                    }
                } else {
                    showError(response.getMessage());
                }
            });
        }).start();

        // Original API call code (commented out until backend is ready)
        /*
        Call<ApiResponse<List<Item>>> call = apiService.getItems(itemType);
        call.enqueue(new Callback<ApiResponse<List<Item>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Item>>> call, Response<ApiResponse<List<Item>>> response) {
                swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Item>> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        List<Item> items = apiResponse.getData();
                        if (items != null && !items.isEmpty()) {
                            adapter.updateItems(items);
                            showContent();
                        } else {
                            showEmpty();
                        }
                    } else {
                        showError(apiResponse.getMessage());
                    }
                } else {
                    showError("Gagal memuat data");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Item>>> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
                showError("Error: " + t.getMessage());
            }
        });
        */
    }

    private void onItemClick(Item item) {
        Intent intent = new Intent(getContext(), ItemDetailActivity.class);
        intent.putExtra("item_id", item.getId());
        startActivity(intent);
    }

    private void showContent() {
        recyclerView.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
    }

    private void showEmpty() {
        recyclerView.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.VISIBLE);
    }

    private void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadItems();
    }
}
