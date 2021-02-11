package com.mygreenmc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mygreenmc.data.BooleanTask;
import com.mygreenmc.data.IntTask;
import com.mygreenmc.data.Task;
import com.mygreenmc.data.User;

import java.util.ArrayList;
import java.util.Iterator;

public class SelectTasks extends AppCompatActivity {

    private RecyclerView recycler;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_tasks);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        recycler = findViewById(R.id.SelectTasksRecycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<Task> allTasks = getTasks();

        //allTasks.setClickListener(this)
        //Log.d("TASK NAME", allTasks.get(0).getName());

    }

    protected ArrayList<Task> getTasks(){
        ArrayList<Task> taskList = new ArrayList<>();

        mDatabase.child("tasks").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Task task = null;
                    if(snapshot.child("data").getValue().toString().equals("int")){
                        String name = snapshot.child("name").getValue().toString();
                        String desc = snapshot.child("description").getValue().toString();
                        int pointVal = Integer.parseInt(snapshot.child("pointVal").getValue().toString());
                        String category = snapshot.child("category").getValue().toString();
                        int check = Integer.parseInt(snapshot.child("checkVal").getValue().toString());
                        String compare = snapshot.child("comparison").getValue().toString();
                        task = new IntTask(name, desc, pointVal, 0, category, check, compare);
                    } else{
                        String name = snapshot.child("name").getValue().toString();
                        String desc = snapshot.child("description").getValue().toString();
                        int pointVal = Integer.parseInt(snapshot.child("pointVal").getValue().toString());
                        String category = snapshot.child("category").getValue().toString();
                        boolean check = Boolean.parseBoolean(snapshot.child("checkVal").getValue().toString());
                        task = new BooleanTask(name, desc, pointVal, 0, category, check);
                    }
                    taskList.add(task);
                    Log.d("TEST", task.getName());
                }
                Log.d("TASK_LENGTH", Integer.toString(taskList.size()));
                SelectTaskAdapter adapter = new SelectTaskAdapter(taskList);
                recycler.setAdapter(adapter);
            }

            public void onCancelled(DatabaseError databaseError){

            }
        });


        return taskList;
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