package com.montgomeryenergyconnection;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.montgomeryenergyconnection.data.User;

public class StatsActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private User user;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    ListView drawerList;
    Button select;

    TextView numTasks, numPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Stats");

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        user = new User(firebaseUser.getUid(), firebaseUser.getDisplayName(), this);

        select = findViewById(R.id.editTasks);

        drawerList = findViewById(R.id.leftDrawer);
        drawerLayout = findViewById(R.id.drawer);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String[] navRows = new String[4];
        navRows[0] = "Dashboard";
        navRows[1] = "Stats";
        navRows[2] = "My Account";
        navRows[3] = "Logout";
        drawerList.setAdapter(new NavDrawerAdapter(this, R.layout.nav_item, navRows));
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO: do your stuff

                switch(position)
                {
                    case 0:
                        //Toast.makeText(MainActivity.this, "My Picnics",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(StatsActivity.this, MainActivity.class));
                        finish();
                        break;
                    case 1:
                        startActivity(new Intent(StatsActivity.this, StatsActivity.class));
                        finish();
                        break;
                    case 2:
                        Toast.makeText(StatsActivity.this, "My Account",Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        //Toast.makeText(MainActivity.this, "Logout",Toast.LENGTH_SHORT).show();
                        user.saveToCloud(mDatabase);
                        mAuth.signOut();
                        user.clearFile();
                        Intent i = new Intent(StatsActivity.this,LoginActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(i);
                        finish();
                        break;
                    default:
                }

            }
        });

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        numTasks = findViewById(R.id.numTasks);
        numPoints = findViewById(R.id.numPoints);

        numTasks.setText(Integer.toString(user.getInProgress().size() + user.getComplete().size()));
        numPoints.setText(Integer.toString(user.getPoints()));


    }

    public void select(View v){

        startActivity(new Intent(StatsActivity.this, SelectTasks.class));
        finish();

    }
}