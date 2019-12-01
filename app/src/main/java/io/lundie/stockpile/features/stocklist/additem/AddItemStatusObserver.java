package io.lundie.stockpile.features.stocklist.additem;

import io.lundie.stockpile.features.stocklist.additem.AddItemStatusType.AddItemStatusTypeDef;

@FunctionalInterface
public interface AddItemStatusObserver {
    void update(@AddItemStatusTypeDef int addItemStatus);
}
