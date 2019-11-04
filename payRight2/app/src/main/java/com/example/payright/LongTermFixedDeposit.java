package com.example.payright;

import android.content.Context;
import android.content.res.AssetManager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class LongTermFixedDeposit {
    public JsonObject fundTransfer(Context context, String customer, String term,int amount) {
        String uri = "http://10.93.5.83:9089/irf-provider-container/api/v1.0.0/holdings/deposits/callDeposits";
        JsonObject ftjo = connectAPI("longtermFD.json",context);
        System.out.println(ftjo);



        JsonObject te,party,override;
        te = ftjo.getAsJsonObject("body").getAsJsonObject("commitment");
        te.addProperty("term",term);
        te.addProperty("amount",amount);

        party = ftjo.getAsJsonObject("body");
        JsonArray je = party.getAsJsonArray("partyIds");
        JsonObject jo = je.get(0).getAsJsonObject();
        jo.addProperty("partyId",customer);

        String description="Have you received Deposit Agreement/AAA*207 from "+customer+"}RECEIVED_NOT RECEIVED";
        override = ftjo.getAsJsonObject("header").getAsJsonObject("override");
        JsonArray overrideDetails = override.getAsJsonArray("overrideDetails");
        JsonObject temp = overrideDetails.get(0).getAsJsonObject();
        temp.addProperty("description",description);




        ConnectTemenosAPI connectTemenosAPI = new ConnectTemenosAPI(uri, ftjo);
        String json = connectTemenosAPI.connectApi();
        System.out.println("Response" + json);
        responseJson(json);
        JsonObject response = new JsonParser().parse(json).getAsJsonObject();
        return response;
    }
    public void responseJson(String Json){
        Gson g = new Gson();
        JsonParser parser = new JsonParser();
        JsonObject json = (JsonObject) parser.parse(Json);
        System.out.println();
    }
    private JsonObject connectAPI(String filename, Context context ){
        AssetManager mgr = context.getAssets();
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
}
