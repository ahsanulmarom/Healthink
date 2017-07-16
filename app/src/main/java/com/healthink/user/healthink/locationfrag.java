package com.healthink.user.healthink;


import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class locationfrag extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;
    GPSTracker gps;
    double latitude, longitude;
    private RequestQueue requestQueue;

    public static locationfrag newInstance() {
        // Required empty public constructor
        return new locationfrag();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_location, container, false);
        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately
        gps = new GPSTracker(getActivity());
        requestQueue = Volley.newRequestQueue(getActivity());

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                //Market lokasi lain pengguna
                try {
                    Geocoder geocoder = new Geocoder(getActivity());
                    List<Address> list = geocoder.getFromLocationName("Jalan Kolonel Sugiyono, Pati", 1);
                    if(list.size() > 0 && list != null) {
                            Address add = list.get(0);
                            googleMap.addMarker(new MarkerOptions().position(
                                    new LatLng(add.getLatitude(), add.getLongitude()))
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                                    .title("Test").snippet("Nyoba"));
                        }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //Marker lokasi user
                if (gps.canGetLocation) {
                    LatLng myLoc = new LatLng(gps.getLatitude(), gps.getLongitude());
                    googleMap.addMarker(new MarkerOptions().position(myLoc).title("Your Location").snippet("You are here."));

                    // For zooming automatically to the location of the marker
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(myLoc).zoom(15).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            }
        });
        return view;
    }

    public void getLatLng(String nama, String address) throws IOException {

    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}