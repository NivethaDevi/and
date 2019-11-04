package com.example.payright;

import android.net.Credentials;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class ConnectOData extends AsyncTask<String, Void, Map> {
    protected Map<String, String> doInBackground(String... params) {
        Map<String,String> mResponseList = new HashMap<String,String>();
         DatabaseReference mDatabase;

        try {
            mDatabase = FirebaseDatabase.getInstance().getReference();

            URI uriLogin = URI.create("http://10.93.5.83/APIService/odata/Tenant1/Dataset_DailyActivity2017");
            HttpGet httpget = new HttpGet(uriLogin);
            String auth = "admin@temenos.com" + ':' + "password";
            byte[] encodedAuth = Base64.encodeBase64(
                    auth.getBytes(StandardCharsets.ISO_8859_1));
            String authHeader = "Basic " + new String(encodedAuth);
            httpget.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
            HttpClient client = HttpClientBuilder.create().build();
            HttpResponse response = null;
            response = client.execute(httpget);


            int statusCode = response.getStatusLine().getStatusCode();
            System.out.println("Status:" + statusCode);
            HttpResponse responsenext = null;
            responsenext = client.execute(httpget);
            HttpEntity entitynext = responsenext.getEntity();
            String AddedResult = EntityUtils.toString(entitynext);
            //System.out.println(AddedResult);
            JSONObject VAL = new JSONObject(AddedResult);
                System.out.println(VAL.getJSONArray("value"));
                JSONArray jsonArray = VAL.getJSONArray("value");
                JSONObject menuObject = jsonArray.getJSONObject(0);

                String caloriesConsumed = menuObject.getString("SD_CustomerHealthStatus_CaloriesConsumed");
            String HeartRate = menuObject.getString("SD_CustomerHealthStatus_HeartRate");
            String SleepingFactor = menuObject.getString("SD_CustomerHealthStatus_SleepingFactor");


            mDatabase.child("Customer").child(params[0]).child("Calories").setValue(caloriesConsumed);
            mDatabase.child("Customer").child(params[0]).child("HeartRate").setValue(HeartRate);
            mDatabase.child("Customer").child(params[0]).child("SleepingFactor").setValue(SleepingFactor);
            mResponseList.put("Calories",caloriesConsumed);
            mResponseList.put("HeartRate",HeartRate);
            mResponseList.put("SleepingFactor",SleepingFactor);


        }catch (Exception e) {
            e.printStackTrace();
        }
            return mResponseList;

        }


    protected void onPostExecute(Map<String,String> e) {


    }
}
