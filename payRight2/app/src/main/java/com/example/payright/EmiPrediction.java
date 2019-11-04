package com.example.payright;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.xmlpull.v1.XmlPullParser.TYPES;

public class EmiPrediction extends AppCompatActivity {
    private ActionBarDrawerToggle abdt;
    private NavigationView nv;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.emiprediction);
        final Bundle bundle = getIntent().getExtras();
        final String username = bundle.getString("username").toString();
        final String userid = bundle.getString("userid").toString();
        final Activity act = new Activity();

        System.out.println("healthuserid" + userid);
        mDatabase = FirebaseDatabase.getInstance().getReference("Customer");
        final int yearstoadd=20;

        final Toolbar myToolbar = findViewById(R.id.toolbar);
        myToolbar.setNavigationIcon(R.drawable.nav_drawer);
        setSupportActionBar(myToolbar);


        final NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView tv = (TextView) headerView.findViewById(R.id.greetUser);
        tv.setText("Hi, " + username);
        final TextView surplus = findViewById(R.id.surplus);
        final TextView salarytv = findViewById(R.id.salary);

        final Menu nav_menu = navigationView.getMenu();
        final DatabaseReference mDatabase2;
        mDatabase2 = FirebaseDatabase.getInstance().getReference("Customer");
        mDatabase.keepSynced(true);

        mDatabase.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("retirement")) {
                    nav_menu.findItem(R.id.Health).setVisible(true);
                }
                for (DataSnapshot commit : dataSnapshot.child("commitment").getChildren()) {
                    for (DataSnapshot commit1 : commit.getChildren()) {
                        if (!commit1.hasChild("account")) {
                            CreateUtilityAccounts createUtilityAccounts = new CreateUtilityAccounts();
                            createUtilityAccounts.createAccounts(userid, getApplicationContext());
                        }
                        break;
                    }
                    break;
                }


                DrawerLayout dl;


            dl = findViewById(R.id.drawer_layout);
            abdt =new  ActionBarDrawerToggle(act,dl, myToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
                public void onDrawerClosed (View view){
                    super.onDrawerClosed(view);
                }

                public void onDrawerOpened (View drawerView){
                    super.onDrawerOpened(drawerView);
                }
            }

            ;
        dl.addDrawerListener(abdt);
        abdt.syncState();


            getSupportActionBar().

            setDisplayHomeAsUpEnabled(true);

            getSupportActionBar().

            setHomeButtonEnabled(true);
               final Map<String,Integer> freq_values1 = new HashMap<>();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()

            {
                @Override
                public boolean onNavigationItemSelected (@NonNull MenuItem menuItem) {
                    int id = menuItem.getItemId();
                    Intent dashboard_main;
                    switch (id) {
                        case R.id.home:
                            dashboard_main = new Intent(EmiPrediction.this, Dashboard.class);
                            dashboard_main.putExtra("username", username);
                            dashboard_main.putExtra("userid", userid);

                            startActivity(dashboard_main);
                            break;
                        case R.id.dash_board:
                            dashboard_main = new Intent(EmiPrediction.this, EmiPrediction.class);
                            dashboard_main.putExtra("username", username);
                            dashboard_main.putExtra("userid", userid);

                            startActivity(dashboard_main);
                            break;
                        case R.id.profile:
                            dashboard_main = new Intent(EmiPrediction.this, Profile.class);
                            dashboard_main.putExtra("username", username);
                            dashboard_main.putExtra("userid", userid);

                            startActivity(dashboard_main);
                            break;
                        case R.id.EditProfile:
                            dashboard_main = new Intent(EmiPrediction.this, EditProfile.class);
                            dashboard_main.putExtra("username", username);
                            dashboard_main.putExtra("userid", userid);

                            startActivity(dashboard_main);
                            break;
                        case R.id.Health:
                            dashboard_main = new Intent(EmiPrediction.this, DashboardMain.class);
                            dashboard_main.putExtra("username", username);
                            dashboard_main.putExtra("userid", userid);

                            startActivity(dashboard_main);
                            break;
                        case R.id.acct:
                            dashboard_main = new Intent(EmiPrediction.this, Account.class);
                            dashboard_main.putExtra("username", username);
                            dashboard_main.putExtra("userid", userid);

                            startActivity(dashboard_main);
                            break;
                    }
                    return true;
                }
            });




                ArrayList<String> years = new ArrayList<String>();
                int thisYear = Calendar.getInstance().get(Calendar.YEAR);
                for(int i = thisYear; i <= thisYear+yearstoadd; i++) {
                    years.add(Integer.toString(i));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, years);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                Spinner spinYear = (Spinner)findViewById(R.id.year);

                spinYear.setAdapter(adapter);
                // Set months
                final String[] Months = new String[] { "January", "February",
                        "March", "April", "May", "June", "July", "August", "September",
                        "October", "November", "December" };
                ArrayAdapter<String> adapterMonths = new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_spinner_item, Months);
                adapterMonths.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                Calendar now = Calendar.getInstance();

                final int currentMonth = now.get(Calendar.MONTH);
                final int currentYear = now.get(Calendar.YEAR);

                Spinner spinMonths = (Spinner)findViewById(R.id.month);
                spinMonths.setPrompt("Select Month");
                spinMonths.setAdapter(adapterMonths);






                final Button button1=findViewById(R.id.emi);
                final Button button2=findViewById(R.id.fd);
                final Button button = findViewById(R.id.go);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        button1.setVisibility(View.VISIBLE);
                        button2.setVisibility(View.VISIBLE);
                        LinkedHashMap<String, LinkedHashMap<String,Integer>> inputData = new LinkedHashMap<>();
                        LinkedHashMap<String, Integer> predictedData = new LinkedHashMap<>();
                        LinkedHashMap<String,int[][]> predictedMontlyData = new LinkedHashMap<>();
                        Integer salary = Integer.valueOf(dataSnapshot.child("salary").getValue().toString());

                        Integer pension = 10000;
                        Integer healthInsurance = 40000;
                        int total=0;
                        Map<Integer,String> freq_values = new HashMap<>();
                        freq_values.put(1,"Monthly");
                        freq_values.put(4,"Term");
                        freq_values.put(0,"Once");
                        freq_values.put(12,"Annual");
                        freq_values.put(96,"Adhoc");
                        freq_values.put(6,"Half Yearly");
                        freq_values.put(3,"Quarterly");


                        freq_values1.put("monthly",1);
                        freq_values1.put("term",4);
                        freq_values1.put("once",0);
                        freq_values1.put("annual",12);
                        freq_values1.put("adhoc",96);
                        freq_values1.put("halfyearly",6);
                        freq_values1.put("quarterly",3);
                        synchronized (dataSnapshot) {

                        }

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            System.out.println(snapshot.getValue());
                            if(snapshot.getKey().equals("commitment")){
                                System.out.println("hi:dashboard1");

                                for(DataSnapshot commit : snapshot.getChildren()){
                                    String frequency = commit.child("frequency").getValue().toString();
                                    int freq;
                                    if(frequency.equals("")){
                                        freq = 96;
                                    }else{
                                        freq = freq_values1.get(frequency.toLowerCase().replaceAll("\\s",""));

                                    }
                                    int amount;
                                    if(commit.child("amount").getValue().toString().equals("")) {
                                        amount = 0;
                                    }else{
                                        amount = Integer.valueOf(commit.child("amount").getValue().toString().replace("$", ""));
                                    }
                                    String date[];
                                    int month;
                                    int year;

                                    if(commit.child("amount").getValue().toString().equals("")) {
                                        amount = 0;
                                    }else{
                                        amount = Integer.valueOf(commit.child("amount").getValue().toString().replace("$", ""));
                                    }
                                    if(!commit.child("date").getValue().toString().equals("")) {
                                        date = commit.child("date").getValue().toString().split("/");
                                        System.out.println(date);
                                        month = Integer.valueOf(date[1]) - 1;
                                        year = Integer.valueOf(date[2]);
                                    }else{
                                        month = 0;
                                        year = 0;
                                    }
                           //         System.out.println(date[1]);
                                    System.out.println(commit.child("date").getValue().toString());

                                    //System.out.println(date.split("/",2).toString().indexOf(2));


                                    int margin;
                                    if(commit.child("margin").getValue().toString().equals("")){
                                        margin = 0;
                                    }else {
                                        margin = Integer.valueOf(commit.child("margin").getValue().toString());
                                    }
                                    String commitment = commit.getKey();
                                    if(commitment.toLowerCase().contains("dream")){

                                        double margin1 = (double)margin/100;
                                        double amount2 =  (amount * margin1);
                                        amount = (int)amount2;
                                    }
                                    else if(commitment.toLowerCase().contains("pension")){
                                        HealthPrediction he = new HealthPrediction(userid);
                                        he.doPrediction();

                                        amount = Integer.valueOf(dataSnapshot.child("pensionfund").getValue().toString());
                                        mDatabase.child(userid).child("commitment").child(commitment).child("amount").setValue(amount);

                                        freq=6;
                                         month=currentMonth;
                                         year=currentYear;
                                      //  amount=100000;
                                    }
                                    else if(commitment.toLowerCase().contains("medical")){

                                         amount = Integer.valueOf(dataSnapshot.child("healthinsurance").getValue().toString());
                                        mDatabase.child(userid).child("commitment").child(commitment).child("amount").setValue(amount);

                                        //amount=10000;
                                    }
                                        inputData.put(commitment,freqAmount(freq,amount,month,year));

                                }
                                break;
                            }
                        }

