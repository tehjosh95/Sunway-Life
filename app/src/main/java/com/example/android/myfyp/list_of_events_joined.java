package com.example.android.myfyp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.renderscript.Sampler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.client.FirebaseError;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class list_of_events_joined extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<event_list> AllClubsList;
    ArrayList<ListOfClubs> AllClubsList2;
    ArrayList<clubModel> clubModelList;
    ArrayList<String> keys;
    ArrayList<String> Parentkeys;
    private EditText mSearchField;
    private ImageButton mSearchBtn;
    private list_of_events_adapter adapter;
    private DatabaseReference mUserDatabase, mUserDatabase2;
    private FirebaseAuth firebaseAuth;
    private TextView textReminder;
    private int count;
    int x;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_events_joined);
        firebaseAuth = FirebaseAuth.getInstance();
        AllClubsList = new ArrayList<>();
        AllClubsList2 = new ArrayList<>();
        clubModelList = new ArrayList<>();
        keys = new ArrayList<>();
        Parentkeys = new ArrayList<>();
        mUserDatabase = FirebaseDatabase.getInstance().getReference("join_event");
        mUserDatabase2 = FirebaseDatabase.getInstance().getReference("Item Information");

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
        final String userID = firebaseAuth.getCurrentUser().getUid();
        Query firebaseSearchQuery = mUserDatabase.orderByChild(userID + "/eventName").startAt(searchText).endAt(searchText + "\uf8ff");
        firebaseSearchQuery.addListenerForSingleValueEvent(valueEventListener);
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            count = 0;
            keys.clear();
            Parentkeys.clear();
            AllClubsList.clear();
            for (DataSnapshot postsnapshot : dataSnapshot.getChildren()) {
                if (postsnapshot.hasChild(firebaseAuth.getCurrentUser().getUid())) {
                    for (DataSnapshot nextsnap : postsnapshot.getChildren()) {
                        final event_list eventList = nextsnap.getValue(event_list.class);
                        if (nextsnap.getKey().equals(firebaseAuth.getCurrentUser().getUid())) {
                            AllClubsList.add(eventList);
                            keys.add(nextsnap.getKey());
                            Parentkeys.add(postsnapshot.getKey());
                            Log.d("^^^^^^^Call1", "" + "call1");
                            Log.d("***parentkey", postsnapshot.getKey());
                        }
                    }
                }
            }

            adapter = new list_of_events_adapter(list_of_events_joined.this, AllClubsList);
            Log.d("****clubsize", "" + AllClubsList.size());
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
                    clubModelList.clear();
                    for (int x = 0; x < Parentkeys.size(); x++) {
                        if (dataSnapshot.hasChild(Parentkeys.get(x))) {
                            clubModel Clubmodel = dataSnapshot.child(Parentkeys.get(x)).getValue(clubModel.class);
                            Log.d("****yeap", "yeap");
                            clubModelList.add(Clubmodel);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(list_of_events_joined.this, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View childView, int position) {
                    clubModel ClubModel1 = clubModelList.get(position);
                    Intent intent = new Intent(list_of_events_joined.this, ViewActivity.class);
                    intent.putExtra("myname", ClubModel1.getItem_name());
                    intent.putExtra("myplace", ClubModel1.getItem_place());
                    intent.putExtra("myprice", ClubModel1.getItem_price());
                    intent.putExtra("myurl", ClubModel1.getImageLink());
                    intent.putExtra("mykey", mUserDatabase2.getKey());
                    intent.putExtra("myowner", ClubModel1.getItem_owner());
                    intent.putExtra("myownername", ClubModel1.getOwnerName());
                    intent.putExtra("myparentkey", ClubModel1.getParentkey());
                    startActivity(intent);
                }

                @Override
                public void onItemLongPress(View childView, final int position) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(list_of_events_joined.this);
                    alertDialog.setTitle("Undo?");

                    alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            mUserDatabase.child(Parentkeys.get(position)).child(keys.get(position)).removeValue();
                            mSearchBtn.performClick();
                            Log.d("***allclublist", "" + AllClubsList.size());
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