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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ListOfClubsView extends AppCompatActivity{
    public interface MyCallback {
         void onCallback(String value);
    }

    private ImageView profilePic;
    private TextView profileName, profileCont, profileDesc;
    private Button EditButton, btnJoin;
    private FloatingActionButton fabbb;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDataRef, mDataRef2, mDataRef3;
    private String nameofclub;
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_clubs_view);

        firebaseAuth = FirebaseAuth.getInstance();

        profilePic = findViewById(R.id.ivimage);
        profileName = findViewById(R.id.tvname);
        profileCont = findViewById(R.id.tvcont);
        profileDesc = findViewById(R.id.tvdesc);
        EditButton = findViewById(R.id.btnEdit);
        btnJoin = findViewById(R.id.btnJoin);
        fabbb = (FloatingActionButton)findViewById(R.id.fabbb);

        firebaseDatabase = FirebaseDatabase.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mDataRef = mDatabase.getReference();
        mDataRef = firebaseDatabase.getReference().child("join_list");
        mDataRef2 = firebaseDatabase.getReference();

        Intent startingIntent = getIntent();
        final String myurl = startingIntent.getStringExtra("isimg");
        final String Name = startingIntent.getStringExtra("isname");
        final String Cont = startingIntent.getStringExtra("iscont");
        final String Desc = startingIntent.getStringExtra("isdesc");
        final String Myuid = startingIntent.getStringExtra("isuid");

//        final String Owner = startingIntent.getStringExtra("myowner");
//        final String myKey = startingIntent.getStringExtra("mykey");

        Glide.with(this).load(myurl).thumbnail(0.1f).into(profilePic);
        profileName.setText(Name);
        profileCont.setText(Cont);
        profileDesc.setText(Desc);
        Log.d("^^^uidview", "" + Myuid);

//        if (!firebaseAuth.getCurrentUser().getUid().equals(Owner)){
//            EditButton.setVisibility(View.GONE);
//        }

        fabbb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ListOfClubsView.this, SecondActivity.class));
            }
        });

        EditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserDetails.username = firebaseAuth.getCurrentUser().getUid();
                UserDetails.chatWith = Myuid;
                UserDetails.name = Name;
                startActivity(new Intent(ListOfClubsView.this, Chat.class));
                finish();
            }
        });

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readData(new MyCallback(){
                    @Override
                    public void onCallback(String value) {
                        nameofclub = value;
                        Log.d("***method2", "" + nameofclub);
                        mDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.child(Myuid).hasChild(firebaseAuth.getCurrentUser().getUid())){
                                    String status = "pending";
                                    join_list joinList = new join_list(status, nameofclub, firebaseAuth.getCurrentUser().getDisplayName());
                                    mDataRef.child(Myuid).child(firebaseAuth.getCurrentUser().getUid()).setValue(joinList);
                                    Log.d("***method3", "" + nameofclub);
                                    Toast.makeText(ListOfClubsView.this, "On pending list...", Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(ListOfClubsView.this, "Already joined!!", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
            }
        });

//        EditButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(ListOfClubsView.this, EditActivity.class);
//                intent.putExtra("thename", Name);
//                intent.putExtra("theplace", Place);
//                intent.putExtra("theprice",Price);
//                intent.putExtra("theurl", myurl);
//                intent.putExtra("theowner", Owner);
//                intent.putExtra("thekey", myKey);
//                startActivity(intent);
//            }
//        });
    }
    public void readData( final MyCallback myCallback){
        Intent startingIntent = getIntent();
        final String Myuid = startingIntent.getStringExtra("isuid");
        mDataRef2 = mDataRef2.child("Clubs").child(Myuid);
        mDataRef2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ListOfClubs listOfClubs = dataSnapshot.getValue(ListOfClubs.class);
                nameofclub = listOfClubs.getName();
                myCallback.onCallback(nameofclub);
                Log.d("***method", "" + nameofclub);
                Log.d("***methoddown", "" + Myuid);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
