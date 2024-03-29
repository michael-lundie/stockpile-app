package io.lundie.stockpile.data.repository.ItemListRepositoryUtils;

import static io.lundie.stockpile.data.repository.ItemListRepositoryUtils.PagingArrayStatusType.PagingArrayStatusTypeDef;

/**
 * This is a wrapper class for {@link PagingArrayStatusType}. Since LiveData cannot
 * contain primitive types and the use of {@link androidx.annotation.IntDef} is preferred over Enum
 * types in android a wrapper class is necessary.
 */
public class PagingArrayStatusEvent {
    private @PagingArrayStatusTypeDef int pagingStatus;

    public PagingArrayStatusEvent(@PagingArrayStatusTypeDef int eventStatus) {
        this.pagingStatus = eventStatus;
    }

    public int getPagingStatus() { return pagingStatus; }
    public void setPagingStatus(@PagingArrayStatusTypeDef int pagingStatus) {
        this.pagingStatus = pagingStatus;
    }
}
