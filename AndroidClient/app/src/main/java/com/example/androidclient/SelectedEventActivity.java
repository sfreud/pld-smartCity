package com.example.androidclient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class SelectedEventActivity extends Activity {
    TextView selectedEventEndLocation = null;
    String summary = null;
    String startTime = null;
    String location = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_event);

        Intent intent = getIntent();
        ArrayList<CharSequence> eventToList = intent.getCharSequenceArrayListExtra(UpcomingEventsActivity.SELECTED_EVENT);
        summary = (String) eventToList.get(0);
        startTime = (String) eventToList.get(1);
        location = (String) eventToList.get(2);

        TextView infosSelectedEvent = (TextView) findViewById(R.id.infos_selected_event);
        infosSelectedEvent.setText(summary + "\n" + startTime + "\n" + location);

        selectedEventEndLocation = (TextView) findViewById(R.id.selectedEventEndLocation);

        Button verifyLocations = (Button) findViewById(R.id.verify_locations);
        verifyLocations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VerifyLocationTask vlt = new VerifyLocationTask(location);
                vlt.execute();
            }
        });
    }

    public String makeGeocodingRequestURL(String adress) throws UnsupportedEncodingException {
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/geocode/json?address=");
        urlString.append(URLEncoder.encode(adress, "UTF-8"));
        urlString.append("&key=");
        urlString.append(getString(R.string.Geocoding_API_KEY));
        return urlString.toString();
    }

    public Pair<LatLng,String> getLatLng(String result) {
        LatLng ll;
        String adress = null;
        try {
            //Tranform the string into a json object
            final JSONObject json = new JSONObject(result);
            JSONArray resultsArray = json.getJSONArray("results");
            JSONObject results = resultsArray.getJSONObject(0);
            adress = results.getString("formatted_address");
            JSONObject geometry = results.getJSONObject("geometry");
            JSONObject location = geometry.getJSONObject("location");
            Double lat = location.getDouble("lat");
            Double lng = location.getDouble("lng");
            Log.d("LatLng found", lat + "," + lng);
            ll = new LatLng(lat, lng);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return new Pair(ll,adress);
    }


    protected class VerifyLocationTask extends AsyncTask<Void, Void, Pair<LatLng,String>> {
        private ProgressDialog progressDialog;
        String adress;

        VerifyLocationTask(String adr) {
            adress = adr;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(SelectedEventActivity.this);
            progressDialog.setMessage("Vérification en cours...");
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }

        @Override
        protected Pair<LatLng,String> doInBackground(Void... params) {

            Pair<LatLng,String> ll = null;
            JSONParser jParser = new JSONParser();
            String json = null;
            try {
                json = jParser.getJSONFromUrl(makeGeocodingRequestURL(adress));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (json != null) {
                ll = getLatLng(json);
            }

            return ll;
        }

        @Override
        protected void onPostExecute(Pair<LatLng,String> result) {
            super.onPostExecute(result);
            progressDialog.hide();

            if(result!=null){
                selectedEventEndLocation.setText(result.second);
            }else{
                selectedEventEndLocation.setText("L'adresse n'existe pas");
            }
        }
    };
}
