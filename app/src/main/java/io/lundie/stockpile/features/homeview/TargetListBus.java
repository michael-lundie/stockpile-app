package io.lundie.stockpile.features.homeview;

import java.util.ArrayList;

import io.lundie.stockpile.data.model.ItemPile;
import io.lundie.stockpile.data.model.Target;
import timber.log.Timber;

public class TargetListBus {
    private ArrayList<Target> targets;

    public void setTargets(ArrayList<Target> targets) {
        this.targets = targets;
    }

    public ArrayList<Target> getTargets() {
        return this.targets;
    }

    public void empty() {
        targets = null;
    }
}