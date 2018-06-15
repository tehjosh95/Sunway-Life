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

public class List_of_successful extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<join_list> AllClubsList;
    ArrayList<String> keys;
    ArrayList<String> profilekey;
    ArrayList<UserProfile> AllUsers;
    private EditText mSearchField;
    private ImageButton mSearchBtn;
    private TextView textReminder;
    private PendingListAdapter adapter;
    private DatabaseReference mUserDatabase, mUserDatabase2;
    private FirebaseAuth firebaseAuth;
    private int count;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_pending);
        firebaseAuth = FirebaseAuth.getInstance();
        AllClubsList = new ArrayList<>();
        keys = new ArrayList<>();
        profilekey = new ArrayList<>();
        AllUsers = new ArrayList<>();
        mUserDatabase = FirebaseDatabase.getInstance().getReference("join_list").child("members").child(firebaseAuth.getCurrentUser().getUid());
        mUserDatabase2 = FirebaseDatabase.getInstance().getReference("Users");

        toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        toolbar.setTitle("Successfully joined");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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
        Query firebaseSearchQuery = mUserDatabase.orderByChild("myname").startAt(searchText).endAt(searchText + "\uf8ff");
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
                final join_list joinList = postsnapshot.getValue(join_list.class);
                if (joinList.getStatus().equals("successful")) {
                    AllClubsList.add(joinList);
                    keys.add(postsnapshot.getKey().toString());
                }
            }
            adapter = new PendingListAdapter(List_of_successful.this, AllClubsList);
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

            recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(List_of_successful.this, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View childView, int position) {
                    UserProfile userProfile = AllUsers.get(position);
                    String key = profilekey.get(position);
                    Intent intent = new Intent(List_of_successful.this, ProfileActivity.class);
                    intent.putExtra("isid", userProfile.getStudentName());
                    intent.putExtra("isname", userProfile.getStudentName());
                    intent.putExtra("iscourse", userProfile.getStudentCourse());
                    intent.putExtra("isphone", userProfile.getStudentPhone());
                    intent.putExtra("istype", userProfile.getUserType());
                    intent.putExtra("isid", key);
                    startActivity(intent);
                }

                @Override
                public void onItemLongPress(View childView, final int position) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(List_of_successful.this);
                    alertDialog.setTitle("Undo?");

                    alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            mUserDatabase.child(keys.get(position)).child("status").setValue("pending");
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