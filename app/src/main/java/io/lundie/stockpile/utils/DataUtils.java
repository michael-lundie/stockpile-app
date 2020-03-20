package io.lundie.stockpile.utils;

import com.google.gson.Gson;

import javax.inject.Inject;

import io.lundie.stockpile.data.model.firestore.ItemPile;
import io.lundie.stockpile.data.model.internal.ItemPileRef;

public class DataUtils {

    private final Gson gson;

    @Inject
    public DataUtils(Gson gson) {
        this.gson = gson;
    }

    public String serializeItemPileToJson(ItemPile itemPile) {
        return gson.toJson(itemPile);
    }

    public ItemPile deserializeToItemPile(String jsonString) {
        return gson.fromJson(jsonString, ItemPile.class);
    }

    public String serializeItemPileRefToJson(ItemPileRef itemPileRef) {
        return gson.toJson(itemPileRef);
    }

    public ItemPileRef deserializeToItemPileRef(String jsonString) {
        return gson.fromJson(jsonString, ItemPileRef.class);
    }
}
