package com.example.android.myfyp;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class AddActivity extends AppCompatActivity {
    private clubModel ClubModel = new clubModel();
    Toolbar toolbar;
    private FirebaseStorage mStorage;
    private StorageReference mStorRef;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDataRef;
    private FirebaseAuth mAuth;
    private TextInputLayout date_layout, starttime_layout, endtime_layout;
    private EditText item_name, item_description, item_date, item_start_time, item_end_time, item_fee_member, item_fee_nonmember, item_venue;
    private static final String TAG = "AddActivity";
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
    Calendar myCalendar = Calendar.getInstance();
    int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
    int minute = myCalendar.get(Calendar.MINUTE);
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

        toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        toolbar.setTitle("Add Events");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        date_layout = findViewById(R.id.date_layout);
        starttime_layout = findViewById(R.id.starttime_layout);
        endtime_layout = findViewById(R.id.endtime_layout);

        item_name = findViewById(R.id.item_name);
        item_description = findViewById(R.id.item_description);
        item_date = findViewById(R.id.item_date);
        item_start_time = findViewById(R.id.item_start_time);
        item_end_time = findViewById(R.id.item_end_time);
        item_fee_member = findViewById(R.id.item_fee_member);
        item_fee_nonmember = findViewById(R.id.item_fee_nonmember);
        item_venue = findViewById(R.id.item_venue);

        final DatePickerDialog.OnDateSetListener dates = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                myCalendar.set(Calendar.YEAR, i);
                myCalendar.set(Calendar.MONTH, i2);
                myCalendar.set(Calendar.DAY_OF_MONTH, i2);
                updateLabel();
            }
        };

        fab = (FloatingActionButton) findViewById(R.id.fabb);
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

        item_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(AddActivity.this, dates, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        item_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                item_start_time.setText(hourOfDay + ":" + minute);
                            }
                        }, hour, minute, false);
                timePickerDialog.show();
            }
        });

        item_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                item_end_time.setText(hourOfDay + ":" + minute);
                            }
                        }, hour, minute, false);
                timePickerDialog.show();
            }
        });

    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
        item_date.setText(sdf.format(myCalendar.getTime()));
    }

    private void uploadItem() {
        this.uploadImageToFirebase();

    }

    public void onSuccessfulSave() {
        Toast.makeText(AddActivity.this, "Successfully uploaded item.", Toast.LENGTH_LONG).show();
        btnUploadItem.setEnabled(true);
        finish();
    }

//    private boolean isInputInvalid(String name, String place, String price) {
//        boolean inputInvalid = false;
//
//        if (name.isEmpty()) {
//            itemName.setError("Please enter an item name.");
//            inputInvalid = true;
//        } else {
//            itemName.setError(null);
//        }
//        if (place.isEmpty()) {
//            itemPlace.setError("Please fill in a short description of the item.");
//            inputInvalid = true;
//        } else {
//            itemPlace.setError(null);
//        }
//        if (price.isEmpty()) {
//            itemPrice.setError("Please explain how to collect the items.");
//            inputInvalid = true;
//        } else {
//            itemPrice.setError(null);
//        }
//        return inputInvalid;
//    }

//    private boolean isNumeric(String s) {
//        try {
//            Long.parseLong(s);
//        } catch (NumberFormatException e) {
//            return false;
//        } catch (Exception e) {
//            return false;
//        }
//        return true;
//    }

//    private void updateView(clubModel ClubModel) {
//        if (ClubModel != null) {
//
//            imageView.setVisibility(View.VISIBLE);
//            itemName.setText(ClubModel.getItem_name());
//            itemPlace.setText(ClubModel.getItem_place());
//            itemPrice.setText(ClubModel.getItem_price());
//        }
//    }
//
//    public void onFailedSave() {
//        Toast.makeText(AddActivity.this, "Item is not uploaded.Please try again.", Toast.LENGTH_LONG).show();
//        btnUploadItem.setEnabled(true);
//    }

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
                Log.d("*****url", "" + imgUrl);


                String name = item_name.getText().toString().trim();
                String desc = item_description.getText().toString().trim();
                String date = item_date.getText().toString().trim();
                String starttime = item_start_time.getText().toString().trim();
                String endtime = item_end_time.getText().toString().trim();
                String memberfee = item_fee_member.getText().toString().trim();
                String nonmemberfee = item_fee_nonmember.getText().toString().trim();
                String venue = item_venue.getText().toString().trim();

                String owner = mAuth.getCurrentUser().getUid();
                String ownername = mAuth.getCurrentUser().getDisplayName();
                int position = 0;
                Log.d("*****url1", "" + imgUrl);

//        if (isInputInvalid(name, place, price)) {
//            onFailedSave();
//        } else {
                btnUploadItem.setEnabled(false);
                ClubModel.setItem_name(name);
                ClubModel.setItem_desc(desc);
                ClubModel.setItem_date(date);
                ClubModel.setItem_start_time(starttime);
                ClubModel.setItem_end_time(endtime);
                ClubModel.setFee_for_member(memberfee);
                ClubModel.setFee_for_nonmember(nonmemberfee);
                ClubModel.setVenue(venue);

                ClubModel.setItem_owner(owner);
                ClubModel.setItem_position(position);
                ClubModel.setImageLink(imgUrl);
                ClubModel.setImgName(imageFileName);
                ClubModel.setParentkey("");
                ClubModel.setOwnerName(ownername);

                final ProgressDialog progDialog = new ProgressDialog(AddActivity.this,
                        R.style.Theme_AppCompat_DayNight_NoActionBar);

                progDialog.setIndeterminate(true);
                progDialog.setMessage("Uploading....");
                progDialog.show();
                final clubModel ClubModel = new clubModel(name, desc, date, starttime, endtime, memberfee, nonmemberfee, venue, ownername, owner, position, imgUrl, imageFileName, "");

                Log.d("&&&&&&&uid", "" + owner);
                final Runnable uploadTask = new Runnable() {
                    @Override
                    public void run() {
                        mDataRef.child("Item Information").push().setValue(ClubModel);
                        progDialog.dismiss();
                    }
                };
                new Thread(uploadTask).start();
                onSuccessfulSave();
                Log.d("*****url2", "" + imgUrl);
            }
        });
    }
}
