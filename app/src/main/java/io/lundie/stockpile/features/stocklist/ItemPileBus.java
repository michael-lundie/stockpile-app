package io.lundie.stockpile.features.stocklist;

import io.lundie.stockpile.data.model.ItemPile;

public class ItemPileBus {
    private ItemPile itemPile;

    public void setItemPile(ItemPile itemPile) {
        this.itemPile = itemPile;
    }

    public ItemPile getItemPile() {
        return this.itemPile;
    }
}
