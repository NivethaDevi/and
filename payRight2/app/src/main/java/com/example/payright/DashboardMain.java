package com.example.payright;

import android.content.Intent;
import android.graphics.Color;
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

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DashboardMain extends AppCompatActivity {
    private DrawerLayout dl;
    private ActionBarDrawerToggle abdt;
    private NavigationView nv;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_main);
        Bundle bundle = getIntent().getExtras();
        final String username = bundle.getString("username").toString();
        final String userid = bundle.getString("userid").toString();
        System.out.println("healthuserid"+userid);
        mDatabase = FirebaseDatabase.getInstance().getReference("Customer");

        final String[] values = {""};
        Toolbar myToolbar = findViewById(R.id.toolbar);
        myToolbar.setNavigationIcon(R.drawable.nav_drawer);
        setSupportActionBar(myToolbar);
        final TextView calories= findViewById(R.id.caloriesConsumed);
        final TextView heartrate= findViewById(R.id.HeartRate);
        final TextView sleepingfactor= findViewById(R.id.SleepingFactor);
        final TextView smoker= findViewById(R.id.Smoker);
        final TextView alcohol1= findViewById(R.id.alcohol);
        final TextView bmi= findViewById(R.id.bmi_status);
        final TextView diet1= findViewById(R.id.diet);
        final TextView stress1= findViewById(R.id.stress);
        final TextView retirement1= findViewById(R.id.retirement);
        final TextView healthstatus1= findViewById(R.id.healthstatus);
        final TextView pf1= findViewById(R.id.pf);
        final TextView healthinsurance1= findViewById(R.id.healthinsurance);
        final TextView lifeExpectancy= findViewById(R.id.healthstatus);
        final TextView pf= findViewById(R.id.pf);
        final TextView healthinsurance= findViewById(R.id.healthinsurance);


//        mDatabase.child(userid).child("Calories").setValue("ModerateHealthy");
//        mDatabase.child(userid).child("HeartRate").setValue("NotHealthy");
//        mDatabase.child(userid).child("SleepingFactor").setValue("Active");
        mDatabase.keepSynced(true);
        mDatabase.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String caloriesConsumed = dataSnapshot.child("Calories").getValue().toString();
                String HeartRate1 = dataSnapshot.child("HeartRate").getValue().toString();
                String SleepingFactor = dataSnapshot.child("SleepingFactor").getValue().toString();
                String smoke = dataSnapshot.child("smoke").getValue().toString();
                String alcohol = dataSnapshot.child("alcohol").getValue().toString();
                String stress = dataSnapshot.child("stress").getValue().toString();
                String diet = dataSnapshot.child("diet").getValue().toString();
                int height = Integer.valueOf(dataSnapshot.child("height").getValue().toString());
                int weight = Integer.valueOf(dataSnapshot.child("weight").getValue().toString());
                int salary = Integer.valueOf(dataSnapshot.child("salary").getValue().toString());
                int retirement = Integer.valueOf(dataSnapshot.child("retirement").getValue().toString());
                int age = Integer.valueOf(dataSnapshot.child("age").getValue().toString());

                calories.setText("Calories consumed:"+caloriesConsumed);
                heartrate.setText("Heart rate:"+HeartRate1);
                sleepingfactor.setText("Sleeping factor:"+SleepingFactor);
                smoker.setText("Smoke:"+smoke);
                alcohol1.setText("Alcohol:"+alcohol);
                stress1.setText("Stress:"+stress);
                diet1.setText("Diet:"+diet);
                retirement1.setText("Retirement:"+retirement);



                System.out.println("Smoke:"+smoke);
                String bmi_status = calculateEMI(height,weight);
                bmi.setText("BMI:"+bmi_status);



                Map<String, Integer> status = new HashMap<String, Integer>();
                status.put("ModerateHealthy", 1);
                status.put("NotHealthy", 0);
                status.put("Active", 2);
                status.put("Heavy smoker",0);
                status.put("Light smoker",1);
                status.put("Former smoker",0);
                status.put("Do not smoke",0);
                status.put("Heavy drinker",0);
                status.put("Several drink per day",0);
                status.put("Almost daily ",1);
                status.put("Few drinks per week",1);
                status.put("Occasional",0);
                status.put("Poor",0);
                status.put("Insufficient",0);
                status.put("Moderate",1);
                status.put("Healthy",2);
                status.put("High",0);
                status.put("Low",1);



