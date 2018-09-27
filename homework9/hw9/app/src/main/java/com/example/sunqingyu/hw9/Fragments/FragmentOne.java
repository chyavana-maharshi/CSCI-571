package com.example.sunqingyu.hw9.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.sunqingyu.hw9.PlaceAutocompleteAdapter;
import com.example.sunqingyu.hw9.R;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class FragmentOne extends Fragment {

    EditText keyword,distance,location;
    Spinner category;
    Button searchB;
    RadioGroup radioButtons;
    RadioButton radioB;
    TextView keywordE,locationE;
//    RadioGroup radioButtons;
//    RadioButton radioB;
//    EditText location;
    protected GeoDataClient mGeoDataClient;
    protected PlaceDetectionClient mPlaceDetectionClient;
    private AutoCompleteTextView fromText;
    private PlaceAutocompleteAdapter placeAutocompleteAdapter;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-38,-169),new LatLng(72,137)
    );

    public FragmentOne() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_one, container, false);
        fromText = (AutoCompleteTextView) view.findViewById(R.id.locationText);
        fromText.setEnabled(false);
        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radioButton);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radio = (RadioButton) group.findViewById(checkedId);
                String hah = radio.getText().toString();
                // checkedId is the RadioButton selected
                if(hah.equals("Current Location")){

                    fromText.setEnabled(false);
                }else{
                    fromText.setEnabled(true);
                    Log.d("66666",hah);
                }

            }
        });


        mGeoDataClient = Places.getGeoDataClient(getActivity(), null);
        mPlaceDetectionClient = Places.getPlaceDetectionClient(getActivity(), null);
        placeAutocompleteAdapter = new PlaceAutocompleteAdapter(getActivity(),mGeoDataClient,LAT_LNG_BOUNDS,null);
        fromText.setAdapter(placeAutocompleteAdapter);

        Button clearButton = (Button) view.findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            keyword = (EditText) view.findViewById(R.id.keywordText);
            keywordE = (TextView) view.findViewById(R.id.keywordError);
            locationE = (TextView) view.findViewById(R.id.locationError);
            searchB = (Button) view.findViewById(R.id.searchButton);
            category = (Spinner) view.findViewById(R.id.spinner);
            distance = (EditText) view.findViewById(R.id.distanceText);
            radioButtons = (RadioGroup)view.findViewById(R.id.radioButton);
            radioB = (RadioButton) view.findViewById(R.id.hereButton);
            location = (EditText) view.findViewById(R.id.locationText);

            keywordE.setVisibility(View.GONE);
            locationE.setVisibility(View.GONE);
            keyword.getText().clear();
            distance.getText().clear();
            category.setSelection(0);
            radioB.setChecked(true);
            location.setEnabled(false);
            location.getText().clear();
            }
        });

        return view;

    }


}
