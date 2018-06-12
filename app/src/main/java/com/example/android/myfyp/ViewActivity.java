package com.example.android.myfyp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    private ImageView profilePic;
    private TextView profileName, profilePlace, profilePrice;
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
        profileName = findViewById(R.id.tvname);
        profilePlace = findViewById(R.id.tvcont);
        profilePrice = findViewById(R.id.tvdesc);
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
        final String Place = startingIntent.getStringExtra("myplace");
        final String Price = startingIntent.getStringExtra("myprice");
        final String Owner = startingIntent.getStringExtra("myowner");
        final String myKey = startingIntent.getStringExtra("mykey");
        final String ownername = startingIntent.getStringExtra("myownername");
        final String parentkey = startingIntent.getStringExtra("myparentkey");

        Glide.with(this).load(myurl).thumbnail(0.1f).into(profilePic);
        profileName.setText(Name);
        profilePlace.setText(Place);
        profilePrice.setText(Price);

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
//        if (!firebaseAuth.getCurrentUser().getUid().equals(Owner)) {
//            EditButton.setVisibility(View.GONE);
//        }else{
//            btnchat.setVisibility(View.GONE);
//            join.setVisibility(View.GONE);
//        }

        Log.d("****itemname", "" + Name);
        Log.d("****itemplace", "" + Place);
        Log.d("****itemprice", "" + Price);

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
                intent.putExtra("theplace", Place);
                intent.putExtra("theprice", Price);
                intent.putExtra("theurl", myurl);
                intent.putExtra("theowner", Owner);
                intent.putExtra("thekey", myKey);
                startActivity(intent);
            }
        });
    }
}
