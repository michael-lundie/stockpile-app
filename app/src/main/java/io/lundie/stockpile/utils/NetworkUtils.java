package io.lundie.stockpile.utils;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;

import javax.inject.Inject;

public class NetworkUtils {

    private Application context;

    @Inject
    public NetworkUtils(Application application) {
        this.context = application;
    }

    public boolean isInternetAvailable() {
        boolean result = false;
        ConnectivityManager cManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cManager == null) {
            return false;
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network network = cManager.getActiveNetwork();
            NetworkCapabilities capabilities = cManager.getNetworkCapabilities(network);
            if(capabilities == null) {
                return false;
            } else {
                return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
            }
        } else {
            Network[] info = cManager.getAllNetworks();
            for (Network network : info) {
                // Using deprecated methods in order to support API 21 -> 23
                if (network != null && cManager.getNetworkInfo(network).isConnected()) {
                    return true;
                }
            }
        } return result;
    }
}
