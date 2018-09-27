package com.example.sunqingyu.hw9.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sunqingyu.hw9.DetailActivity;
import com.example.sunqingyu.hw9.ListActivity;
import com.example.sunqingyu.hw9.LocationUtils;
import com.example.sunqingyu.hw9.MyRecyclerAdapter;
import com.example.sunqingyu.hw9.MyReviewAdpater;
import com.example.sunqingyu.hw9.R;
import com.example.sunqingyu.hw9.ReviewUtils;
import com.google.android.gms.location.places.Places;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class ReviewsFragment extends Fragment{

    List<ReviewUtils> reviewList;
    private List <ReviewUtils> reorderList;
    RecyclerView mReviewView;
    MyReviewAdpater radapter;
    TextView noReviews;
    RecyclerView.LayoutManager layoutManager;
    static String place_id,address;
    RequestQueue rq;
    private Spinner spinnerSource,spinnerOrder;
    private List<String> list = new ArrayList<String>();
    private List<String> list2 = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    private ArrayAdapter<String> adapter2;
    public ReviewsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DetailActivity activity = (DetailActivity) getActivity();
        place_id = activity.getMyData();
//        address = activity.getaddress();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_reviews, container, false);
        mReviewView = (RecyclerView) view.findViewById(R.id.recycleViewReview);
        mReviewView.setLayoutManager(new LinearLayoutManager(getActivity()));
        reviewList = new ArrayList<>();
        noReviews = (TextView)view.findViewById(R.id.reviewError);
        rq = Volley.newRequestQueue(getActivity().getApplicationContext());

        list.add("Google reviews");
        list.add("Yelp reviews");
        spinnerSource = (Spinner) view.findViewById(R.id.reviewSpinner);
        adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSource.setAdapter(adapter);
        spinnerSource.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {//选择item的选择点击监听事件
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {

                if((adapter.getItem(arg2)).contains("Yelp")){
                    Log.d("you selected",adapter.getItem(arg2));
                    getYelpReviews();

                }else{
                    getReviews();
                    Log.d("you selected",adapter.getItem(arg2));
                }
            }

            public void onNothingSelected(AdapterView<?> arg0) {

               // getReviews();
            }
        });

        list2.add("Default order");
        list2.add("Highest rating");
        list2.add("Lowest rating");
        list2.add("Most recent");
        list2.add("Least recent");
        spinnerOrder = (Spinner) view.findViewById(R.id.orderSpinner);
        adapter2 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, list2);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOrder.setAdapter(adapter2);
        spinnerOrder.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {

                if((adapter2.getItem(arg2)).contains("Default")){

                    radapter = new MyReviewAdpater(getActivity(), reviewList);
                    mReviewView.setAdapter(radapter);

                }else if((adapter2.getItem(arg2)).contains("Lowest")){

                    sortStringMethod(reorderList);
                    radapter = new MyReviewAdpater(getActivity(), reorderList);
                    mReviewView.setAdapter(radapter);
                }else if((adapter2.getItem(arg2)).contains("Highest")){
                    sortStringMethod2(reorderList);
                    radapter = new MyReviewAdpater(getActivity(), reorderList);
                    mReviewView.setAdapter(radapter);
                }else if((adapter2.getItem(arg2)).contains("Least")){
                    sortStringMethod3(reorderList);
                    radapter = new MyReviewAdpater(getActivity(), reorderList);
                    mReviewView.setAdapter(radapter);
                }else if((adapter2.getItem(arg2)).contains("Most")){
                    sortStringMethod4(reorderList);
                    radapter = new MyReviewAdpater(getActivity(), reorderList);
                    mReviewView.setAdapter(radapter);
                }

            }

            public void onNothingSelected(AdapterView<?> arg0) {

                // getReviews();
            }
        });

        return view;
    }


    public static void sortStringMethod(List list){
        Collections.sort(list, new Comparator(){
            @Override
            public int compare(Object o1, Object o2) {
                ReviewUtils stu1=(ReviewUtils)o1;
                ReviewUtils stu2=(ReviewUtils)o2;
                return stu1.getReviewRate().compareTo(stu2.getReviewRate());
            }
        });

        for(int i=0;i<list.size();i++){
            ReviewUtils st=(ReviewUtils)list.get(i);
        }
    }
    public static void sortStringMethod2(List list){
        Collections.sort(list, new Comparator(){
            @Override
            public int compare(Object o1, Object o2) {
                ReviewUtils stu1=(ReviewUtils)o1;
                ReviewUtils stu2=(ReviewUtils)o2;
                return stu2.getReviewRate().compareTo(stu1.getReviewRate());
            }
        });

        for(int i=0;i<list.size();i++){
            ReviewUtils st=(ReviewUtils)list.get(i);
        }
    }
    public static void sortStringMethod3(List list){
        Collections.sort(list, new Comparator(){
            @Override
            public int compare(Object o1, Object o2) {
                ReviewUtils stu1=(ReviewUtils)o1;
                ReviewUtils stu2=(ReviewUtils)o2;
                return stu1.getReviewDate().compareTo(stu2.getReviewDate());
            }
        });

        for(int i=0;i<list.size();i++){
            ReviewUtils st=(ReviewUtils)list.get(i);
        }
    }
    public static void sortStringMethod4(List list){
        Collections.sort(list, new Comparator(){
            @Override
            public int compare(Object o1, Object o2) {
                ReviewUtils stu1=(ReviewUtils)o1;
                ReviewUtils stu2=(ReviewUtils)o2;
                return stu2.getReviewDate().compareTo(stu1.getReviewDate());
            }
        });

        for(int i=0;i<list.size();i++){
            ReviewUtils st=(ReviewUtils)list.get(i);
        }
    }
    private void getReviews(){
        reviewList = new ArrayList<>();
        String request_url = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + place_id + "&key=AIzaSyC3db2_p93Rfd1heCsaHsTu7X0GEmjZvWo";

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, request_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        JsonObject listLocation = gson.fromJson(response.toString(), JsonObject.class);


                        JsonObject newResponse1 =listLocation.getAsJsonObject().get("result").getAsJsonObject();
                        if(!newResponse1.has("reviews")){
                            noReviews.setVisibility(View.VISIBLE);
                            mReviewView.setVisibility(View.GONE);
                        }else {
                            noReviews.setVisibility(View.GONE);
                            mReviewView.setVisibility(View.VISIBLE);

                            JsonArray newResponse = listLocation.getAsJsonObject().get("result")
                                    .getAsJsonObject().getAsJsonArray("reviews");
                            for (int i = 0; i < newResponse.size(); i++) {

                                ReviewUtils reviewUtils = new ReviewUtils();

                                try {
                                    String reviewName = newResponse.get(i).getAsJsonObject().get("author_name").getAsString();
                                    reviewUtils.setReviewName(reviewName);
                                    String reviewText = newResponse.get(i).getAsJsonObject().get("text").getAsString();
                                    reviewUtils.setReviewText(reviewText);
                                    String reviewRate = newResponse.get(i).getAsJsonObject().get("rating").getAsString();
                                    reviewUtils.setReviewRate(reviewRate);
                                    String reviewPhoto = newResponse.get(i).getAsJsonObject().get("profile_photo_url").getAsString();
                                    reviewUtils.setReviewPhoto(reviewPhoto);
                                    String reviewDate = newResponse.get(i).getAsJsonObject().get("time").getAsString();
                                    Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                                    cal.setTimeInMillis(Long.parseLong(reviewDate) * 1000L);
                                    String date = DateFormat.format("yyyy-MM-dd hh:mm:ss", cal).toString();
                                    reviewUtils.setReviewDate(date);
                                    String reviewUrl = newResponse.get(i).getAsJsonObject().get("author_url").getAsString();
                                    reviewUtils.setReviewUrl(reviewUrl);


                                } catch (UnsatisfiedLinkError e) {
                                    e.printStackTrace();
                                }

                                reviewList.add(reviewUtils);

                            }

                            reorderList = new ArrayList<>(reviewList);
                            radapter = new MyReviewAdpater(getActivity(), reviewList);

                            mReviewView.setAdapter(radapter);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                noReviews.setVisibility(View.VISIBLE);
                mReviewView.setVisibility(View.GONE);
                Log.i("Volley Has Error: ", error.toString());
            }
        });

        rq.add(getRequest);

    }

    public void getYelpReviews(){
        reviewList = new ArrayList<>();
        String request_url = "http://10.0.2.2:3000/yelp";

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, request_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Gson gson = new Gson();
                        JsonObject listLocation = gson.fromJson(response.toString(), JsonObject.class);
                        JsonArray newResponse = listLocation.getAsJsonObject().getAsJsonArray("reviews");


//                        if(newResponse.size()<1){
//                            noReviews.setVisibility(View.VISIBLE);
//                            mReviewView.setVisibility(View.GONE);
//                        }else{
//                            noReviews.setVisibility(View.GONE);
//                            mReviewView.setVisibility(View.VISIBLE);
//                        }

                        for(int i = 0; i < newResponse.size(); i++){

                            ReviewUtils reviewUtils = new ReviewUtils();

                            try {
                                String reviewName = newResponse.get(i).getAsJsonObject().get("user").getAsJsonObject().get("name").getAsString();
                                reviewUtils.setReviewName(reviewName);
                                String reviewText = newResponse.get(i).getAsJsonObject().get("text").getAsString();
                                reviewUtils.setReviewText(reviewText);
                                String reviewRate = newResponse.get(i).getAsJsonObject().get("rating").getAsString();
                                reviewUtils.setReviewRate(reviewRate);
                                String reviewPhoto = newResponse.get(i).getAsJsonObject().getAsJsonObject().get("user").getAsJsonObject().get("image_url").getAsString();
                                reviewUtils.setReviewPhoto(reviewPhoto);
                                String reviewDate = newResponse.get(i).getAsJsonObject().get("time_created").getAsString();
                                reviewUtils.setReviewDate(reviewDate);
                                String reviewUrl = newResponse.get(i).getAsJsonObject().get("url").getAsString();
                                reviewUtils.setReviewUrl(reviewUrl);




                            } catch (UnsatisfiedLinkError e) {
                                e.printStackTrace();
                            }

                            reviewList.add(reviewUtils);

                        }
                        reorderList = new ArrayList<>(reviewList);
                        radapter = new MyReviewAdpater(getActivity(), reviewList);
                        mReviewView.setAdapter(radapter);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                noReviews.setVisibility(View.VISIBLE);
                mReviewView.setVisibility(View.GONE);
                Log.i("Volley Has Error: ", error.toString());
            }
        });

        rq.add(getRequest);

    }
}