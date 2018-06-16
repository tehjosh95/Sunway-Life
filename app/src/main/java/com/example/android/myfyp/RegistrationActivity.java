package com.example.android.myfyp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {

    private EditText UserId, UserName, UserCourse, UserPhone, UserPassword;
    private Button regButton;
    private TextView userLogin, referAdmin;
    private FirebaseAuth firebaseAuth;
    private Switch switch1;
    private ImageView userProfilePic;
    String studentid, studentname, studentcourse, studentphone, type, password;
    private DatabaseReference mDataRef;
    private FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        setupUIViews();

        mDatabase = FirebaseDatabase.getInstance();
        mDataRef = mDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    //Upload data to the database
                    String user_email = UserId.getText().toString().trim() + "@imail.sunway.edu.my";
                    String user_password = UserPassword.getText().toString().trim();

                    firebaseAuth.createUserWithEmailAndPassword(user_email, user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                //sendEmailVerification();
                                sendUserData();
                                firebaseAuth.signOut();
                                Toast.makeText(RegistrationActivity.this, "Successfully Registered, Upload complete!", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                            } else {
                                Toast.makeText(RegistrationActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            }
        });

        userLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
            }
        });

    }

    private void setupUIViews() {
        UserId = (EditText) findViewById(R.id.UserId);
        UserCourse = (EditText) findViewById(R.id.UserCourse);
        UserName = (EditText) findViewById(R.id.UserName);
        UserPhone = findViewById(R.id.UserPhone);
        regButton = (Button) findViewById(R.id.btnRegister);
        userLogin = (TextView) findViewById(R.id.tvUserLogin);
        userProfilePic = (ImageView) findViewById(R.id.ivProfile);
        switch1 = (Switch) findViewById(R.id.switch1);
        referAdmin = (TextView) findViewById(R.id.referAdmin);
        UserPassword = findViewById(R.id.UserPassword);

        switch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (switch1.isChecked()) {
                    UserId.setVisibility(View.GONE);
                    UserCourse.setVisibility(View.GONE);
                    UserName.setVisibility(View.GONE);
                    UserPhone.setVisibility(View.GONE);
                    regButton.setVisibility(View.GONE);
                    UserPassword.setVisibility(View.GONE);
                    referAdmin.setVisibility(View.VISIBLE);
                } else {
                    UserId.setVisibility(View.VISIBLE);
                    UserCourse.setVisibility(View.VISIBLE);
                    UserName.setVisibility(View.VISIBLE);
                    UserPhone.setVisibility(View.VISIBLE);
                    regButton.setVisibility(View.VISIBLE);
                    UserPassword.setVisibility(View.VISIBLE);
                    referAdmin.setVisibility(View.GONE);
                }
            }
        });
    }

    private Boolean validate() {
        Boolean result = false;

        studentid = UserId.getText().toString();
        studentname = UserName.getText().toString();
        studentcourse = UserCourse.getText().toString();
        studentphone = UserPhone.getText().toString();
        type = "student";
        password = UserPassword.getText().toString();


        if (studentid.isEmpty() || studentcourse.isEmpty() || studentname.isEmpty() || studentphone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter all the details", Toast.LENGTH_SHORT).show();
        } else {
            result = true;
        }

        return result;
    }


    private void sendEmailVerification() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        sendUserData();
                        Toast.makeText(RegistrationActivity.this, "Successfully Registered, Verification mail sent!", Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                    } else {
                        Toast.makeText(RegistrationActivity.this, "Verification mail has'nt been sent!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendUserData() {

        UserProfile userProfile = new UserProfile(studentid, studentname, studentcourse, studentphone, type);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(studentid)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });

        mDataRef.child("Users").child(firebaseAuth.getCurrentUser().getUid()).setValue(userProfile);
//        myRef.setValue(userProfile);
    }
}
