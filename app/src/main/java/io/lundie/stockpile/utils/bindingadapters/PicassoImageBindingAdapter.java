package io.lundie.stockpile.utils.bindingadapters;

import android.util.Log;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import io.lundie.stockpile.R;

public class PicassoImageBindingAdapter {
    private static final String LOG_TAG = PicassoImageBindingAdapter.class.getSimpleName();

    private final Picasso picasso;
    private static final String ROOT_URI = "gs://stockpile-d958b.appspot.com/";

    public PicassoImageBindingAdapter(Picasso picasso) {
        Log.d(LOG_TAG, "ImageLoad BindingAdapter called.");
        Log.d(LOG_TAG, "ImageLoad: Picasso --> " + picasso);
        this.picasso = picasso;
    }

    @BindingAdapter("imageUrl")
    public void loadImage(ImageView view, String imagePath) {
        Log.d(LOG_TAG, "ImageLoad : loadImage is called.");
        Log.d(LOG_TAG, "ImageLoad : imageUrl --> " + imagePath);


        //TODO: Change error and placeholder image
        picasso.load(ROOT_URI + imagePath)
                .error(R.drawable.ic_broken_image_white_24dp)
                .placeholder(R.drawable.ic_broken_image_white_24dp)
                .fit()
                .into(view, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d(LOG_TAG, "ImageLoad PicassoImageBindingAdapter Picasso Success.");
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d(LOG_TAG, "ImageLoad PicassoImageBindingAdapter Picasso Failed.");
                    }
                });
    }
}
