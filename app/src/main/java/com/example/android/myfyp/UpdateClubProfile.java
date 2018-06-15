package com.example.android.myfyp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

public class UpdateClubProfile extends AppCompatActivity {
    private ListOfClubs listOfClubs = new ListOfClubs();
    private EditText newUserName, newUserAdvisor, newUserDesc, newUserEmail, usertype;
    private Button save;
    private FirebaseAuth firebaseAuth;
    private ImageView imgView;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDataRef, mDataRef2, mDataRef3;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseStorage mStorage;
    private StorageReference storageRef;
    private StorageReference mStorRef;
    String userType, theurl;
    private String imageFileName;
    private String imgUrl;
    private String theKey;
    CatLoadingView mView;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_club_profile);

        usertype = findViewById(R.id.edit_user_type);
        newUserName = findViewById(R.id.etNameUpdate);
        newUserAdvisor = findViewById(R.id.etAdvisorUpdate);
        newUserEmail = findViewById(R.id.etEmailUpdate);
        newUserDesc = findViewById(R.id.etDescUpdate);
        imgView = findViewById(R.id.ivProfileUpdate);
        toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        toolbar.setTitle("Update Club Profile");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        save = findViewById(R.id.btnSave);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        mDataRef = firebaseDatabase.getReference();
        mStorage = FirebaseStorage.getInstance();
        mStorRef = mStorage.getReference();
        mDataRef = mDataRef.child("Clubs").child(firebaseAuth.getCurrentUser().getUid());
        storageRef = storage.getReferenceFromUrl("gs://myfyp-25f5d.appspot.com/images");

        mDataRef2 = firebaseDatabase.getReference();
        mDataRef2 = mDataRef2.child("messages");
        mDataRef3 = firebaseDatabase.getReference();
        mDataRef3 = mDataRef3.child("join_list").child("members");

        mDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ListOfClubs listOfClubs = dataSnapshot.getValue(ListOfClubs.class);
                usertype.setText(listOfClubs.getUserType());
                newUserName.setText(listOfClubs.getName());
                newUserAdvisor.setText(listOfClubs.getAdvisor());
                newUserEmail.setText(listOfClubs.getEmail());
                newUserDesc.setText(listOfClubs.getDesc());
                theurl = listOfClubs.getImage();
                Glide.with(UpdateClubProfile.this).load(theurl).into(imgView);
                userType = listOfClubs.getUserType();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(UpdateClubProfile.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mView = new CatLoadingView();
                mView.show(getSupportFragmentManager(), "");
                uploadItem();
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

    private void uploadItem() {
        this.uploadImageToFirebase();

    }
//    }

    public void onSuccessfulSave() {
        Toast.makeText(UpdateClubProfile.this, "Successfully uploaded item.", Toast.LENGTH_LONG).show();
        save.setEnabled(true);
        mView.dismiss();
        finish();
        startActivity(new Intent(UpdateClubProfile.this, ClubProfileActivity.class));
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
                    this.imgView.setImageURI(selectedImage);
                }
                break;
        }
    }

    private void uploadImageToFirebase() {
        // Get the data from an ImageView as bytes
        this.imgView.setDrawingCacheEnabled(true);
        this.imgView.buildDrawingCache();
        Bitmap bitmap = this.imgView.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
//        StorageReference mountainsRef = storageRef.child("myimagename.jpg");
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
                Log.d("*****url", "" + imgUrl);


                final String name = newUserName.getText().toString();
                String adv = newUserAdvisor.getText().toString();
                String email = newUserEmail.getText().toString();
                String desc = newUserDesc.getText().toString();
                String type = userType;
                String myuid = firebaseAuth.getCurrentUser().getUid();
                int position = 0;

                save.setEnabled(false);
                listOfClubs.setImage(imgUrl);

//                final ProgressDialog progDialog = new ProgressDialog(UpdateClubProfile.this,
//                        R.style.Theme_AppCompat_DayNight_NoActionBar);
//
//                progDialog.setIndeterminate(true);
//                progDialog.setMessage("Uploading....");
//                progDialog.show();

                final ListOfClubs listOfClubs = new ListOfClubs(name, imgUrl, adv, desc, type, myuid, email);

                mDataRef3.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postsnap : dataSnapshot.getChildren()){
                            for (DataSnapshot nextsnap : postsnap.getChildren()){
                                if(nextsnap.child("clubname").getValue(String.class).equals(firebaseAuth.getCurrentUser().getDisplayName())){
                                    mDataRef3.child(postsnap.getKey()).child(nextsnap.getKey()).child("clubname").setValue(name);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                mDataRef2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postsnap : dataSnapshot.getChildren()){
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

                        final Runnable uploadTask = new Runnable() {
                            @Override
                            public void run() {
                                mDataRef.setValue(listOfClubs);
//                                progDialog.dismiss();
                            }
                        };
                        new Thread(uploadTask).start();
                        onSuccessfulSave();                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }
}