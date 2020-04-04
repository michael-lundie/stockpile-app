package io.lundie.stockpile.features.authentication;

import android.content.SharedPreferences;

import javax.inject.Inject;

public class UserPrefs {

    private static final String ENABLE_ON_BOARDING = "enable_on_boarding";

    private SharedPreferences sharedPreferences;

    @Inject
    public UserPrefs(SharedPreferences sharedPrefs) { this.sharedPreferences = sharedPrefs; }

    public void setIsOnBoardingEnabled(Boolean isOnBoardingEnabled) {
        sharedPreferences.edit().putBoolean(ENABLE_ON_BOARDING, isOnBoardingEnabled).apply();
    }

    public boolean getIsOnBoardingEnabled() {
        return sharedPreferences.getBoolean(ENABLE_ON_BOARDING, true);
    }
}
