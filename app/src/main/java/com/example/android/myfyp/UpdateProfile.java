package com.example.android.myfyp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UpdateProfile extends AppCompatActivity {

    private EditText newUserName, newUserCourse, newUserPhone, etIdupdate;
    private Button save;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDataRef, mDataRef2, mDataRef3;
    String userType;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        etIdupdate = findViewById(R.id.etIdUpdate);
        newUserName = findViewById(R.id.etNameUpdate);
        newUserCourse = findViewById(R.id.etCourseUpdate);
        newUserPhone = findViewById(R.id.etPhoneUpdate);
        save = findViewById(R.id.btnSave);


        toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        toolbar.setTitle("Update Profile");
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
        mDataRef = mDataRef.child("Users").child(firebaseAuth.getCurrentUser().getUid());
        mDataRef2 = firebaseDatabase.getReference();
        mDataRef2 = mDataRef2.child("messages");
        mDataRef3 = firebaseDatabase.getReference();
        mDataRef3 = mDataRef3.child("join_list").child("members");


        mDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                etIdupdate.setText(userProfile.getStudentID());
                newUserName.setText(userProfile.getStudentName());
                newUserCourse.setText(userProfile.getStudentCourse());
                newUserPhone.setText(userProfile.getStudentPhone());
                userType = userProfile.getUserType();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(UpdateProfile.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = etIdupdate.getText().toString();
                final String name = newUserName.getText().toString();
                String course = newUserCourse.getText().toString();
                String phone = newUserPhone.getText().toString();
                String type = userType;
                final UserProfile userProfile = new UserProfile(id, name, course, phone, type);

                mDataRef2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postsnap : dataSnapshot.getChildren()){
                            Log.d("***mynam1", "" + postsnap.child("others").getValue(String.class));
                            Log.d("***uid1", "" + firebaseAuth.getCurrentUser().getDisplayName());
                            if(postsnap.child("others").getValue(String.class).equals(firebaseAuth.getCurrentUser().getDisplayName())){
                                mDataRef2.child(postsnap.getKey()).child("others").setValue(name);
                            }
                        }
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .build();

                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                    }
                                });
                        mDataRef.setValue(userProfile);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                mDataRef3.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postsnap : dataSnapshot.getChildren()){
                            for (DataSnapshot nextsnap : postsnap.getChildren()){
                                Log.d("***mynam2", "" + nextsnap.child("myname").getValue(String.class));
                                Log.d("***uid2", "" + firebaseAuth.getCurrentUser().getDisplayName());
                                if(nextsnap.child("myname").getValue(String.class).equals(firebaseAuth.getCurrentUser().getDisplayName())){
                                    mDataRef3.child(postsnap.getKey()).child(nextsnap.getKey()).child("myname").setValue(name);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

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
