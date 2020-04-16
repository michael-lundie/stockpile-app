package io.lundie.stockpile.utils.bindingadapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.databinding.BindingAdapter;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import io.lundie.stockpile.R;
import io.lundie.stockpile.utils.picasso.PicassoRoundedCorners;
import timber.log.Timber;

import static io.lundie.stockpile.features.stocklist.manageitem.ImageUpdateStatusType.AVAILABLE;
import static io.lundie.stockpile.features.stocklist.manageitem.ImageUpdateStatusType.FAILED;
import static io.lundie.stockpile.features.stocklist.manageitem.ImageUpdateStatusType.NONE;
import static io.lundie.stockpile.features.stocklist.manageitem.ImageUpdateStatusType.UPLOADING;

public class PicassoImageBindingAdapter {

    private final Picasso picasso;
    private static final String ROOT_URI = "gs://stockpile-d958b.appspot.com/";

    private int picassoErrorImage = R.drawable.error_image;
    private int placeholderNoImage = R.drawable.image_notify_no_image;
    private int placeholderUploading = R.drawable.image_uploading;

    public PicassoImageBindingAdapter(Picasso picasso) { this.picasso = picasso; }

    @BindingAdapter({"imageUrl", "showPicassoError", "picassoRoundedCorners"})
    public void loadImage(ImageView view, String imagePath, boolean showPicassoError,
                          boolean picassoRoundedCorners) {
        if(imagePath != null && !imagePath.isEmpty()) {
            if(picassoRoundedCorners) {
                loadPicassoWithRoundedCorners(view, imagePath, showPicassoError, null,
                        NetworkPolicy.OFFLINE);
            } else {
                loadPicassoImage(view, imagePath, showPicassoError, null,
                        NetworkPolicy.OFFLINE);
            }

        } else {
            // We must cancel any requests here otherwise our recycler viewer will jumble out images.
            cancelAndShowErrorImage(view, showPicassoError, null);
        }
    }

    @BindingAdapter({"imageUrl", "imageStatus", "showPicassoError",
            "picassoRoundedCorners", "progressBar"})
    public void loadImage(ImageView view, String imagePath, String imageStatus, boolean showPicassoError,
                          boolean picassoRoundedCorners, ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);
        if(imageStatus == null) {
            cancelAndShowErrorImage(view, showPicassoError, progressBar);
        } else {
            switch(imageStatus) {
                case AVAILABLE:
                    if(imagePath != null && !imagePath.isEmpty()) {
                        if(picassoRoundedCorners) {
                            loadPicassoWithRoundedCorners(view, imagePath, showPicassoError, progressBar, NetworkPolicy.OFFLINE);
                        } else {
                            loadPicassoImage(view, imagePath, showPicassoError, progressBar, NetworkPolicy.OFFLINE);
                        }

                    } else {
                        // We must cancel any requests here otherwise our recycler viewer will jumble out images.
                        cancelAndShowErrorImage(view, showPicassoError, progressBar);
                    }
                    break;
                case UPLOADING:
                    // Reset the image cache, in-case of updates
                    picasso.invalidate(ROOT_URI + imagePath);
                    picasso.cancelRequest(view);
                    break;
                case FAILED:
                    picasso.cancelRequest(view);
                    view.setImageDrawable(view.getContext().getDrawable(picassoErrorImage));
                    progressBar.setVisibility(View.INVISIBLE);
                    break;
                case NONE:
                    cancelAndShowErrorImage(view, showPicassoError, progressBar);
                    break;
            }
        }
    }

    private void cancelAndShowErrorImage(ImageView view, boolean showPicassoError,
                                         ProgressBar progressBar) {
        if(progressBar != null)  {
            progressBar.setVisibility(View.INVISIBLE);
        }
        picasso.cancelRequest(view);
        if (!showPicassoError) {
            view.setVisibility(View.GONE);
        } else {
            view.setImageDrawable(view.getContext().getDrawable(placeholderNoImage));
        }
    }

    private void loadPicassoWithRoundedCorners(ImageView view, String imagePath, boolean showPicassoError,
                                               ProgressBar progressBar, NetworkPolicy networkPolicy) {
        picasso.load(ROOT_URI + imagePath)
                .networkPolicy(networkPolicy)
                .error(picassoErrorImage)
                .fit().centerCrop()
                .transform(new PicassoRoundedCorners(
                        view.getContext().getResources().getDimensionPixelSize(R.dimen.picasso_rounded),
                        0))
                .into(view, new Callback() {
                    @Override
                    public void onSuccess() {
                        handlePicassoSuccess(view, progressBar);
                    }

                    @Override
                    public void onError(Exception e) {
                        if(networkPolicy == NetworkPolicy.NO_CACHE) {
                            handlePicassoError(e, showPicassoError, view, progressBar);
                        } else {
                            loadPicassoWithRoundedCorners(view, imagePath, showPicassoError, progressBar, NetworkPolicy.NO_CACHE);
                        }
                    }
                });
    }

    private void loadPicassoImage(ImageView view, String imagePath, boolean showPicassoError,
                                  ProgressBar progressBar, NetworkPolicy networkPolicy) {
        picasso.load(ROOT_URI + imagePath)
                .networkPolicy(networkPolicy)
                .error(picassoErrorImage)
                .fit().centerCrop()
                .into(view, new Callback() {
                    @Override
                    public void onSuccess() {
                        handlePicassoSuccess(view, progressBar);
                    }

                    @Override
                    public void onError(Exception e) {
                        if(networkPolicy == NetworkPolicy.NO_CACHE) {
                            handlePicassoError(e, showPicassoError, view, progressBar);
                        } else {
                            loadPicassoImage(view, imagePath, showPicassoError, progressBar, NetworkPolicy.NO_CACHE);
                        }
                    }
                });
    }

    private void handlePicassoSuccess(ImageView view, ProgressBar progressBar) {
        if(progressBar != null)  {
            progressBar.setVisibility(View.INVISIBLE);
        }
        view.setVisibility(View.VISIBLE);
    }

    private void handlePicassoError(Exception e, boolean showPicassoError, ImageView view,
                                    ProgressBar progressBar) {
        if(progressBar != null)  {
            progressBar.setVisibility(View.INVISIBLE);
        }
        if(!showPicassoError) {
            Timber.e(e, "Loading Image with Picasso failed.");
            view.setVisibility(View.GONE);
        } else {
            view.setImageDrawable(view.getContext().getDrawable(picassoErrorImage));
        }
    }
}