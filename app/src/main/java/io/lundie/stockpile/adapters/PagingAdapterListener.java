package io.lundie.stockpile.adapters;

import io.lundie.stockpile.data.model.ItemPile;

public interface PagingAdapterListener {
    void onObjectClicked(ItemPile itemPile);
    void onLoadMore();
}