package com.example.android.myfyp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class Chat extends AppCompatActivity {
    LinearLayout layout;
    RelativeLayout layout_2;
    ImageView sendButton;
    EditText messageArea;
    TextView chatname;
    ScrollView scrollView;
    Firebase reference1, reference2;
    private DatabaseReference mDataRef;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        layout = (LinearLayout) findViewById(R.id.layout1);
        layout_2 = (RelativeLayout)findViewById(R.id.layout2);
        chatname = (TextView)findViewById(R.id.chatname);
        sendButton = (ImageView)findViewById(R.id.sendButton);
        messageArea = (EditText)findViewById(R.id.messageArea);
        scrollView = (ScrollView)findViewById(R.id.scrollView);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        mDataRef = firebaseDatabase.getReference().child("messages");
        chatname.setText(UserDetails.name);
        Intent startingIntent = getIntent();
        String recipient = startingIntent.getStringExtra("recipient");
        String sender = startingIntent.getStringExtra("sender");

        if ((UserDetails.username.equals("")) && (UserDetails.chatWith.equals("")) ){
            UserDetails.username = sender;
            UserDetails.chatWith = recipient;
        }

        Firebase.setAndroidContext(this);
        reference1 = new Firebase("https://myfyp-25f5d.firebaseio.com/messages/" + UserDetails.username + "_" + UserDetails.chatWith + "/chat");
        reference2 = new Firebase("https://myfyp-25f5d.firebaseio.com/messages/" + UserDetails.chatWith + "_" + UserDetails.username + "/chat");
        Log.d("****userD, username5","" + UserDetails.username);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();
                if(!messageText.equals("")){

                    ChatModel chatModel = new ChatModel();
                    chatModel.setUser(UserDetails.username);
                    chatModel.setMessage(messageText);
                    chatModel.setTimestamp(ServerValue.TIMESTAMP);

                    reference1.push().setValue(chatModel);
                    reference2.push().setValue(chatModel);

//                    Map<String, String> map = new HashMap<>();
//                    map.put("message", messageText);
//                    map.put("user", UserDetails.username);
//                    String one = reference1.push().getKey();
//                    String two = reference2.push().getKey();
//                    reference1.child(one).setValue(map);
//                    reference2.child(two).setValue(map);
                    Map lastTime = new HashMap();
                    lastTime.put("lasttime", ServerValue.TIMESTAMP);
                    mDataRef.child(UserDetails.chatWith + "_" + UserDetails.username).updateChildren(lastTime);
                    mDataRef.child(UserDetails.username + "_" + UserDetails.chatWith).updateChildren(lastTime);

                    Map one = new HashMap<>();
                    one.put("others", UserDetails.name);
                    mDataRef.child(UserDetails.username + "_" + UserDetails.chatWith).updateChildren(one);

                    Map two = new HashMap<>();
                    two.put("others", firebaseAuth.getCurrentUser().getDisplayName());
                    mDataRef.child(UserDetails.chatWith + "_" + UserDetails.username).updateChildren(two);

                    messageArea.setText("");
                    sendNotification();
                }
            }
        });

        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatModel chatModel = dataSnapshot.getValue(ChatModel.class);
                String message = chatModel.getMessage();
                String userName = chatModel.getUser();
                Long time = (Long)chatModel.getTimestamp() ;

                DateFormat sfd = DateFormat.getDateTimeInstance();
                Date netDate = (new Date(time));
                //                sfd.format(new Date(time));
                Log.d("***time", "" + sfd.format(netDate));
                if(userName.equals(UserDetails.username)){
                    addMessageBox("" + sfd.format(netDate) +":-\n" +  message, 1);
//                    addMessageBox("Me:-\n" + message, 1);
                }
                else{
                    addMessageBox("" + sfd.format(netDate) +":-\n" + message, 2);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void addMessageBox(String message, int type){
        TextView textView = new TextView(Chat.this);
        textView.setText(message);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;

        if(type == 1) {
            lp2.gravity = Gravity.RIGHT;
            textView.setBackgroundResource(R.drawable.bubble_in);
        }
        else{
            lp2.gravity = Gravity.LEFT;
            textView.setBackgroundResource(R.drawable.bubble_out);
        }
        textView.setLayoutParams(lp2);
        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    private void sendNotification()
    {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    Intent startingIntent = getIntent();
                    String recipient = startingIntent.getStringExtra("recipient");
                    String sender = startingIntent.getStringExtra("sender");

                    if ((UserDetails.username.equals("")) && (UserDetails.chatWith.equals("")) ){
                        UserDetails.username = sender;
                        UserDetails.chatWith = recipient;
                    }
                    String send_email;

                    //This is a Simple Logic to Send Notification different Device Programmatically....
                        send_email = UserDetails.chatWith;
                        Log.d("******************", "" + send_email);
                    try {

                        String jsonResponse;

                        URL url = new URL("https://onesignal.com/api/v1/notifications");
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setUseCaches(false);
                        con.setDoOutput(true);
                        con.setDoInput(true);

                        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        con.setRequestProperty("Authorization", "Basic ZTEwMWYyMzgtZDQzMy00YTdhLTgwMDQtMWEwMThlYThkMDAy");
                        con.setRequestMethod("POST");

                        String strJsonBody = "{"
                                + "\"app_id\": \"b3a81949-ef0d-4352-9e80-65c16ca77e9e\","

                                + "\"filters\": [{\"field\": \"tag\", \"key\": \"User_ID\", \"relation\": \"=\", \"value\": \"" + send_email + "\"}],"

                                + "\"data\": {\"UserDetails.username\": \"UserDetails.chatWith\", \"UserDetails.chatWith\": \"UserDetails.username\"},"
                                + "\"contents\": {\"en\": \"You have a new message from " + UserDetails.username + "\"}"
                                + "}";


                        System.out.println("strJsonBody:\n" + strJsonBody);

                        byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                        con.setFixedLengthStreamingMode(sendBytes.length);

                        OutputStream outputStream = con.getOutputStream();
                        outputStream.write(sendBytes);

                        int httpResponse = con.getResponseCode();
                        System.out.println("httpResponse: " + httpResponse);

                        if (httpResponse >= HttpURLConnection.HTTP_OK
                                && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                            Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        } else {
                            Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        }
                        System.out.println("jsonResponse:\n" + jsonResponse);

                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Log.d("backpressed", "onBackPressed Called");
        finish();
        startActivity(new Intent(Chat.this, Inbox.class));
    }
}
