package io.lundie.stockpile.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import javax.inject.Inject;

import io.lundie.stockpile.data.model.firestore.ItemPile;
import io.lundie.stockpile.data.model.internal.ItemPileRef;

public class DataUtils {

    private final Gson gson;

    @Inject
    public DataUtils(Gson gson) {
        this.gson = gson;
    }

    public DataUtils() {
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss:S").create();
    }

    public String serializeItemPileToJson(ItemPile itemPile) {
        return gson.toJson(itemPile);
    }

    public ItemPile deserializeToItemPile(String jsonString) {
        return gson.fromJson(jsonString, ItemPile.class);
    }

    public String serializeItemPileArrayToJson(ArrayList<ItemPile> itemPile) {
        return gson.toJson(itemPile);
    }

    public ArrayList<ItemPile> deserializeToItemPileArray(String jsonString) {
        Type arrayListItemPileToken = new TypeToken<ArrayList<ItemPile>>() {}.getType();
        return gson.fromJson(jsonString, arrayListItemPileToken);
    }

    public String serializeItemPileRefToJson(ItemPileRef itemPileRef) {
        return gson.toJson(itemPileRef);
    }

    public ItemPileRef deserializeToItemPileRef(String jsonString) {
        return gson.fromJson(jsonString, ItemPileRef.class);
    }
}
