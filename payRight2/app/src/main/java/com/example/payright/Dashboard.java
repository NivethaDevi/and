package com.example.payright;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.opengl.Visibility;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import org.apache.http.entity.StringEntity;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import ai.api.android.AIConfiguration;
import ai.api.AIServiceContext;
import ai.api.AIServiceContextBuilder;
import ai.api.android.AIDataService;
import ai.api.android.AIService;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;

import static android.graphics.BlendMode.COLOR;
import static org.apache.http.params.CoreProtocolPNames.PROTOCOL_VERSION;

public class Dashboard extends AppCompatActivity {
    private DrawerLayout dl;
    private ActionBarDrawerToggle abdt;
    private NavigationView nv;
    private DatabaseReference mDatabase;

    EditText editText;

    UUID uuid = UUID.randomUUID();
    AIDataService aiDataService;
    AIServiceContext customAIServiceContext;
    AIRequest aiRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        Bundle bundle = getIntent().getExtras();
        final String username = bundle.getString("username").toString();
        final String userid = bundle.getString("userid").toString();
        mDatabase = FirebaseDatabase.getInstance().getReference("Customer");

        final String[] values = {""};
        Toolbar myToolbar =  findViewById(R.id.toolbar);
        myToolbar.setNavigationIcon(R.drawable.nav_drawer);
        setSupportActionBar(myToolbar);
        final String[] Months = new String[] { "January", "February",
                "March", "April", "May", "June", "July", "August", "September",
                "October", "November", "December" };
        mDatabase.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int salary = Integer.valueOf(dataSnapshot.child("salary").getValue().toString());
                int expenses =1000;

                System.out.println(expenses);
//                if(dataSnapshot.hasChild("smoke")){
//                    HealthPrediction hp=new HealthPrediction(userid);
//                    hp.doPrediction();;
//                }
                PredictGeneric pg = new PredictGeneric();
                Calendar now = Calendar.getInstance();
                pg.predictSurplus(userid,salary);
                System.out.println(dataSnapshot.getChildren());
                System.out.println(dataSnapshot.hasChild("surplus"));
                System.out.println(dataSnapshot.hasChild("salary"));
                int sur_income;

                    if(!dataSnapshot.hasChild("surplus"))

                {
                    mDatabase.child(userid).child("surplus").setValue(salary);
                    sur_income = salary;
                    mDatabase.child(userid).child("emiamount").setValue(0);
                }else {

                    sur_income = Integer.valueOf(dataSnapshot.child("surplus").getValue().toString());
                }
                double temp = sur_income - expenses;
                long tot_sav = Math.round(temp);
                System.out.println(expenses);
                System.out.println(tot_sav);
                PieChart pie =  findViewById(R.id.piechart);
                PieChart pieChart = findViewById(R.id.piechart);
                ArrayList NoOfEmp = new ArrayList();
                ArrayList kpi = new ArrayList();
                kpi.add(new Entry(expenses, 0));
                kpi.add(new Entry((sur_income-expenses), 1));
                PieDataSet dataSet = new PieDataSet(kpi, "Surplus Income");


                ArrayList attr = new ArrayList();

                attr.add("Spent");
                attr.add("Outstanding");

                PieData data_pie = new PieData(attr, dataSet);
                pieChart.setData(data_pie);
                dataSet.setColors(new int[]{getResources().getColor(R.color.design_default_color_error),getResources().getColor(R.color.colorPrimary)});
                pieChart.animateXY(2000, 2000);

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
                        dashboard_main = new Intent(Dashboard.this, Dashboard.class);
                        dashboard_main.putExtra("username",username);
                        dashboard_main.putExtra("userid",userid);
                        startActivity(dashboard_main);
                        break;
                    case R.id.dash_board:
                        dashboard_main = new Intent(Dashboard.this, EmiPrediction.class);
                        dashboard_main.putExtra("username",username);
                        dashboard_main.putExtra("userid",userid);
                        startActivity(dashboard_main);
                        break;
                    case R.id.profile:
                         dashboard_main = new Intent(Dashboard.this, Profile.class);
                        dashboard_main.putExtra("username",username);
                        dashboard_main.putExtra("userid",userid);
                        startActivity(dashboard_main);
                        break;
                    case R.id.EditProfile:
                        dashboard_main = new Intent(Dashboard.this, EditProfile.class);
                        dashboard_main.putExtra("username",username);
                        dashboard_main.putExtra("userid",userid);
                        startActivity(dashboard_main);
                        break;
                    case R.id.Health:

                                dashboard_main = new Intent(Dashboard.this, DashboardMain.class);
                        dashboard_main.putExtra("username",username);
                        dashboard_main.putExtra("userid",userid);
                        startActivity(dashboard_main);
                        break;
                    case R.id.acct:
                        dashboard_main = new Intent(Dashboard.this, Account.class);
                        dashboard_main.putExtra("username", username);
                        dashboard_main.putExtra("userid", userid);

                        startActivity(dashboard_main);
                        break;

                }
                return true;
            }
        });

       // ConnectOData connectOData = new ConnectOData();
        //connectOData.execute(userid);



    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tool_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Bundle bundle = getIntent().getExtras();
        final String username = bundle.getString("username").toString();
        final String userid = bundle.getString("userid").toString();
        int id = item.getItemId();
        DatabaseReference mDatabase2;
        mDatabase2 = FirebaseDatabase.getInstance().getReference("Customer");

        //noinspection SimplifiableIfStatement
        if (id == R.id.chatbot) {
            DatabaseReference mDatabase1;
            mDatabase1 = FirebaseDatabase.getInstance().getReference("");
            mDatabase1.child("chat").removeValue();
            Intent chatbot = new Intent(Dashboard.this, chatbotActivity.class);
            chatbot.putExtra("username",username);
            chatbot.putExtra("userid",userid);
            startActivity(chatbot);
        }
        if(id == R.id.logout){
            moveTaskToBack(true);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
        if(id == R.id.sqlserver){
            SqlServerConnect sql = new SqlServerConnect(userid,username);
            sql.sqlImport();
            SqlServerConnectEMI sql1 = new SqlServerConnectEMI(userid,username);
            sql1.sqlImport();
        }
        return super.onOptionsItemSelected(item);
    }
}
