package com.example.android.myfyp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.roger.catloadinglibrary.CatLoadingView;

import java.io.ByteArrayOutputStream;

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
    int test = 1;
    private String imageFileName;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef;
    CatLoadingView mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        setupUIViews();

        mDatabase = FirebaseDatabase.getInstance();
        mDataRef = mDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://myfyp-25f5d.appspot.com/images");
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mView = new CatLoadingView();
                mView.show(getSupportFragmentManager(), "");
                mView.setCanceledOnTouchOutside(false);
                mView.setCancelable(false);
                if (validate() && test == 0) {
                    String user_email = UserId.getText().toString().trim() + "@imail.sunway.edu.my";
                    String user_password = UserPassword.getText().toString().trim();

                    firebaseAuth.createUserWithEmailAndPassword(user_email, user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                uploadImageToFirebase();
                                Log.d("***registered", "registered");
                                Toast.makeText(RegistrationActivity.this, "Successfully Registered, Upload complete!", Toast.LENGTH_SHORT).show();
//                                finish();
//                                startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                            } else {
                                mView.dismiss();
                                Toast.makeText(RegistrationActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    mView.dismiss();
                    Toast.makeText(RegistrationActivity.this, "Please fill in all required data", Toast.LENGTH_SHORT).show();
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

    public void handleChooseImage(View view) {
        Intent pickerPhotoIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickerPhotoIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case 0:
                if (requestCode == RESULT_OK) {
                }
                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    this.userProfilePic.setImageURI(selectedImage);
                    test = 0;
                }
                break;
        }
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
                        Toast.makeText(RegistrationActivity.this, "Successfully Registered, Verification mail sent!", Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        finish();
                    } else {
                        Toast.makeText(RegistrationActivity.this, "Verification mail has'nt been sent!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

//    private void sendUserData(String imgurl) {
//
//        UserProfile userProfile = new UserProfile(studentid, studentname, studentcourse, studentphone, type, imgurl);
//
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
//                .setDisplayName(studentid)
//                .build();
//
//        user.updateProfile(profileUpdates)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                    }
//                });
//
//        mDataRef.child("Users").child(firebaseAuth.getCurrentUser().getUid()).setValue(userProfile);
////        myRef.setValue(userProfile);
//    }

    private void uploadImageToFirebase() {
        this.userProfilePic.setDrawingCacheEnabled(true);
        this.userProfilePic.buildDrawingCache();
        Bitmap bitmap = this.userProfilePic.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        this.imageFileName = StringUtils.getRandomString(20) + ".jpg";
        StorageReference mountainsRef = storageRef.child(imageFileName);
        UploadTask uploadTask = mountainsRef.putBytes(data);

        Log.d("***running", "running");

        uploadTask.addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception exception) {
                mView.dismiss();
                Log.i("MainActivity", "Upload failed");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                final String imgUrl = downloadUrl.toString();

                final UserProfile userProfile = new UserProfile(studentid, studentname, studentcourse, studentphone, type, imgUrl);

                final Runnable uploadTask = new Runnable() {
                    @Override
                    public void run() {
                        Log.d("***upload", "upload");
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(studentid)
                                .build();

                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        mDataRef.child("Users").child(firebaseAuth.getCurrentUser().getUid()).setValue(userProfile).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                sendEmailVerification();
//                                                mView.dismiss();
                                                firebaseAuth.signOut();
                                                finish();
                                            }
                                        });
                                    }
                                });
                    }
                };
                new Thread(uploadTask).start();
                onSuccessfulSave();
            }
        });
    }

    public void onSuccessfulSave() {
        Toast.makeText(RegistrationActivity.this, "Successfully uploaded item.", Toast.LENGTH_LONG).show();
        regButton.setEnabled(true);
        Log.d("***doneupload", "doneupload");
//        mView.dismiss();
        finish();
    }
}
