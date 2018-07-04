package com.example.android.myfyp;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.roger.catloadinglibrary.CatLoadingView;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EditActivity extends AppCompatActivity {
    private clubModel ClubModel = new clubModel();
    private Button editButton;
    private ImageView profilePic;
    private EditText item_name, item_description, item_date, item_start_time, item_end_time, item_fee_member, item_fee_nonmember, item_venue;
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
    CatLoadingView mView;
    Toolbar toolbar;
    Calendar myCalendar = Calendar.getInstance();
    int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
    int minute = myCalendar.get(Calendar.MINUTE);
    Boolean thefinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        mDatabase = FirebaseDatabase.getInstance();
        mDataRef = mDatabase.getReference();
        mStorage = FirebaseStorage.getInstance();
        mStorRef = mStorage.getReference();
        mAuth = FirebaseAuth.getInstance();

        toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        toolbar.setTitle("Edit Events");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        profilePic = findViewById(R.id.image_preview);
        item_name = findViewById(R.id.item_name);
        item_description = findViewById(R.id.item_description);
        item_date = findViewById(R.id.item_date);
        item_start_time = findViewById(R.id.item_start_time);
        item_end_time = findViewById(R.id.item_end_time);
        item_fee_member = findViewById(R.id.item_fee_member);
        item_fee_nonmember = findViewById(R.id.item_fee_nonmember);
        item_venue = findViewById(R.id.item_venue);
        editButton = findViewById(R.id.edit_button);
        fabbb = (FloatingActionButton) findViewById(R.id.fab);

        Intent startingIntent = getIntent();
        String theUrl = startingIntent.getStringExtra("theurl");
        String theName = startingIntent.getStringExtra("thename");
        String theDesc = startingIntent.getStringExtra("thedesc");
        String theDate = startingIntent.getStringExtra("thedate");
        String theStarttime = startingIntent.getStringExtra("thestarttime");
        String theEndtime = startingIntent.getStringExtra("theendtime");
        String theMemberfee = startingIntent.getStringExtra("thememberfee");
        String theNonmemberfee = startingIntent.getStringExtra("thenonmemberfee");
        String theVenue = startingIntent.getStringExtra("thevenue");
        thefinish = startingIntent.getBooleanExtra("theisfinish", false);

        String theOwner = startingIntent.getStringExtra("theowner");
        theKey = startingIntent.getStringExtra("thekey");

        Glide.with(this).load(theUrl).thumbnail(0.1f).into(profilePic);
        item_name.setText(theName);
        item_description.setText(theDesc);
        item_date.setText(theDate);
        item_start_time.setText(theStarttime);
        item_end_time.setText(theEndtime);
        item_fee_member.setText(theMemberfee);
        item_fee_nonmember.setText(theNonmemberfee);
        item_venue.setText(theVenue);

        mUserRef = mDatabase.getReference();
        mUserRef = mUserRef.child("Item Information").child(mAuth.getCurrentUser().getUid());
        storageRef = storage.getReferenceFromUrl("gs://myfyp-25f5d.appspot.com/images");

        final DatePickerDialog.OnDateSetListener dates = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                myCalendar.set(Calendar.YEAR, i);
                myCalendar.set(Calendar.MONTH, i1);
                myCalendar.set(Calendar.DAY_OF_MONTH, i2);

                updateLabel();
            }
        };

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userProfile = dataSnapshot.getValue(UserProfile.class);
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
                mView = new CatLoadingView();
                mView.show(getSupportFragmentManager(), "");
                mView.setCanceledOnTouchOutside(false);
                mView.setCancelable(false);
                uploadItem();
            }
        });

        fabbb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EditActivity.this, SecondActivity.class));
            }
        });

        item_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(EditActivity.this, dates, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        item_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(EditActivity.this,
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
                TimePickerDialog timePickerDialog = new TimePickerDialog(EditActivity.this,
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
        String myFormat = "dd/MM/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        item_date.setText(sdf.format(myCalendar.getTime()));
    }

    private void uploadItem() {
        String name = item_name.getText().toString().trim();
        String desc = item_description.getText().toString().trim();
        String date = item_date.getText().toString().trim();
        String starttime = item_start_time.getText().toString().trim();
        String endtime = item_end_time.getText().toString().trim();
        String feeformember = item_fee_member.getText().toString().trim();
        String feefornonmember = item_fee_nonmember.getText().toString().trim();
        String venue = item_venue.getText().toString().trim();

        if (validate(name, desc, date, starttime, endtime, feeformember, feefornonmember, venue)) {
            this.uploadImageToFirebase();
        } else{
            mView.dismiss();
            Toast.makeText(EditActivity.this, "Please fill in all required data", Toast.LENGTH_SHORT).show();
        }
    }
//    }

    public void onSuccessfulSave() {
        Toast.makeText(EditActivity.this, "Successfully uploaded item.", Toast.LENGTH_LONG).show();
        editButton.setEnabled(true);
        mView.dismiss();
        finish();
        startActivity(new Intent(EditActivity.this, ActivityPosted.class));
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
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                imgUrl = downloadUrl.toString();
                Log.d("*****url", "" + imgUrl);

                String name = item_name.getText().toString().trim().toUpperCase();
                String desc = item_description.getText().toString().trim();
                String date = item_date.getText().toString().trim();
                String starttime = item_start_time.getText().toString().trim();
                String endtime = item_end_time.getText().toString().trim();
                String feeformember = item_fee_member.getText().toString().trim();
                String feefornonmember = item_fee_nonmember.getText().toString().trim();
                String venue = item_venue.getText().toString().trim();

                String owner = mAuth.getCurrentUser().getUid();
                String ownername = mAuth.getCurrentUser().getDisplayName();
                int position = 0;
                Log.d("*****url1", "" + imgUrl);

                editButton.setEnabled(false);
                ClubModel.setItem_name(name);
                ClubModel.setItem_desc(desc);
                ClubModel.setItem_date(date);
                ClubModel.setItem_start_time(starttime);
                ClubModel.setItem_end_time(endtime);
                ClubModel.setFee_for_member(feeformember);
                ClubModel.setFee_for_nonmember(feefornonmember);
                ClubModel.setVenue(venue);

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
                final clubModel ClubModel = new clubModel(name, desc, date, starttime, endtime, feeformember, feefornonmember, venue, ownername, owner, position, imgUrl, imageFileName, theKey, thefinish);

                Log.d("&&&&&&&uid", "" + owner);
                final Runnable uploadTask = new Runnable() {
                    @Override
                    public void run() {
                        mDataRef.child("Item Information").child(theKey).setValue(ClubModel);
                        progDialog.dismiss();
                    }
                };
                new Thread(uploadTask).start();
                onSuccessfulSave();
                Log.d("*****url2", "" + imgUrl);
            }
        });
    }
    public Boolean validate(String i1, String i2, String i3, String i4, String i5, String i6, String i7, String i8){
        if(i1.isEmpty() || i2.isEmpty() || i3.isEmpty() || i4.isEmpty() || i5.isEmpty() || i6.isEmpty() || i7.isEmpty() || i8.isEmpty()){
            return false;
        }else{
            return true;
        }
    }
}
