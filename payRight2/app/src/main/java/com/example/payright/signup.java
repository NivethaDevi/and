package com.example.payright;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Date;

public class signup extends AppCompatActivity {
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        final Button signup =  findViewById(R.id.signup);
        final TextView username = findViewById(R.id.username);
        final TextView password =  findViewById(R.id.pass_word);
        final TextView dob =  findViewById(R.id.dob);
        final TextView gender =  findViewById(R.id.gender);


        mDatabase = FirebaseDatabase.getInstance().getReference("");

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(username.getText().toString());
                System.out.println(password.getText().toString());
                System.out.println();

                JsonObject customerjo;
                customerjo = connectAPI("customer.json");

                JsonArray ja = customerjo.getAsJsonObject("body").getAsJsonArray("customerNames");
                JsonObject tempjo = ja.get(0).getAsJsonObject();
                tempjo.addProperty("customerName",username.getText().toString());
                JsonObject ae;
                ae = customerjo.getAsJsonObject("body");
                ae.addProperty("customerMnemonic", username.getText().toString());
                System.out.println("temp"+tempjo);
                System.out.println(customerjo);

                // customerjo.add("customerNames",ja);
                System.out.println(customerjo);
                System.out.println(ja);
                System.out.println("temp"+tempjo);
                System.out.println();

                String uri = "http://10.93.5.83:9089/irf-provider-container/api/v1.0.0/party/customers/";
                ConnectTemenosAPI connectTemenosAPI = new ConnectTemenosAPI(uri,customerjo);
                String json = connectTemenosAPI.connectApi();
                System.out.println("Response"+json);
                responseJson(json);
                JsonObject response = new JsonParser().parse(json).getAsJsonObject();
                String UserId = response.getAsJsonObject("header").get("id").getAsString();

                System.out.println(UserId);


                Toast.makeText(getApplicationContext(),"Your ID is:"+UserId,
                        Toast.LENGTH_SHORT).show();

                AccountCreate ac = new AccountCreate();
                JsonObject acc = ac.createAccount(getApplicationContext(),UserId,"PrimaryAccount");
                String accountID = acc.getAsJsonObject("data").getAsJsonObject("response2").getAsJsonObject("header").get("id").getAsString();
                String emiAccountID = acc.getAsJsonObject("data").getAsJsonObject("response1").getAsJsonObject("header").get("id").getAsString();

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date birthDate = null;
                try {
                    birthDate = sdf.parse(dob.getText().toString());

                    AgeCalculator age = new AgeCalculator();
                    int ageyear = age.calculateAge(birthDate);
                    System.out.println(ageyear);
                    mDatabase.child("Customer").child(UserId).child("age").setValue(ageyear);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //mDatabase.setValue(UserId);
                mDatabase.child("Customer").child(UserId).child("Username").setValue(username.getText().toString());
                mDatabase.child("Customer").child(UserId).child("Password").setValue(password.getText().toString());
                mDatabase.child("Customer").child(UserId).child("dob").setValue(dob.getText().toString());
                mDatabase.child("Customer").child(UserId).child("gender").setValue(gender.getText().toString());
                mDatabase.child("Customer").child(UserId).child("accountno").setValue(accountID);
                mDatabase.child("Customer").child(UserId).child("emiaccountno").setValue(emiAccountID);

                Intent dashboard = new Intent(signup.this, EditProfile.class);
                dashboard.putExtra("username",username.getText().toString());
                dashboard.putExtra("userid",UserId);
                startActivity(dashboard);
            }
        });
    }
    private JsonObject connectAPI(String filename ){
        AssetManager mgr = this.getApplicationContext().getAssets();
        JsonObject jsonObject = new JsonObject();
        try{
            InputStream in = mgr.open(filename, AssetManager.ACCESS_BUFFER);
            try (Reader reader = new InputStreamReader(in)) {

                jsonObject = new JsonParser().parse(reader).getAsJsonObject();
                return jsonObject;

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return jsonObject;
    }
    public void responseJson(String Json){
        Gson g = new Gson();
        JsonParser parser = new JsonParser();
        JsonObject json = (JsonObject) parser.parse(Json);
        System.out.println();
    }
}
