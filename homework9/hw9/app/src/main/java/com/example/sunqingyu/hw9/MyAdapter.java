package com.example.sunqingyu.hw9;

import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.CustomViewHolder> {
    private List<FeedItem> feedItemList;
    private Context mContext;

    public MyAdapter(Context context, List feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int kkk) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_pic, parent,false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {

//        customViewHolder.itemView.setTag(feedItemList.get(i));
        FeedItem feedItem = feedItemList.get(i);
 //       Glide.with(mContext).load(feedItem.getThumbnail()).into(customViewHolder.imageView);
        customViewHolder.imageView.setImageBitmap(feedItem.getThumbnail());

        //Render image using Picasso library
//        if (!TextUtils.isEmpty(feedItem.getThumbnail())) {
//            Picasso.get().load(feedItem.getThumbnail())
//                   .into(customViewHolder.imageView);
////            Picasso.get().load(feedItem.getThumbnail()).into(customViewHolder.imageView);
////        }

        //Setting text view title
        //customViewHolder.textView.setText(Html.fromHtml(feedItem.getTitle()));
    }

    @Override
    public int getItemCount() {
        return feedItemList.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public CustomViewHolder(View view) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.thumbnail);
        }
    }
}
