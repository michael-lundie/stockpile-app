package io.lundie.stockpile.utils.threadpool;

/**
 * Interface used in conjunction with callback runnable.
 */
public interface RunnableInterface {
    void onRunCompletion();
}