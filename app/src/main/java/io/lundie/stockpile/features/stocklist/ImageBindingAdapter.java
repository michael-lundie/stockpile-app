package io.lundie.stockpile.features.stocklist;

import android.util.Log;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import io.lundie.stockpile.R;

public class ImageBindingAdapter {
    private static final String LOG_TAG = ImageBindingAdapter.class.getSimpleName();

    private final Picasso picasso;

    public ImageBindingAdapter(Picasso picasso) {
        Log.d(LOG_TAG, "ImageLoad BindingAdapter called.");
        Log.d(LOG_TAG, "ImageLoad: Picasso --> " + picasso);
        this.picasso = picasso;
    }

    @BindingAdapter("imageUrl")
    public void loadImage(ImageView view, String imageUrl) {
        Log.d(LOG_TAG, "ImageLoad : loadImage is called.");
        Log.d(LOG_TAG, "ImageLoad : imageUrl --> " + imageUrl);


        //TODO: Change error and placeholder image
        picasso.load("gs://stockpile-d958b.appspot.com/users/8vcCZK0oVZMG14stnFKaT4bmyan2/test.jpg")
                .error(R.drawable.ic_broken_image_white_24dp)
                .placeholder(R.drawable.ic_broken_image_white_24dp)
                .fit()
                .into(view, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d(LOG_TAG, "ImageLoad ImageBindingAdapter Picasso Success.");
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d(LOG_TAG, "ImageLoad ImageBindingAdapter Picasso Failed.");
                    }
                });
    }
}
