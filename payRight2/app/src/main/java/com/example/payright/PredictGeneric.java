package com.example.payright;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.xmlpull.v1.XmlPullParser.TYPES;

public class PredictGeneric {
    public int Surplus;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabase1;

    public void predictSurplus(final String userid, final int salary) {

        mDatabase = FirebaseDatabase.getInstance().getReference("Customer");
        mDatabase1 = FirebaseDatabase.getInstance().getReference();

        mDatabase.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                int yearstoadd = 20;
                Calendar now = Calendar.getInstance();

                ArrayList<String> years = new ArrayList<String>();
                int thisYear = Calendar.getInstance().get(Calendar.YEAR);
                for (int i = thisYear; i <= thisYear + 5; i++) {
                    years.add(Integer.toString(i));
                }


                // Set months
                final String[] Months = new String[]{"January", "February",
                        "March", "April", "May", "June", "July", "August", "September",
                        "October", "November", "December"};

                final int currentMonth = now.get(Calendar.MONTH);
                final int currentYear = now.get(Calendar.YEAR);
                System.out.println("current:" + currentMonth + ":" + currentYear);

                //      Integer healthInsurance = Integer.valueOf(dataSnapshot.child("healthinsurance").getValue().toString());

                LinkedHashMap<String, LinkedHashMap<String, Integer>> inputData = new LinkedHashMap<>();
                LinkedHashMap<String, Integer> predictedData = new LinkedHashMap<>();
                LinkedHashMap<String, int[][]> predictedMontlyData = new LinkedHashMap<>();

                Integer pension = 10000;
                Integer healthInsurance = 40000;
                int total = 0;
                Map<String, Integer> freq_values1 = new HashMap<>();
                freq_values1.put("monthly", 1);
                freq_values1.put("term", 4);
                freq_values1.put("once", 0);
                freq_values1.put("annual", 12);
                freq_values1.put("adhoc", 96);
                freq_values1.put("halfyearly",6);
                freq_values1.put("quarterly",3);

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    System.out.println(snapshot.getValue());
                    if (snapshot.getKey().equals("commitment")) {
                        System.out.println("hi:dashboard1");

                        for (DataSnapshot commit : snapshot.getChildren()) {
                            String frequency = commit.child("frequency").getValue().toString();
                            int freq;
                            if (frequency.equals("")) {
                                freq = 96;
                            } else {
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
                            if(!commit.child("date").getValue().toString().equals("")) {
                                 date = commit.child("date").getValue().toString().split("/");
                                 System.out.println(date);
                                month = Integer.valueOf(date[1]) - 1;
                                year = Integer.valueOf(date[2]);
                            }else{
                                month = 0;
                                year = 0;
                            }

                            System.out.println(commit.child("date").getValue().toString());

                            //System.out.println(date.split("/",2).toString().indexOf(2));

                            int margin;
                            if (commit.child("margin").getValue().toString().equals("")) {
                                margin = 0;
                            } else {
                                margin = Integer.valueOf(commit.child("margin").getValue().toString());
                            }
                            String commitment = commit.getKey();
                            if (commitment.toLowerCase().contains("dream")) {

                                double margin1 = (double)margin/100;
                                double amount2 =  (amount * margin1);

                                amount = (int)amount2;
                            } else if (commitment.toLowerCase().contains("pension")) {
                                   amount = Integer.valueOf(dataSnapshot.child("pensionfund").getValue().toString());
                                freq=6;
                                month=currentMonth;
                                year=currentYear;
                            } else if (commitment.toLowerCase().contains("medical")) {
                                 amount = Integer.valueOf(dataSnapshot.child("healthinsurance").getValue().toString());
                                //amount = 10000;
                            }
                            inputData.put(commitment, freqAmount(freq, amount, month, year));

                        }
                        break;

                    }
                }
                Prediction prediction = new Prediction(inputData, salary, yearstoadd, currentMonth, currentYear);
                predictedData = prediction.predictEMI();
                predictedMontlyData = prediction.predictEMIMonthly();

                String month = Months[currentMonth];
                int year = currentYear;
                Map<Integer, String> freq_values = new HashMap<>();
                freq_values.put(1, "Monthly");
                freq_values.put(4, "Term");
                freq_values.put(0, "Once");
                freq_values.put(12, "Annual");
                freq_values.put(96, "Adhoc");


                int i = -1;
                //for(i = -1; i < predictedData.size(); i ++) {
                for (LinkedHashMap.Entry<String, Integer> entry : predictedData.entrySet()) {
                    ++i;

                    System.out.println(inputData);
                    System.out.println(predictedData);
                    System.out.println(predictedMontlyData);

                    String key = entry.getKey();
                    LinkedHashMap<String, Integer> infA = new LinkedHashMap<>();
                    infA = inputData.get(key);
                    System.out.println(infA);
                    System.out.println(entry.getKey());
                    System.out.println(entry.getValue().toString());
                    int month_pos = Arrays.asList(Months).indexOf(month);
                    System.out.println(entry.getValue().toString());

                    int[][] monthEMI = predictedMontlyData.get(key);
                    System.out.println("everymont:" + year + ";" + month_pos);
                    total += monthEMI[year][month_pos];

                    System.out.println(monthEMI[year][currentMonth]);
                }


                Surplus = salary - total;
                mDatabase1.child("Customer").child(userid).child("surplus").setValue(Surplus);
                mDatabase1.child("Customer").child(userid).child("emiamount").setValue(total);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
        //ConnectOData connectOData = new ConnectOData();
        //connectOData.execute(userid);

        public LinkedHashMap<String,Integer> freqAmount(int frequency, int amount,int month, int year){
            LinkedHashMap<String,Integer> fA = new LinkedHashMap<>();
            fA.put("frequency",frequency);
            fA.put("amount",amount);
            fA.put("month",month);
            fA.put("year",year);
            return fA;
        }
}