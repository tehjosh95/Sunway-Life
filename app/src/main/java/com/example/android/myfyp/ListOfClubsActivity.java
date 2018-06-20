package com.example.android.myfyp;

import android.app.Dialog;
import android.content.ClipData;
import android.view.Menu;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.content.Intent;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;
import android.widget.AdapterView.OnItemClickListener;

import com.bumptech.glide.Glide;
import com.firebase.client.FirebaseError;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListOfClubsActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<ListOfClubs> AllClubsList;

    Toolbar toolbar;
    private EditText mSearchField;
    private ImageButton mSearchBtn, filter_btn;
    private ClubListAdapter adapter;
    private Dialog rankDialog;
    private TextView textReminder;
    private RecyclerView mResultList;
//    ArrayList<String> selectedItems;
//    ArrayList<Integer> checked;

    String selectedItems= "";
    int checked = -1;
    private DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_clubs);

        AllClubsList = new ArrayList<>();
        mUserDatabase = FirebaseDatabase.getInstance().getReference("Clubs");
//        selectedItems=new ArrayList<String>();
//        checked=new ArrayList<Integer>() ;

        filter_btn = findViewById(R.id.filter_btn);
        toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        toolbar.setTitle("List of Clubs");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        textReminder = findViewById(R.id.textReminder);
        mSearchField = (EditText) findViewById(R.id.search_field);
        mSearchBtn = (ImageButton) findViewById(R.id.search_btn);
        mSearchBtn.setVisibility(View.GONE);
        recyclerView = (RecyclerView) findViewById(R.id.result_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUserDatabase.addValueEventListener(valueEventListener);

        mSearchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mSearchBtn.performClick();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        filter_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rankDialog = new Dialog(ListOfClubsActivity.this, R.style.FullHeightDialog);
                rankDialog.setContentView(R.layout.checkbox2);
                rankDialog.setCancelable(true);
                final ListView chl=(ListView) rankDialog.findViewById(R.id.checkable_list);
                chl.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                String[] items={"student leadership bodies","student volunteer groups","sports clubs","martial arts clubs","international student body","religious", "cultural", "arts and music", "business", "accounting and finance", "uniform or affiliate", "general"};
                ArrayAdapter<String> aa=new ArrayAdapter<String>( rankDialog.getContext(),R.layout.checkbox, R.id.txt_title,items);
                chl.setAdapter(aa);

                if(checked != -1) {
                    chl.setItemChecked(checked, true);
                }

                chl.setOnItemClickListener(new OnItemClickListener(){
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // selected item
                        String selectedItem = ((TextView) view).getText().toString();
                        Log.d("***position", "" + position);
                        if(!selectedItems.equals(selectedItem)) {
                            selectedItems = selectedItem;
                            checked = position;
                        }else{
                            selectedItems = "";
                            checked = -1;
                            chl.setItemChecked(position, false);
                        }
                        mSearchField.setText("");
                        mUserDatabase.addListenerForSingleValueEvent(valueEventListener);
                    }
                });

                rankDialog.show();
            }
        });

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchText = mSearchField.getText().toString();

                if (searchText.length() > 0) {
                    firebaseUserSearch(searchText);
                    Log.d("more than 0", "more than 0");
                } else {
                    Log.d("less than 0", "less than 0");
                    AllClubsList.clear();
                    mUserDatabase.addListenerForSingleValueEvent(valueEventListener);
                }
            }
        });
    }

    private void firebaseUserSearch(String searchText) {

//        Toast.makeText(ListOfClubsActivity.this, "Started Search", Toast.LENGTH_LONG).show();
        AllClubsList.clear();
            Query firebaseSearchQuery = mUserDatabase.orderByChild("name").startAt(searchText).endAt(searchText + "\uf8ff");
            firebaseSearchQuery.addListenerForSingleValueEvent(valueEventListener);
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            AllClubsList.clear();
            Log.d("***triggered", "triggered");
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                ListOfClubs ItemModel = snapshot.getValue(ListOfClubs.class);
                Boolean flag = false;
                String filter = "";
//                AllClubsList.add(ItemModel);
////                Log.d("***", "three");
//                adapter = new ClubListAdapter(ListOfClubsActivity.this, AllClubsList);
//                recyclerView.setAdapter(adapter);
                if (ItemModel.getCategory().equals(selectedItems)) {
                    flag = true;
                    filter = selectedItems;
                }

                Log.d("***selectedItem", "" + selectedItems);

                if (flag) {
                    if (ItemModel.getCategory().toString().equals(filter)) {
                        AllClubsList.add(ItemModel);
                        Log.d("***", "one");
                    }
                } else if (!flag && !selectedItems.equals("")) {
                    Log.d("***", "two");
                } else if (!flag && selectedItems.equals("")) {
                    AllClubsList.add(ItemModel);
                    Log.d("***", "three");
                }
            }

            adapter = new ClubListAdapter(ListOfClubsActivity.this, AllClubsList);
            recyclerView.setAdapter(adapter);

            if (AllClubsList.size() > 0){
                recyclerView.setVisibility(View.VISIBLE);
                textReminder.setVisibility(View.GONE);
            }else{
                textReminder.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }

            recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(ListOfClubsActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View childView, int position) {
                    ListOfClubs listOfClubs = AllClubsList.get(position);
                    Intent intent = new Intent(ListOfClubsActivity.this, ListOfClubsView.class);
                    intent.putExtra("isname", listOfClubs.getName());
                    intent.putExtra("isadvisor", listOfClubs.getAdvisor());
                    intent.putExtra("isemail", listOfClubs.getEmail());
                    intent.putExtra("isdesc", listOfClubs.getDesc());
                    intent.putExtra("isimg", listOfClubs.getImage());
                    intent.putExtra("isuid", listOfClubs.getMyUid());
                    intent.putExtra("iscategory", listOfClubs.getCategory());
                    intent.putExtra("fromchat", 0);
                    Log.d("^^^^^^^", "" + listOfClubs.getMyUid());
                    startActivity(intent);
                }

                @Override
                public void onItemLongPress(View childView, int position) {

                }
            }));
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

