package com.example.android.myfyp;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
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

public class ProfileActivity extends AppCompatActivity {

    private ImageView profilePic;
    private Button profileUpdate, changePassword, profileChat;
    private EditText editId, editName, editCourse, editPhone, editType;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDataRef;
    private FloatingActionButton fab;
    Toolbar toolbar;
    private TextInputLayout edit_user_contact_text_input_layout3, edit_user_contact_text_input_layout4, edit_user_contact_text_input_layout5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profilePic = findViewById(R.id.ivProfilePic);

        toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        toolbar.setTitle("Profile");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        edit_user_contact_text_input_layout3 = findViewById(R.id.edit_user_contact_text_input_layout3);
        edit_user_contact_text_input_layout4 = findViewById(R.id.edit_user_contact_text_input_layout4);
        edit_user_contact_text_input_layout5 = findViewById(R.id.edit_user_contact_text_input_layout5);
        editId = findViewById(R.id.edit_user_id);
        editName = findViewById(R.id.edit_user_name);
        editCourse = findViewById(R.id.edit_user_course);
        editPhone = findViewById(R.id.edit_user_contact);
        editType = findViewById(R.id.edit_user_type);

        profileUpdate = findViewById(R.id.btnProfileUpdate);
        profileChat = findViewById(R.id.btnChat);
        changePassword = findViewById(R.id.btnChangePassword);
        fab = (FloatingActionButton) findViewById(R.id.fabb);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        mDataRef = firebaseDatabase.getReference();
        mDataRef = mDataRef.child("Users").child(firebaseAuth.getCurrentUser().getUid());

        final Intent startingIntent = getIntent();
        Bundle extras = startingIntent.getExtras();

        if (extras == null) {
            profileChat.setVisibility(View.GONE);
            mDataRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                    editId.setText(userProfile.getStudentID());
                    editName.setText(userProfile.getStudentName());
                    editCourse.setText(userProfile.getStudentCourse());
                    editPhone.setText(userProfile.getStudentPhone());
                    editType.setText(userProfile.getUserType());
                    Glide.with(ProfileActivity.this).load(userProfile.getImgurl()).thumbnail(0.1f).into(profilePic);
                    Log.d("***edittype", userProfile.getUserType());
                    if (userProfile.getUserType().equals("admin")) {
                        edit_user_contact_text_input_layout3.setVisibility(View.GONE);
                        edit_user_contact_text_input_layout4.setHint("Description");
                        edit_user_contact_text_input_layout5.setVisibility(View.GONE);
//                        editCourse.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(ProfileActivity.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            final String id = startingIntent.getStringExtra("isstudentid");
            final String name = startingIntent.getStringExtra("isname");
            final String course = startingIntent.getStringExtra("iscourse");
            final String phone = startingIntent.getStringExtra("isphone");
            final String type = startingIntent.getStringExtra("istype");
            final int fromchat = startingIntent.getIntExtra("fromchat", 0);
            final String url = startingIntent.getStringExtra("isurl");
            profileUpdate.setVisibility(View.GONE);
            changePassword.setVisibility(View.GONE);

            if (type.equals("admin")) {
                edit_user_contact_text_input_layout3.setVisibility(View.GONE);
                edit_user_contact_text_input_layout4.setHint("Description");
                editCourse.setLines(4);
                edit_user_contact_text_input_layout5.setVisibility(View.GONE);
            }

            editId.setText(id);
            editName.setText(name);
            editCourse.setText(course);
            editPhone.setText(phone);
            editType.setText(type);
            Glide.with(ProfileActivity.this).load(url).thumbnail(0.1f).into(profilePic);
            if(fromchat == 1){
                profileChat.setVisibility(View.GONE);
            }
        }

//        if(firebaseAuth.getCurrentUser().getUid().equals("XHR842kZD3cTZTwz7nM5LWJESW72")){
//            edit_user_contact_text_input_layout3.setVisibility(View.GONE);
//            edit_user_contact_text_input_layout4.setHint("Description");
//            editCourse.setLines(4);
//            edit_user_contact_text_input_layout5.setVisibility(View.GONE);
//        }

        profileUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, UpdateProfile.class));
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, UpdatePassword.class));
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(ProfileActivity.this, SecondActivity.class));
            }
        });

//        final String key = startingIntent.getStringExtra("isid");
//        if (key == null){
//            profileChat.setVisibility(View.GONE);
//            fab.setVisibility(View.GONE);
//        }


        profileChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = startingIntent.getStringExtra("isstudentid");
                final String key = startingIntent.getStringExtra("isid");

                UserDetails.username = firebaseAuth.getCurrentUser().getUid();
                UserDetails.chatWith = key;
                UserDetails.name = name;
                startActivity(new Intent(ProfileActivity.this, Chat.class));
                finish();
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
