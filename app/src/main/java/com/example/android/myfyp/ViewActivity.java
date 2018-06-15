package com.example.android.myfyp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

public class ViewActivity extends AppCompatActivity {
    Toolbar toolbar;
    private ImageView profilePic;
    private EditText item_name, item_description, item_date, item_start_time, item_end_time, item_fee_member, item_fee_nonmember, item_venue;
    private Button EditButton, btnchat, join;
    private FloatingActionButton fabbb;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDataRef, mDataRef2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        firebaseAuth = FirebaseAuth.getInstance();

        profilePic = findViewById(R.id.ivimage);
        toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        toolbar.setTitle("Event details");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        item_name = findViewById(R.id.item_name);
        item_description = findViewById(R.id.item_description);
        item_date = findViewById(R.id.item_date);
        item_start_time = findViewById(R.id.item_start_time);
        item_end_time = findViewById(R.id.item_end_time);
        item_fee_member = findViewById(R.id.item_fee_member);
        item_fee_nonmember = findViewById(R.id.item_fee_nonmember);
        item_venue = findViewById(R.id.item_venue);

        EditButton = findViewById(R.id.btnEdit);
        btnchat = findViewById(R.id.btnchat);
        join = findViewById(R.id.btnToJoin);
        fabbb = (FloatingActionButton) findViewById(R.id.fabbb);

        firebaseDatabase = FirebaseDatabase.getInstance();
        mDataRef = firebaseDatabase.getReference();
        mDataRef = firebaseDatabase.getReference().child("join_event");

        final String myid = firebaseAuth.getCurrentUser().getUid();
        mDataRef2 = firebaseDatabase.getReference().child("Users");

        final Intent startingIntent = getIntent();
        final String myurl = startingIntent.getStringExtra("myurl");
        final String Name = startingIntent.getStringExtra("myname");
        final String Desc = startingIntent.getStringExtra("mydesc");
        final String Date = startingIntent.getStringExtra("mydate");
        final String Starttime = startingIntent.getStringExtra("mystarttime");
        final String Endtime = startingIntent.getStringExtra("myendtime");
        final String Memberfee = startingIntent.getStringExtra("mymemberfee");
        final String Nonmemberfee = startingIntent.getStringExtra("mynonmemberfee");
        final String Venue = startingIntent.getStringExtra("myvenue");
        final String Owner = startingIntent.getStringExtra("myowner");
        final String myKey = startingIntent.getStringExtra("mykey");
        final String ownername = startingIntent.getStringExtra("myownername");
        final String parentkey = startingIntent.getStringExtra("myparentkey");

        Glide.with(this).load(myurl).thumbnail(0.1f).into(profilePic);
        item_name.setText(Name);
        item_description.setText(Desc);
        item_date.setText(Date);
        item_start_time.setText(Starttime);
        item_end_time.setText(Endtime);
        item_fee_member.setText(Memberfee);
        item_fee_nonmember.setText(Nonmemberfee);
        item_venue.setText(Venue);

        mDataRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(myid)) {
                    UserProfile userProfile = dataSnapshot.child(myid).getValue(UserProfile.class);
                    if (userProfile.getUserType().equals("student")) {
                        EditButton.setVisibility(View.GONE);
                    } else if(userProfile.getUserType().equals("admin") && Owner.equals(myid)){
                        btnchat.setVisibility(View.GONE);
                        join.setVisibility(View.GONE);
                    }else if(userProfile.getUserType().equals("admin") && !Owner.equals(myid)){
                        btnchat.setVisibility(View.GONE);
                        join.setVisibility(View.GONE);
                        EditButton.setVisibility(View.GONE);
                    }
                }else{
                    if(Owner.equals(myid)){
                        btnchat.setVisibility(View.GONE);
                        join.setVisibility(View.GONE);
                    }else {
                        btnchat.setVisibility(View.GONE);
                        join.setVisibility(View.GONE);
                        EditButton.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        fabbb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ViewActivity.this, SecondActivity.class));
            }
        });

        btnchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String ownername = startingIntent.getStringExtra("myownername");
                final String Owner = startingIntent.getStringExtra("myowner");

                UserDetails.username = firebaseAuth.getCurrentUser().getUid();
                UserDetails.chatWith = Owner;
                UserDetails.name = ownername;
                startActivity(new Intent(ViewActivity.this, Chat.class));
                finish();
            }
        });

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.child(parentkey).hasChild(firebaseAuth.getCurrentUser().getUid())){
                            String status = "pending";
                            event_list eventList = new event_list(Owner, Name, firebaseAuth.getCurrentUser().getDisplayName(),status, ownername);
                            mDataRef.child(parentkey).child(firebaseAuth.getCurrentUser().getUid()).setValue(eventList);
                            Toast.makeText(ViewActivity.this, "Joined...", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(ViewActivity.this, "Already joined!!" , Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        EditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewActivity.this, EditActivity.class);
                intent.putExtra("thename", Name);
                intent.putExtra("thedesc", Desc);
                intent.putExtra("thedate", Date);
                intent.putExtra("thestarttime", Starttime);
                intent.putExtra("theendtime", Endtime);
                intent.putExtra("thememberfee", Memberfee);
                intent.putExtra("thenonmemberfee", Nonmemberfee);
                intent.putExtra("thevenue", Venue);

                intent.putExtra("theurl", myurl);
                intent.putExtra("theowner", Owner);
                intent.putExtra("thekey", myKey);
                startActivity(intent);
            }
        });
    }
}
