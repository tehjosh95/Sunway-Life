package com.example.android.myfyp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class Users extends AppCompatActivity {
    ListView usersList;
    ArrayList<String> al = new ArrayList<>();
    ArrayList<String> al2 = new ArrayList<>();
    int totalUsers = 0;
    ProgressDialog pd;
    private DatabaseReference mDataRef, mDataRef2;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth firebaseAuth;
    private EditText mSearchField;
    private ImageButton mSearchBtn;
    Toolbar toolbar;
    private TextView remindertext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        remindertext = findViewById(R.id.remindertext);
        mDatabase = FirebaseDatabase.getInstance();
        mDataRef = FirebaseDatabase.getInstance().getReference("Users");
        firebaseAuth = FirebaseAuth.getInstance();
        mSearchField = (EditText) findViewById(R.id.search_field);
        mSearchBtn = (ImageButton) findViewById(R.id.search_btn);
        mSearchBtn.setVisibility(View.GONE);
        usersList = (ListView) findViewById(R.id.usersList);
        mDataRef.addValueEventListener(valueEventListener);

        toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        toolbar.setTitle("List of students");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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
                    Log.d("more than 0", "more than 0");
                } else {
                    Log.d("less than 0", "less than 0");
                    al.clear();
                    mDataRef.addListenerForSingleValueEvent(valueEventListener);
                }
            }
        });

        usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                mDataRef2 = FirebaseDatabase.getInstance().getReference("Users").child(al2.get(position));
                mDataRef2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                        Intent intent = new Intent(Users.this, ProfileActivity.class);
                        intent.putExtra("isstudentid", userProfile.getStudentID());
                        intent.putExtra("isname", userProfile.getStudentName());
                        intent.putExtra("iscourse", userProfile.getStudentCourse());
                        intent.putExtra("isphone", userProfile.getStudentPhone());
                        intent.putExtra("istype", userProfile.getUserType());
                        intent.putExtra("isid", al2.get(position));
                        intent.putExtra("fromchat", 0);
                        intent.putExtra("isurl", userProfile.getImgurl());
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    private void firebaseUserSearch(String searchText) {
        al.clear();
        if (!searchText.equals("")) {
            Query firebaseSearchQuery = mDataRef.orderByChild("studentID").startAt(searchText).endAt(searchText + "\uf8ff");
            firebaseSearchQuery.addListenerForSingleValueEvent(valueEventListener);
        } else {
            Query firebaseSearchQuery = mDataRef;
            firebaseSearchQuery.addListenerForSingleValueEvent(valueEventListener);
        }
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Log.d("***triggered", "triggered");
            al.clear();
            al2.clear();
            for (DataSnapshot postsnapshot : dataSnapshot.getChildren()) {
                UserProfile userProfile = postsnapshot.getValue(UserProfile.class);
                Log.d("***usertype", "" + userProfile.getUserType());
                Log.d("***dataSnapshot", "" + dataSnapshot.getKey());
                Log.d("***postsnapshot", "" + postsnapshot.getKey());

                if(userProfile.getUserType().equals("student")){
                    al.add(userProfile.getStudentID());
                    al2.add(postsnapshot.getKey());
                }
            }
            usersList.setAdapter(new ArrayAdapter<String>(Users.this, android.R .layout.simple_list_item_1, al));
            if (al.size() > 0){
                usersList.setVisibility(View.VISIBLE);
                remindertext.setVisibility(View.GONE);
            }else{
                remindertext.setVisibility(View.VISIBLE);
                usersList.setVisibility(View.GONE);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
}