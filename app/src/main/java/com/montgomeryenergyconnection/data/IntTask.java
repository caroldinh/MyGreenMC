package com.montgomeryenergyconnection.data;

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

    public int getInput(){
        return input;
    }

    public int getCheckVal(){ return checkVal; }
    public String getComparison() { return comparison; }

    public boolean verify(){
        if(comparison.equals("<=")) {
            return input <= checkVal;
        } else if(comparison.equals(">=")){
            return input >= checkVal;
        } else{
            return false;
        }
    }

    public String toString(){
        String str = getName() + "&" + getDescription() + "&" + getPointVal() + "&" +
                getStreak() + "&" + getCategory() + "&" + checkVal + "&" + comparison + "?" + input;
        return str;
    }

}
