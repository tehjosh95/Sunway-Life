package com.example.android.myfyp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;


public class ListOfClubsView extends AppCompatActivity {
    private ImageView profilePic;
    private TextView profileName, profileCont, profileDesc;
    private Button EditButton;
    private FloatingActionButton fabbb;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        firebaseAuth = FirebaseAuth.getInstance();

        profilePic = findViewById(R.id.ivimage);
        profileName = findViewById(R.id.tvname);
        profileCont = findViewById(R.id.tvcont);
        profileDesc = findViewById(R.id.tvdesc);
        EditButton = findViewById(R.id.btnEdit);
        fabbb = (FloatingActionButton)findViewById(R.id.fabbb);

        Intent startingIntent = getIntent();
        final String myurl = startingIntent.getStringExtra("isimg");
        final String Name = startingIntent.getStringExtra("isname");
        final String Cont = startingIntent.getStringExtra("iscont");
        final String Desc = startingIntent.getStringExtra("isdesc");
        final String Myuid = startingIntent.getStringExtra("isuid");

//        final String Owner = startingIntent.getStringExtra("myowner");
//        final String myKey = startingIntent.getStringExtra("mykey");

        Glide.with(this).load(myurl).thumbnail(0.1f).into(profilePic);
        profileName.setText(Name);
        profileCont.setText(Cont);
        profileDesc.setText(Desc);
        Log.d("^^^uidview", "" + Myuid);

//        if (!firebaseAuth.getCurrentUser().getUid().equals(Owner)){
//            EditButton.setVisibility(View.GONE);
//        }

        fabbb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ListOfClubsView.this, SecondActivity.class));
            }
        });

        EditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserDetails.username = firebaseAuth.getCurrentUser().getUid();
                UserDetails.chatWith = Myuid;
                UserDetails.name = Name;
                startActivity(new Intent(ListOfClubsView.this, Chat.class));
                finish();
            }
        });

//        EditButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(ListOfClubsView.this, EditActivity.class);
//                intent.putExtra("thename", Name);
//                intent.putExtra("theplace", Place);
//                intent.putExtra("theprice",Price);
//                intent.putExtra("theurl", myurl);
//                intent.putExtra("theowner", Owner);
//                intent.putExtra("thekey", myKey);
//                startActivity(intent);
//            }
//        });
    }
}
