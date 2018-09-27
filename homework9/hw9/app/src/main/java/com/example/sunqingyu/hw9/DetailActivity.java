package com.example.sunqingyu.hw9;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sunqingyu.hw9.Fragments.InfoFragment;
import com.example.sunqingyu.hw9.Fragments.MapFragment;
import com.example.sunqingyu.hw9.Fragments.PhotosFragment;
import com.example.sunqingyu.hw9.Fragments.ReviewsFragment;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DetailActivity extends AppCompatActivity {

    static String detailTitle,place_id, placeLat, placeLng,placeImg;
    private Context context;
    List<LocationUtils> locationUtilsList;
    protected GeoDataClient mGeoDataClient;
    protected PlaceDetectionClient mPlaceDetectionClient;
    RequestQueue detailRequest;
    static String addressText,phoneText,priceText,ratingText,googleText,websiteText;
    TextView addressT,phoneT,priceT,ratingT,googleT,websiteT,titleT;
    RatingBar ratingBarT;

    MapFragment mapf;


    String request_url = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + place_id + "&key=AIzaSyC3db2_p93Rfd1heCsaHsTu7X0GEmjZvWo";
    public String getMyData() {
        return place_id;
    }
    public String getMyTitle(){
        return detailTitle;
    }
    public String getMyLat(){
        return placeLat;
    }
    public String getMyLng(){
        return placeLng;
    }

    public static void actionStart(Context context, String data1, String data2, String data3, String data4,String data5){
        Intent intent = new Intent(context, DetailActivity.class);
        context.startActivity(intent);
        intent.putExtra("name1",data1);
        Log.d("Response", data1);
        detailTitle = data1;
        place_id = data2;
        placeLat = data3;
        placeLng = data4;
        placeImg = data5;

    }



    private int[] tabIcons = {
            R.drawable.info_outline,
            R.drawable.photos,
            R.drawable.maps,
            R.drawable.review
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle(detailTitle);
        LayoutInflater inflator = (LayoutInflater) this .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.twi_fav, null);

        actionBar.setCustomView(v);

        TextView titleT = (TextView) findViewById(R.id.barTitle);
        titleT.setText(detailTitle);


// Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        viewPager.setOffscreenPageLimit(3);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.detailTabs);
        tabLayout.setupWithViewPager(viewPager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new InfoFragment(), "   INFO    ");
        adapter.addFrag(new PhotosFragment(), "    PHOTOS   ");

         mapf=new MapFragment();
     //   mapf.getaddress(addressText);
        adapter.addFrag(mapf, "    MAP    ");
        adapter.addFrag(new ReviewsFragment(), "   REVIEWS   ");



        viewPager.setAdapter(adapter);


        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne.setText("   INFO    ");
        tabOne.setCompoundDrawablesWithIntrinsicBounds( tabIcons[0],0, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabTwo.setText("    PHOTOS    ");
        tabTwo.setCompoundDrawablesWithIntrinsicBounds( tabIcons[1],0, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);

        TextView tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabThree.setText("    MAP    ");
        tabThree.setCompoundDrawablesWithIntrinsicBounds( tabIcons[2],0, 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabThree);

        TextView tabFour = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabFour.setText("    REVIEWS    ");
        tabFour.setCompoundDrawablesWithIntrinsicBounds(tabIcons[3],0, 0, 0);
        tabLayout.getTabAt(3).setCustomView(tabFour);

     //   detailRequest = Volley.newRequestQueue(this);
        getRequest();


        ImageView shareB = (ImageView)findViewById(R.id.detailShare);
        shareB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String twiUrl = "https://twitter.com/intent/tweet?text=Check out "+detailTitle+" located at "+addressText+". Website: "+websiteText;
                Uri uri = Uri.parse(twiUrl);
                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
            }
        });


        final ImageView favB = (ImageView)findViewById(R.id.detailFav);

        if(SharedPreferenceManager.getInstance(getApplicationContext()).isFavourite(place_id)){
            favB.setImageResource(R.drawable.heart_fill_white);
        } else{
            favB.setImageResource(R.drawable.heart_outline_white);
        }
        favB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                locationUtilsList = new ArrayList<>();
                LocationUtils locationUtils = new LocationUtils();
                locationUtils.setLocationName(detailTitle);
                locationUtils.setlocationVicinity(addressText);
                locationUtils.setlocationImg(placeImg);
                locationUtils.setlocationId(place_id);

                String toastText = detailTitle;
                String keyId = place_id;
                if(SharedPreferenceManager.getInstance(getApplicationContext()).isFavourite(keyId)){
                    toastText += " was removed from favorites";
                    favB.setImageResource(R.drawable.heart_outline_white);
                    SharedPreferenceManager.getInstance(getApplicationContext()).removeFavourite(keyId);
                } else{
                    toastText += " was added to favorites";
                    favB.setImageResource(R.drawable.heart_fill_white);
//                    LocationUtils newFavor = new LocationUtils();
 //                   locationUtilsList.add(locationUtils);
                    Gson gson = new Gson();
                    String jsonInString = gson.toJson(locationUtils);
                    SharedPreferenceManager.getInstance(context).setFavourite(place_id,jsonInString);
                }
                Toast.makeText(getBaseContext(), toastText, Toast.LENGTH_LONG).show();
            }
        });

    }



    public void getRequest(){
        final ProgressDialog pg = new ProgressDialog(DetailActivity.this);
        pg.setMessage("Fetching details");
        pg.show();
        detailRequest = Volley.newRequestQueue(this);
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, request_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pg.dismiss();
                        Gson gson = new Gson();
                        JsonObject detailLocation = gson.fromJson(response.toString(), JsonObject.class);



                        try {

                            addressText = detailLocation.getAsJsonObject().get("result").getAsJsonObject().get("formatted_address").getAsString();


                            if(detailLocation.getAsJsonObject().get("result").getAsJsonObject().has("website")) {
                                websiteText = detailLocation.getAsJsonObject().get("result").getAsJsonObject().get("website").getAsString();
                            }else{websiteText="";}

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
//        Bundle bundle = new Bundle();
//        bundle.putString("add", "123");
//        mapf.setArguments(bundle);

    }


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


        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

    }

//    @Override
//    public void onStart() {
//        super.onStart();
//
//
//    }

}
