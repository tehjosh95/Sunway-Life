package com.example.android.myfyp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.roger.catloadinglibrary.CatLoadingView;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.UUID;

public class AddClubs extends AppCompatActivity {
    private ListOfClubs listofclubs = new ListOfClubs();

    private String uid;
    private FirebaseStorage mStorage;
    private StorageReference mStorRef;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDataRef;
    private FirebaseAuth firebaseAuth2;
    private static final String TAG = "AddActivity";
    private EditText clubName = null;
    private EditText clubAdvisor = null;
    private EditText clubDesc = null;
    private EditText clubEmail = null;
    private EditText clubPass = null;
    private FloatingActionButton fab = null;
    private String imgUrl;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mUserRef;
    private Button btnLoadImage;
    private Button btnUploadItem;
    private ImageView imageView;
    private boolean isImgUploaded = false;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef;
    Toolbar toolbar;
    private String imageFileName;
    CatLoadingView mView;
    int test = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_clubs);

        mDatabase = FirebaseDatabase.getInstance();
        mDataRef = mDatabase.getReference();
        mStorage = FirebaseStorage.getInstance();
        mStorRef = mStorage.getReference();

        toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        toolbar.setTitle("Add Clubs");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        clubName = (EditText) findViewById(R.id.club_name);
        clubEmail = (EditText) findViewById(R.id.club_email);
        clubPass = (EditText) findViewById(R.id.club_password);
        clubAdvisor = (EditText) findViewById(R.id.club_advisor);
        clubDesc = (EditText) findViewById(R.id.club_desc);
        fab = (FloatingActionButton) findViewById(R.id.fabb);
        btnUploadItem = (AppCompatButton) findViewById(R.id.btn_upload);

        imageView = (ImageView) findViewById(R.id.image_preview);

//        mUserRef = mDatabase.getReference();
//        mUserRef = mUserRef.child("Clubs").child(mAuth.getCurrentUser().getUid());
        firebaseAuth = FirebaseAuth.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://myfyp-25f5d.appspot.com/images");

        FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                .setApiKey("AIzaSyBEJ5JQ002kxG_d3MZ1v86fG-2Xnofd28o")
                .setApplicationId("myfyp-25f5d")
                .build();

        try {
            FirebaseApp firebaseApp = FirebaseApp.initializeApp(getApplicationContext(), firebaseOptions, "fyp");
            firebaseAuth2 = FirebaseAuth.getInstance(firebaseApp);
        } catch (IllegalStateException e) {
            firebaseAuth2 = FirebaseAuth.getInstance(FirebaseApp.getInstance("fyp"));
        }

        btnUploadItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mView = new CatLoadingView();
                mView.show(getSupportFragmentManager(), "");
                mView.setCanceledOnTouchOutside(false);
                mView.setCancelable(false);
                uploadItem();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddClubs.this, SecondActivity.class));
            }
        });
    }

    private void uploadItem() {
        String name = clubName.getText().toString();
        String advisor = clubAdvisor.getText().toString();
        String desc = clubDesc.getText().toString();
        String email = clubEmail.getText().toString();
        String pass = clubPass.getText().toString();

        if (validate(name,advisor,desc,email,pass) && test == 0) {
            this.registration();
        }else{
            mView.dismiss();
            Toast.makeText(AddClubs.this, "Please fill in all required data", Toast.LENGTH_SHORT).show();
        }
    }

    public void onSuccessfulSave() {
        Toast.makeText(AddClubs.this, "Successfully uploaded item.", Toast.LENGTH_LONG).show();
        btnUploadItem.setEnabled(true);
        mView.dismiss();
        finish();
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
                    this.imageView.setImageURI(selectedImage);
                    test = 0;
                }
                break;
        }
    }

    private void uploadImageToFirebase() {
        // Get the data from an ImageView as bytes
        this.imageView.setDrawingCacheEnabled(true);
        this.imageView.buildDrawingCache();
        Bitmap bitmap = this.imageView.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        this.imageFileName = StringUtils.getRandomString(20) + ".jpg";
        StorageReference mountainsRef = storageRef.child(imageFileName);
        UploadTask uploadTask = mountainsRef.putBytes(data);

        uploadTask.addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.i("MainActivity", "Upload failed");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                imgUrl = downloadUrl.toString();

                String name = clubName.getText().toString().trim();
                String adv = clubAdvisor.getText().toString().trim();
                String desc = clubDesc.getText().toString().trim();
                String emails = clubEmail.getText().toString().trim();
                String usertype = "club";

                btnUploadItem.setEnabled(false);
                listofclubs.setName(name);
                listofclubs.setAdvisor(adv);
                listofclubs.setDesc(desc);
                listofclubs.setEmail(emails);
                listofclubs.setUserType(usertype);
                listofclubs.setMyUid(uid);

                final ProgressDialog progDialog = new ProgressDialog(AddClubs.this,
                        R.style.Theme_AppCompat_DayNight_NoActionBar);

                progDialog.setIndeterminate(true);
                progDialog.setMessage("Uploading....");
                progDialog.show();
                final ListOfClubs listofclubs = new ListOfClubs(name, imgUrl, adv, desc, usertype, uid, emails);
                final Runnable uploadTask = new Runnable() {
                    @Override
                    public void run() {
                        mDataRef.child("Clubs").child(uid).setValue(listofclubs);
                        progDialog.dismiss();
                    }
                };
                new Thread(uploadTask).start();
                onSuccessfulSave();
            }
        });
    }

    private Boolean validate(String i1, String i2, String i3, String i4, String i5){
        if(i1.isEmpty() || i2.isEmpty() || i3.isEmpty() || i4.isEmpty() || i5.isEmpty()){
            return false;
        }else{
            return true;
        }
    }

    private void registration() {
        String user_email = clubEmail.getText().toString().trim();
        String user_password = clubPass.getText().toString().trim();
        Log.d("****1", "1");
        firebaseAuth2.createUserWithEmailAndPassword(user_email, user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    //sendEmailVerification();
                    uid = firebaseAuth2.getCurrentUser().getUid();
                    FirebaseUser user = firebaseAuth2.getCurrentUser();

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(clubName.getText().toString())
                            .build();

                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                }
                            });
                    uploadImageToFirebase();
                    firebaseAuth2.signOut();
                    Toast.makeText(AddClubs.this, "Successfully Registered, Upload complete!", Toast.LENGTH_SHORT).show();
                } else {
                    mView.dismiss();
                    Toast.makeText(AddClubs.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
