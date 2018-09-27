package com.example.sunqingyu.hw9;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunqingyu.hw9.utils.newFav;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder>{
    private Context context;
    private List<LocationUtils> locationUtils;


    public MyRecyclerAdapter(Context context, List locationUtils) {
        this.context = context;
        this.locationUtils = locationUtils;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.itemView.setTag(locationUtils.get(position));

        final LocationUtils pu = locationUtils.get(position);

        holder.lName.setText(pu.getLocationName());
        holder.lVicinity.setText(pu.getlocationVicinity());

        Picasso.get().load(pu.getlocationImg()).into(holder.lImg);
        if(SharedPreferenceManager.getInstance(context.getApplicationContext()).isFavourite(pu.getlocationId())){
            holder.lFav.setImageResource(R.drawable.heart_fill_red);
        } else{
            holder.lFav.setImageResource(R.drawable.heart_outline_black);
        }
        holder.lFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toastText = pu.getLocationName();
                String keyId = pu.getlocationId();
                if(SharedPreferenceManager.getInstance(context.getApplicationContext()).isFavourite(keyId)){
                    toastText += " was removed from favorites";
                    holder.lFav.setImageResource(R.drawable.heart_outline_black);
                    SharedPreferenceManager.getInstance(context.getApplicationContext()).removeFavourite(keyId);
                } else{
                    toastText += " was added to favorites";
                    holder.lFav.setImageResource(R.drawable.heart_fill_red);
//                    LocationUtils newFavor = new LocationUtils();

                    Gson gson = new Gson();
                    String jsonInString = gson.toJson(pu);
                    SharedPreferenceManager.getInstance(context.getApplicationContext()).setFavourite(pu.getlocationId(),jsonInString);
                }
                Toast.makeText(context, toastText, Toast.LENGTH_LONG).show();
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

                   // Toast.makeText(view.getContext(), cpu.getLocationName()+" "+cpu.getlocationVicinity()+" is "+ cpu.getlocationImg(), Toast.LENGTH_SHORT).show();

                }
            });

        }
    }

}
