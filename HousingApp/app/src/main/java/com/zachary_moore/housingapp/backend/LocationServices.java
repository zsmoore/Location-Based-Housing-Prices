package com.zachary_moore.housingapp.backend;

import android.util.Log;

import java.io.IOException;

public class LocationServices {

    private static final String TAG = "LocationServices";

    public static String makeCalls(String address, String citystatezip) {
        try {
            String zpid = APIWrapper.getZPID(address, citystatezip);
            String price = APIWrapper.getHouseCost(zpid);
            return price;
        } catch (IOException e){
            Log.e(TAG, "Error in makeCalls", e);
            return "ERROR";
        }
    }

}
