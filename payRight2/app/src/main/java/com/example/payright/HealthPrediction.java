package com.example.payright;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HealthPrediction {

    final public String userid;

    public HealthPrediction(String userid) {
        this.userid = userid;
    }

    public void doPrediction(){
    final DatabaseReference mDatabase;
    mDatabase = FirebaseDatabase.getInstance().getReference("Customer");

//     mDatabase.child(userid).child("Calories").setValue("ModerateHealthy");
//        mDatabase.child(userid).child("HeartRate").setValue("NotHealthy");
//        mDatabase.child(userid).child("SleepingFactor").setValue("Active");
        //mDatabase.keepSynced(true);
        ConnectOData connectOData = new ConnectOData();
        connectOData.execute(userid);
        mDatabase.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String caloriesConsumed = dataSnapshot.child("Calories").getValue().toString();
                String HeartRate = dataSnapshot.child("HeartRate").getValue().toString();
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


                String bmi_status = calculateEMI(height, weight);


                Map<String, Integer> status = new HashMap<String, Integer>();
                status.put("ModerateHealthy", 1);
                status.put("NotHealthy", 0);
                status.put("Active", 2);
                status.put("Heavy smoker", 0);
                status.put("Light smoker", 1);
                status.put("Former smoker", 0);
                status.put("Do not smoke", 0);
                status.put("Heavy drinker", 0);
                status.put("Several drink per day", 0);
                status.put("Almost daily ", 1);
                status.put("Few drinks per week", 1);
                status.put("Occasional", 0);
                status.put("Poor", 0);
                status.put("Insufficient", 0);
                status.put("Moderate", 1);
                status.put("Healthy", 2);
                status.put("High", 0);
                status.put("Low", 1);


                System.out.println(status.get(caloriesConsumed));
                System.out.println(status.get(HeartRate));
                System.out.println(status.get(SleepingFactor));
                System.out.println(status.get(smoke));
                System.out.println(status.get(alcohol));
                System.out.println(status.get(bmi_status));
                System.out.println(status.get(diet));
                System.out.println(status.get(stress));
                int healthRating = status.get(caloriesConsumed) + status.get(HeartRate) + status.get(SleepingFactor) + status.get(smoke) + status.get(alcohol) + status.get(bmi_status) + status.get(diet) + status.get(stress);
                mDatabase.child(userid).child("HealthRating").setValue(healthRating);
                Integer lE = calculateLifeExpectancy(healthRating);

                Map expenses = predictTotalRetirementExpenses(salary, age, retirement, lE, healthRating);
                mDatabase.child(userid).child("pensionfund").setValue(expenses.get("pensionFundAnnual"));
                mDatabase.child(userid).child("healthinsurance").setValue(expenses.get("healthInsuranceAnnual"));
                mDatabase.child(userid).child("LifeExpectancy").setValue(lE);


        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
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
