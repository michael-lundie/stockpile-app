package io.lundie.stockpile.utils;

@FunctionalInterface
public interface BooleanStatusObserver {
    void update(boolean status);
}
