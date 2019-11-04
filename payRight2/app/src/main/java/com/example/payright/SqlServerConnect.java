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
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.UUID;

import ai.api.AIServiceContext;
import ai.api.android.AIDataService;
import ai.api.model.AIRequest;

import static java.sql.Types.NULL;

public class SqlServerConnect {
    // SET CONNECTIONSTRING
    String emiQuery,commitmentQuery;
    private DatabaseReference mDatabase;
    String userid;
    String username;
    SqlServerConnect(String userid, String username){
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
                    field.add(doubleQ("StartDate"));
                    field.add(doubleQ("EndDate"));
                    field.add(doubleQ("Frequency"));
                    field.add(doubleQ("FrequencyAmount"));
                    field.add(doubleQ("MarginPercentage"));


                        String username = "nivetha";
                        String password = "Temenos_123";

                        Log.w("Connection", "open");
                      //  stmt = DbConn.createStatement();


                    for (DataSnapshot commit : dataSnapshot.child("commitment").getChildren()) {
                        LinkedHashMap<String,String> lh = new LinkedHashMap<>();
                        String query;
                        StringBuilder updateQuery=new StringBuilder();
                        String commitmentID = String.valueOf(commitmentID(commit.getKey()));
                        fields = new StringBuilder();
                        fields.append(field.get(0));

                        fields.append(",").append(field.get(1));
                        fields.append(",").append(field.get(2));
                        fields.append(",").append(field.get(3));
                        fields.append(",").append(field.get(4));
                        fields.append(",").append(field.get(5));
                        fields.append(",").append(field.get(6));
                        fields.append(",").append(field.get(7));
                        fields.append(",").append(field.get(8));

                        Values = new StringBuilder();
                        Values.append(singleQ(userid)).append(",").append(singleQ(username));
                        Values.append(",").append(singleQ(commitmentID));
                        updateQuery.append(field.get(0)).append("=").append(singleQ(userid));
                        updateQuery.append(",").append(field.get(1)).append("=").append(singleQ(username));
                        updateQuery.append(",").append(field.get(2)).append("=").append(singleQ(commitmentID));
                        Values.append(",").append(singleQ(commit.getKey()));
                        updateQuery.append(",").append(field.get(3)).append("=").append(singleQ(commit.getKey()));

                            for (DataSnapshot commit1 : commit.getChildren()) {
                                lh.put(commit1.getKey(), commit1.getValue().toString());
                            }
                        String date = lh.get("date");
                            if(date.equals("")){
                                date = "null";
                            }else{
                                date = "convert(date,"+singleQ(date)+",103)";
                            }
                        if (commitmentID(commit.getKey()) == 10) {
                                Values.append(",").append("null");
                                Values.append(",").append(date);
                                updateQuery.append(",").append(field.get(4)).append("=").append("null");
                                updateQuery.append(",").append(field.get(5)).append("=").append(date);
                            } else {
                                Values.append(",").append(date);
                                Values.append(",").append("null");
                                updateQuery.append(",").append(field.get(4)).append("=").append(date);
                                updateQuery.append(",").append(field.get(5)).append("=").append("null");
                            }
                            String amount = lh.get("amount").replace("$","");
                            Values.append(",").append(singleQ(lh.get("frequency")));
                            Values.append(",").append(singleQ(amount));
                            Values.append(",").append(singleQ(lh.get("margin")));
                            updateQuery.append(",").append(field.get(6)).append("=").append(singleQ(lh.get("frequency")));
                            updateQuery.append(",").append(field.get(7)).append("=").append(singleQ(amount));
                            updateQuery.append(",").append(field.get(8)).append("=").append(singleQ(lh.get("margin")));

                            query = "MERGE INTO InsightLanding.SURVEY.CustomerCommitment AS TARGET USING(VALUES (" + Values + ")) AS SOURCE (" + fields + ") ON TARGET.[CommitmentId]=SOURCE.[CommitmentId] AND TARGET.[CustomerId]=SOURCE.[CustomerId] WHEN NOT MATCHED BY TARGET THEN INSERT (" + fields + ") VALUES (" + Values + ") WHEN MATCHED THEN UPDATE SET " + updateQuery + ";";
                            System.out.println(query);
                            try{
                        Connection DbConn = DriverManager.getConnection("jdbc:jtds:sqlserver://10.93.5.83:1433/InsightLanding;user=DWUSER;password=DWUSER" );
                        final Statement stmt = DbConn.createStatement() ;
                        Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                           int  re= stmt.executeUpdate(query);

                        }catch (Exception e){
                            e.printStackTrace();
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

}
