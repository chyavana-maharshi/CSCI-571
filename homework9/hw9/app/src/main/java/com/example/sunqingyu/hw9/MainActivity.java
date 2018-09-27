package com.example.sunqingyu.hw9;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sunqingyu.hw9.Fragments.FragmentOne;
import com.example.sunqingyu.hw9.Fragments.FragmentTwo;
import com.example.sunqingyu.hw9.app.AppController;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity {


    EditText keyword,distance,location;
    Spinner category;
    Button searchB;
    RadioGroup radioButtons;
    RadioButton radioB;
    TextView keywordE,locationE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        // Add Fragments to adapter one by one
        adapter.addFragment(new FragmentOne(), "SEARCH");
        adapter.addFragment(new FragmentTwo(), "FAVORITES");
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        TextView tabSearch = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabSearch.setText("  SEARCH      ");
        tabSearch.setCompoundDrawablesWithIntrinsicBounds( R.drawable.search,0, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabSearch);

        TextView tabFavorite = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabFavorite.setText("  FAVORITES     ");
        tabFavorite.setCompoundDrawablesWithIntrinsicBounds( R.drawable.heart_fill_white,0, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabFavorite);



    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    // Adapter for the viewpager using FragmentPagerAdapter
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
    static String lat;
    static String lng;

    public void search(View v){



        keyword = (EditText) findViewById(R.id.keywordText);
        keywordE = (TextView) findViewById(R.id.keywordError);
        locationE = (TextView) findViewById(R.id.locationError);
        searchB = (Button) findViewById(R.id.searchButton);
        category = (Spinner) findViewById(R.id.spinner);
        distance = (EditText) findViewById(R.id.distanceText);
        radioButtons = (RadioGroup) findViewById(R.id.radioButton);
        Integer selectedRadioId = radioButtons.getCheckedRadioButtonId();
        radioB = (RadioButton) findViewById(selectedRadioId);
        location = (EditText) findViewById(R.id.locationText);
        String[] customarr = {"default","airport","amusement_park","aquarium",
                "art_gallery","bakery","bar","beauty_salon","bowling_alley",
                "bus_station","cafe","campground","car_rental","casino",
                "lodging","movie_theater","museum","night_club","park","parking",
                "restaurant","shopping_mall","stadium","subway_station","taxi_stand",
                "train_station","transit_station","travel_agency","zoo"};

        final String keywordText = keyword.getText().toString();
        final String categoryText = customarr[category.getSelectedItemPosition()];
        final String distanceText = distance.getText().toString();
        final String radio = radioB.getText().toString();
        final String locationText = location.getText().toString();

        final RequestQueue queue = Volley.newRequestQueue(this);


        if(TextUtils.isEmpty(keyword.getText()) && (TextUtils.isEmpty(location.getText()) && radio.equals("Other. Specify Location"))) {
            keywordE.setVisibility(VISIBLE);
            locationE.setVisibility(VISIBLE);
            Toast.makeText(getBaseContext(),"Please fix all fields with errors",Toast.LENGTH_SHORT).show();

        }else if(TextUtils.isEmpty(keyword.getText())){
            keywordE.setVisibility(VISIBLE);
            Toast.makeText(getBaseContext(),"Please fix all fields with errors",Toast.LENGTH_SHORT).show();

        }
        else if(TextUtils.isEmpty(location.getText()) && radio.equals("Other. Specify Location")){
            locationE.setVisibility(VISIBLE);
            Toast.makeText(getBaseContext(),"Please fix all fields with errors",Toast.LENGTH_SHORT).show();

        }
        else
        {
            final ProgressDialog pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Fetching results");
            pd.show();
            if (radio.equals("Current Location")) {

                final String urlHere = "http://ip-api.com/json";

                JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, urlHere, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                Gson gson = new Gson();
                                JsonObject userLocation = gson.fromJson(response.toString(), JsonObject.class);
                                lat = userLocation.get("lat").getAsString();
                                lng = userLocation.get("lon").getAsString();
                                Log.d("Response", lat + lng + "here");
                                String urlList = "http://10.0.2.2:3000/ha";
                                StringRequest postRequest = new StringRequest(Request.Method.POST, urlList,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                // response
                                                //Log.d("Response", response);
                                                pd.dismiss();
                                                Intent intent = new Intent("com.example.sunqingyu.hw9.ACTION_START");
                                                startActivity(intent);
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                // error
                                                Log.d("Error.Response", error.toString());
                                            }
                                        }
                                ) {
                                    @Override
                                    protected Map<String, String> getParams() {
                                        Map<String, String> params = new HashMap<String, String>();
                                        params.put("keyword", keywordText);
                                        params.put("category", categoryText);
                                        params.put("distance", distanceText);
                                        params.put("loc", radio);
                                        params.put("location", locationText);
                                        params.put("lat", lat);
                                        params.put("lon", lng);
                                        Log.d("Response", "Got the parameters");
                                        return params;
                                    }
                                };

                                queue.add(postRequest);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("ERROR", "Error occurred ", error);
                            }
                        }
                );

                queue.add(getRequest);
            } else {
                final String urlNotHere = "https://maps.googleapis.com/maps/api/geocode/json?address=" + locationText + "&key=AIzaSyC3db2_p93Rfd1heCsaHsTu7X0GEmjZvWo";

                JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, urlNotHere, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                Gson gson = new Gson();
                                JsonObject userLocation = gson.fromJson(response.toString(), JsonObject.class);
                                lat = userLocation.getAsJsonObject().getAsJsonArray("results").get(0)
                                        .getAsJsonObject().get("geometry")
                                        .getAsJsonObject().get("location")
                                        .getAsJsonObject().get("lat").getAsString();
                                lng = userLocation.getAsJsonObject().getAsJsonArray("results").get(0)
                                        .getAsJsonObject().get("geometry")
                                        .getAsJsonObject().get("location")
                                        .getAsJsonObject().get("lng").getAsString();
                                Log.d("Response", lat + lng + "nothere");
                                String urlList = "http://10.0.2.2:3000/ha";
                                StringRequest postRequest = new StringRequest(Request.Method.POST, urlList,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                // response
                                                //Log.d("Response", response);
                                                pd.dismiss();
                                                Intent intent = new Intent("com.example.sunqingyu.hw9.ACTION_START");
                                                startActivity(intent);
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                // error
                                                Log.d("Error.Response", error.toString());
                                            }
                                        }
                                ) {
                                    @Override
                                    protected Map<String, String> getParams() {
                                        Map<String, String> params = new HashMap<String, String>();
                                        params.put("keyword", keywordText);
                                        params.put("category", categoryText);
                                        params.put("distance", distanceText);
                                        params.put("loc", radio);
                                        params.put("location", locationText);
                                        params.put("lat", lat);
                                        params.put("lon", lng);
                                        Log.d("Response", "Got the parameters");
                                        return params;
                                    }
                                };

                                queue.add(postRequest);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("ERROR", "Error occurred ", error);
                            }
                        }
                );

                queue.add(getRequest);
            }

        }

//        Intent intent = new Intent("com.example.sunqingyu.hw9.ACTION_START");
//        startActivity(intent);
    }
//    public void clear(){
//        keyword = (EditText) findViewById(R.id.keywordText);
//        keywordE = (TextView) findViewById(R.id.keywordError);
//        locationE = (TextView) findViewById(R.id.locationError);
//        searchB = (Button) findViewById(R.id.searchButton);
//        category = (Spinner) findViewById(R.id.spinner);
//        distance = (EditText) findViewById(R.id.distanceText);
//        radioButtons = (RadioGroup) findViewById(R.id.radioButton);
//        Integer selectedRadioId = radioButtons.getCheckedRadioButtonId();
//        radioB = (RadioButton) findViewById(selectedRadioId);
//        location = (EditText) findViewById(R.id.locationText);
//
//        keywordE.setVisibility(View.INVISIBLE);
//        locationE.setVisibility(View.INVISIBLE);
//        keyword.setText("");
//        distance.setText("");
//
//    }
}
