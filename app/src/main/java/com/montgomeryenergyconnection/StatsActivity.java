package com.montgomeryenergyconnection;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.montgomeryenergyconnection.adapters.NavClickListener;
import com.montgomeryenergyconnection.adapters.NavDrawerAdapter;
import com.montgomeryenergyconnection.adapters.TaskStatAdapter;
import com.montgomeryenergyconnection.data.Task;
import com.montgomeryenergyconnection.data.User;

import java.util.ArrayList;

public class StatsActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private User user;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    ListView drawerList;
    Button select;
    RecyclerView recycler;
    TaskStatAdapter adapter;

    TextView numTasks, numPoints;

    ArrayList<Task> tasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Stats");

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = new User(firebaseUser.getUid(), firebaseUser.getDisplayName(), this);

        select = findViewById(R.id.editTasks);

        drawerList = findViewById(R.id.leftDrawer);
        drawerLayout = findViewById(R.id.drawer);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavClickListener listener = new NavClickListener(this, user);
        drawerList.setAdapter(new NavDrawerAdapter(this, R.layout.nav_item, listener.getNavRows()));
        drawerList.setOnItemClickListener(listener);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        numTasks = findViewById(R.id.numTasks);
        numPoints = findViewById(R.id.numPoints);

        numTasks.setText(Integer.toString(user.getInProgress().size() + user.getComplete().size()));
        numPoints.setText(Integer.toString(user.getPoints()));

        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        tasks.addAll(user.getInProgress());
        tasks.addAll(user.getComplete());
        adapter = new TaskStatAdapter(tasks, this.getApplicationContext());
        recycler.setAdapter(adapter);

    }

    public void select(View v){

        startActivity(new Intent(StatsActivity.this, SelectTasks.class));
        finish();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(toggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

}