//                        inputData.put("pension",freqAmount(12,pension,0,2019));
//                        inputData.put("dream",freqAmount(96,pension,3,2020));
//                        inputData.put("Medical Insurance",freqAmount(12,healthInsurance,2,2019));
//                        inputData.put("Emergency",freqAmount(0,0,0,2019));
//                        inputData.put("Long Term FD",freqAmount(1,0,0,2019));
//                        inputData.put("Utility",freqAmount(4,500,5,2019));
                        Prediction prediction = new Prediction(inputData,salary,yearstoadd,currentMonth,currentYear);
                        predictedData = prediction.predictEMI();
                        predictedMontlyData = prediction.predictEMIMonthly();

                        TableLayout mTableLayout;
                        mTableLayout = (TableLayout) findViewById(R.id.table);
                        mTableLayout.setStretchAllColumns(true);
                        mTableLayout.removeAllViews();

                        Spinner spinner = (Spinner)findViewById(R.id.month);
                        String month = spinner.getSelectedItem().toString();
                        Spinner spinner1 = (Spinner)findViewById(R.id.year);
                        int year = Integer.valueOf(spinner1.getSelectedItem().toString());


                      formHeading(month);


                        int i=-1;
                        //for(i = -1; i < predictedData.size(); i ++) {
                        for (LinkedHashMap.Entry<String,Integer> entry : predictedData.entrySet()){
                            ++i;
                            final TextView tv1 = new TextView(getApplicationContext());
                            tv1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                    TableRow.LayoutParams.WRAP_CONTENT));
                            tv1.setGravity(Gravity.LEFT);
                            tv1.setPadding(5, 15, 0, 15);
                            final TextView tv2 = new TextView(getApplicationContext());
                            tv2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                    TableRow.LayoutParams.WRAP_CONTENT));
                            tv2.setGravity(Gravity.LEFT);
                            tv2.setPadding(5, 15, 0, 15);

                            final TextView tv3 = new TextView(getApplicationContext());
                            tv3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                    TableRow.LayoutParams.WRAP_CONTENT));
                            tv3.setGravity(Gravity.LEFT);
                            tv3.setPadding(5, 15, 0, 15);

                            final TextView tv4 = new TextView(getApplicationContext());
                            tv4.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                    TableRow.LayoutParams.WRAP_CONTENT));
                            tv4.setGravity(Gravity.LEFT);
                            tv4.setPadding(5, 15, 0, 15);

                            final TextView tv5 = new TextView(getApplicationContext());
                            tv5.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                    TableRow.LayoutParams.WRAP_CONTENT));
                            tv5.setGravity(Gravity.LEFT);
                            tv5.setPadding(5, 15, 0, 15);
                            if(i == -1) {
                                tv1.setText("Commitment");
                                tv2.setText("Frequency");
                                tv3.setText("Amount");
                                tv4.setText("Amount based on prediction");
                                tv5.setText(month);

                            }else{
                                System.out.println(inputData);
                                System.out.println(predictedData);
                                System.out.println(predictedMontlyData);

                                String key = entry.getKey();
                                LinkedHashMap<String,Integer> infA = new LinkedHashMap<>();
                                infA = inputData.get(key);
                                System.out.println(infA);
                                System.out.println(entry.getKey());
                                System.out.println(entry.getValue().toString());
                                System.out.println(month);
                                int month_pos = Arrays.asList(Months).indexOf(month);
                                System.out.println(entry.getValue().toString());

                                int [][] monthEMI = predictedMontlyData.get(key);
                                System.out.println("everymont:"+year+";"+month_pos);
                                System.out.println(monthEMI[2019][3]);

                                tv1.setText(key);
                                tv2.setText(freq_values.get(infA.get("frequency")));
                                tv3.setText(infA.get("amount").toString());
                                tv4.setText(String.format(entry.getValue().toString()));
                                tv5.setText(String.valueOf(monthEMI[year][month_pos]));
                                total+=monthEMI[year][month_pos];
                                mDatabase.child(userid).child("commitment").child(entry.getKey()).child("monthEmi").setValue(monthEMI[year][month_pos]);
                            }
                            int Surplus = salary - total;

                            mDatabase.child(userid).child("surplus").setValue(Surplus);
                            mDatabase.child(userid).child("emiamount").setValue(total);
                            final TableRow tr = new TableRow(getApplicationContext());
                            tr.setId(i + 1);

                            int leftRowMargin=0;

                            int topRowMargin=0;

                            int rightRowMargin=0;

                            int bottomRowMargin = 0;
                            TableLayout.LayoutParams trParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,

                                    TableLayout.LayoutParams.WRAP_CONTENT);

                            trParams.setMargins(leftRowMargin, topRowMargin, rightRowMargin, bottomRowMargin);

                            tr.setPadding(0, 0, 0, 0);

                            tr.setLayoutParams(trParams);


                            tr.addView(tv1);

                            tr.addView(tv2);
                            tr.addView(tv3);

                            tr.addView(tv4);
                            tr.addView(tv5);


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
                        surplus.setText("Surplus:"+String.valueOf(salary-total));
                        salarytv.setText("Salary:"+salary);
                        surplus.setVisibility(View.VISIBLE);
                        salarytv.setVisibility(View.VISIBLE);
                        Button button1=findViewById(R.id.emi);
                        Button button2=findViewById(R.id.fd);
