package io.lundie.stockpile.utils.bindingadapters;

import android.view.View;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import io.lundie.stockpile.R;
import timber.log.Timber;

public class PicassoImageBindingAdapter {

    private final Picasso picasso;
    private static final String ROOT_URI = "gs://stockpile-d958b.appspot.com/";

    public PicassoImageBindingAdapter(Picasso picasso) { this.picasso = picasso; }

    @BindingAdapter("imageUrl")
    public void loadImage(ImageView view, String imagePath) {
        //TODO: Change error and placeholder image
        picasso.load(ROOT_URI + imagePath)
                .error(R.drawable.ic_broken_image_white_24dp)
                .placeholder(R.drawable.ic_broken_image_white_24dp)
                .fit().centerCrop()
                .into(view, new Callback() {
                    @Override
                    public void onSuccess() {}

                    @Override
                    public void onError(Exception e) {
                        Timber.e(e, "Loading Image with Picasso failed.");
                        view.setVisibility(View.GONE);
                    }
                });
    }
}
