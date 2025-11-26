package com.example.uts;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.widget.Toast;

import com.example.bendaku.api.ApiClient;
import com.example.bendaku.api.ApiService;
import com.example.bendaku.model.StrapiUserDetail;
import com.example.bendaku.utils.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ExtendedFloatingActionButton fabAdd;
    private TextInputEditText searchEditText;
    private SessionManager sessionManager;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initServices();
        setupViewPager();
        setupClickListeners();
        setupSearch();
        refreshUserData();
    }

    private void initViews() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Remove the toolbar title to prevent duplicate "BendaKu" when header collapses
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        fabAdd = findViewById(R.id.fabAdd);
        searchEditText = findViewById(R.id.searchEditText);
    }

    private void initServices() {
        sessionManager = new SessionManager(this);
        ApiClient.init(this);
        apiService = ApiClient.getApiService();
    }

    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("🔍 " + getString(R.string.lost_items));
                    break;
                case 1:
                    tab.setText("✨ " + getString(R.string.found_items));
                    break;
            }
        }).attach();
    }

    private void setupClickListeners() {
        // Main FAB - opens report activity
        fabAdd.setOnClickListener(v -> {
            startActivity(new Intent(this, AddReportActivity.class));
        });
    }

    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Trigger search in current fragment
                triggerSearchInCurrentFragment(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void triggerSearchInCurrentFragment(String query) {
        // Get the current fragment and trigger search
        int currentPosition = viewPager.getCurrentItem();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("f" + currentPosition);
        if (fragment instanceof ItemListFragment) {
            ((ItemListFragment) fragment).performSearch(query);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Clear search when returning to main activity
        if (searchEditText != null) {
            searchEditText.setText("");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_profile) {
            showProfileDialog();
            return true;
        } else if (id == R.id.action_admin) {
            checkAdminAndOpenPanel();
            return true;
        } else if (id == R.id.action_logout) {
            sessionManager.logout();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showProfileDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_profile, null);

        TextView tvName = dialogView.findViewById(R.id.tvProfileName);
        TextView tvUsername = dialogView.findViewById(R.id.tvProfileUsername);
        TextView tvEmail = dialogView.findViewById(R.id.tvProfileEmail);
        TextView tvPhone = dialogView.findViewById(R.id.tvProfilePhone);
        TextView tvStudentId = dialogView.findViewById(R.id.tvProfileStudentId);
        TextView tvRole = dialogView.findViewById(R.id.tvProfileRole);

        applySessionUserFallback(tvName, tvUsername, tvEmail, tvPhone, tvStudentId, tvRole);

        new MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_BendaKu_Dialog)
                .setView(dialogView)
                .setPositiveButton(android.R.string.ok, null)
                .show();

        fetchUserProfile(tvName, tvUsername, tvEmail, tvPhone, tvStudentId, tvRole);
    }

    private void fetchUserProfile(TextView tvName, TextView tvUsername, TextView tvEmail,
                                  TextView tvPhone, TextView tvStudentId, TextView tvRole) {
        if (apiService == null) return;

        apiService.getCurrentUser("*").enqueue(new Callback<StrapiUserDetail>() {
            @Override
            public void onResponse(Call<StrapiUserDetail> call, Response<StrapiUserDetail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    StrapiUserDetail user = response.body();
                    tvName.setText(valueOrDash(user.getFullName()));
                    tvUsername.setText(valueOrDash(user.getUsername()));
                    tvEmail.setText(valueOrDash(user.getEmail()));
                    tvPhone.setText(valueOrDash(user.getPhone()));
                    tvStudentId.setText(valueOrDash(user.getStudentId()));
                    tvRole.setText(user.getIsAdmin() != null && user.getIsAdmin() ? "Admin" : "User");
                } else {
                    Toast.makeText(MainActivity.this, "Gagal memuat profil", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<StrapiUserDetail> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Gagal memuat profil", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applySessionUserFallback(TextView tvName, TextView tvUsername, TextView tvEmail,
                                          TextView tvPhone, TextView tvStudentId, TextView tvRole) {
        if (sessionManager.getUser() != null) {
            String fullName = sessionManager.getUser().getFullName();
            String email = sessionManager.getUser().getEmail();
            String phone = sessionManager.getUser().getPhone();
            String studentId = sessionManager.getUser().getStudentId();

            String username = email;
            if (username != null && username.contains("@")) {
                username = username.substring(0, username.indexOf("@"));
            }

            tvName.setText(valueOrDash(fullName));
            tvUsername.setText(valueOrDash(username));
            tvEmail.setText(valueOrDash(email));
            tvPhone.setText(valueOrDash(phone));
            tvStudentId.setText(valueOrDash(studentId));
            tvRole.setText("User");
        } else {
            tvName.setText("-");
            tvUsername.setText("-");
            tvEmail.setText("-");
            tvPhone.setText("-");
            tvStudentId.setText("-");
            tvRole.setText("-");
        }
    }

    private String valueOrDash(String value) {
        return value != null && !value.isEmpty() ? value : "-";
    }

    private void refreshUserData() {
        if (apiService == null || sessionManager.getJwtToken() == null) {
            return;
        }

        Call<StrapiUserDetail> call = apiService.getCurrentUser("*");
        call.enqueue(new Callback<StrapiUserDetail>() {
            @Override
            public void onResponse(Call<StrapiUserDetail> call, Response<StrapiUserDetail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    StrapiUserDetail userDetail = response.body();
                    com.example.bendaku.model.User user = sessionManager.getUser();
                    if (user != null) {
                        // Update user data from API response
                        if (userDetail.getFullName() != null) {
                            user.setFullName(userDetail.getFullName());
                        }
                        if (userDetail.getEmail() != null) {
                            user.setEmail(userDetail.getEmail());
                        }
                        if (userDetail.getPhone() != null) {
                            user.setPhone(userDetail.getPhone());
                        }
                        if (userDetail.getStudentId() != null) {
                            user.setStudentId(userDetail.getStudentId());
                        }
                        // Update isAdmin from API response
                        Boolean isAdmin = userDetail.getIsAdmin();
                        user.setAdmin(isAdmin != null && isAdmin);
                        sessionManager.updateUser(user);
                    }
                }
            }

            @Override
            public void onFailure(Call<StrapiUserDetail> call, Throwable t) {
                // Silently fail - user data will remain as is
            }
        });
    }

    private void checkAdminAndOpenPanel() {
        if (sessionManager.getUser() == null) {
            Toast.makeText(this, "Sesi tidak valid. Silakan login ulang.", Toast.LENGTH_SHORT).show();
            return;
        }

        // First check from session
        if (sessionManager.getUser().isAdmin()) {
            startActivity(new Intent(this, AdminPanelActivity.class));
            return;
        }

        // If not admin in session, fetch from API to be sure
        if (apiService == null || sessionManager.getJwtToken() == null) {
            Toast.makeText(this, "Akses ditolak. Hanya admin yang bisa mengakses halaman ini.", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<StrapiUserDetail> call = apiService.getCurrentUser("*");
        call.enqueue(new Callback<StrapiUserDetail>() {
            @Override
            public void onResponse(Call<StrapiUserDetail> call, Response<StrapiUserDetail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    StrapiUserDetail userDetail = response.body();
                    Boolean isAdmin = userDetail.getIsAdmin();
                    boolean isAdminUser = isAdmin != null && isAdmin;

                    // Update session
                    com.example.bendaku.model.User user = sessionManager.getUser();
                    if (user != null) {
                        user.setAdmin(isAdminUser);
                        sessionManager.updateUser(user);
                    }

                    if (isAdminUser) {
                        startActivity(new Intent(MainActivity.this, AdminPanelActivity.class));
                    } else {
                        Toast.makeText(MainActivity.this, "Akses ditolak. Hanya admin yang bisa mengakses halaman ini.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Akses ditolak. Hanya admin yang bisa mengakses halaman ini.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<StrapiUserDetail> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Gagal memverifikasi akses admin.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static class ViewPagerAdapter extends FragmentStateAdapter {
        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return ItemListFragment.newInstance("lost");
                case 1:
                    return ItemListFragment.newInstance("found");
                default:
                    return ItemListFragment.newInstance("lost");
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}