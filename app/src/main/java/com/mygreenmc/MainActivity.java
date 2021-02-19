package com.mygreenmc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.mygreenmc.data.User;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private User user;

    private RecyclerView inProg, complete;
    CompleteTaskAdapter inProgressAdapter, completeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        user = new User(firebaseUser.getUid(), firebaseUser.getDisplayName(), this);

        inProg = findViewById(R.id.in_prog);
        complete = findViewById(R.id.complete);
        inProg.setLayoutManager(new LinearLayoutManager(this));
        complete.setLayoutManager(new LinearLayoutManager(this));

        inProgressAdapter = new CompleteTaskAdapter(user.getInProgress(), this.getApplicationContext(), "inProgress");
        inProg.setAdapter(inProgressAdapter);

        completeAdapter = new CompleteTaskAdapter(user.getComplete(), this.getApplicationContext(), "complete");
        complete.setAdapter(completeAdapter);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-message"));

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
            user.saveTasks();

        }
    };

}