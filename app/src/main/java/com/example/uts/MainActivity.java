package com.example.uts;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.bendaku.utils.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ExtendedFloatingActionButton fabAdd;
    private TextInputEditText searchEditText;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initServices();
        setupViewPager();
        setupClickListeners();
        setupSearch();
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

        if (id == R.id.action_admin) {
            if (sessionManager.getUser().isAdmin()) {
                startActivity(new Intent(this, AdminPanelActivity.class));
            }
            return true;
        } else if (id == R.id.action_logout) {
            sessionManager.logout();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
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