//    ChildEventListener childEventListener = new ChildEventListener() {
//        @Override
//        public void onChildAdded(DataSnapshot child, String previousChild) {
//            Log.d("***triggered", "triggered");
//            ListOfClubs ItemModel = child.getValue(ListOfClubs.class);
//            Boolean flag = false;
//            String filter = "";
//            AllClubsList.add(ItemModel);
//            Log.d("***", "three");
//            adapter = new ClubListAdapter(ListOfClubsActivity.this, AllClubsList);
//            recyclerView.setAdapter(adapter);
//            adapter.notifyDataSetChanged();
////                if (ItemModel.getCategory().equals(selectedItems)){
////                    flag = true;
////                    filter = selectedItems;
////                }
//
////            if (flag && selectedItems.equals("")){
////                if(ItemModel.getCategory().toString().equals(filter)) {
////                    AllClubsList.add(ItemModel);
////                    Log.d("***", "one");
////                    adapter = new ClubListAdapter(ListOfClubsActivity.this, AllClubsList);
////                    recyclerView.setAdapter(adapter);
////                    adapter.notifyDataSetChanged();
////                }
////            }else if(!flag && !selectedItems.equals("")) {
////                Log.d("***", "two");
////                adapter = new ClubListAdapter(ListOfClubsActivity.this, AllClubsList);
////                recyclerView.setAdapter(adapter);
////                adapter.notifyDataSetChanged();
////            }else if(!flag && selectedItems.equals("")){
////                AllClubsList.add(ItemModel);
////                Log.d("***", "three");
////                adapter = new ClubListAdapter(ListOfClubsActivity.this, AllClubsList);
////                recyclerView.setAdapter(adapter);
////                adapter.notifyDataSetChanged();
////            }
//
//
//
//            recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(ListOfClubsActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
//                @Override
//                public void onItemClick(View childView, int position) {
//                    ListOfClubs listOfClubs = AllClubsList.get(position);
//                    Intent intent = new Intent(ListOfClubsActivity.this, ListOfClubsView.class);
//                    intent.putExtra("isname", listOfClubs.getName());
//                    intent.putExtra("isadvisor", listOfClubs.getAdvisor());
//                    intent.putExtra("isemail", listOfClubs.getEmail());
//                    intent.putExtra("isdesc", listOfClubs.getDesc());
//                    intent.putExtra("isimg", listOfClubs.getImage());
//                    intent.putExtra("isuid", listOfClubs.getMyUid());
//                    intent.putExtra("iscategory", listOfClubs.getCategory());
//                    intent.putExtra("fromchat", 0);
//                    Log.d("^^^^^^^", "" + listOfClubs.getMyUid());
//                    startActivity(intent);
//                }
//
//                @Override
//                public void onItemLongPress(View childView, int position) {
//
//                }
//            }));
//        }
//
//        public void onChildRemoved(DataSnapshot snapshot) {
//        }
//
//        public void onChildChanged(DataSnapshot snapshot, String previousChild) {
//        }
//
//        public void onChildMoved(DataSnapshot snapshot, String previousChild) {
//        }
//
//        @Override
//        public void onCancelled(DatabaseError databaseError) {
//            System.out.println("The read failed: " + databaseError.getMessage());
//        }
//    };

}
