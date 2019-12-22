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

    @BindingAdapter({"imageUrl", "showPicassoError"})
    public void loadImage(ImageView view, String imagePath, boolean showPicassoError) {
        //TODO: Change error and placeholder image
        if(imagePath != null && !imagePath.isEmpty()) {
            picasso.load(ROOT_URI + imagePath)
                    .error(R.drawable.error_image)
//                .placeholder(R.drawable.ic_broken_image_white_24dp)
                    .fit().centerCrop()
                    .into(view, new Callback() {
                        @Override
                        public void onSuccess() {
                            view.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(Exception e) {
                            if(!showPicassoError) {
                                Timber.e(e, "Loading Image with Picasso failed.");
                                view.setVisibility(View.GONE);
                            } else {
                                view.setImageDrawable(view.getContext().getDrawable(R.drawable.error_image));
                            }
                        }
                    });
        } else {
            // We must cancel any requests here otherwise our recycler viewer will jumble out images.
            picasso.cancelRequest(view);
            if(!showPicassoError) {
                view.setVisibility(View.GONE);
            } else {
                view.setImageDrawable(view.getContext().getDrawable(R.drawable.no_image_image));
            }
        }
    }
}