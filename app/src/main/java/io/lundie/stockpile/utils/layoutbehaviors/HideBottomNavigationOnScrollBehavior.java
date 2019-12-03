package io.lundie.stockpile.utils.layoutbehaviors;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.google.android.material.behavior.HideBottomViewOnScrollBehavior;

/**
 * Required public API for HideBottomViewOnScrollBehavior is not currently available in a non-beta
 * material components library. This class exposes the required functions for use in method
 * {@link io.lundie.stockpile.MainActivity:addKeyboardDetectListener}.
 * @param <V>
 */
public class HideBottomNavigationOnScrollBehavior<V extends View>
        extends HideBottomViewOnScrollBehavior<V> {

    public HideBottomNavigationOnScrollBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void slideUp(V child) {
        super.slideUp(child);
    }

    @Override
    public void slideDown(V child) {
        super.slideDown(child);
    }
}
