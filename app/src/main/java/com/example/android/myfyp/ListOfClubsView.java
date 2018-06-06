package com.example.android.myfyp;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
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
    private TextView profileName, profileCont, profileDesc, averagetext;
    private Button EditButton, btnJoin , rate;
    private FloatingActionButton fabbb;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseDatabase mDatabase;
    private Dialog rankDialog;
    private DatabaseReference mDataRef, mDataRef2, mDataRef3, mDataRef4;
    private String nameofclub;
    private RatingBar ratingbar1, ratingbar2;
    private float count, total, totalperson;
    private String status = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_clubs_view);

        firebaseAuth = FirebaseAuth.getInstance();

        averagetext = findViewById(R.id.averagetext);
        ratingbar1 = findViewById(R.id.ratingBar1);
        profilePic = findViewById(R.id.ivimage);
        profileName = findViewById(R.id.tvname);
        profileCont = findViewById(R.id.tvcont);
        profileDesc = findViewById(R.id.tvdesc);
        EditButton = findViewById(R.id.btnEdit);
        rate = findViewById(R.id.rate);
        btnJoin = findViewById(R.id.btnJoin);
        fabbb = (FloatingActionButton)findViewById(R.id.fabbb);

        firebaseDatabase = FirebaseDatabase.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mDataRef = mDatabase.getReference();
        mDataRef = firebaseDatabase.getReference().child("join_list").child("members");
        mDataRef2 = firebaseDatabase.getReference();
        mDataRef3 = mDatabase.getReference();
        mDataRef3 = firebaseDatabase.getReference().child("join_list").child("members");
        mDataRef4 = mDatabase.getReference();

        Intent startingIntent = getIntent();
        final String myurl = startingIntent.getStringExtra("isimg");
        final String Name = startingIntent.getStringExtra("isname");
        final String Cont = startingIntent.getStringExtra("iscont");
        final String Desc = startingIntent.getStringExtra("isdesc");
        final String Myuid = startingIntent.getStringExtra("isuid");

        Glide.with(this).load(myurl).thumbnail(0.1f).into(profilePic);
        profileName.setText(Name);
        profileCont.setText(Cont);
        profileDesc.setText(Desc);
        Log.d("^^^uidview", "" + Myuid);

        fabbb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ListOfClubsView.this, SecondActivity.class));
            }
        });

        mDataRef4 = firebaseDatabase.getReference().child("join_list").child("members").child(Myuid);

        mDataRef4.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postsnapshot : dataSnapshot.getChildren()){
                    total = 0;
                    totalperson = 0;
                    for (DataSnapshot nextsnap : postsnapshot.getChildren()){
                        final join_list joinList = nextsnap.getValue(join_list.class);
                        total += joinList.getRating();
                        totalperson += 1;
                    }
                    ratingbar1.setRating(total / totalperson);
                    ratingbar1.setClickable(false);
                    averagetext.setText("Average is: " + total / totalperson);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        mDataRef3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(Myuid).hasChild(firebaseAuth.getCurrentUser().getUid())){
                    mDataRef3.removeEventListener(this);
                    mDataRef3 = mDataRef3.child(Myuid).child(firebaseAuth.getCurrentUser().getUid());
                        mDataRef3.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final join_list joinList = dataSnapshot.getValue(join_list.class);
                                if(dataSnapshot.getKey().equals(firebaseAuth.getCurrentUser().getUid())){
                                    status = joinList.getStatus();
                                    count = joinList.getRating();
                                }
                                rate.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if(status.equals("successful")){
                                            rankDialog = new Dialog(ListOfClubsView.this, R.style.FullHeightDialog);
                                            rankDialog.setContentView(R.layout.rank_dialog);
                                            rankDialog.setCancelable(true);
                                            ratingbar2 = (RatingBar)rankDialog.findViewById(R.id.dialog_ratingbar);
                                            ratingbar2.setRating(count);

                                            TextView text = (TextView) rankDialog.findViewById(R.id.rank_dialog_text1);
//                                      text.setText(name);

                                            Button updateButton = (Button) rankDialog.findViewById(R.id.rank_dialog_button);
                                            updateButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    mDataRef.child(Myuid).child(firebaseAuth.getCurrentUser().getUid()).child("rating").setValue(ratingbar2.getRating());
                                                    rankDialog.dismiss();
                                                }
                                            });
                                            rankDialog.show();
                                        }else if(status.equals("pending")){
                                            Toast.makeText(ListOfClubsView.this, "Can't rate yet", Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(ListOfClubsView.this, "GG", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                }else{
                    rate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(ListOfClubsView.this, "Must join first", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
                                    join_list joinList = new join_list(status, nameofclub, firebaseAuth.getCurrentUser().getDisplayName(),0);
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
