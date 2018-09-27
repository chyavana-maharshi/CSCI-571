package com.example.sunqingyu.hw9.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sunqingyu.hw9.ListActivity;
import com.example.sunqingyu.hw9.LocationUtils;
import com.example.sunqingyu.hw9.MyRecyclerAdapter;
import com.example.sunqingyu.hw9.MyRecyclerAdapter2;
import com.example.sunqingyu.hw9.R;
import com.example.sunqingyu.hw9.SharedPreferenceManager;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FragmentTwo extends Fragment implements MyRecyclerAdapter2.RecyclerViewClickListener{
    RecyclerView recyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager layoutManager;
    List<LocationUtils> locationUtilsList;
    TextView fError;

    public FragmentTwo() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_two, container, false);
        fError = (TextView)view.findViewById(R.id.favError);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycleViewContainer2);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(layoutManager);

        locationUtilsList = new ArrayList<>();

        getFav();
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        getFav();
    }

    public void getFav()
    {

        Map<String,?> favorList = SharedPreferenceManager.getInstance(getContext().getApplicationContext()).getAll();
        List<LocationUtils>  locationUtilsList = new ArrayList<LocationUtils>();
        if(favorList.isEmpty() || favorList.size()<1){
            fError.setVisibility(View.VISIBLE);
        }else{
            fError.setVisibility(View.INVISIBLE);
            for(Map.Entry<String,?> entry : favorList.entrySet()){
                if(entry.getValue() instanceof String){
                    String value = (String) entry.getValue();
                    Gson gson = new Gson();
                    LocationUtils res = gson.fromJson(value,LocationUtils.class);
                    locationUtilsList.add(res);
                }
            }

        }
        mAdapter = new MyRecyclerAdapter2(locationUtilsList, getActivity(),this);

        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void recyclerViewListClicked(){
        getFav();
    }


}
