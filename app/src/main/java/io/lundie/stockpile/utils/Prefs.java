package io.lundie.stockpile.utils;

import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

/**
 * Gives access to shared prefs and is injected as a singleton instance via dagger (in-turn
 * providing the application context required for retrieving prefs.
 */
public class Prefs {

    private static final String MANAGE_ITEM_FRAG_STATE_ITEM_PILE = "mif_saved_state_item_pile";
    private static final String MANAGE_ITEM_FRAG_STATE_ITEM_REF = "mif_saved_state_item_pile_ref";
    private static final String MANAGE_ITEM_FRAG_STATE_IS_EDIT = "mif_saved_state_is_edit";
    private static final String MANAGE_ITEM_FRAG_STATE_SELECTED_URI = "mif_saved_state_selected_uri";
    public static final String IMAGE_WORKER_UPLOAD_QUEUE = "image_worker_upload_queue";
    public static final String WIDGET_ITEM_PILE = "widget_item_pile";

    private SharedPreferences sharedPreferences;

    @Inject
    public Prefs(SharedPreferences sharedPrefs) {
        this.sharedPreferences = sharedPrefs;
    }

    public void setSavedStateJsonItemPile(String jsonItemPileString) {
        sharedPreferences.edit().putString(MANAGE_ITEM_FRAG_STATE_ITEM_PILE, jsonItemPileString).apply();
    }

    public String getSavedStateJsonItemPile() {
        return sharedPreferences.getString(MANAGE_ITEM_FRAG_STATE_ITEM_PILE, null);
    }

    public void setSavedStateJsonItemRef(String jsonItemPileRefString) {
        sharedPreferences.edit().putString(MANAGE_ITEM_FRAG_STATE_ITEM_REF, jsonItemPileRefString).apply();
    }

    public String getSavedStateJsonItemRef() {
        return sharedPreferences.getString(MANAGE_ITEM_FRAG_STATE_ITEM_REF, null);
    }

    public void setSavedStateIsEdit(boolean isEditMode) {
        sharedPreferences.edit().putBoolean(MANAGE_ITEM_FRAG_STATE_IS_EDIT, isEditMode).apply();
    }

    public boolean getSavedStateIsEditMode(boolean isEditMode) {
        return sharedPreferences.getBoolean(MANAGE_ITEM_FRAG_STATE_IS_EDIT, false);
    }

    public void setSavedStateSelectedUri(String selectedUri) {
        sharedPreferences.edit().putString(MANAGE_ITEM_FRAG_STATE_SELECTED_URI, selectedUri).apply();
    }

    public String getSavedStateSelectedUri() {
        return sharedPreferences.getString(MANAGE_ITEM_FRAG_STATE_SELECTED_URI, null);
    }


    public void clearManageItemSavedStatePrefs() {
        sharedPreferences.edit().remove(MANAGE_ITEM_FRAG_STATE_ITEM_PILE).apply();
        sharedPreferences.edit().remove(MANAGE_ITEM_FRAG_STATE_ITEM_REF).apply();
        sharedPreferences.edit().remove(MANAGE_ITEM_FRAG_STATE_IS_EDIT).apply();
        sharedPreferences.edit().remove(MANAGE_ITEM_FRAG_STATE_SELECTED_URI).apply();
    }

    public void addNewImageWorkerUpload(String tag) {
        Set<String> queue = sharedPreferences.getStringSet(IMAGE_WORKER_UPLOAD_QUEUE, null);
        if(queue == null) {
            queue = new HashSet<>();
        }
        queue.add(tag);
        sharedPreferences.edit().putStringSet(IMAGE_WORKER_UPLOAD_QUEUE, queue).apply();
    }

    public Set<String> getImageWorkerUploadQueue() {
        return sharedPreferences.getStringSet(IMAGE_WORKER_UPLOAD_QUEUE, null);
    }

    public void removeImageWorkerUpload(String tag, String uuid) {
        Set<String> queue = sharedPreferences.getStringSet(IMAGE_WORKER_UPLOAD_QUEUE, null);
        if(queue != null) {
            queue.remove(tag);
            sharedPreferences.edit().putStringSet(IMAGE_WORKER_UPLOAD_QUEUE, queue).remove(tag).apply();
        }
    }

    public void setItemPilesForWidget(String jsonItemPileString) {
        sharedPreferences.edit().putString(WIDGET_ITEM_PILE, jsonItemPileString).apply();
    }

    public String getItemPilesForWidgetJson() {
        return sharedPreferences.getString(WIDGET_ITEM_PILE, null);
    }
}