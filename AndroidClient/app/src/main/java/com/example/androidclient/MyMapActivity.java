package com.example.androidclient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MyMapActivity extends Activity implements OnMapReadyCallback {

    private MapFragment mapFragment = null;
    private GoogleMap myMap = null;
    LatLng start = null;
    LatLng end = null;
    double totalDistance = 0;
    double totalDuration = 0;
    String startAdress;
    String endAdress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_map);

        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onMapReady(GoogleMap map) {
        myMap = map;
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(SelectedEventActivity.START_END_LATLNG);

        //start = new LatLng(45.782640, 4.878073);
        start = new LatLng(bundle.getDouble("startLat"), bundle.getDouble("startLng"));
        double sourcelat = start.latitude;
        double sourcelog = start.longitude;
        startAdress = bundle.getString("startAdress");

        //end = new LatLng(45.757198, 4.831219);
        end = new LatLng(bundle.getDouble("endLat"), bundle.getDouble("endLng"));
        double destlat = end.latitude;
        double destlog = end.longitude;
        endAdress = bundle.getString("endAdress");

        myMap.setMyLocationEnabled(true);
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(start, 13));

        String url = makeURL(sourcelat, sourcelog, destlat, destlog);
        ConnectAsyncTask cat = new ConnectAsyncTask(url);
        cat.execute();
    }

    public void drawPath(String result) {

        try {
            //Tranform the string into a json object
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONArray legsArray = routes.getJSONArray("legs");
            JSONObject leg = legsArray.getJSONObject(0);
            JSONObject distanceObject = leg.getJSONObject("distance");
            String distanceValue = distanceObject.getString("value");
            totalDistance = Double.parseDouble(distanceValue) / 1000;
            JSONObject durationObject = leg.getJSONObject("duration");
            String durationValue = durationObject.getString("value");
            totalDuration = Double.parseDouble(durationValue) / 60;

            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);

            for (int z = 0; z < list.size() - 1; z++) {
                LatLng src = list.get(z);
                LatLng dest = list.get(z + 1);
                Polyline line = myMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude, dest.longitude))
                        .width(4)
                        .color(Color.BLUE).geodesic(true));
            }

        } catch (JSONException e) {
            e.printStackTrace();
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

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    public String makeURL(double sourcelat, double sourcelog, double destlat, double destlog) {
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
        urlString.append(Double.toString(destlog));
        urlString.append("&sensor=false&mode=driving&alternatives=true");
        return urlString.toString();
    }

    public String makeItineraryURLServer(double sourcelat, double sourcelog, double destlat, double destlog) {
        StringBuilder urlString = new StringBuilder();
        urlString.append("http://10.0.2.2/itinerary");
        urlString.append("?dlat=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append("&dlong");
        urlString.append(Double.toString(sourcelog));
        urlString.append("&alat=");// to
        urlString .append(Double.toString(destlat));
        urlString.append("&along=");
        urlString.append(Double.toString(destlog));
        return urlString.toString();
    }

    protected class ConnectAsyncTask extends AsyncTask<Void, Void, String> {
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
            progressDialog.dismiss();
            if (result != null) {
                drawPath(result);
                MarkerOptions startMarker = new MarkerOptions()
                        .title("Start")
                        .snippet(startAdress)
                        .position(start);
                myMap.addMarker(startMarker);
                MarkerOptions endMarker = new MarkerOptions()
                        .title("End")
                        .snippet(endAdress)
                        .position(end);
                myMap.addMarker(endMarker);
            }
        }
    } ;

}
