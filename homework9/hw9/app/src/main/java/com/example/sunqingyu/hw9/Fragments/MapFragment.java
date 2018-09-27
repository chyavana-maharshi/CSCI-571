package com.example.sunqingyu.hw9.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sunqingyu.hw9.DetailActivity;
import com.example.sunqingyu.hw9.PlaceAutocompleteAdapter;
import com.example.sunqingyu.hw9.R;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;

import org.joda.time.DateTime;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
public class MapFragment extends Fragment implements OnMapReadyCallback {

    RequestQueue detailRequest;
    static String title,lat,lng,originText,destinationText,place_id;
    static Polyline polyline;
    static Double latt,lngg;
    static boolean clicked;
    static TravelMode mode1;
    private List<String> list = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    protected GeoDataClient mGeoDataClient;
    protected PlaceDetectionClient mPlaceDetectionClient;
    private AutoCompleteTextView mMapText;
    private PlaceAutocompleteAdapter placeAutocompleteAdapter;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
         new LatLng(-39,-170),new LatLng(72,137)
    );
    String addressText;
    Spinner travelM;

//    public MapFragment() {
//        // Required empty public constructor
//    }
    private static final int overview = 0;
    private GoogleMap mMap;

    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DetailActivity activity = (DetailActivity) getActivity();
        title = activity.getMyTitle();
        place_id = activity.getMyData();
        lat = activity.getMyLat();
        latt = Double.parseDouble(lat);
        lng = activity.getMyLng();
        lngg = Double.parseDouble(lng);

getRequest();



    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mMapText = (AutoCompleteTextView) view.findViewById(R.id.mapText);
        mGeoDataClient = Places.getGeoDataClient(getActivity(), null);
        mPlaceDetectionClient = Places.getPlaceDetectionClient(getActivity(), null);
        placeAutocompleteAdapter = new PlaceAutocompleteAdapter(getActivity(),mGeoDataClient,LAT_LNG_BOUNDS,null);
        mMapText.setAdapter(placeAutocompleteAdapter);




        originText = "";
        clicked = false;
        list.add("Driving");
        list.add("Bicycling");
        list.add("Transit");
        list.add("Walking");
        travelM = (Spinner)view.findViewById(R.id.mapSpinner);
        adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        travelM.setAdapter(adapter);

        travelM.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {//选择item的选择点击监听事件
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {

                if((adapter.getItem(arg2)).contains("Driving")){
                    Log.d("you selected",adapter.getItem(arg2));
                    mode1 = TravelMode.DRIVING;
                    originText = mMapText.getText().toString();
                    changeM1();
                    clicked = true;
                 //   Log.d("what the ffff",addressText);
                }
                else if((adapter.getItem(arg2)).contains("Bicycling")){
                    Log.d("you selected",adapter.getItem(arg2));
                    mode1 = TravelMode.BICYCLING;
                   // polyline.remove();
                    clicked = true;
                    originText = mMapText.getText().toString();
                    changeM2();

                }
                else if((adapter.getItem(arg2)).contains("Transit")){
                    Log.d("you selected",adapter.getItem(arg2));
                    originText = mMapText.getText().toString();
                    mode1 = TravelMode.TRANSIT;
                    changeM3();
                }
                else if((adapter.getItem(arg2)).contains("Walking")){
                    Log.d("you selected",adapter.getItem(arg2));
                    mode1 = TravelMode.WALKING;
                    originText = mMapText.getText().toString();
                    changeM4();
                }
                else{

                    Log.d("you selected",adapter.getItem(arg2));
                }
            }

            public void onNothingSelected(AdapterView<?> arg0) {


            }
        });
//        TravelMode[] customMode = {TravelMode.DRIVING,TravelMode.BICYCLING,TravelMode.TRANSIT,TravelMode.WALKING};
//        mode1 = customMode[travelM.getSelectedItemPosition()];


