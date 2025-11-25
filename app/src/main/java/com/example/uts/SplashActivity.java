package com.example.uts;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bendaku.utils.SessionManager;
import com.example.bendaku.utils.TokenManager;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2000; // 2 seconds
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize TokenManager for JWT token management
        TokenManager.init(this);
        
        sessionManager = new SessionManager(this);

        new Handler().postDelayed(() -> {
            Intent intent;
            // Check both SessionManager and TokenManager for login status
            if (sessionManager.isLoggedIn() || TokenManager.isLoggedIn()) {
                intent = new Intent(SplashActivity.this, MainActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }
            startActivity(intent);
            finish();
        }, SPLASH_DELAY);
    }
}
