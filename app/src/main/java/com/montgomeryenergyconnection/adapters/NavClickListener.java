package com.montgomeryenergyconnection.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.montgomeryenergyconnection.LoginActivity;
import com.montgomeryenergyconnection.MainActivity;
import com.montgomeryenergyconnection.MyAccount;
import com.montgomeryenergyconnection.PrizesActivity;
import com.montgomeryenergyconnection.StatsActivity;
import com.montgomeryenergyconnection.data.User;


public class NavClickListener implements AdapterView.OnItemClickListener {

    private Context from;
    private FirebaseAuth mAuth;
    private User user;
    private DatabaseReference mDatabase;
    private String[] navRows = {"Dashboard", "Stats", "Prizes", "My Account", "Logout"};

    public NavClickListener(Context from, User user) {

        this.from = from;
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        this.user = user;

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        switch (position) {
            case 0:
                from.startActivity(new Intent(from, MainActivity.class));
                ((AppCompatActivity) from).finish();
                break;
            case 1:
                from.startActivity(new Intent(from, StatsActivity.class));
                ((AppCompatActivity) from).finish();
                break;
            case 2:
                from.startActivity(new Intent(from, PrizesActivity.class));
                ((AppCompatActivity) from).finish();
                break;
            case 3:
                from.startActivity(new Intent(from, MyAccount.class));
                ((AppCompatActivity) from).finish();
                break;
            case 4:
                //Toast.makeText(MainActivity.this, "Logout",Toast.LENGTH_SHORT).show();
                user.saveToCloud(mDatabase);
                mAuth.signOut();
                user.clearFile();
                Intent i = new Intent(from, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                from.startActivity(i);
                ((AppCompatActivity) from).finish();
                break;
            default:
        }
    }

    public String[] getNavRows(){ return navRows; }
}