//        travelM.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//                if
//                changeM();
//                clicked = true;
//                Log.d("mode change","666");
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parentView) {
//                // your code here
//
//                clicked = false;
//            }
//
//        });

        return view;
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        DetailActivity activity = (DetailActivity) getActivity();
//        destinationText = activity.getRequest();
//
//    }

//    public void getaddress(String address){
//        addressText=address;
//
//    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        DetailActivity activity = (DetailActivity) getActivity();
//        destinationText = activity.getRequest();
//        Log.d("woooooo",destinationText);
//    }


//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        Bundle bundle = this.getArguments();
//        if (bundle != null) {
//            String i = bundle.getString("add");
//            Log.d("aaaaaa",i);
//        }
//    }

    public void changeM1(){
        SupportMapFragment mapFragment1 = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment1.getMapAsync(this);
    }
    public void changeM2(){
        SupportMapFragment mapFragment2 = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment2.getMapAsync(this);
    }
    public void changeM3(){
        SupportMapFragment mapFragment3 = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment3.getMapAsync(this);
    }
    public void changeM4(){
        SupportMapFragment mapFragment4 = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment4.getMapAsync(this);
    }
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(latt, lngg);
//        mMap.addMarker(new MarkerOptions().position(sydney).title(title));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,14));
//    }
private DirectionsResult getDirectionsDetails(String origin,String destination,TravelMode mode) {
    DateTime now = new DateTime();
    try {
        return DirectionsApi.newRequest(getGeoContext())
                .mode(mode)
                .origin(origin)
                .destination(destination)
                .departureTime(now)
                .await();
    } catch (ApiException e) {
        e.printStackTrace();
        return null;
    } catch (InterruptedException e) {
        e.printStackTrace();
        return null;
    } catch (IOException e) {
        e.printStackTrace();
        return null;
    }
}

    @Override
    public void onMapReady(GoogleMap googleMap) {
    if(!originText.isEmpty())
    {
            setupGoogleMapScreenSettings(googleMap);
            DirectionsResult results = getDirectionsDetails(originText,addressText, mode1);
            if (results != null) {
                addPolyline(results, googleMap);
                positionCamera(results.routes[overview], googleMap);
                addMarkersToMap(results, googleMap);
            }
    }else{
                mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(latt, lngg);
        mMap.addMarker(new MarkerOptions().position(sydney).title(title));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,14));

    }
    }

    private void setupGoogleMapScreenSettings(GoogleMap mMap) {
        mMap.setBuildingsEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.setTrafficEnabled(true);
        UiSettings mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);
    }

    private void addMarkersToMap(DirectionsResult results, GoogleMap mMap) {
        mMap.addMarker(new MarkerOptions().position(new LatLng(results.routes[overview].legs[overview].startLocation.lat,results.routes[overview].legs[overview].startLocation.lng)).title(originText));
      //  mMap.addMarker(new MarkerOptions().position(new LatLng(results.routes[overview].legs[overview].endLocation.lat,results.routes[overview].legs[overview].endLocation.lng)).title(results.routes[overview].legs[overview].startAddress).snippet(getEndLocationTitle(results)));
    }

    private void positionCamera(DirectionsRoute route, GoogleMap mMap) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(route.legs[overview].startLocation.lat, route.legs[overview].startLocation.lng), 12));
    }

    private void addPolyline(DirectionsResult results, GoogleMap mMap) {
        List<LatLng> decodedPath = PolyUtil.decode(results.routes[overview].overviewPolyline.getEncodedPath());
        polyline = mMap.addPolyline(new PolylineOptions().addAll(decodedPath));
        mMap.addPolyline(new PolylineOptions().addAll(decodedPath));
    }

    private String getEndLocationTitle(DirectionsResult results){
        return  "Time :"+ results.routes[overview].legs[overview].duration.humanReadable + " Distance :" + results.routes[overview].legs[overview].distance.humanReadable;
    }

    private GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext();
        return geoApiContext
                .setQueryRateLimit(3)
                .setApiKey("AIzaSyC3db2_p93Rfd1heCsaHsTu7X0GEmjZvWo")
                .setConnectTimeout(1, TimeUnit.SECONDS)
                .setReadTimeout(1, TimeUnit.SECONDS)
                .setWriteTimeout(1, TimeUnit.SECONDS);
    }

    public void getRequest(){
        String request_url = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + place_id + "&key=AIzaSyC3db2_p93Rfd1heCsaHsTu7X0GEmjZvWo";

        detailRequest = Volley.newRequestQueue(getActivity());
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, request_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Gson gson = new Gson();
                        JsonObject detailLocation = gson.fromJson(response.toString(), JsonObject.class);



                        try {

                            addressText = detailLocation.getAsJsonObject().get("result").getAsJsonObject().get("formatted_address").getAsString();




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
}