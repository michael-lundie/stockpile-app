package io.lundie.stockpile.features.stocklist.itemlist;

import android.util.Log;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import io.lundie.stockpile.R;
import io.lundie.stockpile.data.model.ItemPile;
import io.lundie.stockpile.utils.BindingBaseAdapter;

public class ItemListViewAdapter extends BindingBaseAdapter {

    private static final String LOG_TAG = ItemListViewAdapter.class.getSimpleName();

    private ArrayList<ItemPile> listTypeItems;
    private NavController navController;

    public ItemListViewAdapter(NavController navController) {
        super(navController);
        this.navController = navController;
    }

    @Override
    protected Object getObjForPosition(int position) {
        return listTypeItems.get(position);
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        return R.layout.fragment_item_list_item;
    }

    @Override
    public int getItemCount() {
        if(listTypeItems != null) {
            return listTypeItems.size();
        } else {
            return 0;
        }
    }

    void setListTypeItems(ArrayList<ItemPile> listTypeItems) {
        this.listTypeItems = listTypeItems;
        notifyDataSetChanged();
    }

//    @BindingAdapter({"imageUrl"})
//    public static void loadImage(ImageView view, String imageUrl) {
//
//        Log.d(LOG_TAG, "ImageUrl is: " +imageUrl);
//
//        String fakeUrl = "users/8vcCZK0oVZMG14stnFKaT4bmyan2/test.jpg";
//
//        Picasso.get()
//                .load(fakeUrl)
//                .error(R.drawable.ic_broken_image_white_24dp)
//                .into(view, new Callback() {
//                    @Override
//                    public void onSuccess() {
//                        //TODO: handle event
//                    }
//
//                    @Override
//                    public void onError(Exception e) {
//                        //TODO: handle event
//                    }
//                });
//    }

    @Override
    public void onItemClicked(String itemName) {
        Log.e(LOG_TAG, "Registering item clicked:" + itemName);

    }
}
