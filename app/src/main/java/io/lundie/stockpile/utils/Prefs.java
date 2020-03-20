package io.lundie.stockpile.utils;

import android.content.SharedPreferences;

import javax.inject.Inject;

/**
 * Gives access to shared prefs and is injected as a singleton instance via dagger (in-turn
 * providing the application context required for retrieving prefs.
 */
public class Prefs {

    private static final String MANAGE_ITEM_FRAG_STATE_ITEM_PILE = "mif_saved_state_item_pile";
    private static final String MANAGE_ITEM_FRAG_STATE_ITEM_REF = "mif_saved_state_item_pile_ref";
    private static final String MANAGE_ITEM_FRAG_STATE_IS_EDIT = "mif_saved_state_is_edit";

    private SharedPreferences mSharedPrefs;

    @Inject
    public Prefs(SharedPreferences sharedPrefs) {
        mSharedPrefs = sharedPrefs;
    }

    public void setSavedStateJsonItemPile(String jsonItemPileString) {
        mSharedPrefs.edit().putString(MANAGE_ITEM_FRAG_STATE_ITEM_PILE, jsonItemPileString).apply();
    }

    public String getSavedStateJsonItemPile() {
        return mSharedPrefs.getString(MANAGE_ITEM_FRAG_STATE_ITEM_PILE, null);
    }

    public void setSavedStateJsonItemRef(String jsonItemPileRefString) {
        mSharedPrefs.edit().putString(MANAGE_ITEM_FRAG_STATE_ITEM_REF, jsonItemPileRefString).apply();
    }

    public String getSavedStateJsonItemRef() {
        return mSharedPrefs.getString(MANAGE_ITEM_FRAG_STATE_ITEM_REF, null);
    }

    public void setSavedStateIsEdit(boolean isEditMode) {
        mSharedPrefs.edit().putBoolean(MANAGE_ITEM_FRAG_STATE_IS_EDIT, isEditMode).apply();
    }

    public boolean getSavedStateIsEditMode(boolean isEditMode) {
        return mSharedPrefs.getBoolean(MANAGE_ITEM_FRAG_STATE_IS_EDIT, false);
    }

    public void clearManageItemSavedStatePrefs() {
        mSharedPrefs.edit().remove(MANAGE_ITEM_FRAG_STATE_ITEM_PILE).apply();
        mSharedPrefs.edit().remove(MANAGE_ITEM_FRAG_STATE_ITEM_REF).apply();
        mSharedPrefs.edit().remove(MANAGE_ITEM_FRAG_STATE_IS_EDIT).apply();
    }
}