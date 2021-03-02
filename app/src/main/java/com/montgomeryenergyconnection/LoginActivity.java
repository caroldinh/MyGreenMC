package com.montgomeryenergyconnection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import com.montgomeryenergyconnection.data.User;

public class LoginActivity extends AppCompatActivity {

        private FirebaseAuth mAuth;
        private DatabaseReference mDatabase;
        EditText usernameEditText;
        EditText passwordEditText;
        EditText displayName;
        EditText verifyPassword;
        Button registerButton;
        ProgressBar loadingProgressBar;
        Button loginButton;
        TextView loginInstead;
        TextView registerInstead;
        String TAG = "Register Debugging Tag";
        Context context;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            usernameEditText = findViewById(R.id.username);
            passwordEditText = findViewById(R.id.password);
            displayName = findViewById(R.id.displayname);
            registerButton = findViewById(R.id.register);
            loadingProgressBar = findViewById(R.id.loading);
            loginButton = findViewById(R.id.login);
            loginInstead = findViewById(R.id.logininstead);
            registerInstead = findViewById(R.id.registerInstead);
            verifyPassword = findViewById(R.id.verifyPassword);

            context = this;

            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                    new IntentFilter("custom-message"));

            // Initialize Firebase Auth
            mAuth = FirebaseAuth.getInstance();

            mDatabase = FirebaseDatabase.getInstance().getReference();

        }

        @Override
        public void onStart() {
            super.onStart();
            // Check if user is signed in (non-null) and update UI accordingly.
            FirebaseUser user = mAuth.getCurrentUser();
            // updateUI(currentUser);

            if (user != null) {
                //updateUI(user);
            }
        }

        public void login(View v){

            String email = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            try {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    User u = new User(user.getUid(), user.getDisplayName(), context, mDatabase);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(LoginActivity.this, "Your username or password is incorrect.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
            catch(Exception e){
                //Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_SHORT).show();
            }

        }


        public void register(View v){

            final String email = usernameEditText.getText().toString();
            final String name = displayName.getText().toString();
            final String password = passwordEditText.getText().toString();
            final String verified = verifyPassword.getText().toString();

            if(password.equals(verified) && email.indexOf("@") != -1 && !name.equals("") && password.length() >= 6) {

                try {

                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "createUserWithEmail:success");
                                        final FirebaseUser user = mAuth.getCurrentUser();
                                        User userLog = new User(user.getUid(), name, context);
                                        Log.d("UID: ", user.getUid());
                                        Log.d("NAME: ", name);
                                        //mDatabase.child("users").child("test").setValue("test");
                                        mDatabase.child("users").child(user.getUid()).setValue(userLog);
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(name)
                                                .build();
                                        user.updateProfile(profileUpdates)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.d(TAG, "User profile updated.");
                                                            updateUI(user);
                                                        }
                                                    }
                                                });
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(LoginActivity.this, "Registration failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }

                                    // ...
                                }
                            });

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Registration failed", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }

            else{
                if(!password.equals(verified)){
                    Toast.makeText(getApplicationContext(), "Passwords must match", Toast.LENGTH_SHORT).show();
                } else if(email.indexOf("@")==-1){
                    Toast.makeText(getApplicationContext(), "Invalid email address", Toast.LENGTH_SHORT).show();
                } else if(name.equals("")){
                    Toast.makeText(getApplicationContext(), "Display name cannot be blank", Toast.LENGTH_SHORT).show();
                } else if(password.length() < 6){
                    Toast.makeText(getApplicationContext(), "Password must be greater than six characters", Toast.LENGTH_SHORT).show();
                }



            }

        }


        public void updateUI(FirebaseUser user){

            startActivity(new Intent(LoginActivity.this, SelectTasks.class));
            finish();

        }

        public void showLogin(View v){

            loginButton.setVisibility(View.VISIBLE);
            registerButton.setVisibility(View.GONE);
            displayName.setVisibility(View.GONE);
            registerInstead.setVisibility(View.VISIBLE);
            loginInstead.setVisibility(View.GONE);
            verifyPassword.setVisibility(View.GONE);

            displayName.setText("");
            usernameEditText.setText("");
            passwordEditText.setText("");
            verifyPassword.setText("");


        }

        public void showRegister(View v){

            loginButton.setVisibility(View.GONE);
            registerButton.setVisibility(View.VISIBLE);
            displayName.setVisibility(View.VISIBLE);
            registerInstead.setVisibility(View.GONE);
            loginInstead.setVisibility(View.VISIBLE);
            verifyPassword.setVisibility(View.VISIBLE);

            displayName.setText("");
            usernameEditText.setText("");
            passwordEditText.setText("");
            verifyPassword.setText("");


        }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();

        }
    };



}