//                HorizontalBarChart barChart = findViewById(R.id.Barchart);
//                ArrayList NoOfEmp = new ArrayList();
//                ArrayList kpi = new ArrayList();
//                kpi.add(new BarEntry(status.get(caloriesConsumed), 0));
//                kpi.add(new BarEntry(status.get(HeartRate), 1));
//                kpi.add(new BarEntry(status.get(SleepingFactor), 2));
//                kpi.add(new BarEntry(status.get(bmi_status), 3));
//                kpi.add(new BarEntry(status.get(smoke), 4));
//                kpi.add(new BarEntry(status.get(alcohol), 5));
//                kpi.add(new BarEntry(status.get(diet), 6));
//                kpi.add(new BarEntry(status.get(stress), 7));
//
//
//                BarDataSet dataSet = new BarDataSet(kpi, "Health Level");
//                dataSet.setColor(getResources().getColor(R.color.colorPrimary));
//
//
//                ArrayList attr = new ArrayList();
//
//                attr.add("Calories");
//                attr.add("HeartRate");
//                attr.add("SleepingFactor");
//                attr.add("BMI");
//                attr.add("smoking");
//                attr.add("alcohol");
//                attr.add("diet");
//                attr.add("stress");
//
//
//
//
//                BarData data_bar = new BarData(attr, dataSet);
//                barChart.setData(data_bar);
//                dataSet.setColors(new int[]{ getResources().getColor(R.color.colorPrimary)});
//                barChart.animateXY(2000, 2000);
                int healthRating = status.get(caloriesConsumed)+status.get(HeartRate1)+status.get(SleepingFactor)+status.get(smoke)+status.get(alcohol)+status.get(bmi_status)+status.get(diet);
                mDatabase.child(userid).child("HealthRating").setValue(healthRating);
                Integer lE = calculateLifeExpectancy(healthRating);
                lifeExpectancy.setText("Life Expectancy:"+lE);


                Map expenses = predictTotalRetirementExpenses(salary,age,retirement, lE,healthRating);
                mDatabase.child(userid).child("pensionfund").setValue(expenses.get("pensionFundAnnual"));
                mDatabase.child(userid).child("healthinsurance").setValue(expenses.get("healthInsuranceAnnual"));
                mDatabase.child(userid).child("LifeExpectancy").setValue(lE);
                pf.setText("Pension Fund(Annual):"+expenses.get("pensionFundAnnual"));
                healthinsurance.setText("Health Insurance(Annual):"+expenses.get("healthInsuranceAnnual"));


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView tv = (TextView) headerView.findViewById(R.id.greetUser);
        tv.setText("Hi, " + username);

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
                    case R.id.home:
                        dashboard_main = new Intent(DashboardMain.this, Dashboard.class);
                        dashboard_main.putExtra("username",username);
                        dashboard_main.putExtra("userid",userid);

                        startActivity(dashboard_main);
                        break;
                    case R.id.dash_board:
                        dashboard_main = new Intent(DashboardMain.this, EmiPrediction.class);
                        dashboard_main.putExtra("username",username);
                        dashboard_main.putExtra("userid",userid);

                        startActivity(dashboard_main);
                        break;
                    case R.id.profile:
                        dashboard_main = new Intent(DashboardMain.this, Profile.class);
                        dashboard_main.putExtra("username",username);
                        dashboard_main.putExtra("userid",userid);

                        startActivity(dashboard_main);
                        break;
                    case R.id.EditProfile:
                        dashboard_main = new Intent(DashboardMain.this, EditProfile.class);
                        dashboard_main.putExtra("username",username);
                        dashboard_main.putExtra("userid",userid);

                        startActivity(dashboard_main);
                        break;
                    case R.id.Health:
                         dashboard_main = new Intent(DashboardMain.this, DashboardMain.class);
                        dashboard_main.putExtra("username",username);
                        dashboard_main.putExtra("userid",userid);

                        startActivity(dashboard_main);
                        break;
                    case R.id.acct:
                        dashboard_main = new Intent(DashboardMain.this, Account.class);
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
    public Integer calculateLifeExpectancy(int healthrating){
        switch (healthrating){
            case 0:
                return 45;

            case 1:
                return 48;

            case 2:
                return 50;

            case 3:
                return 55;

            case 4:
                return 60;
            case 5:
                return 65;

            case 6:
                return 70;

            case 7:
                return 75;
            case 8:
                return 80;
            case 9:
                return 85;
            case 10:
                return 88;
            case 11:
                return 90;
            case 12:
                return 92;
            case 13:
                return 94;
            case 14:
                return 95;
            case 15:
                return 98;
            case 16:
                return 100;

        }
        return 45;
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
        Bundle bundle = getIntent().getExtras();
        final String username = bundle.getString("username").toString();
        final String userid = bundle.getString("userid").toString();
        //noinspection SimplifiableIfStatement
        if (id == R.id.chatbot) {
            DatabaseReference mDatabase1;
            mDatabase1 = FirebaseDatabase.getInstance().getReference("");
            mDatabase1.child("chat").removeValue();
            Intent chatbot = new Intent(DashboardMain.this, chatbotActivity.class);
            chatbot.putExtra("username",username);
            chatbot.putExtra("userid",userid);
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
    public String calculateEMI(int height, int weight){
        double bmi = weight/(height*height*0.0001);
        String bmi_status = "NotHealthy";
        if(bmi<18.5 || bmi>=30){
            bmi_status = "NotHealthy";
        }
        if(bmi>=18.5 && bmi<=24.9){
            bmi_status = "Active";
        }
        if(bmi>=25 && bmi<=29.9){
            bmi_status = "ModerateHealthy";
        }
        return bmi_status;
    }
    public Map predictTotalRetirementExpenses(int salary, int age, int retirement, int lE, int healthrating){
        Map<String,Integer> exp = new HashMap<>();
        exp.put("pensionFundAnnual",0);
        exp.put("healthInsuranceAnnual",0);
        if(lE - retirement >0 && age<retirement && age<=lE){
            int yearsAfterRetirement = lE - retirement;
            long expenseAfterRetirement = (long)((0.6*salary*12)*yearsAfterRetirement);
            int ageToRetirement = retirement - age;
            int pensionFundAnnual = (int)expenseAfterRetirement/ageToRetirement;
            int healthInsuranceAnnual = (int)Math.round(salary*12*0.01*(Math.abs(healthrating-12)+1));
            exp.put("pensionFundAnnual",pensionFundAnnual);
            exp.put("healthInsuranceAnnual",healthInsuranceAnnual);
        }
        return exp;
    }



}

