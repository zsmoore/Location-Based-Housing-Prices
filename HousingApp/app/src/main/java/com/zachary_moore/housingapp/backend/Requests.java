package com.zachary_moore.housingapp.backend;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Requests {

    private static final String TAG = "Requests";

    public static InputStream getRequest(String url){

        InputStream in = null;
        try {
            URL sendUrl = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) sendUrl.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream());
        } catch (IOException e) {
            Log.e(TAG, "Error in get params", e);
        }

        return in;
    }

    public static XmlPullParser parse(InputStream in) throws XmlPullParserException, IOException {
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(in, null);
        return parser;
    }

    public static HashMap<String, String> readFeed(XmlPullParser in, List<String> tags) throws XmlPullParserException, IOException {

        HashMap<String, String> result = new HashMap<>();
        for (String tag : tags) {
            result.put(tag, "");
        }

        int eventType = in.getEventType();
        while(eventType != XmlPullParser.END_DOCUMENT) {

            if (eventType == XmlPullParser.START_TAG) {
                String tag = in.getName();
                if (result.containsKey(tag)) {
                    result.put(tag, in.nextText());
                }
            }
            eventType = in.next();
        }
        return result;
    }

    public static HashMap<String, String> getParamsFromXML(String url, List<String> params) {

        InputStream in = getRequest(url);
        try {
            XmlPullParser parser = parse(in);
            return readFeed(parser, params);
        } catch(XmlPullParserException | IOException e) {
            Log.e(TAG, "Error in get params", e);
            return null;
        }
    }



}
