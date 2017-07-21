package com.healthink.user.healthink;


import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    Geocoder geocoder;
    private FirebaseAuth fAuth;
    private FirebaseAuth.AuthStateListener fStateListener;
    private static final String TAG = Home.class.getSimpleName();

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
        geocoder = new Geocoder(getActivity());

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
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference roleUser = database.getReference("userData");
                    roleUser.orderByChild("role").equalTo(1).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                                String key = singleSnapshot.getKey();
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference alamat = database.getReference("userData");
                                alamat.child(key).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                            String name = dataSnapshot.child("displayName").getValue(String.class);
                                            String pos = dataSnapshot.child("address").getValue(String.class);
                                            try {
                                                googleMap.addMarker(new MarkerOptions().position(
                                                        new LatLng(getLat(pos), getLng(pos)))
                                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                                                        .title(name).snippet(pos));
                                            } catch (IOException e1) {
                                                e1.printStackTrace();
                                            }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                //Marker lokasi user
                if (gps.canGetLocation) {
                    try {
                        LatLng myLoc = new LatLng(gps.getLatitude(), gps.getLongitude());
                        googleMap.addMarker(new MarkerOptions().position(myLoc)
                                .title("Your Location").snippet(getAddress(gps.getLatitude(), gps.getLongitude())));
                        // For zooming automatically to the location of the marker
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(myLoc).zoom(15).build();
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    } catch (IOException e) {
                        gps.showSettingAlert();
                    }
                }
            }
        });
        return view;
    }

    public String getAddress(double lat, double lng) throws IOException {
        List<Address> list = geocoder.getFromLocation(lat,lng,1);
        Address addresses = list.get(0);
        String wil = addresses.getSubLocality();
        String kec = addresses.getLocality();
        String kab = addresses.getSubAdminArea();
        return wil + ", " + kec + ", " + kab;
    }

    public double getLat(String add) throws IOException {
        List<Address> list = geocoder.getFromLocationName(add,1);
        Address addresses = list.get(0);
        return addresses.getLatitude();
    }

    public double getLng(String add) throws IOException {
        List<Address> list = geocoder.getFromLocationName(add,1);
        Address addresses = list.get(0);
        return addresses.getLongitude();
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