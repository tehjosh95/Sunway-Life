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

public class PendingListAdapter extends RecyclerView.Adapter<PendingListAdapter.ViewHolder>{

    private Context mContext;
    private ArrayList<join_list> mList;
    public PendingListAdapter(Context context, ArrayList<join_list> list){
        mContext = context;
        mList = list;
    }

    public PendingListAdapter(){
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.pending_layout, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)  {
        join_list contact = mList.get(position);

        TextView name = holder.name;
        TextView status = holder.status;

        name.setText(contact.getMyname());
        status.setText(contact.getStatus());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView name, status;
        View view;

        public ViewHolder(View itemView) {
            super(itemView);
//
            name = itemView.findViewById(R.id.name_text);
            status = itemView.findViewById(R.id.status_text);

        }
        @Override
        public void onClick(View v) {
            Intent open = new Intent(mContext,ListOfClubsActivity.class);
            mContext.startActivity(open);
        }
    }
}
