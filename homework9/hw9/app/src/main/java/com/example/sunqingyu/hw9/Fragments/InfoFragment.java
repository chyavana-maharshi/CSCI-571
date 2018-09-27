package com.example.sunqingyu.hw9.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sunqingyu.hw9.DetailActivity;
import com.example.sunqingyu.hw9.FeedItem;
import com.example.sunqingyu.hw9.MyAdapter;
import com.example.sunqingyu.hw9.R;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InfoFragment extends Fragment{


    static String place_id;


    RequestQueue detailRequest;
    static String addressText,phoneText,priceText,ratingText,googleText,websiteText,latitude,longitude,detailTitle;
    TextView addressT,phoneT,priceT,ratingT,googleT,websiteT;
    RatingBar ratingBarT;
    String request_url;


    public InfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DetailActivity activity = (DetailActivity) getActivity();
        place_id = activity.getMyData();
        detailTitle = activity.getMyTitle();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_info, container, false);
        detailRequest = Volley.newRequestQueue(getActivity().getApplicationContext());
        getRequest();



        // Inflate the layout for this fragment
        return view;
    }
    public void getRequest(){
        request_url = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + place_id + "&key=AIzaSyC3db2_p93Rfd1heCsaHsTu7X0GEmjZvWo";
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, request_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        JsonObject detailLocation = gson.fromJson(response.toString(), JsonObject.class);

                        String priceDollar = "";
                        addressT = (TextView) getView().findViewById(R.id.address);
                        phoneT = (TextView) getView().findViewById(R.id.phoneNumber);
                        priceT = (TextView) getView().findViewById(R.id.priceLevel);
//                        ratingT = (TextView) findViewById(R.id.rating);
                        googleT = (TextView) getView().findViewById(R.id.googlePage);
                        websiteT = (TextView) getView().findViewById(R.id.website);
                        ratingBarT = (RatingBar) getView().findViewById(R.id.ratingBar);

                        TableRow phone = (TableRow) getView().findViewById(R.id.phone);
                        TableRow price = (TableRow) getView().findViewById(R.id.price);
                        TableRow rate = (TableRow) getView().findViewById(R.id.rate);
                        TableRow web = (TableRow) getView().findViewById(R.id.webpage);
                        TableRow goo = (TableRow) getView().findViewById(R.id.google);
                        try {


                            JsonObject detailLocation1= detailLocation.getAsJsonObject().get("result").getAsJsonObject();
                            addressText = detailLocation.getAsJsonObject().get("result").getAsJsonObject().get("formatted_address").getAsString();
                            addressT.setText(addressText);

                            if(detailLocation1.has("formatted_phone_number")){
                            phoneText = detailLocation.getAsJsonObject().get("result").getAsJsonObject().get("formatted_phone_number").getAsString();
                            phoneT.setText(phoneText);
                            }else{phone.setVisibility(View.GONE);}

                            if(detailLocation1.has("price_level")) {
                                priceText = detailLocation.getAsJsonObject().get("result").getAsJsonObject().get("price_level").getAsString();
                                for (int i = 0; i < Integer.parseInt(priceText); i++) {
                                    priceDollar = "$" + priceDollar;
                                }
                                priceT.setText(priceDollar);
                            }else{price.setVisibility(View.GONE);}

                            if(detailLocation1.has("rating")) {
                                ratingText = detailLocation.getAsJsonObject().get("result").getAsJsonObject().get("rating").getAsString();
//                            ratingT.setText(ratingText);
                                ratingBarT.setRating(Float.parseFloat(ratingText));
                            }else{rate.setVisibility(View.GONE);}

                            if(detailLocation1.has("url")) {
                                googleText = detailLocation.getAsJsonObject().get("result").getAsJsonObject().get("url").getAsString();
                                googleT.setText(googleText);
                            }else{goo.setVisibility(View.GONE);}

                            if(detailLocation1.has("website")) {
                                websiteText = detailLocation.getAsJsonObject().get("result").getAsJsonObject().get("website").getAsString();
                                websiteT.setText(websiteText);
                            }else{web.setVisibility(View.GONE);}

                            latitude = detailLocation.getAsJsonObject().get("result").getAsJsonObject().get("geometry").getAsJsonObject()
                            .get("location").getAsJsonObject().get("lat").getAsString();
                            longitude = detailLocation.getAsJsonObject().get("result").getAsJsonObject().get("geometry").getAsJsonObject()
                                    .get("location").getAsJsonObject().get("lng").getAsString();

                            getYelp();

                            Log.d("This URL",request_url);

                        } catch (UnsatisfiedLinkError e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Volley Has Error: ", error.toString());
            }
        });

        detailRequest.add(getRequest);

    }

    private void getYelp(){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "http://10.0.2.2:3000/yelpmatch";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        //Log.d("Response", response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams()
            {

                String[] addressList = addressText.split(",");
                Map<String, String>  params = new HashMap<String, String>();

                if(addressList.length == 4){
                    params.put("name0", detailTitle);
                    params.put("address0", addressList[0]);
                    params.put("city0", addressList[1]);
                    params.put("state0", addressList[2].substring(1,3));
                    Log.d("Response", "Got YELP 4");
                }else if(addressList.length == 3){
                    params.put("name0", detailTitle);
                    params.put("address0", addressList[1]);
                    params.put("city0", addressList[0]);
                    params.put("state0", addressList[1].substring(1,3));
                    Log.d("Response", "Got YELP 3");
                }
                return params;
            }
        };

        queue.add(postRequest);
    }
}