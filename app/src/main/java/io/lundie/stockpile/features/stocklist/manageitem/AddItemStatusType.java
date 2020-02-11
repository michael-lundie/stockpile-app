package io.lundie.stockpile.features.stocklist.manageitem;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class AddItemStatusType {
    public static final int ADDING_ITEM = 1;
    public static final int SUCCESS = 2;
    public static final int SUCCESS_NO_IMAGE = 3;
    public static final int IMAGE_FAILED = 4;
    public static final int FAILED = 5;

    @IntDef({ADDING_ITEM, SUCCESS, SUCCESS_NO_IMAGE, IMAGE_FAILED, FAILED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AddItemStatusTypeDef {}

    private int addItemStatus;

    public AddItemStatusType(@AddItemStatusTypeDef int addItemStatusType) {
        this.addItemStatus = addItemStatusType;
    }

    @AddItemStatusTypeDef
    public int getAddItemStatus() { return this.addItemStatus; }
}
