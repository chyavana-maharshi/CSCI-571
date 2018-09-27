package com.example.sunqingyu.hw9;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.text.TextUtils;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ListActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager layoutManager;
    static String nextPageToken1,prePageToken1,URL;
    List<LocationUtils> locationUtilsList,locationUtilsList2;
    private Stack< String > previousToken;
    Button nextB,prevB;
    RequestQueue rq;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        rq = Volley.newRequestQueue(this);

        recyclerView = (RecyclerView) findViewById(R.id.recycleViewContainer);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);

        locationUtilsList = new ArrayList<>();
        locationUtilsList2 = new ArrayList<>();
        previousToken = new Stack<String>();
        String request_url = "http://10.0.2.2:3000/list";
        sendRequest(request_url);
        final Button buttonNext = findViewById(R.id.nextButton);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                next();
            }
        });
        final Button buttonPre = findViewById(R.id.preButton);
        buttonPre.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pre();
            }
        });



    }


    public void sendRequest(final String request_url){
        final ProgressDialog pg = new ProgressDialog(ListActivity.this);
        pg.setMessage("Fetching results");
        pg.show();

        URL = request_url;
        locationUtilsList = new ArrayList<>();
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, request_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
            public void onResponse(JSONObject response) {
                        pg.dismiss();
                Gson gson = new Gson();
                JsonObject listLocation = gson.fromJson(response.toString(), JsonObject.class);
                JsonArray newResponse = listLocation.getAsJsonObject().getAsJsonArray("results");

                        prevB = (Button) findViewById(R.id.preButton);
                        nextB = (Button) findViewById(R.id.nextButton);
                TextView noResults = (TextView)findViewById(R.id.resultError);
                        if(newResponse.size()<1){
                            noResults.setVisibility(View.VISIBLE);
                            prevB.setVisibility(View.GONE);
                            nextB.setVisibility(View.GONE);
                        }else{
                            noResults.setVisibility(View.GONE);
                        }

                prevB.setEnabled(false);
                if(listLocation.getAsJsonObject().has("next_page_token")){
                    nextPageToken1 = listLocation.get("next_page_token").getAsString();


                    nextB.setEnabled(true);
                }else{
                    nextB.setEnabled(false);
                }



                for(int i = 0; i < newResponse.size(); i++){

                    LocationUtils locationUtils = new LocationUtils();

                    try {
                        String locationName = newResponse.get(i).getAsJsonObject().get("name").getAsString();
                        String locationVicinity = newResponse.get(i).getAsJsonObject().get("vicinity").getAsString();
                        String locationImg = newResponse.get(i).getAsJsonObject().get("icon").getAsString();
                        String locationId = newResponse.get(i).getAsJsonObject().get("place_id").getAsString();
                        String locationLat = newResponse.get(i).getAsJsonObject().get("geometry").getAsJsonObject()
                                .get("location").getAsJsonObject().get("lat").getAsString();
                        String locationLng = newResponse.get(i).getAsJsonObject().get("geometry").getAsJsonObject()
                                .get("location").getAsJsonObject().get("lng").getAsString();

                        locationUtils.setLocationName(locationName);
                        locationUtils.setlocationVicinity(locationVicinity);
                        locationUtils.setlocationImg(locationImg);
                        locationUtils.setlocationId(locationId);
                        locationUtils.setlocationLat(locationLat);
                        locationUtils.setlocationLng(locationLng);


                    } catch (UnsatisfiedLinkError e) {
                        e.printStackTrace();
                    }

                    locationUtilsList.add(locationUtils);

                }

                mAdapter = new MyRecyclerAdapter(ListActivity.this, locationUtilsList);

                recyclerView.setAdapter(mAdapter);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Volley Has Error: ", error.toString());
            }
        });

        rq.add(getRequest);

    }

    public void next(){
        final ProgressDialog pd = new ProgressDialog(ListActivity.this);
        pd.setMessage("Fetching next page");
        pd.show();
        previousToken.push(URL);
        locationUtilsList2 = new ArrayList<>();
        final String nextURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?pagetoken="+nextPageToken1+"&key=AIzaSyC3db2_p93Rfd1heCsaHsTu7X0GEmjZvWo";
        URL = nextURL;
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, nextURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        JsonObject listLocation = gson.fromJson(response.toString(), JsonObject.class);
                        JsonArray newResponse = listLocation.getAsJsonObject().getAsJsonArray("results");


                        pd.dismiss();
                        prevB = (Button) findViewById(R.id.preButton);
                        prevB.setEnabled(true);

                        if(listLocation.getAsJsonObject().has("next_page_token")){

                            nextPageToken1 = listLocation.get("next_page_token").getAsString();
                            nextB = (Button) findViewById(R.id.nextButton);
                            nextB.setEnabled(true);
                        }else{
                            nextB.setEnabled(false);
                        }

                        for(int i = 0; i < newResponse.size(); i++){

                            LocationUtils locationUtils = new LocationUtils();

                            try {
                                String locationName = newResponse.get(i).getAsJsonObject().get("name").getAsString();
                                String locationVicinity = newResponse.get(i).getAsJsonObject().get("vicinity").getAsString();
                                String locationImg = newResponse.get(i).getAsJsonObject().get("icon").getAsString();
                                String locationId = newResponse.get(i).getAsJsonObject().get("place_id").getAsString();
                                String locationLat = newResponse.get(i).getAsJsonObject().get("geometry").getAsJsonObject()
                                        .get("location").getAsJsonObject().get("lat").getAsString();
                                String locationLng = newResponse.get(i).getAsJsonObject().get("geometry").getAsJsonObject()
                                        .get("location").getAsJsonObject().get("lng").getAsString();

                                locationUtils.setLocationName(locationName);
                                locationUtils.setlocationVicinity(locationVicinity);
                                locationUtils.setlocationImg(locationImg);
                                locationUtils.setlocationId(locationId);
                                locationUtils.setlocationLat(locationLat);
                                locationUtils.setlocationLng(locationLng);


                            } catch (UnsatisfiedLinkError e) {
                                e.printStackTrace();
                            }

                            locationUtilsList2.add(locationUtils);

                        }

                        mAdapter = new MyRecyclerAdapter(ListActivity.this, locationUtilsList2);

                        recyclerView.setAdapter(mAdapter);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Volley Has Error: ", error.toString());
            }
        });

        rq.add(getRequest);
    }

    public void pre(){
        locationUtilsList2 = new ArrayList<>();
        prePageToken1 = previousToken.pop();
        final String preURL = prePageToken1;
        System.out.println(preURL);
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, preURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        JsonObject listLocation = gson.fromJson(response.toString(), JsonObject.class);
                        JsonArray newResponse = listLocation.getAsJsonObject().getAsJsonArray("results");
                        System.out.println(newResponse);

                        prevB = (Button) findViewById(R.id.preButton);
                        prevB.setEnabled(true);

                        if(listLocation.getAsJsonObject().has("next_page_token")){
                            nextPageToken1 = listLocation.get("next_page_token").getAsString();
                            nextB = (Button) findViewById(R.id.nextButton);
                            nextB.setEnabled(true);
                        }else{
                            nextB.setEnabled(false);
                        }

                        if(preURL.equals("http://10.0.2.2:3000/list")){
                            prevB.setEnabled(false);
                        }
                        for(int i = 0; i < newResponse.size(); i++){

                            LocationUtils locationUtils = new LocationUtils();

                            try {
                                String locationName = newResponse.get(i).getAsJsonObject().get("name").getAsString();
                                String locationVicinity = newResponse.get(i).getAsJsonObject().get("vicinity").getAsString();
                                String locationImg = newResponse.get(i).getAsJsonObject().get("icon").getAsString();
                                String locationId = newResponse.get(i).getAsJsonObject().get("place_id").getAsString();
                                String locationLat = newResponse.get(i).getAsJsonObject().get("geometry").getAsJsonObject()
                                        .get("location").getAsJsonObject().get("lat").getAsString();
                                String locationLng = newResponse.get(i).getAsJsonObject().get("geometry").getAsJsonObject()
                                        .get("location").getAsJsonObject().get("lng").getAsString();

                                locationUtils.setLocationName(locationName);
                                locationUtils.setlocationVicinity(locationVicinity);
                                locationUtils.setlocationImg(locationImg);
                                locationUtils.setlocationId(locationId);
                                locationUtils.setlocationLat(locationLat);
                                locationUtils.setlocationLng(locationLng);


                            } catch (UnsatisfiedLinkError e) {
                                e.printStackTrace();
                            }

                            locationUtilsList2.add(locationUtils);

                        }

                        mAdapter = new MyRecyclerAdapter(ListActivity.this, locationUtilsList2);

                        recyclerView.setAdapter(mAdapter);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Volley Has Error: ", error.toString());
            }
        });

        rq.add(getRequest);
    }

}
