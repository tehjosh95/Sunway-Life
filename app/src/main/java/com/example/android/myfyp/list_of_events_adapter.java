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

public class list_of_events_adapter extends RecyclerView.Adapter<list_of_events_adapter.ViewHolder> {

    private Context mContext;
    private ArrayList<event_list> mList;

    public list_of_events_adapter(Context context, ArrayList<event_list> list) {
        mContext = context;
        mList = list;
    }

    public list_of_events_adapter() {
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        event_list contact = mList.get(position);

        TextView name = holder.name;
        TextView status = holder.status;

        name.setText(contact.getEventName());
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
        }
    }
}
