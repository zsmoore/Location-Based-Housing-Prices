package com.zachary_moore.housingapp.front;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zachary_moore.housingapp.R;
import com.zachary_moore.housingapp.backend.APIWrapper;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private Geocoder mGeocoder;
    private List<Address> mLocations;
    private LocationManager locationManager;
    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.INTERNET}, 1);

        locationManager =  (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mGeocoder = new Geocoder(getApplicationContext());
        getLocation();

        final TextView t = (TextView) findViewById(R.id.text);

        Button b = (Button) findViewById(R.id.buttonSend);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Location last = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    mLocations = mGeocoder.getFromLocation(last.getLatitude(), last.getLongitude(), 5);
                } catch(IOException | SecurityException e) {
                    Log.e("BAD", "BAD", e);
                }

                for(Address address : mLocations) {
                    try {
                        if(address.getAddressLine(0).contains(",")) {
                            String ret = APIWrapper.getRegions(address.getAddressLine(0).split(",")[0], address.getAddressLine(0).split(",")[1].split(" ")[1].replaceAll("\\s+",""));
                            if (!ret.equals("")) {
                                t.setText(ret);
                                return;
                            }
                        }
                    } catch (IOException | XmlPullParserException e) {
                        Log.e("BAD",  "BAD", e);
                    }
                }
            }
        });
    }

    public void getLocation(){

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                try {
                    mLocations = mGeocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 50);
                } catch (IOException e) {
                    Log.e("BAD","BAD",e);
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
}
