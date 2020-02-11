package io.lundie.stockpile.data.model;

import java.util.ArrayList;

/**
 * Simple POJO class responsible for category data.
 * Note that this class requires an empty constructor for use with cloud firestore.
 */
public class Targets {

    private String targetName;
    private int targetType; // int will adhere to hardcoded list of types
    private int targetFrequency; // will adhere to a hardcoded list of types
    private int targetGoal;
    private ArrayList<String> trackedCategories;

    public Targets() { /* Required empty constructor for Firestore */ }

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
}