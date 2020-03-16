package io.lundie.stockpile.data.repository.ItemListRepositoryUtils;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class PagingArrayStatusType {
    public static final int LOADING_PAGE = 1;
    public static final int LOAD_SUCCESS = 2;
    public static final int LOAD_STOP = 3;
    public static final int LOAD_FAIL = 4;

    @IntDef({LOADING_PAGE, LOAD_SUCCESS, LOAD_STOP, LOAD_FAIL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PagingArrayStatusTypeDef {}

    private int pagingArrayStatus;

    public PagingArrayStatusType(@PagingArrayStatusTypeDef int pagingArrayStatusType) {
        this.pagingArrayStatus = pagingArrayStatusType;
    }

    @PagingArrayStatusTypeDef
    public int getPagingArrayStatus() { return  this.pagingArrayStatus; }
}
