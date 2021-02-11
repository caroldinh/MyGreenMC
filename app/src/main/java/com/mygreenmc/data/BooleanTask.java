package com.mygreenmc.data;

public class BooleanTask extends Task{

    private boolean checkVal;
    private boolean input;

    public BooleanTask(String mName, String mDesc, int mPoint, int mStreak, String mCat, boolean mCheck){
        super(mName, mDesc, mPoint, mStreak, mCat);
        this.checkVal = mCheck;
    }

    public void setInput(boolean val){
        this.input = val;
        if (verify()){
            super.addStreak();
        }
    }

    public boolean verify(){
        return input == checkVal;
    }

}
