package com.example.payright;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import ai.api.AIServiceContext;
import ai.api.android.AIDataService;
import ai.api.model.AIRequest;

import static java.sql.Types.NULL;

public class SqlServerConnectEMI {
    // SET CONNECTIONSTRING
    String emiQuery,commitmentQuery;
    private DatabaseReference mDatabase;
    String userid;
    String username;
    SqlServerConnectEMI(String userid, String username){
        this.userid=userid;
        this.username = username;
    }
    public void sqlImport() {
        try {

            executeQuery();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public void executeQuery(){
        try {
            //  ResultSet reset = stmt.executeQuery(emiQuery);
            mDatabase = FirebaseDatabase.getInstance().getReference("Customer");

            mDatabase.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                    StringBuilder Values=new StringBuilder();
                    StringBuilder fields=new StringBuilder();
                    ArrayList<String> field = new ArrayList<>();
                    field.add(doubleQ("CustomerID"));
                    field.add(doubleQ("CustomerName"));
                    field.add(doubleQ("CommitmentId"));
                    field.add(doubleQ("CommitmentName"));
                    field.add(doubleQ("Year"));
                    field.add(doubleQ("Month"));
                    field.add(doubleQ("EmiAmount"));
                    field.add(doubleQ("YearMonth"));


                    String username = "nivetha";
                    String password = "Temenos_123";

                    Log.w("Connection", "open");
                    //  stmt = DbConn.createStatement();
                    Calendar now = Calendar.getInstance();
                    final int currentMonth = now.get(Calendar.MONTH);
                    final int currentYear = now.get(Calendar.YEAR);
                    int yearstoadd = 10;
                    int freq=0,amount=0,month=0,year=0;
                    Integer salary = Integer.valueOf(dataSnapshot.child("salary").getValue().toString());
                    LinkedHashMap<String, Integer> predictedData = new LinkedHashMap<>();
                    LinkedHashMap<String, int[][]> predictedMontlyData = new LinkedHashMap<>();
                    Map<String, Integer> freq_values1 = new HashMap<>();
                    LinkedHashMap<String, LinkedHashMap<String, Integer>> inputData = new LinkedHashMap<>();
                    String commitment = new String();
                    StringBuilder updateQuery;
                    String query;
                    for (DataSnapshot commit : dataSnapshot.child("commitment").getChildren()) {
                        LinkedHashMap<String, String> lh = new LinkedHashMap<>();
                        updateQuery = new StringBuilder();
                        String commitmentID = String.valueOf(commitmentID(commit.getKey()));
                        fields = new StringBuilder();
                        fields.append(field.get(0));
                        fields.append(",").append(field.get(1));
                        fields.append(",").append(field.get(2));
                        fields.append(",").append(field.get(3));
                        fields.append(",").append(field.get(4));
                        fields.append(",").append(field.get(5));
                        fields.append(",").append(field.get(6));


                        freq_values1.put("monthly", 1);
                        freq_values1.put("term", 4);
                        freq_values1.put("once", 0);
                        freq_values1.put("annual", 12);
                        freq_values1.put("adhoc", 96);
                        freq_values1.put("halfyearly", 6);
                        freq_values1.put("quarterly", 3);

                        String frequency = commit.child("frequency").getValue().toString();
                        if (frequency.equals("")) {
                            freq = 96;
                        } else {
                            freq = freq_values1.get(frequency.toLowerCase().replaceAll("\\s", ""));

                        }
                        if (commit.child("amount").getValue().toString().equals("")) {
                            amount = 0;
                        } else {
                            amount = Integer.valueOf(commit.child("amount").getValue().toString().replace("$", ""));
                        }
                        String date[];

                        if (commit.child("amount").getValue().toString().equals("")) {
                            amount = 0;
                        } else {
                            amount = Integer.valueOf(commit.child("amount").getValue().toString().replace("$", ""));
                        }
                        if (!commit.child("date").getValue().toString().equals("")) {
                            date = commit.child("date").getValue().toString().split("/");
                            System.out.println(date);
                            month = Integer.valueOf(date[1]) - 1;
                            year = Integer.valueOf(date[2]);
                        } else {
                            month = 0;
                            year = 0;
                        }
                        //         System.out.println(date[1]);
                        System.out.println(commit.child("date").getValue().toString());

                        //System.out.println(date.split("/",2).toString().indexOf(2));


                        int margin;
                        if (commit.child("margin").getValue().toString().equals("")) {
                            margin = 0;
                        } else {
                            margin = Integer.valueOf(commit.child("margin").getValue().toString());
                        }
                        commitment = commit.getKey();
                        if (commitment.toLowerCase().contains("dream")) {

                            double margin1 = (double) margin / 100;
                            double amount2 = (amount * margin1);
                            amount = (int) amount2;
                        } else if (commitment.toLowerCase().contains("pension")) {
                            amount = Integer.valueOf(dataSnapshot.child("pensionfund").getValue().toString());
                            mDatabase.child(userid).child("commitment").child(commitment).child("amount").setValue(amount);

                            freq = 6;
                            month = currentMonth;
                            year = currentYear;
                            //  amount=100000;
                        } else if (commitment.toLowerCase().contains("medical")) {

                            amount = Integer.valueOf(dataSnapshot.child("healthinsurance").getValue().toString());
                            mDatabase.child(userid).child("commitment").child(commitment).child("amount").setValue(amount);

                            //amount=10000;
                        }

                        inputData.put(commitment, freqAmount(freq, amount, month, year));


                        Prediction prediction = new Prediction(inputData, salary, yearstoadd, currentMonth, currentYear);
                        predictedData = prediction.predictEMI();
                        predictedMontlyData = prediction.predictEMIMonthly();
                        final String[] Months = new String[]{"January", "February",
                                "March", "April", "May", "June", "July", "August", "September",
                                "October", "November", "December"};
                        for (LinkedHashMap.Entry<String, Integer> entry : predictedData.entrySet()) {
                            int month_pos = Arrays.asList(Months).indexOf(month);
                            String key = entry.getKey();
                            int[][] monthEMI = predictedMontlyData.get(key);
                            for (int i = currentYear; i < currentYear + yearstoadd; i++) {
                                for (int j = 0; j < 12; j++) {
                                    Values = new StringBuilder();
                                    updateQuery = new StringBuilder();
                                    Values.append(singleQ(userid)).append(",").append(singleQ(username));
                                    Values.append(",").append(singleQ(String.valueOf(commitmentID(entry.getKey()))));
                                    updateQuery.append(field.get(0)).append("=").append(singleQ(userid));
                                    updateQuery.append(",").append(field.get(1)).append("=").append(singleQ(username));
                                    updateQuery.append(",").append(field.get(2)).append("=").append(singleQ(String.valueOf(commitmentID(entry.getKey()))));
                                    Values.append(",").append(singleQ(entry.getKey()));
                                    updateQuery.append(",").append(field.get(3)).append("=").append(singleQ(entry.getKey()));
                                    LinkedHashMap<String,Integer> infA = new LinkedHashMap<>();
                                    String amt=new String();
                                    if(monthEMI[i][j+1]!=0) {
                                        infA = inputData.get(key);
                                        amt = infA.get("amount").toString();
                                    }else{
                                        amt="0";
                                    }
                                    Values.append(",").append(singleQ(String.valueOf(i)));
                                    Values.append(",").append(singleQ(String.valueOf(j + 1)));
                                    Values.append(",").append(singleQ(amt));

                                    updateQuery.append(",").append(field.get(4)).append("=").append(singleQ(String.valueOf(i)));
                                    updateQuery.append(",").append(field.get(5)).append("=").append(singleQ(String.valueOf(j + 1)));
                                    updateQuery.append(",").append(field.get(6)).append("=").append(amt);

                                    query = "MERGE INTO InsightLanding.SURVEY.CustomerEmi AS TARGET USING(VALUES (" + Values + ")) AS SOURCE (" + fields + ") ON TARGET.[CustomerId]=SOURCE.[CustomerId] AND TARGET.[CommitmentId]=SOURCE.[CommitmentId] AND TARGET.[Year]=SOURCE.[Year] AND TARGET.[Month]=SOURCE.[Month] WHEN NOT MATCHED BY TARGET THEN INSERT (" + fields + ") VALUES (" + Values + ") WHEN MATCHED THEN UPDATE SET " + updateQuery + ";";
                                    System.out.println(query);
                                    try {
                                        Connection DbConn = DriverManager.getConnection("jdbc:jtds:sqlserver://10.93.5.83:1433/InsightLanding;user=DWUSER;password=DWUSER");
                                        final Statement stmt = DbConn.createStatement();
                                        Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                                        int re = stmt.executeUpdate(query);

                                        System.out.println(re);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }


                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public String singleQ(String str){
        String singleQuote = "\'";
        return singleQuote+str+singleQuote;
    }
    public String doubleQ(String str){
        String singleQuote = "\"";
        return singleQuote+str+singleQuote;
    }
    public int commitmentID(String str){
        int id=11;
        String strFin = str.toLowerCase().replaceAll("\\s","");
        if(strFin.contains("education")){
            id=1;
        }
        if(strFin.contains("transport")){
            id=2;
        }
        if(strFin.contains("fuel")){
            id=3;
        }
        if(strFin.contains("utility")){
            id=4;
        }
        if(strFin.contains("fixeddeposit")){
            id=5;
        }
        if(strFin.contains("fd")||str.toLowerCase().contains("longtermfd")){
            id=6;
        }
        if(strFin.contains("mortgage")){
            id=7;
        }
        if(strFin.contains("pension")){
            id=8;
        }
        if(strFin.contains("medical")){
            id=9;
        }
        if(strFin.contains("dream")){
            id=10;
        }

        return id;
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
