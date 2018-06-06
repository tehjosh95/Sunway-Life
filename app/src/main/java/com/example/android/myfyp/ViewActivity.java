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


public class ViewActivity extends AppCompatActivity {
    private ImageView profilePic;
    private TextView profileName, profilePlace, profilePrice;
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
        profilePlace = findViewById(R.id.tvcont);
        profilePrice = findViewById(R.id.tvdesc);
        EditButton = findViewById(R.id.btnEdit);
        fabbb = (FloatingActionButton) findViewById(R.id.fabbb);

        Intent startingIntent = getIntent();
        final String myurl = startingIntent.getStringExtra("myurl");
        final String Name = startingIntent.getStringExtra("myname");
        final String Place = startingIntent.getStringExtra("myplace");
        final String Price = startingIntent.getStringExtra("myprice");
        final String Owner = startingIntent.getStringExtra("myowner");
        final String myKey = startingIntent.getStringExtra("mykey");

        Glide.with(this).load(myurl).thumbnail(0.1f).into(profilePic);
        profileName.setText(Name);
        profilePlace.setText(Place);
        profilePrice.setText(Price);

        if (!firebaseAuth.getCurrentUser().getUid().equals(Owner)) {
            EditButton.setVisibility(View.GONE);
        }


        Log.d("****itemname", "" + Name);
        Log.d("****itemplace", "" + Place);
        Log.d("****itemprice", "" + Price);

        fabbb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ViewActivity.this, SecondActivity.class));
            }
        });

        EditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewActivity.this, EditActivity.class);
                intent.putExtra("thename", Name);
                intent.putExtra("theplace", Place);
                intent.putExtra("theprice", Price);
                intent.putExtra("theurl", myurl);
                intent.putExtra("theowner", Owner);
                intent.putExtra("thekey", myKey);
                startActivity(intent);
            }
        });
    }
}
