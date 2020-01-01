package io.lundie.stockpile.data.repository.ItemListRepositoryUtils;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class DateQueryStatusType {
    public static final int FETCHING_PAGE = 1;
    public static final int REQUEST_NEXT_PAGE = 2;
    public static final int FETCH_FAILED = 3;
    public static final int SUCCESS = 4;

    @IntDef({FETCHING_PAGE, REQUEST_NEXT_PAGE, FETCH_FAILED, SUCCESS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DateQueryStatusTypeDef {}

    private int dateQueryStatus;

    public DateQueryStatusType(@DateQueryStatusTypeDef int dateQueryStatus) {
        this.dateQueryStatus = dateQueryStatus;
    }
}
