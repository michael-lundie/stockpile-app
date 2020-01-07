package io.lundie.stockpile.features.homeview;

import static io.lundie.stockpile.features.homeview.PagingArrayStatusType.*;

/**
 * This is a wrapper class for {@link PagingArrayStatusType}. Since LiveData cannot
 * contain primitive types and the use of {@link androidx.annotation.IntDef} is preferred over Enum
 * types in android a wrapper class is necessary.
 */
public class PagingArrayStatusEvent {
    private @PagingArrayStatusTypeDef int pagingStatus;

    PagingArrayStatusEvent(@PagingArrayStatusTypeDef int eventStatus) {
        this.pagingStatus = eventStatus;
    }

    public int getPagingStatus() { return pagingStatus; }
    public void setPagingStatus(@PagingArrayStatusTypeDef int pagingStatus) {
        this.pagingStatus = pagingStatus;
    }
}
