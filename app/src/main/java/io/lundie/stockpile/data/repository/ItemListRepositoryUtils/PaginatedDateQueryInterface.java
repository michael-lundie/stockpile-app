package io.lundie.stockpile.data.repository.ItemListRepositoryUtils;

import java.util.ArrayList;

import javax.annotation.Nullable;

import io.lundie.stockpile.data.model.ItemPile;
import io.lundie.stockpile.data.repository.ItemListRepositoryUtils.DateQueryStatusType.DateQueryStatusTypeDef;

public interface PaginatedDateQueryInterface {
    void postData(@DateQueryStatusTypeDef int queryStatus,
                  @Nullable ArrayList<ItemPile> expiringItemsList);
}
