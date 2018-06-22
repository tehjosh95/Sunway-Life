package com.example.android.myfyp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.roger.catloadinglibrary.CatLoadingView;

public class UpdatePassword extends AppCompatActivity {

    private Button update;
    private EditText newPassword;
    Toolbar toolbar;
    CatLoadingView mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        update = findViewById(R.id.btnUpdatePassword);
        newPassword = findViewById(R.id.etNewPassword);

        toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        toolbar.setTitle("Update Password");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userPasswordNew = newPassword.getText().toString().trim();
                if (!newPassword.getText().toString().equals("")) {
                    if (newPassword.getText().length() >= 6) {
                        mView = new CatLoadingView();
                        mView.show(getSupportFragmentManager(), "");
                        mView.setCanceledOnTouchOutside(false);
                        mView.setCancelable(false);
                        firebaseUser.updatePassword(userPasswordNew).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(UpdatePassword.this, "Password Changed", Toast.LENGTH_SHORT).show();
                                    mView.dismiss();
                                    finish();
                                } else {
                                    mView.dismiss();
                                    newPassword.setText("");
                                    Toast.makeText(UpdatePassword.this, "Password Update Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        newPassword.setText("");
                        Toast.makeText(UpdatePassword.this, "Password length cannot be less than 6 characters", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    newPassword.setText("");
                    Toast.makeText(UpdatePassword.this, "Please fill in all required data", Toast.LENGTH_SHORT).show();
                }
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
}
