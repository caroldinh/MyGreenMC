package com.montgomeryenergyconnection;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.montgomeryenergyconnection.adapters.CompleteTaskAdapter;
import com.montgomeryenergyconnection.adapters.NavClickListener;
import com.montgomeryenergyconnection.adapters.NavDrawerAdapter;
import com.montgomeryenergyconnection.data.Task;
import com.montgomeryenergyconnection.data.User;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private User user;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    ListView drawerList;
    TextView none, done;

    private RecyclerView inProg, complete;
    CompleteTaskAdapter inProgressAdapter, completeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Dashboard");

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if(firebaseUser == null) {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
            killActivity();
            Log.d("KILLACTIVITY", "killed");
        } else{
            user = new User(firebaseUser.getUid(), firebaseUser.getDisplayName(), this);
            if(user == null){
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                killActivity();
            }
        }
            /***
        } else{
            Log.d("LOGIN", "false");
            try{

            } catch (Exception e){
                Intent i = new Intent(MainActivity.this,LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                killActivity();
            }
        }

        ***/

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        int lastTimeStarted = settings.getInt("last_time_started", -1);
        Calendar calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DAY_OF_YEAR);

        if(today != lastTimeStarted) {

            for(Task task : user.getComplete()){
                task.addStreak();
            }
            user.saveTasks();
            user.resetTasks();
            user.saveToCloud(mDatabase);

            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("last_time_started", today);
            editor.commit();
        }


    }

    @Override
    public void onStart(){
        super.onStart();

        inProg = findViewById(R.id.in_prog);
        complete = findViewById(R.id.complete);
        inProg.setLayoutManager(new LinearLayoutManager(this){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        complete.setLayoutManager(new LinearLayoutManager(this){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });

        inProgressAdapter = new CompleteTaskAdapter(user.getInProgress(), this.getApplicationContext(), "inProgress");
        inProg.setAdapter(inProgressAdapter);

        completeAdapter = new CompleteTaskAdapter(user.getComplete(), this.getApplicationContext(), "complete");
        complete.setAdapter(completeAdapter);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-message"));

        drawerList = findViewById(R.id.leftDrawer);
        drawerLayout = findViewById(R.id.drawer);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavClickListener listener = new NavClickListener(this, user);
        drawerList.setAdapter(new NavDrawerAdapter(this, R.layout.nav_item, listener.getNavRows()));
        drawerList.setOnItemClickListener(listener);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        done = findViewById(R.id.done);
        none = findViewById(R.id.none);

        if(user.getInProgress().size() == 0){
            done.setVisibility(View.VISIBLE);
        } else{
            done.setVisibility(View.GONE);
        }
        if(user.getComplete().size() == 0){
            none.setVisibility(View.VISIBLE);
        } else{
            none.setVisibility(View.GONE);
        }

    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            // Get extra data included in the Intent
            String recycler = intent.getStringExtra("RECYCLER");
            int position = intent.getIntExtra("POSITION", 0);
            boolean verified = intent.getBooleanExtra("VERIFIED", false);
            Log.d("BROADCAST CHECK", "Received");
            Log.d("VERIFIED", verified + "");

            try{

                if(verified && recycler.equals("inProgress")){
                    user.getComplete().add(user.getInProgress().get(position));
                    completeAdapter.notifyItemInserted(user.getComplete().size()-1);
                    completeAdapter.notifyItemRangeChanged(0, user.getComplete().size());
                    user.getInProgress().remove(position);
                    inProgressAdapter.notifyItemRemoved(position);
                    inProgressAdapter.notifyItemRangeChanged(0, user.getInProgress().size());

                } else if(!verified && recycler.equals("complete")){
                    user.getInProgress().add(user.getComplete().get(position));
                    inProgressAdapter.notifyItemInserted(user.getInProgress().size()-1);
                    inProgressAdapter.notifyItemRangeChanged(0, user.getInProgress().size());
                    user.getComplete().remove(position);
                    completeAdapter.notifyItemRemoved(position);
                    completeAdapter.notifyItemRangeChanged(0, user.getComplete().size());

                }

            }catch(Exception e){

            }

            if(user.getInProgress().size() == 0){
                done.setVisibility(View.VISIBLE);
            } else{
                done.setVisibility(View.GONE);
            }
            if(user.getComplete().size() == 0){
                none.setVisibility(View.VISIBLE);
            } else{
                none.setVisibility(View.GONE);
            }
            user.saveTasks();

        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(toggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    public void killActivity(){
        finish();
    }

}