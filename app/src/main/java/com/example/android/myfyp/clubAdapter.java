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

public class clubAdapter extends RecyclerView.Adapter<clubAdapter.ViewHolder> {


    private Context mContext;
    private ArrayList<clubModel> mList;

    public clubAdapter(Context context, ArrayList<clubModel> list) {
        mContext = context;
        mList = list;
    }

    public clubAdapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.items, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        clubModel contact = mList.get(position);

        TextView item_name = holder.item_name;
        TextView item_desc = holder.item_desc;
        TextView item_price = holder.item_price;
        TextView item_price2 = holder.item_price2;
        ImageView item_img = holder.item_image;

        item_name.setText(contact.getItem_name());
        item_desc.setText(contact.getItem_desc());
        item_price.setText(contact.getItem_date());
        item_price2.setText(contact.getItem_start_time());
        Glide.with(mContext).load(contact.getImageLink()).thumbnail(0.1f).into(item_img);

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView item_image;
        public TextView item_name, item_desc, item_price, item_price2;
        View view;


        public ViewHolder(View itemView) {
            super(itemView);

            item_price2 = itemView.findViewById(R.id.rv_item_price2);
            item_image = itemView.findViewById(R.id.rv_item_img);
            item_name = itemView.findViewById(R.id.rv_item_name);
            item_desc = itemView.findViewById(R.id.rv_item_desc);
            item_price = itemView.findViewById(R.id.rv_item_price);


        }

        @Override
        public void onClick(View v) {
            ;
        }
    }
}
