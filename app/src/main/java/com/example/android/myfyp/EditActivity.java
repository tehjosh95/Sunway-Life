package com.example.android.myfyp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class EditActivity extends AppCompatActivity {
    private clubModel ClubModel = new clubModel();
    private Button editButton;
    private ImageView profilePic;
    private EditText profileName, profilePlace, profilePrice;
    private Button EditButton;
    private FloatingActionButton fabbb;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef;
    private DatabaseReference mUserRef;
    private FirebaseStorage mStorage;
    private StorageReference mStorRef;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDataRef;
    private FirebaseAuth mAuth;
    private UserProfile userProfile;
    private String imageFileName;
    private String imgUrl;
    private String theKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        mDatabase = FirebaseDatabase.getInstance();
        mDataRef = mDatabase.getReference();
        mStorage = FirebaseStorage.getInstance();
        mStorRef = mStorage.getReference();
        mAuth = FirebaseAuth.getInstance();


        profilePic = findViewById(R.id.editImg);
        profileName = findViewById(R.id.edit_name);
        profilePlace = findViewById(R.id.edit_place);
        profilePrice = findViewById(R.id.edit_price);
        editButton = findViewById(R.id.edit_button);
        fabbb = (FloatingActionButton)findViewById(R.id.fab);

        Intent startingIntent = getIntent();
        String theUrl = startingIntent.getStringExtra("theurl");
        String theName = startingIntent.getStringExtra("thename");
        String thePlace = startingIntent.getStringExtra("theplace");
        String thePrice = startingIntent.getStringExtra("theprice");
        String theOwner = startingIntent.getStringExtra("theowner");
        theKey = startingIntent.getStringExtra("thekey");

        Glide.with(this).load(theUrl).into(profilePic);
        profileName.setText(theName);
        profilePlace.setText(thePlace);
        profilePrice.setText(thePrice);

        mUserRef = mDatabase.getReference();
        mUserRef = mUserRef.child("Item Information").child(mAuth.getCurrentUser().getUid());
        storageRef = storage.getReferenceFromUrl("gs://myfyp-25f5d.appspot.com/images");

        final ProgressDialog progDialog = new ProgressDialog(EditActivity.this,
                R.style.Theme_AppCompat_DayNight_NoActionBar);
        progDialog.setIndeterminate(true);
        progDialog.setMessage("Loading user data...");
        progDialog.show();
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userProfile = dataSnapshot.getValue(UserProfile.class);
                progDialog.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        mUserRef.addValueEventListener(userListener);

        DatabaseReference databaseReference = mDatabase.getReference().child("Item Information");

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadItem();
            }
        });

        fabbb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EditActivity.this, SecondActivity.class));
            }
        });
    }

    private void uploadItem() {
        this.uploadImageToFirebase();

    }
//    }

    public void onSuccessfulSave() {
        Toast.makeText(EditActivity.this, "Successfully uploaded item.", Toast.LENGTH_LONG).show();
        editButton.setEnabled(true);
        finish();
        startActivity(new Intent(EditActivity.this,ActivityPosted.class));
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
                    this.profilePic.setImageURI(selectedImage);
                }
                break;
        }
    }

    private void uploadImageToFirebase() {
        // Get the data from an ImageView as bytes
        this.profilePic.setDrawingCacheEnabled(true);
        this.profilePic.buildDrawingCache();
        Bitmap bitmap = this.profilePic.getDrawingCache();
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
                Log.d("*****url",""+imgUrl);


                String name = profileName.getText().toString().trim();
                String place = profilePlace.getText().toString().trim();
                String price = profilePrice.getText().toString().trim();
                String owner = mAuth.getCurrentUser().getUid();
                int position = 0;
                Log.d("*****url1",""+imgUrl);

//        if (isInputInvalid(name, place, price)) {
//            onFailedSave();
//        } else {
                editButton.setEnabled(false);
                ClubModel.setItem_name(name);
                ClubModel.setItem_place(place);
                ClubModel.setItem_price(price);
                ClubModel.setItem_owner(owner);
                ClubModel.setItem_position(position);
                ClubModel.setImageLink(imgUrl);
                ClubModel.setImgName(imageFileName);
                ClubModel.setParentkey(theKey);

                final ProgressDialog progDialog = new ProgressDialog(EditActivity.this,
                        R.style.Theme_AppCompat_DayNight_NoActionBar);

                progDialog.setIndeterminate(true);
                progDialog.setMessage("Uploading....");
                progDialog.show();
                final clubModel ClubModel = new clubModel(name, place ,price, owner, position, imgUrl, imageFileName, theKey);

                Log.d("&&&&&&&uid",""+owner);
                final Runnable uploadTask = new Runnable() {
                    @Override
                    public void run() {
                        mDataRef.child("Item Information").child(theKey).setValue(ClubModel);
                        progDialog.dismiss();
                    }
                };
                new Thread(uploadTask).start();
                onSuccessfulSave();
                Log.d("*****url2",""+imgUrl);
            }
        });
    }
}
