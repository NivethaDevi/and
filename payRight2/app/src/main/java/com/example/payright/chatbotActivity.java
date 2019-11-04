package com.example.payright;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

import ai.api.AIServiceContext;
import ai.api.AIServiceContextBuilder;
import ai.api.android.AIConfiguration;
import ai.api.android.AIDataService;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

public class chatbotActivity extends AppCompatActivity {
    private DrawerLayout dl;
    private ActionBarDrawerToggle abdt;
    private NavigationView nv;
    private DatabaseReference mDatabase;
     EditText editText;
     public String bot_message = "";

    UUID uuid = UUID.randomUUID();
    AIDataService aiDataService;
    AIServiceContext customAIServiceContext;
    AIRequest aiRequest;
    final LinkedList<String> bot_messages = new LinkedList<>();
    final LinkedList<String> user_messages = new LinkedList<>();

    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Bundle bundle1 = getIntent().getExtras();
        final String userid = bundle1.getString("userid").toString();
        final RecyclerView recyclerView;
        final EditText editText;
        RelativeLayout addBtn;
        final FirebaseRecyclerAdapter<ChatMessage,chat_rec> adapter;
        Boolean flagFab = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatbot);

        Toolbar myToolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        //myToolbar.inflateMenu(R.menu.tool_menu1);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        editText = (EditText)findViewById(R.id.editText);
        addBtn = (RelativeLayout)findViewById(R.id.addBtn);

        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new    LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        //ref.keepSynced(true);




        System.out.println(userid);
        adapter = new FirebaseRecyclerAdapter<ChatMessage, chat_rec>(ChatMessage.class,R.layout.msg,chat_rec.class,ref.child("chat")) {
            @Override
            protected void populateViewHolder(chat_rec viewHolder, ChatMessage model, int position) {

                if (model.getMsgUser().equals("user")) {


                    viewHolder.rightText.setText(model.getMsgText());

                    viewHolder.rightText.setVisibility(View.VISIBLE);
                    viewHolder.leftText.setVisibility(View.GONE);
                }
                else {
                    viewHolder.leftText.setText(model.getMsgText());

                    viewHolder.rightText.setVisibility(View.GONE);
                    viewHolder.leftText.setVisibility(View.VISIBLE);
                }
            }
        };

        initChatBot();
        sendMessage("hi");
        recyclerView.setAdapter(adapter);
        System.out.println("bot1:"+bot_message);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("bot2:"+bot_message);
                String message = editText.getText().toString().trim();
                System.out.println("user2:"+message);
                System.out.println(message);
                if (!message.equals("")) {

                    ChatMessage chatMessage = new ChatMessage(message, "user");
                    ref.child("chat").push().setValue(chatMessage);
                    String bot_msg = bot_message.toLowerCase().replaceAll("\\s+","");
                    System.out.println(bot_msg);
                    bot_messages.add(bot_msg);
                    user_messages.add(editText.getText().toString());
                //   messages.put(bot_msg,editText.getText().toString());
//                    if(ques.containsKey(bot_msg)){
//                        ref.child("Customer").child(userid).child(ques.get(bot_msg)).setValue(editText.getText().toString());
//                    }
                    initChatBot();
                    sendMessage(editText.getText().toString());
                }

                editText.setText("");
                if (!chatbotActivity.this.isFinishing()){
                    recyclerView.setAdapter(adapter);
                }
            }
        });

    }


    private  void initChatBot(){
        final AIConfiguration config = new AIConfiguration("beca5cf920c9482db78cc5deaef68e5f",
        AIConfiguration.SupportedLanguages.English, AIConfiguration.RecognitionEngine.System);
        System.out.println("text");

        aiDataService = new AIDataService(this,config);
         customAIServiceContext  = AIServiceContextBuilder.buildFromSessionId(uuid.toString());
         aiRequest = new AIRequest();

    }

    private void sendMessage(String msg){
        editText = findViewById(R.id.editText);
        editText.setText("");
        aiRequest.setQuery(msg);
        ChatBot chatBot = new ChatBot(chatbotActivity.this,aiDataService,customAIServiceContext);
        System.out.println("text");
        System.out.println(aiRequest);
        chatBot.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,aiRequest);

    }


    public void callback(AIResponse aiResponse){
        Bundle bundle = getIntent().getExtras();
        final String userid = bundle.getString("userid").toString();
        System.out.println("userid:"+userid);
        System.out.println("ai:"+aiResponse);
        if(aiResponse!=null){
            String reply = aiResponse.getResult().getFulfillment().getSpeech();
            DatabaseReference ref;
            ref = FirebaseDatabase.getInstance().getReference();
           // ref.keepSynced(true);
            ChatMessage chatMessage = new ChatMessage(reply, "bot");
            System.out.println("reply");
            ref.child("chat").push().setValue(chatMessage);
            bot_message = reply;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tool_menu1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Bundle bundle = getIntent().getExtras();
        final String username = bundle.getString("username").toString();
        final String userid = bundle.getString("userid").toString();
        //noinspection SimplifiableIfStatement
        if (id == R.id.chatbot) {
            process(bot_messages,user_messages);
            //createAccounts(userid);
           // HealthPrediction he = new HealthPrediction(userid);
            //he.doPrediction();
            DatabaseReference mDatabase1;
            mDatabase1 = FirebaseDatabase.getInstance().getReference("");
            mDatabase1.child("chat").removeValue();
            Intent chatbot = new Intent(chatbotActivity.this, Dashboard.class);
            chatbot.putExtra("userid",userid);
            chatbot.putExtra("username",username);
            startActivity(chatbot);
            return true;
        }
        if(id == R.id.logout){
            process(bot_messages,user_messages);
            //createAccounts(userid);
//            HealthPrediction he = new HealthPrediction(userid);
  //          he.doPrediction();
            moveTaskToBack(true);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        process(bot_messages,user_messages);
        Bundle bundle = getIntent().getExtras();
        final String userid = bundle.getString("userid").toString();
        final String username = bundle.getString("username").toString();
        createAccounts(userid);
//        HealthPrediction he = new HealthPrediction(userid);
//        he.doPrediction();
      Intent chatbot = new Intent(chatbotActivity.this, Dashboard.class);
        chatbot.putExtra("userid",userid);
        chatbot.putExtra("username",username);
        startActivity(chatbot);

    }

    public void process(LinkedList<String> bot,LinkedList<String> user){
        final Map<String,String> ques = new HashMap<>();

        Bundle bundle = getIntent().getExtras();
        final String userid = bundle.getString("userid").toString();
        System.out.println("userid:"+userid);

        String commitment = "null";
        //ref.child("Customer").child(userid).child("commitment");
        for (int i=0;i<bot.size();i++){
            String key = bot.get(i);
            String trimKey = key.toLowerCase().replaceAll("\\s","");
            String value = user.get(i);
            if(trimKey.contains("valueofdream")){
                ref.child("Customer").child(userid).child("commitment").child(commitment).child("amount").setValue(value);
            }else if(trimKey.contains("margin")){
                ref.child("Customer").child(userid).child("commitment").child(commitment).child("margin").setValue(value);
            }else if(trimKey.contains("date")){
                ref.child("Customer").child(userid).child("commitment").child(commitment).child("date").setValue(value);
            }
            else if(trimKey.contains("whatisthecommitment")){
                commitment = value;
//                ref.child("Customer").child(userid).child("commitment").child(commitment).setValue("frequency");
//                ref.child("Customer").child(userid).child("commitment").child(commitment).setValue("date");
//                ref.child("Customer").child(userid).child("commitment").child(commitment).setValue("amount");
//                ref.child("Customer").child(userid).child("commitment").child(commitment).setValue("margin");
//
                if(commitment.contains("medical")){
                        HealthPrediction he = new HealthPrediction(userid);
                        he.doPrediction();
               }
                ref.child("Customer").child(userid).child("commitment").child(commitment).child("frequency").setValue("");
                ref.child("Customer").child(userid).child("commitment").child(commitment).child("date").setValue("");
                ref.child("Customer").child(userid).child("commitment").child(commitment).child("amount").setValue("");
                ref.child("Customer").child(userid).child("commitment").child(commitment).child("margin").setValue("");
            }
            else if(trimKey.contains("frequency")){
                ref.child("Customer").child(userid).child("commitment").child(commitment).child("frequency").setValue(value);
            }
            else if(trimKey.contains("amount")){
                ref.child("Customer").child(userid).child("commitment").child(commitment).child("amount").setValue(value);
            }
            else if(trimKey.contains("date")){
                ref.child("Customer").child(userid).child("commitment").child(commitment).child("date").setValue(value);
            }else{
                int in = 0;
                if(trimKey.contains("smoke")){
                    in=1;
                    ref.child("Customer").child(userid).child("smoke").setValue(value);
                }
                if(trimKey.contains("retire")){
                    in=1;

                    ref.child("Customer").child(userid).child("retirement").setValue(value);
                }
                if(trimKey.contains("drink")){
                    in=1;

                    ref.child("Customer").child(userid).child("alcohol").setValue(value);
                }
                if(trimKey.contains("diet")){
                    in=1;

                    ref.child("Customer").child(userid).child("diet").setValue(value);
                }
                if(trimKey.contains("stressed")){
                    in=1;

                    ref.child("Customer").child(userid).child("stress").setValue(value);
                }
                if(in==1){
                        HealthPrediction he = new HealthPrediction(userid);
                        he.doPrediction();
                }

            }
        }
    }
    public void createAccounts(final String userid) {
        mDatabase = FirebaseDatabase.getInstance().getReference("Customer");

        mDatabase.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                for (DataSnapshot commit : dataSnapshot.child("commitment").getChildren()) {
                    for (DataSnapshot commit1 : commit.getChildren()) {
                        if (!commit1.hasChild("account")) {
                            CreateUtilityAccounts createUtilityAccounts = new CreateUtilityAccounts();
                            createUtilityAccounts.createAccounts(userid, getApplicationContext());
                        }
                        break;
                    }
                    break;
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
