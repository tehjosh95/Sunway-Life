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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseOptions;
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
import java.util.UUID;

public class AddActivity extends AppCompatActivity {
    private clubModel ClubModel = new clubModel();

    private FirebaseStorage mStorage;
    private StorageReference mStorRef;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDataRef;
    private FirebaseAuth mAuth;
    private static final String TAG = "AddActivity";
    private EditText itemName = null;
    private EditText itemPlace = null;
    private EditText itemPrice = null;
    private FloatingActionButton fab = null;
    private String imgUrl;
    private UserProfile userProfile;
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
        setContentView(R.layout.activity_add);

        mDatabase = FirebaseDatabase.getInstance();
        mDataRef = mDatabase.getReference();
        mStorage = FirebaseStorage.getInstance();
        mStorRef = mStorage.getReference();
        mAuth = FirebaseAuth.getInstance();

        itemName = (EditText) findViewById(R.id.item_name);
        itemPlace = (EditText) findViewById(R.id.item_place);
        itemPrice = (EditText) findViewById(R.id.item_price);
        fab = (FloatingActionButton)findViewById(R.id.fabb);
        btnUploadItem = (AppCompatButton) findViewById(R.id.btn_upload);

        imageView = (ImageView) findViewById(R.id.image_preview);

        mUserRef = mDatabase.getReference();
        mUserRef = mUserRef.child("Item Information").child(mAuth.getCurrentUser().getUid());
        storageRef = storage.getReferenceFromUrl("gs://myfyp-25f5d.appspot.com/images");

        final ProgressDialog progDialog = new ProgressDialog(AddActivity.this,
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

        btnUploadItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadItem();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddActivity.this, SecondActivity.class));
            }
        });
    }

    private void uploadItem() {
        this.uploadImageToFirebase();

    }

    public void onSuccessfulSave() {
        Toast.makeText(AddActivity.this, "Successfully uploaded item.", Toast.LENGTH_LONG).show();
        btnUploadItem.setEnabled(true);
        finish();
    }

    private boolean isInputInvalid(String name, String place, String price) {
        boolean inputInvalid = false;

        if (name.isEmpty()) {
            itemName.setError("Please enter an item name.");
            inputInvalid = true;
        } else {
            itemName.setError(null);
        }
        if (place.isEmpty()) {
            itemPlace.setError("Please fill in a short description of the item.");
            inputInvalid = true;
        } else {
            itemPlace.setError(null);
        }
        if (price.isEmpty()) {
            itemPrice.setError("Please explain how to collect the items.");
            inputInvalid = true;
        } else {
            itemPrice.setError(null);
        }
        return inputInvalid;
    }

    private boolean isNumeric(String s) {
        try {
            Long.parseLong(s);
        } catch (NumberFormatException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    private void updateView(clubModel ClubModel) {
        if (ClubModel != null) {

            imageView.setVisibility(View.VISIBLE);
            itemName.setText(ClubModel.getItem_name());
            itemPlace.setText(ClubModel.getItem_place());
            itemPrice.setText(ClubModel.getItem_price());
        }
    }

    public void onFailedSave() {
        Toast.makeText(AddActivity.this, "Item is not uploaded.Please try again.", Toast.LENGTH_LONG).show();
        btnUploadItem.setEnabled(true);
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
                Log.d("*****url",""+imgUrl);


                String name = itemName.getText().toString().trim();
                String place = itemPlace.getText().toString().trim();
                String price = itemPrice.getText().toString().trim();
                String owner = mAuth.getCurrentUser().getUid();
                int position = 0;
                Log.d("*****url1",""+imgUrl);

//        if (isInputInvalid(name, place, price)) {
//            onFailedSave();
//        } else {
                btnUploadItem.setEnabled(false);
                ClubModel.setItem_name(name);
                ClubModel.setItem_place(place);
                ClubModel.setItem_price(price);
                ClubModel.setItem_owner(owner);
                ClubModel.setItem_position(position);
                ClubModel.setImageLink(imgUrl);
                ClubModel.setImgName(imageFileName);
                ClubModel.setParentkey("");

                final ProgressDialog progDialog = new ProgressDialog(AddActivity.this,
                        R.style.Theme_AppCompat_DayNight_NoActionBar);

                progDialog.setIndeterminate(true);
                progDialog.setMessage("Uploading....");
                progDialog.show();
                final clubModel ClubModel = new clubModel(name, place ,price, owner, position, imgUrl, imageFileName,"");

                Log.d("&&&&&&&uid",""+owner);
                final Runnable uploadTask = new Runnable() {
                    @Override
                    public void run() {
                        mDataRef.child("Item Information").push().setValue(ClubModel);
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
