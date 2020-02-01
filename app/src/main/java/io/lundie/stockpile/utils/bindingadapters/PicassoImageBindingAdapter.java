package io.lundie.stockpile.utils.bindingadapters;

import android.view.View;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import io.lundie.stockpile.R;
import io.lundie.stockpile.utils.PicassoRoundedCorners;
import timber.log.Timber;

public class PicassoImageBindingAdapter {

    private final Picasso picasso;
    private static final String ROOT_URI = "gs://stockpile-d958b.appspot.com/";
    private int picassoErrorImage = R.drawable.error_image;

    public PicassoImageBindingAdapter(Picasso picasso) { this.picasso = picasso; }

    @BindingAdapter({"imageUrl", "showPicassoError", "picassoRoundedCorners"})
    public void loadImage(ImageView view, String imagePath, boolean showPicassoError,
                          boolean picassoRoundedCorners) {
        //TODO: Change error and placeholder image
        if(imagePath != null && !imagePath.isEmpty()) {
            if(picassoRoundedCorners) {
                loadPicassoWithRoundedCorners(view, imagePath, showPicassoError);
            } else {
                loadPicasso(view, imagePath, showPicassoError);
            }

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

    private void loadPicassoWithRoundedCorners(ImageView view, String imagePath, boolean showPicassoError) {
        picasso.load(ROOT_URI + imagePath)
                .error(picassoErrorImage)
//                .placeholder(R.drawable.ic_broken_image_white_24dp)
                .fit().centerCrop()
                .transform(new PicassoRoundedCorners(
                        view.getContext().getResources().getDimensionPixelSize(R.dimen.picasso_rounded),
                        0))
                .into(view, new Callback() {
                    @Override
                    public void onSuccess() {
                        handlePicassoSuccess(view);
                    }

                    @Override
                    public void onError(Exception e) {
                        handlePicassoError(e, showPicassoError, view);
                    }
                });
    }

    private void loadPicasso(ImageView view, String imagePath, boolean showPicassoError) {
        picasso.load(ROOT_URI + imagePath)
                .error(picassoErrorImage)
//                .placeholder(R.drawable.ic_broken_image_white_24dp)
                .fit().centerCrop()
                .into(view, new Callback() {
                    @Override
                    public void onSuccess() {
                        handlePicassoSuccess(view);
                    }

                    @Override
                    public void onError(Exception e) {
                        handlePicassoError(e, showPicassoError, view);
                    }
                });
    }

    private void handlePicassoSuccess(ImageView view) {
        view.setVisibility(View.VISIBLE);
    }

    private void handlePicassoError(Exception e, boolean showPicassoError, ImageView view) {
        if(!showPicassoError) {
            Timber.e(e, "Loading Image with Picasso failed.");
            view.setVisibility(View.GONE);
        } else {
            view.setImageDrawable(view.getContext().getDrawable(R.drawable.error_image));
        }
    }
}