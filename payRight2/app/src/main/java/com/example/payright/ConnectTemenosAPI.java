package com.example.payright;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.AsyncTask;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonArray;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;


import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import com.google.gson.*;


public class ConnectTemenosAPI {
    Activity activity;
    String uri;
    JsonObject jsonObject;
    Map<String,String> mResponseList;
    String filename;
    ConnectTemenosAPI(String uri, JsonObject jsonObject){
        this.uri =uri;
        this.jsonObject = jsonObject;
    }
    protected String connectApi(Void... params) {
        String AddedResult =  new String();
        try {
            URI uriLogin = URI.create(uri);
            HttpPost request = new HttpPost(uriLogin);
            HttpClient client = HttpClientBuilder.create().build();
            HttpResponse response = null;
                request.addHeader("content-type", "application/json");
                System.out.println(jsonObject);
                String str = String.valueOf(jsonObject);
                StringEntity params1 =new StringEntity(str);
                System.out.println("Entity:"+params1);
                request.setEntity(params1);
            response = client.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            System.out.println("Status:" + statusCode);
            HttpEntity entitynext = response.getEntity();
            AddedResult = EntityUtils.toString(entitynext);
            return AddedResult;
        }catch (Exception e) {
            e.printStackTrace();
        }
            return AddedResult;
        }

}
