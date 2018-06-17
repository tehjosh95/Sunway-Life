package com.example.android.myfyp;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.renderscript.Sampler;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

public class List_of_joined extends AppCompatActivity {
    public interface MyCallback {
        void onCallback(ArrayList<join_list> value);
    }

    RecyclerView recyclerView;
    ArrayList<join_list> AllClubsList;
    ArrayList<ListOfClubs> AllClubsList2;
    ArrayList<String> keys;
    ArrayList<String> Parentkeys;
    private EditText mSearchField;
    private ImageButton mSearchBtn;
    private TextView textReminder;
    private ListOfJoinedAdapter adapter;
    private DatabaseReference mUserDatabase, mUserDatabase2;
    private FirebaseAuth firebaseAuth;
    private int count;
    int x;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_event_club_successful);
        firebaseAuth = FirebaseAuth.getInstance();
        AllClubsList = new ArrayList<>();
        AllClubsList2 = new ArrayList<>();
        keys = new ArrayList<>();
        Parentkeys = new ArrayList<>();
        mUserDatabase = FirebaseDatabase.getInstance().getReference("join_list").child("members");
        mUserDatabase2 = FirebaseDatabase.getInstance().getReference("Clubs");

        toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle("Joined clubs");
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
        final String userID = firebaseAuth.getCurrentUser().getUid();
        Query firebaseSearchQuery = mUserDatabase.orderByChild(userID + "/clubname").startAt(searchText).endAt(searchText + "\uf8ff");
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
                        final join_list joinList = nextsnap.getValue(join_list.class);
                        if (nextsnap.getKey().equals(firebaseAuth.getCurrentUser().getUid())) {
                            AllClubsList.add(joinList);
                            keys.add(nextsnap.getKey());
                            Parentkeys.add(postsnapshot.getKey());
                            Log.d("^^^^^^^Call1", "" + "call1");
                        }
                    }
                }
            }

            adapter = new ListOfJoinedAdapter(List_of_joined.this, AllClubsList);
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
                    for (int x = 0; x < Parentkeys.size(); x++) {
                        if (dataSnapshot.hasChild(Parentkeys.get(x))) {
                            ListOfClubs listOfClubs = dataSnapshot.child(Parentkeys.get(x)).getValue(ListOfClubs.class);
                            AllClubsList2.add(listOfClubs);
                            Log.d("^^^^^^^listclubs2size", "" + AllClubsList2.size());
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(List_of_joined.this, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View childView, int position) {
                    ListOfClubs listOfClubs = AllClubsList2.get(position);
                    Log.d("^^^^^^^listclubs", "" + listOfClubs);
                    Intent intent = new Intent(List_of_joined.this, ListOfClubsView.class);
                    intent.putExtra("isname", listOfClubs.getName());
                    intent.putExtra("isadvisor", listOfClubs.getAdvisor());
                    intent.putExtra("isemail", listOfClubs.getEmail());
                    intent.putExtra("isdesc", listOfClubs.getDesc());
                    intent.putExtra("isimg", listOfClubs.getImage());
                    intent.putExtra("isuid", listOfClubs.getMyUid());
                    intent.putExtra("fromchat", 0);
                    Log.d("^^^^^^^", "" + listOfClubs.getMyUid());
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