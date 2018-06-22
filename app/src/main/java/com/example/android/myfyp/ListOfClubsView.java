package com.example.android.myfyp;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ListOfClubsView extends AppCompatActivity {
    public interface MyCallback {
        void onCallback(String value);
    }
    Toolbar toolbar;
    private ImageView profilePic;
    private TextView averagetext;
    private EditText edit_name, edit_advisor, edit_email, edit_desc, mycategory;
    private Button EditButton, btnJoin, rate;
    private FloatingActionButton fabbb;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseDatabase mDatabase;
    private Dialog rankDialog;
    private DatabaseReference mDataRef, mDataRef2, mDataRef3, mDataRef4, mDataRef5;
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
        edit_name = findViewById(R.id.edit_name);
        edit_advisor = findViewById(R.id.edit_advisor);
        edit_email = findViewById(R.id.edit_email);
        edit_desc = findViewById(R.id.edit_desc);
        EditButton = findViewById(R.id.btnEdit);
        rate = findViewById(R.id.rate);
        btnJoin = findViewById(R.id.btnJoin);
        fabbb = (FloatingActionButton) findViewById(R.id.fabbb);
        mycategory = findViewById(R.id.category);

        toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        toolbar.setTitle("Clubs Profile");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final String myid = firebaseAuth.getCurrentUser().getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mDataRef = firebaseDatabase.getReference().child("join_list").child("members");
        mDataRef2 = firebaseDatabase.getReference();
        mDataRef3 = firebaseDatabase.getReference().child("join_list").child("members");
        mDataRef5 = firebaseDatabase.getReference().child("Users");

        Intent startingIntent = getIntent();
        final String myurl = startingIntent.getStringExtra("isimg");
        final String Name = startingIntent.getStringExtra("isname");
        final String Adv = startingIntent.getStringExtra("isadvisor");
        final String Desc = startingIntent.getStringExtra("isdesc");
        final String Myuid = startingIntent.getStringExtra("isuid");
        final String email = startingIntent.getStringExtra("isemail");
        final String category = startingIntent.getStringExtra("iscategory");
        final int fromchat = startingIntent.getIntExtra("fromchat", 0);

        if(fromchat == 1){
            EditButton.setVisibility(View.GONE);
        }

        mDataRef5.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(myid)){
                    UserProfile userProfile = dataSnapshot.child(myid).getValue(UserProfile.class);
                    if(!userProfile.getUserType().equals("student")){
                        rate.setVisibility(View.GONE);
                        btnJoin.setVisibility(View.GONE);
                    }
                }else{
                    EditButton.setVisibility(View.GONE);
                    rate.setVisibility(View.GONE);
                    btnJoin.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Glide.with(this).load(myurl).thumbnail(0.1f).into(profilePic);
        edit_name.setText(Name);
        edit_advisor.setText(Adv);
        edit_email.setText(email);
        edit_desc.setText(Desc);
        mycategory.setText(category);

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
                total = 0;
                totalperson = 0;
                for (DataSnapshot postsnapshot : dataSnapshot.getChildren()) {
                    final join_list joinList = postsnapshot.getValue(join_list.class);
                    total += joinList.getRating();
                    if (joinList.getRating() > 0) {
                        totalperson += 1;
                    }
                }
                Float ans = total / totalperson;
                if (!Float.isNaN(ans)) {
                    ratingbar1.setIsIndicator(true);
                    ratingbar1.setRating(total / totalperson);
                    ratingbar1.setClickable(false);
                    averagetext.setText("Average is: " + Float.toString(total / totalperson));
                }else{
                    ratingbar1.setIsIndicator(true);
                    ratingbar1.setRating(0);
                    ratingbar1.setClickable(false);
                    ratingbar1.setEnabled(false);
                    averagetext.setText("Average is: " + "0");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        mDataRef3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(Myuid).hasChild(firebaseAuth.getCurrentUser().getUid())) {
                    mDataRef3.removeEventListener(this);
                    mDataRef3 = mDataRef3.child(Myuid).child(firebaseAuth.getCurrentUser().getUid());
                    mDataRef3.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final join_list joinList = dataSnapshot.getValue(join_list.class);
                            if (dataSnapshot.getKey().equals(firebaseAuth.getCurrentUser().getUid())) {
                                status = joinList.getStatus();
                                count = joinList.getRating();
                            }
                            rate.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (status.equals("successful")) {
                                        rankDialog = new Dialog(ListOfClubsView.this, R.style.FullHeightDialog);
                                        rankDialog.setContentView(R.layout.rank_dialog);
                                        rankDialog.setCancelable(true);
                                        ratingbar2 = (RatingBar) rankDialog.findViewById(R.id.dialog_ratingbar);
                                        ratingbar2.setRating(count);

                                        TextView text = (TextView) rankDialog.findViewById(R.id.rank_dialog_text1);

                                        Button updateButton = (Button) rankDialog.findViewById(R.id.rank_dialog_button);
                                        updateButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                mDataRef.child(Myuid).child(firebaseAuth.getCurrentUser().getUid()).child("rating").setValue(ratingbar2.getRating());
                                                rankDialog.dismiss();
                                            }
                                        });
                                        rankDialog.show();
                                    } else if (status.equals("pending")) {
                                        Toast.makeText(ListOfClubsView.this, "Can't rate yet", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ListOfClubsView.this, "GG", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
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
                readData(new MyCallback() {
                    @Override
                    public void onCallback(String value) {
                        nameofclub = value;
                        Log.d("***method2", "" + nameofclub);
                        mDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.child(Myuid).hasChild(firebaseAuth.getCurrentUser().getUid())) {
                                    String status = "pending";
                                    join_list joinList = new join_list(status, nameofclub, firebaseAuth.getCurrentUser().getDisplayName(), 0);
                                    mDataRef.child(Myuid).child(firebaseAuth.getCurrentUser().getUid()).setValue(joinList);
                                    Log.d("***method3", "" + nameofclub);
                                    Toast.makeText(ListOfClubsView.this, "On pending list...", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ListOfClubsView.this, "Already joined!!", Toast.LENGTH_SHORT).show();
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

    public void readData(final MyCallback myCallback) {
        Intent startingIntent = getIntent();
        final String Myuid = startingIntent.getStringExtra("isuid");
        Log.d("***isuid", "" + Myuid);
        mDataRef2 = mDataRef2.child("Clubs").child(Myuid);
        mDataRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ListOfClubs listOfClubs = dataSnapshot.getValue(ListOfClubs.class);
                Log.d("***listOfClubs", "" + listOfClubs);
                if(listOfClubs != null) {
                    nameofclub = listOfClubs.getName();
                    myCallback.onCallback(nameofclub);
                }else{
                    myCallback.onCallback("");
                }
                Log.d("***method", "" + nameofclub);
                Log.d("***methoddown", "" + Myuid);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
