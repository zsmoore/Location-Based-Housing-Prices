package com.zachary_moore.housingapp.backend;

import android.net.Uri;
import android.util.Log;
import android.util.Pair;

import com.zachary_moore.housingapp.BuildConfig;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class APIWrapper {

    private static final String ZWS_ID = BuildConfig.zws_id;
    private static final String BASE_URL = "www.zillow.com";
    private static final String WEBSERVICE = "webservice";
    private static final String DEEPSEARCH = "GetSearchResults.htm";
    private static final String ZESTIMATE = "GetZestimate.htm";
    private static final String REGIONCHILDREN = "GetRegionChildren.htm";
    private static final String TAG = "API";

    public static String getZPID(String address, String citystatezip) throws IOException{

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(BASE_URL)
                .appendPath(WEBSERVICE)
                .appendPath(DEEPSEARCH)
                .appendQueryParameter("zws-id", ZWS_ID)
                .appendQueryParameter("address", address)
                .appendQueryParameter("citystatezip", citystatezip);

        ArrayList<String> params = new ArrayList<>();
        params.add("zpid");

        HashMap<String, String> result = Requests.getParamsFromXML(builder.toString(), params);
        if (result != null) {
            return result.get("zpid");
        } else {
            throw new IOException();
        }
    }

    public static String getHouseCost(String zpid) throws IOException {

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(BASE_URL)
                .appendPath(WEBSERVICE)
                .appendPath(ZESTIMATE)
                .appendQueryParameter("zws-id", ZWS_ID)
                .appendQueryParameter("zpid", zpid);

        ArrayList<String> params = new ArrayList<>();
        params.add("low");
        params.add("high");

        HashMap<String, String> result = Requests.getParamsFromXML(builder.toString(), params);
        if(result != null) {
            return "High:\t" + result.get("high") + "\nLow:\t" + result.get("low");
        } else {
            throw new IOException();
        }
    }

    public static String getRegion(String cityName, String state) throws IOException {

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(BASE_URL)
                .appendPath(WEBSERVICE)
                .appendPath(REGIONCHILDREN)
                .appendQueryParameter("zws-id", ZWS_ID)
                .appendQueryParameter("state", state)
                .appendQueryParameter("city", cityName).appendQueryParameter("childtype", "neighborhood");

        ArrayList<String> params = new ArrayList<>();
        params.add("zindex");
        params.add("name");

        HashMap<String, String> result = Requests.getParamsFromXML(builder.toString(), params);
        if(result != null) {
            return "Closest neighborhood is:\t" + result.get("name") + "\nAverage cost is:\t" + NumberFormat.getCurrencyInstance(Locale.US).format(new Double(result.get("zindex")));
        } else {
            throw new IOException();
        }
    }

    public static String getRegions(String cityName, String state) throws IOException, XmlPullParserException {

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(BASE_URL)
                .appendPath(WEBSERVICE)
                .appendPath(REGIONCHILDREN)
                .appendQueryParameter("zws-id", ZWS_ID)
                .appendQueryParameter("state", state)
                .appendQueryParameter("city", cityName).appendQueryParameter("childtype", "neighborhood");

        ArrayList<Pair<String, String>> neighborhoods = new ArrayList<>();

        InputStream in = Requests.getRequest(builder.toString());
        XmlPullParser parser = Requests.parse(in);

        int eventType = parser.getEventType();
        while(eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if (parser.getName().equals("name")) {
                        eventType = parser.next();
                        String name = parser.getText();
                        while (eventType != XmlPullParser.START_TAG) {
                            eventType = parser.next();
                        }
                        if (parser.getName().equals("zindex")) {
                            parser.next();
                            String zindex = parser.getText();
                            neighborhoods.add(new Pair<>(name, zindex));
                        }
                    }
                    break;
                default:
                    break;
            }
            eventType = parser.next();
        }

        String resultString = "";
        for (Pair<String, String> hood : neighborhoods) {
            resultString += "Neighborhood Name:\t" + hood.first + "\nAverage Cost:\t" + hood.second + "\n";
        }
        Log.d(TAG, "getRegions: " + resultString);
        return resultString;
    }
 }
