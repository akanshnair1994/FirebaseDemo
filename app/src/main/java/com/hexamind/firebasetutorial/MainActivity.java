package com.hexamind.firebasetutorial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Base64;

public class MainActivity extends AppCompatActivity {
    private FirebaseDatabase db;
    private DatabaseReference ref;
    private static final String TAG = MainActivity.class.getName();
    private EditText username, password;
    private AppCompatButton login;
    private ProgressBar progressBar;
    boolean userExists = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        progressBar = findViewById(R.id.progressBar);

        db = FirebaseDatabase.getInstance();
        ref = db.getReference("users");

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uname = username.getText().toString();
                String pwd = password.getText().toString();

                progressBar.setVisibility(View.VISIBLE);
                if (!getUser(username.getText().toString(), password.getText().toString()))
                    addUser(uname, pwd);
                else {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void addUser(String username, String password) {
        User user = new User(username, password);

        ref.push()
                .setValue(user);
        ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Toast.makeText(MainActivity.this, "User was added successfully..", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(MainActivity.this, "There was some problem adding the user", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error: " + databaseError.toException());
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private boolean getUser(final String username, final String password) {
        ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            User user = ds.getValue(User.class);
                            if (user.getUsername().equals(username) ) {
                                if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                                    Toast.makeText(MainActivity.this, "User details are correct. Please proceed", Toast.LENGTH_SHORT).show();
                                    userExists = true;
                                } else {
                                    Toast.makeText(MainActivity.this, "User details are incorrect. Please try again later....", Toast.LENGTH_SHORT).show();
                                    userExists = false;
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "User doesn't match. Please try again with different credentials.", Toast.LENGTH_SHORT).show();
                                userExists = false;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(MainActivity.this, "There was some problem. Please try again later....", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error: " + databaseError.toException());
                        progressBar.setVisibility(View.GONE);
                        userExists = false;
                    }
                });

        return userExists;
    }
}
