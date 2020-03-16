package io.lundie.stockpile.features.targets;

import io.lundie.stockpile.data.model.firestore.Target;

public class TargetBus {
    private Target target;

    public void setTarget(Target target) {
        this.target = target;
    }

    public Target getTarget() {
        return this.target;
    }

    public void clear() {
        target = null;
    }
}
