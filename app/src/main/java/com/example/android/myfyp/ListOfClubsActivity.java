package com.example.android.myfyp;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
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


        mSearchField = (EditText) findViewById(R.id.search_field);
        mSearchBtn = (ImageButton) findViewById(R.id.search_btn);

        recyclerView = (RecyclerView) findViewById(R.id.result_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String searchText = mSearchField.getText().toString();

                firebaseUserSearch(searchText);

            }
        });

    }

    private void firebaseUserSearch(String searchText) {

        Toast.makeText(ListOfClubsActivity.this, "Started Search", Toast.LENGTH_LONG).show();

        Query firebaseSearchQuery = mUserDatabase.orderByChild("name").startAt(searchText).endAt(searchText + "\uf8ff");
        firebaseSearchQuery.addChildEventListener(childEventListener);
        Log.d("***searched", "" + searchText);

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
