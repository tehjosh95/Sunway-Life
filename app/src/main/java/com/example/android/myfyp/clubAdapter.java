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

public class clubAdapter extends RecyclerView.Adapter<clubAdapter.ViewHolder>{


    private Context mContext;
    private ArrayList<clubModel> mList;
    public clubAdapter(Context context, ArrayList<clubModel> list){
        mContext = context;
        mList = list;
    }

    public clubAdapter(){
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
    public void onBindViewHolder(ViewHolder holder, int position)  {
        clubModel contact = mList.get(position);

        TextView item_name = holder.item_name;
        TextView item_place = holder.item_place;
        TextView item_price = holder.item_price;
        ImageView item_img = holder.item_image;

        item_name.setText(contact.getItem_name());
        item_place.setText(contact.getItem_place());
        item_price.setText(contact.getItem_price());
        Glide.with(mContext).load(contact.getImageLink()).into(item_img);

    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView item_image;
        public TextView item_name,item_place,item_price;
        View view;


        public ViewHolder(View itemView) {
            super(itemView);
//
            item_image = itemView.findViewById(R.id.rv_item_img);
            item_name = itemView.findViewById(R.id.rv_item_name);
            item_place = itemView.findViewById(R.id.rv_item_place);
            item_price = itemView.findViewById(R.id.rv_item_price);


        }
        @Override
        public void onClick(View v) {
            Intent open = new Intent(mContext,ProfileActivity.class);
            mContext.startActivity(open);
        }
    }
}
