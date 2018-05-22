package com.example.android.myfyp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Inbox extends AppCompatActivity {
    Toolbar toolbar;

    ListView usersList;
    TextView noUsersText;
    ArrayList<Long> al = new ArrayList<>();
    ArrayList<Long> al2 = new ArrayList<>();
    ArrayList<String> al3 = new ArrayList<>();
    ArrayList<String> al4 = new ArrayList<>();
    int totalUsers = 0;
    ProgressDialog pd;

    private DatabaseReference mDataRef;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth firebaseAuth;
    private ListAdapter myAdapter;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        mDatabase = FirebaseDatabase.getInstance();
        mDataRef = mDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        toolbar = (Toolbar)findViewById(R.id.toolbarMain);
        toolbar.setTitle("Long Press to Delete");
        usersList = (ListView)findViewById(R.id.usersList);
        noUsersText = (TextView)findViewById(R.id.noUsersText);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        mDataRef = firebaseDatabase.getReference("messages");
        mDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                al.clear();
                String currentuser = firebaseAuth.getCurrentUser().getEmail().toString();
                int x = 0;
                for (DataSnapshot child: snapshot.getChildren()) {
                    String email = child.getKey();
                    int index = email.indexOf("_");
                    String key1 = decodeUserEmail(email.substring(0,index));
                    String key2 = decodeUserEmail(email.substring(index + 1));
                    String time1 = key1 + "_" + key2;
                    String time2 = key2 + "_" + key1;
                    long timing = child.child("lasttime").getValue(Long.class);

                    if(key1.equals(currentuser)){
                        Log.d("***orderby","" + child.getKey());
                        Log.d("***timing","" + timing);
                        al.add(timing);
                        al2.add(timing);
                        al3.add(key2);
                        x += 1;
                        totalUsers++;
                    }
                }
//                arrayName = new lala[contacts.size()];
//                arrayName = contacts.toArray(arrayName);
                Collections.sort(al);
                Collections.reverse(al);

                for(int loop1 = 0 ; loop1 < al.size();loop1++){
                    for(int loop2 = 0 ; loop2 < al2.size();loop2++){
                        if(al.get(loop1).equals(al2.get(loop2))){
                            al4.add(loop1, al3.get(loop2));
                            Log.d("***hello","" );
                        }
                    }
                }

                if(totalUsers == 0){
                    Log.d("***totalusers1","" + totalUsers);
                    noUsersText.setVisibility(View.VISIBLE);
                    usersList.setVisibility(View.GONE);
                }
                else{
                    noUsersText.setVisibility(View.GONE);
                    usersList.setVisibility(View.VISIBLE);

                    myAdapter = new ArrayAdapter<String>(Inbox.this,android.R.layout.simple_list_item_1,al4);
                    usersList.setAdapter(myAdapter);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        });

        usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                UserDetails.username = encodeUserEmail(user.getEmail());
                UserDetails.chatWith = encodeUserEmail(al4.get(position));
                Log.d("****chatwith1","****chatwith" + UserDetails.chatWith);
                Log.d("****username2","****username" + UserDetails.username);
                startActivity(new Intent(com.example.android.myfyp.Inbox.this, Chat.class));
            }
        });


        usersList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(com.example.android.myfyp.Inbox.this);
                // Setting Dialog Title
                alertDialog.setTitle("Delete?");
                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        UserDetails.username = encodeUserEmail(user.getEmail());
                        UserDetails.chatWith = encodeUserEmail(al4.get(position));
                        String delete = UserDetails.username + "_" + UserDetails.chatWith;
                        String delete2 = UserDetails.chatWith + "_" + UserDetails.username;
                        Log.d("****delete" , "" + delete);
                        mDataRef.child(delete).removeValue();
                        mDataRef.child(delete2).removeValue();
                    }
                });

                alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                        startActivity(new Intent(com.example.android.myfyp.Inbox.this, com.example.android.myfyp.Inbox.class));
                    }
                });

                alertDialog.show();
                return true;
            }
        });
    }

    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    static String decodeUserEmail(String userEmail) {
        return userEmail.replace(",", ".");
    }

    @Override
    public void onBackPressed() {
        Log.d("backpressed", "onBackPressed Called");
        finish();
        startActivity(new Intent(Inbox.this, SecondActivity.class));
    }

}
