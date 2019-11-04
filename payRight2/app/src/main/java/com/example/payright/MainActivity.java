package com.example.payright;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDatabase = FirebaseDatabase.getInstance().getReference("Customer");

        final Button signin =  findViewById(R.id.signin);
        final TextView username = findViewById(R.id.username);
        final TextView password =  findViewById(R.id.pass_word);
        final TextView signup =  findViewById(R.id.sign_up);
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // mDatabase.getRef().child("Customer").child("1");
                mDatabase.child(username.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        System.out.println("child"+dataSnapshot.child("Username").getValue());
                        if(password.getText().toString().equals(dataSnapshot.child("Password").getValue())){
                            Intent dashboard = new Intent(MainActivity.this, Dashboard.class);
                            dashboard.putExtra("username",dataSnapshot.child("Username").getValue().toString());
                            dashboard.putExtra("userid",username.getText().toString());
                            startActivity(dashboard);
                        }else{
                            Toast.makeText(getApplicationContext(),"Incorrect UserID/Password",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(),"Incorrect UserID/Password",
                                Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signup = new Intent(MainActivity.this, signup.class);
                startActivity(signup);
            }
        });
    }
}
