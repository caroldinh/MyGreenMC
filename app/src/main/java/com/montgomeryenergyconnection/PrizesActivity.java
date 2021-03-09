package com.montgomeryenergyconnection;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.montgomeryenergyconnection.adapters.NavClickListener;
import com.montgomeryenergyconnection.adapters.NavDrawerAdapter;
import com.montgomeryenergyconnection.adapters.PrizeAdapter;
import com.montgomeryenergyconnection.data.User;

public class PrizesActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private User user;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    ListView drawerList;
    Button select;
    RecyclerView recycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prizes);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Prizes");

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

        String[][] prizes = {
                {"Shower Timer", "200"},
                {"Solar Charger", "300"},
                {"Power Outlet Switch", "400"},
                {"Flashlight", "500"}
        };

        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });

        PrizeAdapter adapter = new PrizeAdapter(prizes, this.getApplicationContext());
        recycler.setAdapter(adapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(toggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

}