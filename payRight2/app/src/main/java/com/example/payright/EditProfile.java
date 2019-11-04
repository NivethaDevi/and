package com.example.payright;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfile extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabase1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editprofile);
        Bundle bundle = getIntent().getExtras();

        final String username = bundle.getString("username").toString();
        final String userid = bundle.getString("userid").toString();
        final Button submit =  findViewById(R.id.Submit);
        final TextView height = findViewById(R.id.Height);
        final TextView weight =  findViewById(R.id.Weight);
//        final TextView retirement =  findViewById(R.id.retirement_age);
        final TextView salary =  findViewById(R.id.salary);
        ConnectOData connectOData = new ConnectOData();
        connectOData.execute(userid);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase1 = FirebaseDatabase.getInstance().getReference("Customer");


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDatabase.child("Customer").child(userid).child("height").setValue(height.getText().toString());
                mDatabase.child("Customer").child(userid).child("weight").setValue(weight.getText().toString());
             //   mDatabase.child("Customer").child(userid).child("retirement").setValue(retirement.getText().toString());
                mDatabase.child("Customer").child(userid).child("salary").setValue(salary.getText().toString());


                mDatabase1.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String primaryacct = dataSnapshot.child("accountno").getValue().toString();
                String tilacct = "10995";
                FundTransfer ft = new FundTransfer();
                ft.fundTransfer(getApplicationContext(),tilacct,primaryacct,Integer.valueOf(salary.getText().toString()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(),"Incorrect UserID/Password",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                mDatabase.child("chat").removeValue();
                Intent dashboard = new Intent(EditProfile.this, chatbotActivity.class);
                dashboard.putExtra("username",username);
                dashboard.putExtra("userid",userid);
                startActivity(dashboard);
            }
        });
    }
}
