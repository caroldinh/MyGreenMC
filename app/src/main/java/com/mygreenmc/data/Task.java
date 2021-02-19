package com.mygreenmc.data;

public abstract class Task {

    private String name;
    private String description;
    private int pointVal;
    private int streak;
    private String category;

    public Task(String mName, String mDesc, int mPoint, int mStreak, String mCat){
        this.name = mName;
        this.description = mDesc;
        this.pointVal = mPoint;
        this.streak = mStreak;
        this.category = mCat;
    }

    public String getName(){
        return this.name;
    }

    public String getDescription(){
        return this.description;
    }

    public String getCategory(){
        return this.category;
    }

    public int getStreak(){ return this.streak; }

    public int getPointVal(){
        return this.pointVal;
    }

    public void addStreak(){
        streak++;
    }

    public abstract boolean verify();

    public abstract String toString();

}
