package io.lundie.stockpile.features.homeview;

import java.util.ArrayList;

import io.lundie.stockpile.R;
import io.lundie.stockpile.adapters.BindingBaseListenerAdapter;
import io.lundie.stockpile.data.model.firestore.Target;
import timber.log.Timber;

public class HomeTargetsRecycleAdapter extends BindingBaseListenerAdapter {

    private ArrayList<Target> targetItems;

    HomeTargetsRecycleAdapter(OnItemClickListener listener) {
        super(listener);
    }

    @Override
    protected Object getObjForPosition(int position) {
        return targetItems.get(position);
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        return R.layout.fragment_home_targets_item;
    }

    @Override
    public int getItemCount() {
        if(targetItems != null) {
            Timber.e("Recycle Adapter returning, %s", targetItems.size());
            return targetItems.size();
        } else {
            return 0;
        }
    }

    void setTargetItems(ArrayList<Target> targetItems) {
        this.targetItems = targetItems;
    }

}