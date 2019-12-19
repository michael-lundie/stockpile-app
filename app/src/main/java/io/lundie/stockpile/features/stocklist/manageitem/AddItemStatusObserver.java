package io.lundie.stockpile.features.stocklist.manageitem;

import io.lundie.stockpile.features.stocklist.manageitem.AddItemStatusType.AddItemStatusTypeDef;

@FunctionalInterface
public interface AddItemStatusObserver {
    void update(@AddItemStatusTypeDef int addItemStatus);
}
