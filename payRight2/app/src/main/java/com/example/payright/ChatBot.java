package com.example.payright;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.SurfaceHolder.Callback;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import ai.api.AIServiceContext;
import ai.api.android.AIDataService;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;


public class ChatBot extends AsyncTask<AIRequest, Void, AIResponse> {
    Activity activity;
    AIDataService aiDataService;
    AIServiceContext aiServiceContext;
    ChatBot(Activity activity,AIDataService aiDataService,AIServiceContext aiServiceContext){
        this.activity = activity;
        this.aiDataService = aiDataService;
        this.aiServiceContext = aiServiceContext;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }
    @Override
    protected AIResponse doInBackground(AIRequest... aiRequests) {
        final AIRequest request=aiRequests[0];
        System.out.println("inside chatbot1");
        try{
            System.out.println("inside chatbot");
            return aiDataService.request(request,aiServiceContext);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    protected void onPostExecute(AIResponse aiResponse) {
        System.out.println("inside postexecute");

        ((chatbotActivity)activity).callback(aiResponse);
    }
}
