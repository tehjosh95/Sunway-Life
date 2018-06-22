package com.example.android.myfyp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

public class UpdateProfile extends AppCompatActivity {
    private UserProfile userProfile = new UserProfile();
    private ImageView ivProfileUpdate;
    private EditText newUserName, newUserCourse, newUserPhone, etIdupdate;
    private Button save;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDataRef, mDataRef2, mDataRef3;
    private String imageFileName;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseStorage mStorage;
    private StorageReference storageRef;
    private StorageReference mStorRef;
    String userType;
    Toolbar toolbar;
    CatLoadingView mView;
    private String imgUrl;
    private TextInputLayout edit_user_contact_text_input_layout2, edit_user_contact_text_input_layout3, edit_user_contact_text_input_layout4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        edit_user_contact_text_input_layout2 = findViewById(R.id.edit_user_contact_text_input_layout2);
        edit_user_contact_text_input_layout3 = findViewById(R.id.edit_user_contact_text_input_layout3);
        edit_user_contact_text_input_layout4 = findViewById(R.id.edit_user_contact_text_input_layout4);

        etIdupdate = findViewById(R.id.etIdUpdate);
        newUserName = findViewById(R.id.etNameUpdate);
        newUserCourse = findViewById(R.id.etCourseUpdate);
        newUserPhone = findViewById(R.id.etPhoneUpdate);
        save = findViewById(R.id.btnSave);
        ivProfileUpdate = findViewById(R.id.ivProfileUpdate);

        mStorage = FirebaseStorage.getInstance();
        mStorRef = mStorage.getReference();
        storageRef = storage.getReferenceFromUrl("gs://myfyp-25f5d.appspot.com/images");

        toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        toolbar.setTitle("Update Profile");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        mDataRef = firebaseDatabase.getReference();
        mDataRef = mDataRef.child("Users").child(firebaseAuth.getCurrentUser().getUid());
        mDataRef2 = firebaseDatabase.getReference();
        mDataRef2 = mDataRef2.child("messages");
        mDataRef3 = firebaseDatabase.getReference();
        mDataRef3 = mDataRef3.child("join_list").child("members");

        if(firebaseAuth.getCurrentUser().getUid().equals("XHR842kZD3cTZTwz7nM5LWJESW72")){
            edit_user_contact_text_input_layout2.setVisibility(View.GONE);
            edit_user_contact_text_input_layout3.setHint("Description");
            newUserCourse.setLines(4);
            edit_user_contact_text_input_layout4.setVisibility(View.GONE);
        }

        mDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                etIdupdate.setText(userProfile.getStudentID());
                newUserName.setText(userProfile.getStudentName());
                newUserCourse.setText(userProfile.getStudentCourse());
                newUserPhone.setText(userProfile.getStudentPhone());
                userType = userProfile.getUserType();
                Glide.with(UpdateProfile.this).load(userProfile.getImgurl()).thumbnail(0.1f).into(ivProfileUpdate);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(UpdateProfile.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mView = new CatLoadingView();
                mView.show(getSupportFragmentManager(), "");
                mView.setCanceledOnTouchOutside(false);
                mView.setCancelable(false);

                final String id = etIdupdate.getText().toString();
                final String name = newUserName.getText().toString();
                String course = newUserCourse.getText().toString();
                String phone = newUserPhone.getText().toString();
                String type = userType;
                String imgurl = "";
                if(validate(id, name, course, phone)){
                    uploadImageToFirebase();
                }else{
                    mView.dismiss();
                    Toast.makeText(UpdateProfile.this, "Please fill in all required data", Toast.LENGTH_SHORT).show();
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
                    this.ivProfileUpdate.setImageURI(selectedImage);
                }
                break;
        }
    }

    public Boolean validate(String i1, String i2, String i3, String i4){
        if(i1.isEmpty() || i2.isEmpty() || i3.isEmpty() || i4.isEmpty()){
            return false;
        }else{
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void uploadImageToFirebase() {
        // Get the data from an ImageView as bytes
        this.ivProfileUpdate.setDrawingCacheEnabled(true);
        this.ivProfileUpdate.buildDrawingCache();
        Bitmap bitmap = this.ivProfileUpdate.getDrawingCache();
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
                mView.dismiss();
                Log.i("MainActivity", "Upload failed");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                imgUrl = downloadUrl.toString();
                Log.d("*****url", "" + imgUrl);

                final String id = etIdupdate.getText().toString();
                final String name = newUserName.getText().toString();
                String course = newUserCourse.getText().toString();
                String phone = newUserPhone.getText().toString();
                String type = userType;

                save.setEnabled(false);
                userProfile.setImgurl(imgUrl);

                final UserProfile userProfile = new UserProfile(id, name, course, phone, type, imgUrl);

                final Runnable uploadTask = new Runnable() {
                    @Override
                    public void run() {
                        mDataRef.setValue(userProfile);
                    }
                };
                new Thread(uploadTask).start();
                onSuccessfulSave();
            }
        });
    }

    public void onSuccessfulSave() {
        Toast.makeText(UpdateProfile.this, "Successfully uploaded item.", Toast.LENGTH_LONG).show();
        save.setEnabled(true);
        mView.dismiss();
        finish();
    }
}
