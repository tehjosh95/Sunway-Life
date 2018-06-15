package com.example.android.myfyp;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
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
import android.support.v7.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.firebase.client.FirebaseError;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class ListOfClubsActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<ListOfClubs> AllClubsList;

    Toolbar toolbar;
    private EditText mSearchField;
    private ImageButton mSearchBtn;
    private ClubListAdapter adapter;

    private RecyclerView mResultList;

    private DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_clubs);
        AllClubsList = new ArrayList<>();
        mUserDatabase = FirebaseDatabase.getInstance().getReference("Clubs");

        toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        toolbar.setTitle("List of Clubs");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSearchField = (EditText) findViewById(R.id.search_field);
        mSearchBtn = (ImageButton) findViewById(R.id.search_btn);
        mSearchBtn.setVisibility(View.GONE);
        recyclerView = (RecyclerView) findViewById(R.id.result_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUserDatabase.addChildEventListener(childEventListener);
        mSearchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("***beforeTextChanged", "beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mUserDatabase.removeEventListener(childEventListener);
                mSearchBtn.performClick();
                Log.d("***onTextChanged", "onTextChanged");
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d("***afterTextChanged", "afterTextChanged");
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
                    mUserDatabase.addChildEventListener(childEventListener);
                }
            }
        });
    }

    private void firebaseUserSearch(String searchText) {

//        Toast.makeText(ListOfClubsActivity.this, "Started Search", Toast.LENGTH_LONG).show();
        AllClubsList.clear();
        Query firebaseSearchQuery = mUserDatabase.orderByChild("name").startAt(searchText).endAt(searchText + "\uf8ff");
        firebaseSearchQuery.addChildEventListener(childEventListener);
    }

    ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot child, String previousChild) {
            ListOfClubs ItemModel = child.getValue(ListOfClubs.class);
            AllClubsList.add(ItemModel);
            Log.d("***", "one");
            adapter = new ClubListAdapter(ListOfClubsActivity.this, AllClubsList);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(ListOfClubsActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View childView, int position) {
                    ListOfClubs listOfClubs = AllClubsList.get(position);
                    Intent intent = new Intent(ListOfClubsActivity.this, ListOfClubsView.class);
                    intent.putExtra("isname", listOfClubs.getName());
                    intent.putExtra("isadvisor", listOfClubs.getAdvisor());
                    intent.putExtra("isemail", listOfClubs.getEmail());
                    intent.putExtra("isdesc", listOfClubs.getDesc());
                    intent.putExtra("isimg", listOfClubs.getImage());
                    intent.putExtra("isuid", listOfClubs.getMyUid());
                    Log.d("^^^^^^^", "" + listOfClubs.getMyUid());
                    startActivity(intent);
                }

                @Override
                public void onItemLongPress(View childView, int position) {

                }
            }));
        }

        public void onChildRemoved(DataSnapshot snapshot) {
        }

        public void onChildChanged(DataSnapshot snapshot, String previousChild) {
        }

        public void onChildMoved(DataSnapshot snapshot, String previousChild) {
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            System.out.println("The read failed: " + databaseError.getMessage());
        }
    };
}
