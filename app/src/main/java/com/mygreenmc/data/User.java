package com.mygreenmc.data;

import java.util.ArrayList;

public class User {

    public String uid;
    public String name;
    public ArrayList<Task> complete;
    public ArrayList<Task> inProgress;

    public User(String mUID, String mName){
        this.uid = mUID;
        this.name = mName;
    }

    public void addTask(Task task){
        inProgress.add(task);
    }

}
