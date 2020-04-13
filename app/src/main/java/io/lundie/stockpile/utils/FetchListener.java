package io.lundie.stockpile.utils;

@FunctionalInterface
public interface FetchListener {
    void update(boolean status);
}
