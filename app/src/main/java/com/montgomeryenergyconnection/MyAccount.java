package com.montgomeryenergyconnection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.montgomeryenergyconnection.adapters.NavClickListener;
import com.montgomeryenergyconnection.adapters.NavDrawerAdapter;
import com.montgomeryenergyconnection.data.User;

public class MyAccount extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private User user;
    private FirebaseUser firebaseUser;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    ListView drawerList;
    EditText name, email, password, newPass1, newPass2;
    TextView pass1, pass2;
    Button save, reset;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Stats");

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = new User(firebaseUser.getUid(), firebaseUser.getDisplayName(), this);

        drawerList = findViewById(R.id.leftDrawer);
        drawerLayout = findViewById(R.id.drawer);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavClickListener listener = new NavClickListener(this, user);
        drawerList.setAdapter(new NavDrawerAdapter(this, R.layout.nav_item, listener.getNavRows()));
        drawerList.setOnItemClickListener(listener);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        save = findViewById(R.id.save);
        reset = findViewById(R.id.reset);
        password = findViewById(R.id.currPassword);
        pass1 = findViewById(R.id.pass1);
        pass2 = findViewById(R.id.pass2);
        newPass1 = findViewById(R.id.newPass1);
        newPass2 = findViewById(R.id.newPass2);
        name = findViewById(R.id.displayname);
        email = findViewById(R.id.email);

        name.setText(firebaseUser.getDisplayName());
        email.setText(firebaseUser.getEmail());
        password.setText("");

    }

    public void showResetPassword(View view){

        password.setText("");

        if(reset.getText().equals("cancel")){
            reset.setText("Reset Password");
            reset.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
            pass1.setVisibility(View.GONE);
            pass2.setVisibility(View.GONE);
            newPass1.setVisibility(View.GONE);
            newPass2.setVisibility(View.GONE);

        }else {
            reset.setText("cancel");
            reset.setBackgroundColor(ContextCompat.getColor(this, R.color.colorSecondary));
            pass1.setVisibility(View.VISIBLE);
            pass2.setVisibility(View.VISIBLE);
            newPass1.setVisibility(View.VISIBLE);
            newPass2.setVisibility(View.VISIBLE);
            newPass1.setText("");
            newPass2.setText("");

        }

    }

    public void save(View view){

        if(password.getText().toString().equals("")){
            Toast.makeText(this, "Please confirm your password", Toast.LENGTH_SHORT).show();
        } else if(!newPass1.getText().toString().equals(newPass2.getText().toString())){
            Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show();
        }

        else{

            password.setText("");

            String myPassword = password.getText().toString();
            String myEmail = email.getText().toString();
            String myName = name.getText().toString();

            try{

                mAuth.signInWithEmailAndPassword(firebaseUser.getEmail(), myPassword)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = task.getResult().getUser();
                                    user.updateEmail(myEmail);
                                    if(!myPassword.equals("")){
                                        if(myPassword.length() >= 6){
                                            user.updatePassword(newPass1.getText().toString());
                                        } else{
                                            Toast.makeText(MyAccount.this, "Password must be longer than 6 characters", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(myName)
                                            .build();
                                    user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(MyAccount.this, "Account updated successfully", Toast.LENGTH_SHORT).show();
                                                showResetPassword(view);
                                            }else
                                                Toast.makeText(MyAccount.this, "Your password was incorrect.", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                } else {
                                    Toast.makeText(MyAccount.this, "Your password was incorrect.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            } catch(Exception e){
                Toast.makeText(MyAccount.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(toggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

}