package io.lundie.stockpile.features.stocklist;

import io.lundie.stockpile.data.model.ItemPile;
import timber.log.Timber;

public class ItemPileBus {
    private ItemPile itemPile;

    public void setItemPile(ItemPile itemPile) {
        if(itemPile.getExpiry() != null ) {
            Timber.e("ADAPTER: BUS --> Expiry list items : %s", itemPile.getExpiry().size());
        } else {
            Timber.e("ADAPTER: BUS --> EXPIRY IS SET NULL");
        }

        this.itemPile = itemPile;
    }

    public ItemPile getItemPile() {
        return this.itemPile;
    }

    public void empty() {
        itemPile = null;
    }
}
