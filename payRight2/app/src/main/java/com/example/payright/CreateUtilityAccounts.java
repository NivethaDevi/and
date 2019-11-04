package com.example.payright;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonObject;

import java.util.LinkedHashMap;

public class CreateUtilityAccounts {
    private DatabaseReference mDatabase;


    public void createAccounts(final String userid,final Context context){
        mDatabase = FirebaseDatabase.getInstance().getReference("Customer");
        final LinkedHashMap<String, String> Details = new LinkedHashMap<>();
        mDatabase.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    System.out.println(snapshot.getValue());
                    if (snapshot.getKey().equals("commitment")) {
                        int i = 0;
                        String account1 = new String();
                        String account2 = new String();
                        for (DataSnapshot commit : snapshot.getChildren()) {
                            String accountname = commit.getKey() + " account";
                            if (!commit.hasChild("account")) {
                                AccountCreate ac = new AccountCreate();
                                JsonObject acc = ac.createAccount(context, userid,accountname);
                                System.out.println(acc);
                                    account1 = acc.getAsJsonObject("data").getAsJsonObject("response2").getAsJsonObject("header").get("id").getAsString();
                                    Details.put("account", accountname + ":" + account1);
                                    mDatabase.child(userid).child("commitment").child(commit.getKey()).child("account").setValue(account1);

                            } else {
                                Details.put("account", accountname + ":" + commit.child("account").getValue());
                            }
                        }
                    }
                }
            }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }

