package com.example.sunqingyu.hw9;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MyRecyclerAdapter2 extends RecyclerView.Adapter<MyRecyclerAdapter2.ViewHolder>{
    private Context context;
    private List<LocationUtils> locationUtils;
    private static RecyclerViewClickListener favCallback;

    public MyRecyclerAdapter2( List <LocationUtils> locationUtils,Context context,RecyclerViewClickListener favItemListener) {
        this.context = context;
        this.locationUtils = locationUtils;
        favCallback = favItemListener;
    }

    @Override
    public MyRecyclerAdapter2.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemView.setTag(locationUtils.get(position));

        final LocationUtils pu = locationUtils.get(position);
        holder.lFav.setImageResource(R.drawable.heart_fill_red);
        holder.lName.setText(pu.getLocationName());
        holder.lVicinity.setText(pu.getlocationVicinity());

        Picasso.get().load(pu.getlocationImg()).into(holder.lImg);

        holder.lFav.setOnClickListener(new View.OnClickListener(){
        @Override
            public void onClick(View v){
            String toastText = pu.getLocationName() + " was removed from favorites";
            Toast.makeText(context, toastText, Toast.LENGTH_LONG).show();
            String keyId = pu.getlocationId();
            SharedPreferenceManager.getInstance(context.getApplicationContext()).removeFavourite(keyId);
            favCallback.recyclerViewListClicked();
        }
        });
    }

    @Override
    public int getItemCount() {
        return locationUtils.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView lName;
        public TextView lVicinity;
        public ImageView lImg,lFav;


        public ViewHolder(View itemView) {
            super(itemView);

            lName = (TextView) itemView.findViewById(R.id.pName);
            lVicinity = (TextView) itemView.findViewById(R.id.pVicinity);
            lImg = (ImageView) itemView. findViewById(R.id.pImg);
            lFav = (ImageView) itemView.findViewById(R.id.pFav);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    LocationUtils cpu = (LocationUtils) view.getTag();
                    DetailActivity.actionStart(view.getContext(),cpu.getLocationName(),cpu.getlocationId(),cpu.getlocationLat(),cpu.getlocationLng(),cpu.getlocationImg());

                    Toast.makeText(view.getContext(), cpu.getLocationName()+" "+cpu.getlocationVicinity()+" is "+ cpu.getlocationImg(), Toast.LENGTH_SHORT).show();

                }
            });

        }
    }

    public interface RecyclerViewClickListener{
        public void recyclerViewListClicked();
    }
}
