package com.mygreenmc.data;

public class IntTask extends Task{

    private int checkVal;
    private String comparison;
    private int input;

    public IntTask(String mName, String mDesc, int mPoint, int mStreak, String mCat, int mCheck, String mCompare){
        super(mName, mDesc, mPoint, mStreak, mCat);
        this.checkVal = mCheck;
        this.comparison = mCompare;
        this.input = 0;
    }

    public void setInput(int val){
        this.input = val;
        if (verify()){
            super.addStreak();
        }
    }

    public boolean verify(){
        if(comparison == "<=") {
            return input <= checkVal;
        } else if(comparison == ">="){
            return input >= checkVal;
        } else{
            return false;
        }
    }

}