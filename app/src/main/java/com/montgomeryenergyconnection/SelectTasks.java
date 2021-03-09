package com.montgomeryenergyconnection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.montgomeryenergyconnection.adapters.SelectTaskAdapter;
import com.montgomeryenergyconnection.data.BooleanTask;
import com.montgomeryenergyconnection.data.IntTask;
import com.montgomeryenergyconnection.data.Task;
import com.montgomeryenergyconnection.data.User;

import java.util.ArrayList;

public class SelectTasks extends AppCompatActivity {

    private RecyclerView electricity, water, transportation, home;
    private Button next;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private User user;

    private ArrayList<Task> allTasks = new ArrayList<>();
    private ArrayList<Task> electricityTasks = new ArrayList<>();
    private ArrayList<Task> waterTasks = new ArrayList<>();
    private ArrayList<Task> transportTasks = new ArrayList<>();
    private ArrayList<Task> homeTasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_tasks);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        electricity = findViewById(R.id.electricity);
        water = findViewById(R.id.water);
        transportation = findViewById(R.id.transportation);
        home = findViewById(R.id.home);

        next = findViewById(R.id.next);

        electricity.setLayoutManager(new LinearLayoutManager(this){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        water.setLayoutManager(new LinearLayoutManager(this){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        transportation.setLayoutManager(new LinearLayoutManager(this){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        home.setLayoutManager(new LinearLayoutManager(this){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });

        allTasks = getTasks();

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        user = new User(firebaseUser.getUid(), firebaseUser.getDisplayName(), this);
        // updateUI(currentUser);

    }

    protected ArrayList<Task> getTasks() {
        ArrayList<Task> taskList = new ArrayList<>();

        mDatabase.child("tasks").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Task task = null;
                    if (snapshot.child("data").getValue().toString().equals("int")) {
                        String name = snapshot.child("name").getValue().toString();
                        String desc = snapshot.child("description").getValue().toString();
                        int pointVal = Integer.parseInt(snapshot.child("pointVal").getValue().toString());
                        String category = snapshot.child("category").getValue().toString();
                        int check = Integer.parseInt(snapshot.child("checkVal").getValue().toString());
                        String compare = snapshot.child("comparison").getValue().toString();
                        task = new IntTask(name, desc, pointVal, 0, category, check, compare);
                    } else {
                        String name = snapshot.child("name").getValue().toString();
                        String desc = snapshot.child("description").getValue().toString();
                        int pointVal = Integer.parseInt(snapshot.child("pointVal").getValue().toString());
                        String category = snapshot.child("category").getValue().toString();
                        boolean check = Boolean.parseBoolean(snapshot.child("checkVal").getValue().toString());
                        task = new BooleanTask(name, desc, pointVal, 0, category, check);
                    }

                    Log.d("TEST", task.getName());
                    taskList.add(task);
                }
                Log.d("TASK_LENGTH", Integer.toString(taskList.size()));
                sortTasks(taskList);
            }

            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return taskList;
    }

    private void sortTasks(ArrayList<Task> allTasks) {

        for (Task task : allTasks) {
            Log.d("CATEGORY", task.getCategory());
            if (task.getCategory().equals("Water")) {
                waterTasks.add(task);
            } else if (task.getCategory().equals("Electricity")) {
                electricityTasks.add(task);
            } else if (task.getCategory().equals("Home")) {
                homeTasks.add(task);
            } else if (task.getCategory().equals("Transportation")) {
                transportTasks.add(task);
            }
        }

        SelectTaskAdapter waterAdapter = new SelectTaskAdapter(waterTasks, this.getApplicationContext(), user);
        water.setAdapter(waterAdapter);

        SelectTaskAdapter electricityAdapter = new SelectTaskAdapter(electricityTasks, this.getApplicationContext(), user);
        electricity.setAdapter(electricityAdapter);

        SelectTaskAdapter transportAdapter = new SelectTaskAdapter(transportTasks, this.getApplicationContext(), user);
        transportation.setAdapter(transportAdapter);

        SelectTaskAdapter homeAdapter = new SelectTaskAdapter(homeTasks, this.getApplicationContext(), user);
        home.setAdapter(homeAdapter);

    }

    public void save(View v) {

        ArrayList<Task> selected = new ArrayList<>();

        int waterCount = 0;
        for (int i = 0; i < waterTasks.size(); i++) {
            CheckBox c = water.getChildAt(i).findViewById(R.id.checkBox);
            if (c.isChecked()) {
                waterCount++;
                selected.add(waterTasks.get(i));
            }
        }
        int elecCount = 0;
        for (int i = 0; i < electricityTasks.size(); i++) {
            CheckBox c = electricity.getChildAt(i).findViewById(R.id.checkBox);
            if (c.isChecked()) {
                elecCount++;
                selected.add(electricityTasks.get(i));
            }
        }
        int transCount = 0;
        for (int i = 0; i < transportTasks.size(); i++) {
            CheckBox c = transportation.getChildAt(i).findViewById(R.id.checkBox);
            if (c.isChecked()) {
                transCount++;
                selected.add(transportTasks.get(i));
            }
        }
        int homeCount = 0;
        for (int i = 0; i < homeTasks.size(); i++) {
            CheckBox c = home.getChildAt(i).findViewById(R.id.checkBox);
            if (c.isChecked()) {
                homeCount++;
                selected.add(homeTasks.get(i));
            }
        }

        if (waterCount > 0 && elecCount > 0 && transCount > 0 && homeCount > 0) {

            ArrayList<String> myTasks = new ArrayList<String>();
            for(Task task : user.getInProgress()){
                myTasks.add(task.getName());
            }
            for(Task task : user.getComplete()){
                myTasks.add(task.getName());
            }

            for(Task task : selected){
                if(myTasks.indexOf(task.getName()) == -1 && myTasks.indexOf(task.getName()) == -1){
                    user.addTask(task);
                }
            }

            myTasks.clear();
            for(Task task : selected){
                myTasks.add(task.getName());
            }

            ArrayList<Task> tempInProgress = new ArrayList<>();
            tempInProgress.addAll(user.getInProgress());
            ArrayList<Task> tempComplete = new ArrayList<>();
            tempComplete.addAll(user.getComplete());
            for(Task task : tempInProgress){
                if(myTasks.indexOf(task.getName()) == -1){
                    user.getInProgress().remove(task);
                }
            }
            for(Task task : tempComplete){
                if(myTasks.indexOf(task.getName()) == -1){
                    user.getComplete().remove(task);
                }
            }

            user.saveTasks();
            user.saveToCloud(mDatabase);


            startActivity(new Intent(SelectTasks.this, MainActivity.class));
        } else {
            Toast.makeText(this, "Please select at least one task from each category", Toast.LENGTH_LONG).show();
        }

    }
}


/***

 mDatabase.child("tasks").addListenerForSingleValueEvent(new ValueEventListener() {
@Override
public void onDataChange(DataSnapshot dataSnapshot) {


}

public void onCancelled(DatabaseError databaseError){

}
});

 ***/