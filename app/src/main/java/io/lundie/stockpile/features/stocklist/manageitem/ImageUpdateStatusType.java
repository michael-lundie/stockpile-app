package io.lundie.stockpile.features.stocklist.manageitem;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ImageUpdateStatusType {
    public static final int ADDING_IMAGE = 1;
    public static final int SUCCESS = 2;
    public static final int TIME_OUT = 3;
    public static final int IMAGE_FAILED = 5;

    @IntDef({ADDING_IMAGE, SUCCESS, TIME_OUT, IMAGE_FAILED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ImageUpdateStatusTypeDef {}

    private int imageUpdateStatus;

    public ImageUpdateStatusType(@ImageUpdateStatusTypeDef int imageUpdateStatus) {
        this.imageUpdateStatus = imageUpdateStatus;
    }

    @ImageUpdateStatusTypeDef
    public int getImageUpdateStatus() { return this.imageUpdateStatus; }
}
