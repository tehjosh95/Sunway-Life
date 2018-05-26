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

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.UUID;

public class AddClubs extends AppCompatActivity {
    private ListOfClubs listofclubs= new ListOfClubs();

    private String uid;
    private FirebaseStorage mStorage;
    private StorageReference mStorRef;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDataRef;
    private FirebaseAuth firebaseAuth2;
    private static final String TAG = "AddActivity";
    private EditText clubName = null;
    private EditText clubContact = null;
    private EditText clubDesc = null;
    private EditText clubEmail = null;
    private EditText clubPass = null;
    private FloatingActionButton fab = null;
    private String imgUrl;
    private FirebaseAuth firebaseAuth;
    //    private UserProfile userProfile;
    private DatabaseReference mUserRef;
    private Button btnLoadImage;
    private Button btnUploadItem;
    private ImageView imageView;
    private boolean isImgUploaded = false;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef;

    private String imageFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_clubs);

        mDatabase = FirebaseDatabase.getInstance();
        mDataRef = mDatabase.getReference();
        mStorage = FirebaseStorage.getInstance();
        mStorRef = mStorage.getReference();

        clubName = (EditText) findViewById(R.id.club_name);
        clubEmail = (EditText) findViewById(R.id.club_email);
        clubPass = (EditText) findViewById(R.id.club_password);
        clubContact = (EditText) findViewById(R.id.club_contact);
        clubDesc = (EditText) findViewById(R.id.club_desc);
        fab = (FloatingActionButton)findViewById(R.id.fabb);
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
            FirebaseApp firebaseApp = FirebaseApp.initializeApp(getApplicationContext(),firebaseOptions,"fyp");
            firebaseAuth2 = FirebaseAuth.getInstance(firebaseApp);
        }catch(IllegalStateException e){
            firebaseAuth2 = FirebaseAuth.getInstance(FirebaseApp.getInstance("fyp"));
        }

//        final ProgressDialog progDialog = new ProgressDialog(AddClubs.this,
//                R.style.Theme_AppCompat_DayNight_NoActionBar);
//        progDialog.setIndeterminate(true);
//        progDialog.setMessage("Loading user data...");
//        progDialog.show();
//        ValueEventListener userListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                userProfile = dataSnapshot.getValue(UserProfile.class);
//                progDialog.dismiss();
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        };

//        mUserRef.addValueEventListener(userListener);

//        DatabaseReference databaseReference = mDatabase.getReference().child("Item Information");

        btnUploadItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        this.registration();
//        this.uploadImageToFirebase();
    }

    public void onSuccessfulSave() {
        Toast.makeText(AddClubs.this, "Successfully uploaded item.", Toast.LENGTH_LONG).show();
        btnUploadItem.setEnabled(true);
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
                if(requestCode == RESULT_OK) {
                }
                break;
            case 1:
                if(resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    this.imageView.setImageURI(selectedImage);
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
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                imgUrl = downloadUrl.toString();

                String name = clubName.getText().toString().trim();
                String cont = clubContact.getText().toString().trim();
                String desc = clubDesc.getText().toString().trim();
                String usertype = "club";

//        if (isInputInvalid(name, place, price)) {
//            onFailedSave();
//        } else {
                btnUploadItem.setEnabled(false);
                listofclubs.setName(name);
                listofclubs.setContact(cont);
                listofclubs.setDesc(desc);
                listofclubs.setUserType(usertype);
                listofclubs.setMyUid(uid);

                final ProgressDialog progDialog = new ProgressDialog(AddClubs.this,
                        R.style.Theme_AppCompat_DayNight_NoActionBar);

                progDialog.setIndeterminate(true);
                progDialog.setMessage("Uploading....");
                progDialog.show();
                final ListOfClubs listofclubs = new ListOfClubs(name, imgUrl, cont, desc, usertype, uid);
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

    private void registration(){
        String user_email = clubEmail.getText().toString().trim();
        String user_password = clubPass.getText().toString().trim();
        Log.d("****1", "1");
        firebaseAuth2.createUserWithEmailAndPassword(user_email, user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
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
                }else{
                    Toast.makeText(AddClubs.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
