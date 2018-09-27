package com.example.sunqingyu.hw9;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MyReviewAdpater extends RecyclerView.Adapter<MyReviewAdpater.ViewHolder>{
    private Context context;
    private List<ReviewUtils> reviewUtils;
    public MyReviewAdpater(Context context, List reviewUtils) {
        this.context = context;
        this.reviewUtils = reviewUtils;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_review, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int i) {

        holder.itemView.setTag(reviewUtils.get(i));

        ReviewUtils ru = reviewUtils.get(i);

        holder.lReviewName.setText(ru.getReviewName());
        holder.lReviewText.setText(ru.getReviewText());
        holder.lReviewRate.setRating(Float.parseFloat(ru.getReviewRate()));
//        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
//        cal.setTimeInMillis(Long.parseLong(ru.getReviewDate()) * 1000L);
//        String date = DateFormat.format("yyyy-MM-dd hh:mm:ss", cal).toString();
//        holder.lReviewDate.setText(date);

        holder.lReviewDate.setText(ru.getReviewDate());

        Picasso.get().load(ru.getReviewPhoto()).into(holder.lReviewPhoto);
    }

    @Override
    public int getItemCount() {
        return reviewUtils.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView lReviewName,lReviewText,lReviewDate;
        public ImageView lReviewPhoto;
        public RatingBar lReviewRate;

        public ViewHolder(View itemView) {
            super(itemView);

            lReviewDate = (TextView) itemView.findViewById(R.id.pReviewDate);
            lReviewText = (TextView) itemView.findViewById(R.id.pReviewText);
            lReviewName = (TextView) itemView.findViewById(R.id.pReviewName);
            lReviewRate = (RatingBar) itemView.findViewById(R.id.reviewBar);

            lReviewPhoto = (ImageView) itemView. findViewById(R.id.pReviewPhoto);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    ReviewUtils cpu = (ReviewUtils) view.getTag();
                    String url = cpu.getReviewUrl();
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                    context.startActivity(intent);

                    //Toast.makeText(view.getContext(), cpu.getReviewName(), Toast.LENGTH_SHORT).show();

                }
            });

        }
    }

}