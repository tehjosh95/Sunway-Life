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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
    ArrayList<String> al5 = new ArrayList<>();
    ArrayList<String> al6 = new ArrayList<>();
    int totalUsers = 0;
    ProgressDialog pd;

    private DatabaseReference mDataRef, mDataRef2;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth firebaseAuth;
    private ListAdapter myAdapter;
    private FirebaseDatabase firebaseDatabase;
    private String Chatname;

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
        mDataRef2 = firebaseDatabase.getReference("Users");
        mDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                al.clear();
                al2.clear();
                al3.clear();
                al4.clear();
                al5.clear();
                al6.clear();
                String currentuser = firebaseAuth.getCurrentUser().getUid();
                int x = 0;
                for (DataSnapshot child: snapshot.getChildren()) {
                    String key = child.getKey();
                    int index = key.indexOf("_");
                    String key1 = decodeUserEmail(key.substring(0,index));
                    final String key2 = decodeUserEmail(key.substring(index + 1));
                    long timing = child.child("lasttime").getValue(Long.class);
                    Log.d("*****key2", "" + key2);
                    String theirname = child.child("others").getValue(String.class);
                    if(key1.equals(currentuser)){
                        Log.d("***orderby","" + child.getKey());
                        Log.d("***timing","" + timing);
                        al.add(timing);
                        al2.add(timing);
                        al3.add(key2);
                        al5.add(theirname);
                        mDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Collections.sort(al);
                                Collections.reverse(al);
                                Log.d("***sizeal", "" + al6.size());
                                al6.clear();
                                al4.clear();
                                for (int loop1 = 0; loop1 < al.size(); loop1++) {
                                    for (int loop2 = 0; loop2 < al2.size(); loop2++) {
                                        if (al.get(loop1).equals(al2.get(loop2))) {
                                            al4.add(loop1, al3.get(loop2));
                                            if (al5.size() == al2.size()) {
                                                DateFormat sfd = DateFormat.getDateTimeInstance();
                                                SimpleDateFormat sdf2 = new SimpleDateFormat("EEE \t\t\t" + "HH:mm");
                                                String simple = sdf2.format(al2.get(loop2));
                                                Date netDate = (new Date(al2.get(loop2)));
//                                              al6.add(loop1, al5.get(loop2) + "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + "(" + simple + ")");
                                                al6.add(loop1, al5.get(loop2));
                                            }
                                        }
                                    }
                                }
                                if (al6.size() < 1) {
                                   Log.d("***totalusers1", "" + totalUsers);
                                    noUsersText.setVisibility(View.VISIBLE);
                                    usersList.setVisibility(View.GONE);
                                    } else {
                                    noUsersText.setVisibility(View.GONE);
                                    usersList.setVisibility(View.VISIBLE);
                                    Log.d("*****list8", "" + al6.size());
                                    myAdapter = new ArrayAdapter<String>(Inbox.this, android.R.layout.simple_list_item_1, al6);
                                    usersList.setAdapter(myAdapter);
                                    }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                }
                                });
                        Log.d("*****al5size", "" + al5.size());
//                        al5.add(Chatname);
                        x += 1;
                        totalUsers++;
                    }
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
                UserDetails.username = encodeUserEmail(user.getUid());
                UserDetails.chatWith = encodeUserEmail(al4.get(position));
                UserDetails.name = al6.get(position);
                Log.d("****chatwith1","****chatwith" + UserDetails.chatWith);
                Log.d("****username2","****username" + UserDetails.username);
                finish();
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
                        UserDetails.username = encodeUserEmail(user.getUid());
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
