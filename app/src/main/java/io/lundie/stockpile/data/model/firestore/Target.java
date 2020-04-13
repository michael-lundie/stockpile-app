package io.lundie.stockpile.data.model.firestore;

import java.util.ArrayList;

/**
 * Simple POJO class responsible for Target data. This reflects the model to be used by firestore.
 * Note that this class requires an clear constructor for use with cloud firestore.
 */
public class Target {

    private String targetName;
    private int targetType;
    private int targetFrequency;
    private int targetGoal;
    private int targetStartDay;
    private int targetProgress;
    private ArrayList<String> trackedCategories;

    public Target() { /* Required clear constructor for Firestore */ }

    public String getTargetName() { return targetName; }

    public void setTargetName(String targetName) { this.targetName = targetName; }

    public int getTargetType() { return targetType; }

    public void setTargetType(int targetType) { this.targetType = targetType; }

    public int getTargetFrequency() { return targetFrequency; }

    public void setTargetFrequency(int targetFrequency) { this.targetFrequency = targetFrequency; }

    public ArrayList<String> getTrackedCategories() { return trackedCategories; }

    public void setTrackedCategories(ArrayList<String> trackedCategories) {
        this.trackedCategories = trackedCategories;
    }

    public int getTargetGoal() { return targetGoal; }

    public void setTargetGoal(int targetGoal) { this.targetGoal = targetGoal; }

    public int getTargetStartDay() { return targetStartDay; }

    public void setTargetStartDay(int targetStartDay) { this.targetStartDay = targetStartDay; }

    public int getTargetProgress() {
        return targetProgress;
    }

    public void setTargetProgress(int targetProgress) {
        this.targetProgress = targetProgress;
    }
}