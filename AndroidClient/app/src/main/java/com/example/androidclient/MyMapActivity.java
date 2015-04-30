package com.example.androidclient;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.maps.MapView;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class MyMapActivity extends Activity implements OnMapReadyCallback {

    private MapFragment mapFragment = null;
    private GoogleMap myMap = null;
    LatLng start = null;
    LatLng end = null;
    double totalDistance = 0;
    double totalDuration = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_map);

       mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap map) {

        myMap = map;

        ConnectAsyncTask2 cat2 = new ConnectAsyncTask2("INSA de Lyon","Place Bellecour, Lyon");
        cat2.execute();
    }

    public void drawPath(String  result) {

        try {
            //Tranform the string into a json object
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONArray legsArray = routes.getJSONArray("legs");
            JSONObject leg = legsArray.getJSONObject(0);
            JSONObject distanceObject = leg.getJSONObject("distance");
            String distanceValue = distanceObject.getString("value");
            totalDistance = Double.parseDouble(distanceValue)/1000;
            JSONObject durationObject = leg.getJSONObject("duration");
            String durationValue = durationObject.getString("value");
            totalDuration = Double.parseDouble(durationValue)/60;

            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);

            for(int z = 0; z<list.size()-1;z++){
                LatLng src= list.get(z);
                LatLng dest= list.get(z+1);
                Polyline line = myMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude,   dest.longitude))
                        .width(4)
                        .color(Color.BLUE).geodesic(true));
            }

        }
        catch (JSONException e) {

        }
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng( (((double) lat / 1E5)),
                    (((double) lng / 1E5) ));
            poly.add(p);
        }

        return poly;
    }

    public String makeURL (double sourcelat, double sourcelog, double destlat, double destlog ){
        StringBuilder urlString = new StringBuilder();
        urlString.append("http://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString
                .append(Double.toString(sourcelog));
        urlString.append("&destination=");// to
        urlString
                .append(Double.toString(destlat));
        urlString.append(",");
        urlString.append(Double.toString( destlog));
        urlString.append("&sensor=false&mode=driving&alternatives=true");
        return urlString.toString();
    }

    public String makeGeocodingRequestURL(String adress) throws UnsupportedEncodingException {
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/geocode/json?address=");
        urlString.append(URLEncoder.encode(adress, "UTF-8"));
        urlString.append("&key=");
        urlString.append(getString(R.string.Geocoding_API_KEY));
        return urlString.toString();
    }

    public LatLng getLatLng(String result)
    {
        LatLng ll = null;
        try {
            //Tranform the string into a json object
            final JSONObject json = new JSONObject(result);
            JSONArray resultsArray = json.getJSONArray("results");
            JSONObject results = resultsArray.getJSONObject(0);
            JSONObject geometry = results.getJSONObject("geometry");
            JSONObject location = geometry.getJSONObject("location");
            Double lat = location.getDouble("lat");
            Double lng = location.getDouble("lng");
            Log.d("LatLng found",lat+","+lng);
            ll = new LatLng(lat,lng);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return ll;
    }

    public class ConnectAsyncTask extends AsyncTask<Void, Void, String> {
        private ProgressDialog progressDialog;
        String url;

        ConnectAsyncTask(String urlPass) {
            url = urlPass;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MyMapActivity.this);
            progressDialog.setMessage("Fetching route, Please wait...");
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            JSONParser jParser = new JSONParser();
            String json = jParser.getJSONFromUrl(url);
            return json;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.hide();
            if (result != null) {
                drawPath(result);
                MarkerOptions startMarker = new MarkerOptions()
                        .title("Start")
                        .position(start);
                myMap.addMarker(startMarker);
                MarkerOptions endMarker = new MarkerOptions()
                        .title("End")
                        .snippet(totalDistance + "km ("+totalDuration+" minutes)")
                        .position(end);
                myMap.addMarker(endMarker);
            }
        }
    };

    public class ConnectAsyncTask2 extends AsyncTask<Void, Void, List<LatLng>> {
        private ProgressDialog progressDialog;
        String startAdress;
        String endAdress;

        ConnectAsyncTask2(String sAdr,String eAdress) {
            startAdress = sAdr;
            endAdress = eAdress;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MyMapActivity.this);
            progressDialog.setMessage("Fetching route, Please wait...");
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }

        @Override
        protected List<LatLng> doInBackground(Void... params) {
            List<LatLng> lls = new ArrayList<LatLng>();

            LatLng ll1 = null;
            JSONParser jParser = new JSONParser();
            String json = null;
            try {
                json = jParser.getJSONFromUrl(makeGeocodingRequestURL(startAdress));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if(json!=null)
            {
                ll1 = getLatLng(json);
            }
            lls.add(ll1);

            LatLng ll2 = null;
            String json2 = null;
            try {
                json2 = jParser.getJSONFromUrl(makeGeocodingRequestURL(endAdress));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if(json2!=null)
            {
                ll2 = getLatLng(json2);
            }
            lls.add(ll2);

            return lls;
        }

        @Override
        protected void onPostExecute(List<LatLng> result) {
            super.onPostExecute(result);
            progressDialog.hide();

            //start = new LatLng(45.782640, 4.878073);
            start = result.get(0);
            double sourcelat = start.latitude;
            double sourcelog = start.longitude;

            //end = new LatLng(45.757198, 4.831219);
            end = result.get(1);
            double destlat = end.latitude;
            double destlog = end.longitude;

            LatLng mapCenter = new LatLng((start.latitude+end.latitude)/2, (start.longitude+end.longitude)/2);
            myMap.setMyLocationEnabled(true);
            myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapCenter, 13));

            String url = makeURL (sourcelat, sourcelog, destlat, destlog );
            ConnectAsyncTask cat = new ConnectAsyncTask(url);
            cat.execute();
        }
    };

  }