//                        if(year==currentYear && month.equals(Months[currentMonth])){
//                            button1.setVisibility(View.VISIBLE);
//                            button2.setVisibility(View.VISIBLE);
//                        }
                    }
});

                Button emi = findViewById(R.id.emi);
                emi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Spinner spinner = (Spinner)findViewById(R.id.year);
                        int displayyear = Integer.valueOf(spinner.getSelectedItem().toString());
                        Spinner spinner1 = (Spinner)findViewById(R.id.month);
                        int displaymonth = Arrays.asList(Months).indexOf(spinner1.getSelectedItem().toString());
                        if(displayyear>=currentYear){
                            if((displayyear==currentYear && displaymonth>=currentMonth)||displayyear>currentYear) {
                        FundTransfer ft = new FundTransfer();
                        String debtAccount = dataSnapshot.child("accountno").getValue().toString();
                        String creditAccount = dataSnapshot.child("emiaccountno").getValue().toString();
                        int amount = Integer.valueOf(dataSnapshot.child("emiamount").getValue().toString());
                        JsonObject ftjo = ft.fundTransfer(getApplicationContext(),debtAccount,creditAccount,amount);
                        String ftID = ftjo.getAsJsonObject("header").get("id").getAsString();
                        mDatabase.child(userid).child("ft").setValue(ftID);
                        Toast.makeText(getApplicationContext(),"EMI successfully executed",
                                Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(),"EMI can't be executed for past months",
                                Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"EMI can't be executed for past months",
                            Toast.LENGTH_SHORT).show();
                }
                    }
                });
                button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String accountno = dataSnapshot.child("accountno").getValue().toString();
                        String emiaccountno = dataSnapshot.child("accountno").getValue().toString();

                        Spinner spinner = (Spinner)findViewById(R.id.year);
                        int displayyear = Integer.valueOf(spinner.getSelectedItem().toString());
                        Spinner spinner1 = (Spinner)findViewById(R.id.month);
                        int displaymonth = Arrays.asList(Months).indexOf(spinner1.getSelectedItem().toString());
                        if(displayyear>=currentYear){
                            if((displayyear==currentYear && displaymonth>=currentMonth)||displayyear>currentYear) {
                        for (DataSnapshot commit : dataSnapshot.child("commitment").getChildren()) {
                            String acc = userid;
                            String accountTemp = commit.child("account").getValue().toString();
                            String amountemi = commit.child("monthEmi").getValue().toString();
                            String frequency = commit.child("frequency").getValue().toString().toLowerCase().replaceAll("\\s","");

//                            for (DataSnx`apshot commit1 : commit.getChildren()) {
//                                System.out.println(commit1.getKey());

                             //   if (commit1.getKey().equals("frequency")) {
                                    // frequency = commit1.getValue().toString();

                                    if(frequency.equals("")){
                                        if(!commit.getKey().contains("pension")){
                                        frequency="adhoc";}
                                        else{
                                            frequency="half yearly";
                                        }
                                    }
                                            switch (frequency) {
                                                case "monthly":
                                                    FundTransfer ft = new FundTransfer();
                                                    ft.fundTransfer(getApplicationContext(), emiaccountno, accountTemp, Integer.valueOf(amountemi));
                                                    break;
                                                case "adhoc":
                                                    String dates[] = commit.child("date").getValue().toString().split("/");
                                                    int month = Integer.valueOf(dates[1]);
                                                    int year = Integer.valueOf(dates[2]);
                                                    String startdate1= "01/"+(displaymonth+1)+"/"+displayyear;

                                                    SimpleDateFormat
                                                            myFormat = new SimpleDateFormat("dd/MM/yyyy");
                                                    Date date1 = null;
                                                    Date date2 = null;
                                                    try {
                                                        date1 = myFormat.parse(startdate1.toString());
                                                        date2 = myFormat.parse(commit.child("date").getValue().toString());

                                                    } catch (ParseException e) {
                                                        e.printStackTrace();
                                                    }
                                                    long diff = date2.getTime() - date1.getTime();
                                                    System.out.println("dream-start:"+startdate1);
                                                    System.out.println("dream-end:"+date2);
                                                    System.out.println("Days: " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));

                                                    int term_days = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                                                    System.out.println("dream-days:"+term_days);

                                                    String term_days_d = term_days + "D";
                                                  LongTermFixedDeposit ltfd = new LongTermFixedDeposit();
                                                   ltfd.fundTransfer(getApplicationContext(), acc, term_days_d, Integer.valueOf(amountemi));

                                                    break;
                                                default:
                                                    int freq_month = freq_values1.get(frequency.toLowerCase().replaceAll("\\s",""));
                                                    dates = commit.child("date").getValue().toString().split("/");
                                                    month = displaymonth+1;
                                                    year = displayyear;
                                                    int term = (freq_month - (month % freq_month));
                                                    int tempmonth = month + term;
                                                    if (tempmonth > 12) {
                                                        tempmonth = tempmonth % 12;
                                                        year +=1;
                                                    }
                                                    startdate1= "01/"+(displaymonth+1)+"/"+displayyear;

                                                    String enddate = "01/" + tempmonth + "/" + year;
                                                    System.out.println("term-start:"+startdate1);
                                                    System.out.println("term-end:"+enddate);
                                                    myFormat = new SimpleDateFormat("dd/MM/yyyy");
                                                    date1 = null;
                                                    date2 = null;
                                                    try {
                                                        date1 = myFormat.parse(startdate1);
                                                        date2 = myFormat.parse(enddate);

                                                    } catch (ParseException e) {
                                                        e.printStackTrace();
                                                    }
                                                    diff = date2.getTime() - date1.getTime();
                                                    System.out.println("Days: " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
                                                    term_days = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                                                    System.out.println(term_days);
                                                    term_days_d = term_days + "D";
                                                    ltfd = new LongTermFixedDeposit();
                                                    ltfd.fundTransfer(getApplicationContext(), acc, term_days_d, Integer.valueOf(amountemi));


                                            }

                                        }
                                String primaryacct = dataSnapshot.child("accountno").getValue().toString();
                                String tilacct = "10995";
                                FundTransfer ft = new FundTransfer();
                                String salary = dataSnapshot.child("salary").getValue().toString();
                                ft.fundTransfer(getApplicationContext(),tilacct,primaryacct,Integer.valueOf(salary));

                                Toast.makeText(getApplicationContext(),"Payment successfully initiated",
                                        Toast.LENGTH_SHORT).show();
                                }else{
                                Toast.makeText(getApplicationContext(),"Payment can't be initiated for past months",
                                        Toast.LENGTH_SHORT).show();
                            }
                                }else{
                            Toast.makeText(getApplicationContext(),"Payment can't be initiated for past months",
                                    Toast.LENGTH_SHORT).show();
                        }

             ;
                    }


                });
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //ConnectOData connectOData = new ConnectOData();
        //connectOData.execute(userid);

    }
    public LinkedHashMap<String,Integer> freqAmount(int frequency, int amount,int month, int year){
        LinkedHashMap<String,Integer> fA = new LinkedHashMap<>();
        fA.put("frequency",frequency);
        fA.put("amount",amount);
        fA.put("month",month);
        fA.put("year",year);
        return fA;
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
            mDatabase1 = FirebaseDatabase.getInstance().getReference("Customer");
            mDatabase1.child("chat").removeValue();
            Intent chatbot = new Intent(EmiPrediction.this, chatbotActivity.class);            chatbot.putExtra("username",username);
            chatbot.putExtra("username",username);
            chatbot.putExtra("userid", userid);
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
    public void formHeading(String month){
        TableLayout mTableLayout1;
        mTableLayout1 = (TableLayout) findViewById(R.id.table1);
        mTableLayout1.setStretchAllColumns(true);
        mTableLayout1.removeAllViews();
        int leftRowMargin=0;

        int topRowMargin=0;

        int rightRowMargin=0;

        int bottomRowMargin = 0;

        int textSize = 0, smallTextSize =0, mediumTextSize = 0;
        int i=-1;
        int rows = 3;
        final TextView tv1 = new TextView(getApplicationContext());
        tv1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        tv1.setGravity(Gravity.LEFT);
        tv1.setPadding(5, 15, 0, 15);
        final TextView tv2 = new TextView(getApplicationContext());
        tv2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        tv2.setGravity(Gravity.LEFT);
        tv2.setPadding(5, 15, 0, 15);

        final TextView tv3 = new TextView(getApplicationContext());
        tv3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        tv3.setGravity(Gravity.LEFT);
        tv3.setPadding(5, 15, 0, 15);

        final TextView tv4 = new TextView(getApplicationContext());
        tv4.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        tv4.setGravity(Gravity.LEFT);
        tv4.setPadding(5, 15, 0, 15);

        final TextView tv5 = new TextView(getApplicationContext());
        tv5.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        tv5.setGravity(Gravity.LEFT);
        tv5.setPadding(5, 15, 0, 15);
        tv1.setText("Commitment");

        tv2.setText("Frequency");
        tv3.setText("Amount");
        tv4.setText("Predicted Amount");
        tv5.setText(month);
        tv1.setTextColor(Color.parseColor("#1f06f0"));
        tv2.setTextColor(Color.parseColor("#1f06f0"));
        tv3.setTextColor(Color.parseColor("#1f06f0"));
        tv4.setTextColor(Color.parseColor("#1f06f0"));
        tv5.setTextColor(Color.parseColor("#1f06f0"));

        final TableRow tr = new TableRow(getApplicationContext());
        tr.setId(i + 1);

        TableLayout.LayoutParams trParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,

                TableLayout.LayoutParams.WRAP_CONTENT);

        trParams.setMargins(leftRowMargin, topRowMargin, rightRowMargin, bottomRowMargin);

        tr.setPadding(0, 0, 0, 0);

        tr.setLayoutParams(trParams);


        tr.addView(tv1);

        tr.addView(tv2);
        tr.addView(tv3);

        tr.addView(tv4);
        tr.addView(tv5);


        mTableLayout1.addView(tr, trParams);




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

        mTableLayout1.addView(trSep, trParamsSep);
    }
}