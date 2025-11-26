package com.example.bendaku.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.bendaku.repository.ClaimRepository;
import com.example.bendaku.repository.ItemRepository;

public class NetworkStateReceiver extends BroadcastReceiver {

    public static final String ACTION_NETWORK_STATE_CHANGED = "com.example.uts.NETWORK_STATE_CHANGED";
    public static final String EXTRA_IS_CONNECTED = "is_connected";

    private NetworkStateListener listener;

    public interface NetworkStateListener {
        void onNetworkStateChanged(boolean isConnected);
    }

    public void setListener(NetworkStateListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) {
            return;
        }

        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction()) ||
            ACTION_NETWORK_STATE_CHANGED.equals(intent.getAction())) {
            
            boolean isConnected = isNetworkAvailable(context);
            
            // Notify listener
            if (listener != null) {
                listener.onNetworkStateChanged(isConnected);
            }
            
            // Broadcast to other components
            Intent broadcastIntent = new Intent(ACTION_NETWORK_STATE_CHANGED);
            broadcastIntent.putExtra(EXTRA_IS_CONNECTED, isConnected);
            context.sendBroadcast(broadcastIntent);
            
            // Auto-sync when online
            if (isConnected) {
                syncDataFromStrapi(context);
            }
        }
    }

    private boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager connectivityManager = 
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            
            if (connectivityManager != null) {
                android.net.Network activeNetwork = connectivityManager.getActiveNetwork();
                if (activeNetwork != null) {
                    android.net.NetworkCapabilities capabilities = 
                        connectivityManager.getNetworkCapabilities(activeNetwork);
                    return capabilities != null && (
                        capabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        capabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_ETHERNET)
                    );
                }
                // Fallback for older Android versions
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
            }
        } catch (Exception e) {
            // Ignored - fallback to false
        }
        return false;
    }

    private void syncDataFromStrapi(Context context) {
        // Sync items
        ItemRepository itemRepository = new ItemRepository(context);
        itemRepository.syncAllItems();
        
        // Sync claims (only for admin users)
        ClaimRepository claimRepository = new ClaimRepository(context);
        claimRepository.syncAllClaims();
    }
}

