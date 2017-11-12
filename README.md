# Location-Based-Housing-Prices
Android app that will get average housing price based on geo-location

This code requires a zillow API key in order to work.
If you look at the API Wrapper class there is the line `private static final String ZWS_ID = BuildConfig.zws_id;`
In order for the code to work you need to get your api key and load it into the buildconfig with it being named `zws_id`.  
Once that is set the code will be able to properly communicate to the Zillow API
