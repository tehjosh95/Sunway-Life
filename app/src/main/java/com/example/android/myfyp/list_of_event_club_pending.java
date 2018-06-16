package com.example.android.myfyp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class list_of_event_club_pending extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<event_list> AllClubsList;
    ArrayList<String> keys;
    ArrayList<String> profilekey;
    ArrayList<UserProfile> AllUsers;
    private EditText mSearchField;
    private ImageButton mSearchBtn;
    private list_of_event_clubs_adapter2 adapter;
    private DatabaseReference mUserDatabase, mUserDatabase2;
    private FirebaseAuth firebaseAuth;
    private TextView textReminder;
    private int count;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_successful);
        firebaseAuth = FirebaseAuth.getInstance();
        AllClubsList = new ArrayList<>();
        keys = new ArrayList<>();
        profilekey = new ArrayList<>();
        AllUsers = new ArrayList<>();

//        Intent startingIntent = getIntent();
//        final String key = startingIntent.getStringExtra("mykey");

        String key = getParent().getIntent().getStringExtra("mykey");

        mUserDatabase = FirebaseDatabase.getInstance().getReference("join_event").child(key);
        mUserDatabase2 = FirebaseDatabase.getInstance().getReference("Users");

//        toolbar = (Toolbar) findViewById(R.id.toolbarMain);
//        toolbar.setTitle("Pending join");
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

        textReminder = findViewById(R.id.textReminder);
        mSearchField = (EditText) findViewById(R.id.search_field);
        mSearchBtn = (ImageButton) findViewById(R.id.search_btn);
        mSearchBtn.setVisibility(View.GONE);
        recyclerView = (RecyclerView) findViewById(R.id.result_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUserDatabase.addValueEventListener(valueEventListener);
        mSearchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mSearchBtn.performClick();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchText = mSearchField.getText().toString();

                if (searchText.length() > 0) {
                    firebaseUserSearch(searchText);
                } else {
                    AllClubsList.clear();
                    mUserDatabase.addListenerForSingleValueEvent(valueEventListener);
                }
            }
        });
    }

    private void firebaseUserSearch(String searchText) {
//        Toast.makeText(ListOfClubsActivity.this, "Started Search", Toast.LENGTH_LONG).show();
        AllClubsList.clear();
        Query firebaseSearchQuery = mUserDatabase.orderByChild("studentName").startAt(searchText).endAt(searchText + "\uf8ff");
        firebaseSearchQuery.addListenerForSingleValueEvent(valueEventListener);
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Log.d("******","listenerrunning");
            count = 0;
            keys.clear();
            AllClubsList.clear();
            for (DataSnapshot postsnapshot : dataSnapshot.getChildren()) {
                final event_list eventList = postsnapshot.getValue(event_list.class);
                if (eventList.getStatus().equals("pending")) {
                    AllClubsList.add(eventList);
                    keys.add(postsnapshot.getKey().toString());
                }
            }
            adapter = new list_of_event_clubs_adapter2(list_of_event_club_pending.this, AllClubsList);
            recyclerView.setAdapter(adapter);

            if(AllClubsList.size()>0){
                recyclerView.setVisibility(View.VISIBLE);
                textReminder.setVisibility(View.GONE);
            }else{
                textReminder.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }

            mUserDatabase2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    AllUsers.clear();
                    profilekey.clear();
                    for (int x = 0; x < keys.size(); x++) {
                        if (dataSnapshot.hasChild(keys.get(x))) {
                            UserProfile listOfClubs = dataSnapshot.child(keys.get(x)).getValue(UserProfile.class);
                            AllUsers.add(listOfClubs);
                            profilekey.add(keys.get(x));
                            Log.d("^^^^^^^listclubs2size", "" + AllUsers.size());
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(list_of_event_club_pending.this, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View childView, int position) {
                    UserProfile userProfile = AllUsers.get(position);
                    String key = profilekey.get(position);
                    Intent intent = new Intent(list_of_event_club_pending.this, ProfileActivity.class);
                    intent.putExtra("isstudentid", userProfile.getStudentID());
                    intent.putExtra("isname", userProfile.getStudentName());
                    intent.putExtra("iscourse", userProfile.getStudentCourse());
                    intent.putExtra("isphone", userProfile.getStudentPhone());
                    intent.putExtra("istype", userProfile.getUserType());
                    intent.putExtra("isid", key);
                    startActivity(intent);
                }

                @Override
                public void onItemLongPress(View childView, final int position) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(list_of_event_club_pending.this);
                    alertDialog.setTitle("Approve?");

                    alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            event_list eventList = AllClubsList.get(position);
                            mUserDatabase.child(keys.get(position)).child("status").setValue("successful");
                            mSearchBtn.performClick();
                        }
                    });
                    alertDialog.show();
                }
            }));
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
}