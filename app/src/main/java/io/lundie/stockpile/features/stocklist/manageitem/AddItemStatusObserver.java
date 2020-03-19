package io.lundie.stockpile.features.stocklist.manageitem;

import io.lundie.stockpile.features.stocklist.manageitem.ImageUpdateStatusType.ImageUpdateStatusTypeDef;

@FunctionalInterface
public interface AddItemStatusObserver {
    void update(@ImageUpdateStatusTypeDef int addItemStatus);
}
