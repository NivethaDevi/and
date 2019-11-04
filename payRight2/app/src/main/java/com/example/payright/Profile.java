package com.example.payright;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Profile extends AppCompatActivity {
    private DrawerLayout dl;
    private ActionBarDrawerToggle abdt;
    private NavigationView nv;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        Bundle bundle = getIntent().getExtras();
        final String username = bundle.getString("username").toString();
        final String userid = bundle.getString("userid").toString();
        mDatabase = FirebaseDatabase.getInstance().getReference("Customer");
        final TextView gender = findViewById(R.id.gender);
        final TextView age = findViewById(R.id.Smoker);
        final TextView alcohol = findViewById(R.id.alcohol);
        final TextView height = findViewById(R.id.height);
        final TextView weight = findViewById(R.id.weight);
        final TextView  dob= findViewById(R.id.dob);
        final TextView uid = findViewById(R.id.userid);
        final TextView accountno = findViewById(R.id.account);
        final TextView salary = findViewById(R.id.salary);
        final TextView emiaccount = findViewById(R.id.accountemi);




        final String[] values = {""};
        Toolbar myToolbar =  findViewById(R.id.toolbar);
        myToolbar.setNavigationIcon(R.drawable.nav_drawer);
        setSupportActionBar(myToolbar);

        mDatabase.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                uid.setText("UserID: "+userid);
                gender.setText("Gender: "+dataSnapshot.child("gender").getValue().toString());
                age.setText("Age: "+dataSnapshot.child("age").getValue().toString());
                //alcohol.setText("Alcohol consumption: "+dataSnapshot.child("alcohol").getValue().toString());
                height.setText("Height: "+dataSnapshot.child("height").getValue().toString());
                weight.setText("Weight: "+dataSnapshot.child("weight").getValue().toString());
                dob.setText("DOB: "+dataSnapshot.child("dob").getValue().toString());
                accountno.setText("Account No: "+dataSnapshot.child("accountno").getValue().toString());
                salary.setText("Salary: "+dataSnapshot.child("salary").getValue().toString());
                emiaccount.setText("EMI Account No: "+dataSnapshot.child("emiaccountno").getValue().toString());
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView tv = (TextView) headerView.findViewById(R.id.greetUser);
        tv.setText("Hi, "+username);

        final Menu nav_menu = navigationView.getMenu();
        DatabaseReference mDatabase2;
        mDatabase2 = FirebaseDatabase.getInstance().getReference("Customer");
        mDatabase2.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("retirement")) {
                    nav_menu.findItem(R.id.Health).setVisible(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        dl =  findViewById(R.id.drawer_layout);
        abdt = new ActionBarDrawerToggle(this,dl,myToolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close ){
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        dl.addDrawerListener(abdt);
        abdt.syncState();


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                Intent dashboard_main;
                switch(id){
                    case R.id.home:
                        dashboard_main = new Intent(Profile.this, Dashboard.class);
                        dashboard_main.putExtra("username",username);
                        dashboard_main.putExtra("userid",userid);

                        startActivity(dashboard_main);
                        break;
                    case R.id.dash_board:
                        dashboard_main = new Intent(Profile.this, EmiPrediction.class);
                        dashboard_main.putExtra("username",username);
                        dashboard_main.putExtra("userid",userid);

                        startActivity(dashboard_main);
                        break;
                    case R.id.profile:
                        dashboard_main = new Intent(Profile.this, Profile.class);
                        dashboard_main.putExtra("username",username);
                        dashboard_main.putExtra("userid",userid);

                        startActivity(dashboard_main);
                        break;
                    case R.id.EditProfile:
                        dashboard_main = new Intent(Profile.this, EditProfile.class);
                        dashboard_main.putExtra("username",username);
                        dashboard_main.putExtra("userid",userid);

                        startActivity(dashboard_main);
                        break;
                    case R.id.Health:
                        dashboard_main = new Intent(Profile.this, DashboardMain.class);
                        dashboard_main.putExtra("username",username);
                        dashboard_main.putExtra("userid",userid);

                        startActivity(dashboard_main);
                        break;
                    case R.id.acct:
                        dashboard_main = new Intent(Profile.this, Account.class);
                        dashboard_main.putExtra("username", username);
                        dashboard_main.putExtra("userid", userid);

                        startActivity(dashboard_main);
                        break;

                }
                return true;
            }
        });

        ConnectOData connectOData = new ConnectOData();
        connectOData.execute(userid);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tool_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.chatbot) {
            Bundle bundle = getIntent().getExtras();
            final String username = bundle.getString("username").toString();
            final String userid = bundle.getString("userid").toString();
            DatabaseReference mDatabase1;
            mDatabase1 = FirebaseDatabase.getInstance().getReference("");
            mDatabase1.child("chat").removeValue();
            Intent chatbot = new Intent(Profile.this, chatbotActivity.class);
            chatbot.putExtra("userid",userid);
            chatbot.putExtra("username",username);
            startActivity(chatbot);
            return true;
        }
        if(id == R.id.logout){
            moveTaskToBack(true);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
        return super.onOptionsItemSelected(item);
    }
}
