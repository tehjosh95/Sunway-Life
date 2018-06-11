package com.example.android.myfyp;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.renderscript.Sampler;
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

public class list_of_event_club extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<event_list> AllClubsList;
    ArrayList<ListOfClubs> AllClubsList2;
    ArrayList<clubModel> clubModelList;
    ArrayList<String> keys;
    ArrayList<String> Parentkeys;
    private EditText mSearchField;
    private ImageButton mSearchBtn;
    private list_of_event_club_adapter adapter;
    private DatabaseReference mUserDatabase, mUserDatabase2;
    private FirebaseAuth firebaseAuth;
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

        mSearchField = (EditText) findViewById(R.id.search_field);
        mSearchBtn = (ImageButton) findViewById(R.id.search_btn);
        mSearchBtn.setVisibility(View.GONE);
        recyclerView = (RecyclerView) findViewById(R.id.result_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUserDatabase2.addValueEventListener(valueEventListener);
        mSearchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mUserDatabase2.removeEventListener(valueEventListener);
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
                    mUserDatabase2.addListenerForSingleValueEvent(valueEventListener);
                }
            }
        });
    }

    private void firebaseUserSearch(String searchText) {
//        Toast.makeText(ListOfClubsActivity.this, "Started Search", Toast.LENGTH_LONG).show();
        AllClubsList.clear();
        Query firebaseSearchQuery = mUserDatabase2.orderByChild("item_name").startAt(searchText).endAt(searchText + "\uf8ff");
        firebaseSearchQuery.addListenerForSingleValueEvent(valueEventListener);
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            count = 0;
            keys.clear();
            Parentkeys.clear();
            clubModelList.clear();
            for (DataSnapshot postsnapshot : dataSnapshot.getChildren()) {
                final clubModel eventList = postsnapshot.getValue(clubModel.class);
                Log.d("***getEventOwner", eventList.getItem_owner());
                if (eventList.getItem_owner().equals(firebaseAuth.getCurrentUser().getUid())) {
                    clubModelList.add(eventList);
//                    keys.add(nextsnap.getKey());
                    Parentkeys.add(postsnapshot.getKey());
                    Log.d("^^^^^^^Call1", "" + "call1");
                    Log.d("***parentkey", postsnapshot.getKey());
                }
            }

            adapter = new list_of_event_club_adapter(list_of_event_club.this, clubModelList);
            Log.d("****clubsize", "" + clubModelList.size());
            recyclerView.setAdapter(adapter);

//            mUserDatabase2.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    clubModelList.clear();
//                    for (int x = 0; x < Parentkeys.size(); x++) {
//                        if (dataSnapshot.hasChild(Parentkeys.get(x))) {
//                            clubModel Clubmodel = dataSnapshot.child(Parentkeys.get(x)).getValue(clubModel.class);
//                            Log.d("****yeap", "yeap");
//                            clubModelList.add(Clubmodel);
//                        }
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//
            recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(list_of_event_club.this, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View childView, int position) {
                    Intent intent = new Intent(list_of_event_club.this, tabs2.class);
                    intent.putExtra("mykey", Parentkeys.get(position));
                    startActivity(intent);
                }

                @Override
                public void onItemLongPress(View childView, int position) {

                }
            }));
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
}