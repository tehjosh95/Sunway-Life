package com.example.android.myfyp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.support.v7.widget.RecyclerView;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;


import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class activity_finish extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    ArrayList<clubModel> clubModelList;
    ArrayList<String> itemKey;

    private ProgressDialog progDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDataRef;
    private FirebaseDatabase mDatabase;
    private TextView textReminder;
    private Button logout;
    private int pos;
    private String key;
    private int[] arrayPosition;
    private String[] arrayUrl;
    private String[] arrayDelete;
    private FloatingActionButton fab = null;
    private FirebaseDatabase firebaseDatabase;
    private clubAdapter adapter;
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    private StorageReference storageRef;

    clubModel[] arrayName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);

        fab = (FloatingActionButton) findViewById(R.id.fabbb);
        textReminder = findViewById(R.id.textReminder);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        mDataRef = firebaseDatabase.getReference().child("Item Information");

        recyclerView = findViewById(R.id.rvv);
        toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        toolbar.setTitle("Finished activity");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        clubModelList = new ArrayList<>();
        itemKey = new ArrayList<>();
        mDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                clubModelList.clear();
                itemKey.clear();
                arrayPosition = new int[(int) snapshot.getChildrenCount()];
                arrayUrl = new String[(int) snapshot.getChildrenCount()];
                pos = 0;
                int x = 0;
                for (DataSnapshot child : snapshot.getChildren()) {
                    clubModel ClubModel = child.getValue(clubModel.class);
                    String owner = firebaseAuth.getCurrentUser().getUid();

                    if (owner.equals(ClubModel.getItem_owner()) && ClubModel.getFinish()) {
                        ClubModel.setItem_position(pos);
                        ClubModel.setParentkey(child.getKey().toString());
                        mDataRef.child(child.getKey()).setValue(ClubModel);
                        arrayPosition[x] = pos;
                        arrayUrl[x] = ClubModel.getImageLink();
                        clubModelList.add(ClubModel);
                        itemKey.add(ClubModel.getParentkey());
                        x += 1;
                    }
                    pos += 1;
                }
                arrayName = new clubModel[clubModelList.size()];
                arrayName = clubModelList.toArray(arrayName);
                adapter = new clubAdapter(activity_finish.this, clubModelList);
                recyclerView.setAdapter(adapter);

                if(clubModelList.size()>0){
                    recyclerView.setVisibility(View.VISIBLE);
                    textReminder.setVisibility(View.GONE);
                }else{
                    textReminder.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
                progDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(activity_finish.this, new RecyclerItemClickListener.OnItemClickListener() {
                    public void onItemClick(View view, int position) {
                        clubModel ClubModel1 = clubModelList.get(position);
                        Intent intent = new Intent(activity_finish.this, ViewActivity.class);
                        intent.putExtra("myname", ClubModel1.getItem_name());
                        intent.putExtra("mydesc", ClubModel1.getItem_desc());
                        intent.putExtra("mydate", ClubModel1.getItem_date());
                        intent.putExtra("mystarttime", ClubModel1.getItem_start_time());
                        intent.putExtra("myendtime", ClubModel1.getItem_end_time());
                        intent.putExtra("mymemberfee", ClubModel1.getFee_for_member());
                        intent.putExtra("mynonmemberfee", ClubModel1.getFee_for_nonmember());
                        intent.putExtra("myvenue", ClubModel1.getVenue());
                        intent.putExtra("myurl", ClubModel1.getImageLink());
                        intent.putExtra("myowner", ClubModel1.getItem_owner());
                        intent.putExtra("myownername", ClubModel1.getOwnerName());
                        intent.putExtra("mykey", ClubModel1.getParentkey());
                        intent.putExtra("myparentkey", "");
                        intent.putExtra("myisfinish", ClubModel1.getFinish());
                        startActivity(intent);
                    }

                    public void onItemLongPress(View childView, final int position) {

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity_finish.this);
                        alertDialog.setTitle("Undo?");

                        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                mDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        mDataRef.child(itemKey.get(position)).child("finish").setValue(false);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        });
                        alertDialog.show();
                    }
                })
        );

        progDialog = ProgressDialog.show(this, null, "Wait.....");
        progDialog.setContentView(new ProgressBar(this));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(activity_finish.this, SecondActivity.class));
            }
        });
    }
}
