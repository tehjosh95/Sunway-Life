package com.example.android.myfyp;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ClubProfileActivity extends AppCompatActivity {

    Toolbar toolbar;
    private ImageView profilePic;
    private Button profileUpdate, changePassword;
    private EditText edit_user_type, edit_user_name, edit_user_advisor, edit_user_email;
    private TextView edit_user_desc;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDataRef;
    private FloatingActionButton fab = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_profile);

        profilePic = findViewById(R.id.ivProfilePic);
        edit_user_type = findViewById(R.id.edit_user_type);
        edit_user_name = findViewById(R.id.edit_user_name);
        edit_user_advisor = findViewById(R.id.edit_user_advisor);
        edit_user_email = findViewById(R.id.edit_user_email);
        edit_user_desc = findViewById(R.id.edit_user_desc);
        profileUpdate = findViewById(R.id.btnProfileUpdate);
        changePassword = findViewById(R.id.btnChangePassword);
        fab = (FloatingActionButton) findViewById(R.id.fabb);

        toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        toolbar.setTitle("Profile");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        mDataRef = firebaseDatabase.getReference();
        mDataRef = mDataRef.child("Clubs").child(firebaseAuth.getCurrentUser().getUid());

        mDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ListOfClubs listOfClubs = dataSnapshot.getValue(ListOfClubs.class);
                edit_user_type.setText(listOfClubs.getUserType());
                edit_user_name.setText(listOfClubs.getName());
                edit_user_advisor.setText(listOfClubs.getAdvisor());
                edit_user_email.setText(listOfClubs.getEmail());
                edit_user_desc.setText(listOfClubs.getDesc());
                Glide.with(ClubProfileActivity.this).load(listOfClubs.getImage()).thumbnail(0.1f).into(profilePic);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ClubProfileActivity.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        profileUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ClubProfileActivity.this, UpdateClubProfile.class));
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ClubProfileActivity.this, UpdatePassword.class));
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(ClubProfileActivity.this, SecondActivity.class));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
