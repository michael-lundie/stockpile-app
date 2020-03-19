package io.lundie.stockpile.utils;

import android.app.Application;
import android.content.SharedPreferences;

import javax.inject.Inject;

/**
 * Gives access to shared prefs and is injected as a singleton instance via dagger (in-turn
 * providing the application context required for retrieving prefs.
 */
public class Prefs {

    private static final String MANAGE_ITEM_FRAG_ITEM_PILE = "manage_item_frag_item_pile";

    private SharedPreferences mSharedPrefs;
    private Application mApplication;

    @Inject
    public Prefs(Application application, SharedPreferences sharedPrefs) {
        mApplication = application;
        mSharedPrefs = sharedPrefs;
    }

}