package com.example.android.myfyp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ClubListAdapter extends RecyclerView.Adapter<ClubListAdapter.ViewHolder>{


    private Context mContext;
    private ArrayList<ListOfClubs> mList;
    public ClubListAdapter(Context context, ArrayList<ListOfClubs> list){
        mContext = context;
        mList = list;
    }

    public ClubListAdapter(){
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.list_layout, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)  {
        ListOfClubs contact = mList.get(position);

        TextView name = holder.name;
        TextView status = holder.status;
        ImageView image = holder.image;

        name.setText(contact.getName());
        status.setText(contact.getDesc());
        Glide.with(mContext).load(contact.getImage()).into(image);
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView image;
        public TextView name, status;
        View view;


        public ViewHolder(View itemView) {
            super(itemView);
//
            name = itemView.findViewById(R.id.name_text);
            status = itemView.findViewById(R.id.status_text);
            image = itemView.findViewById(R.id.profile_image);


        }
        @Override
        public void onClick(View v) {
            Intent open = new Intent(mContext,ListOfClubsActivity.class);
            mContext.startActivity(open);
        }
    }
}
