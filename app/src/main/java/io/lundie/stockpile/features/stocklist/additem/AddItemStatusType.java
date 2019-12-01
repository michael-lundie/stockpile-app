package io.lundie.stockpile.features.stocklist.additem;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class AddItemStatusType {
    public static final int ADDING_ITEM = 0;
    public static final int SUCCESS = 1;
    public static final int IMAGE_FAILED = 2;
    public static final int FAILED = 3;

    @IntDef({ADDING_ITEM, SUCCESS, IMAGE_FAILED, FAILED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AddItemStatusTypeDef {}

    private int signInStatus;

    @AddItemStatusTypeDef
    public int getAddItemStatus() { return  this.signInStatus; }
}
