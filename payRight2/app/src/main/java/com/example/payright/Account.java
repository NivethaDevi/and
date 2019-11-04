package com.example.payright;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Account extends AppCompatActivity {
    private DrawerLayout dl;
    private ActionBarDrawerToggle abdt;
    private NavigationView nv;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account);
        Bundle bundle = getIntent().getExtras();
        final String username = bundle.getString("username").toString();
        final String userid = bundle.getString("userid").toString();
        System.out.println("healthuserid" + userid);
        mDatabase = FirebaseDatabase.getInstance().getReference("Customer");

        Toolbar myToolbar = findViewById(R.id.toolbar);
        myToolbar.setNavigationIcon(R.drawable.nav_drawer);
        setSupportActionBar(myToolbar);


        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);


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

        dl = findViewById(R.id.drawer_layout);
        abdt = new ActionBarDrawerToggle(this, dl, myToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
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
                switch (id) {
                    case R.id.acct:
                        dashboard_main = new Intent(Account.this, Account.class);
                        dashboard_main.putExtra("username", username);
                        dashboard_main.putExtra("userid", userid);

                        startActivity(dashboard_main);
                        break;
                    case R.id.home:
                        dashboard_main = new Intent(Account.this, Dashboard.class);
                        dashboard_main.putExtra("username", username);
                        dashboard_main.putExtra("userid", userid);

                        startActivity(dashboard_main);
                        break;
                    case R.id.dash_board:
                        dashboard_main = new Intent(Account.this, EmiPrediction.class);
                        dashboard_main.putExtra("username", username);
                        dashboard_main.putExtra("userid", userid);

                        startActivity(dashboard_main);
                        break;
                    case R.id.profile:
                        dashboard_main = new Intent(Account.this, Profile.class);
                        dashboard_main.putExtra("username", username);
                        dashboard_main.putExtra("userid", userid);

                        startActivity(dashboard_main);
                        break;
                    case R.id.EditProfile:
                        dashboard_main = new Intent(Account.this, EditProfile.class);
                        dashboard_main.putExtra("username", username);
                        dashboard_main.putExtra("userid", userid);

                        startActivity(dashboard_main);
                        break;
                    case R.id.Health:
                        dashboard_main = new Intent(Account.this, DashboardMain.class);
                        dashboard_main.putExtra("username", username);
                        dashboard_main.putExtra("userid", userid);

                        startActivity(dashboard_main);
                        break;
                }
                return true;
            }
        });
        final ArrayList Details = new ArrayList();
        mDatabase.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    System.out.println(snapshot.getValue());
                    if (snapshot.getKey().equals("commitment")) {
                        int i=0;
                        for (DataSnapshot commit : snapshot.getChildren()) {
                            String accountname = commit.getKey() + " account";
                            if(commit.hasChild("account")) {
                                Details.add( accountname + ":" + commit.child("account").getValue());
                            }
                        }
                    }
                    if (snapshot.getKey().equals("ft")) {
                        for (DataSnapshot ft : snapshot.getChildren()) {
                                Details.add( ft.getValue());
                        }
                    }
                }
                            TableLayout mTableLayout;
                mTableLayout = (TableLayout) findViewById(R.id.table1);
                mTableLayout.setStretchAllColumns(true);
                mTableLayout.removeAllViews();
                int i = -1;
                //for(i = -1; i < predictedData.size(); i ++) {
                for (int counter = 0; counter < Details.size(); counter++) {
                    ++i;
                    final TextView tv1 = new TextView(getApplicationContext());
                    tv1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT));
                    tv1.setGravity(Gravity.LEFT);
                    tv1.setPadding(5, 15, 0, 15);

                    tv1.setText(Details.get(counter).toString());
                    final TableRow tr = new TableRow(getApplicationContext());
                    tr.setId(i + 1);

                    int leftRowMargin = 0;

                    int topRowMargin = 0;

                    int rightRowMargin = 0;

                    int bottomRowMargin = 0;
                    TableLayout.LayoutParams trParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                            TableLayout.LayoutParams.WRAP_CONTENT);
                    trParams.setMargins(leftRowMargin, topRowMargin, rightRowMargin, bottomRowMargin);
                    tr.setPadding(0, 0, 0, 0);
                    tr.setLayoutParams(trParams);
                    tr.addView(tv1);
                    mTableLayout.addView(tr, trParams);


                    // add separator row
                    final TableRow trSep = new TableRow(getApplicationContext());
                    TableLayout.LayoutParams trParamsSep = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                            TableLayout.LayoutParams.WRAP_CONTENT);
                    trParamsSep.setMargins(leftRowMargin, topRowMargin, rightRowMargin, bottomRowMargin);
                    trSep.setLayoutParams(trParamsSep);
                    TextView tvSep = new TextView(getApplicationContext());
                    TableRow.LayoutParams tvSepLay = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT);
                    tvSepLay.span = 4;
                    tvSep.setLayoutParams(tvSepLay);
                    tvSep.setBackgroundColor(Color.parseColor("#d9d9d9"));
                    tvSep.setHeight(1);
                    trSep.addView(tvSep);
                    mTableLayout.addView(trSep, trParamsSep);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public LinkedHashMap<String,Integer> freqAmount(int frequency, int amount,int month, int year){
        LinkedHashMap<String,Integer> fA = new LinkedHashMap<>();
        fA.put("frequency",frequency);
        fA.put("amount",amount);
        fA.put("month",month);
        fA.put("year",year);
        return fA;
    }


}