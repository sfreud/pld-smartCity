package com.example.androidclient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;


public class SelectedEventActivity extends Activity {
    public final static String START_END_LATLNG = "com.example.androidclient.START_END_LATLNG";
    EditText selectedEventStartLocation = null;
    EditText selectedEventEndLocation = null;
    LatLng selectedEventStartLatLng = null;
    LatLng selectedEventEndLatLng = null;
    String summary = null;
    String startTime = null;
    String endLocation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_event);

        Intent intent = getIntent();
        ArrayList<CharSequence> eventToList = intent.getCharSequenceArrayListExtra(UpcomingEventsActivity.SELECTED_EVENT);
        summary = (String) eventToList.get(0);
        startTime = (String) eventToList.get(1);
        endLocation = (String) eventToList.get(2);

        TextView selectedEventSummary = (TextView) findViewById(R.id.selected_event_summary);
        selectedEventSummary.setText(summary);
        TextView selectedEventStartTime = (TextView) findViewById(R.id.selected_event_startTime);
        selectedEventStartTime.setText(startTime);
        selectedEventStartLocation = (EditText) findViewById(R.id.selectedEventStartLocation);
        selectedEventEndLocation = (EditText) findViewById(R.id.selectedEventEndLocation);
        selectedEventEndLocation.setText(endLocation);

        Button verifyLocations = (Button) findViewById(R.id.verify_locations);
        verifyLocations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VerifyLocationTask vlt = new VerifyLocationTask(selectedEventStartLocation);
                vlt.execute();
                VerifyLocationTask vlt2 = new VerifyLocationTask(selectedEventEndLocation);
                vlt2.execute();
            }
        });

        Button selectedEventGetMap = (Button) findViewById(R.id.selected_event_get_Map);
        selectedEventGetMap.setOnClickListener(new View.OnClickListener() {
                                                   @Override
                                                   public void onClick(View v) {
                                                       Intent intent2 = new Intent(SelectedEventActivity.this, MyMapActivity.class);
                                                       double[] latlngArray = {selectedEventStartLatLng.latitude, selectedEventStartLatLng.longitude
                                                               , selectedEventEndLatLng.latitude, selectedEventEndLatLng.longitude};
                                                       intent2.putExtra(START_END_LATLNG, latlngArray);
                                                       startActivity(intent2);
                                                   }
                                               }
        );
    }

    public String makeGeocodingRequestURL(String adress) throws UnsupportedEncodingException {
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/geocode/json?address=");
        urlString.append(URLEncoder.encode(adress, "UTF-8"));
        urlString.append("&key=");
        urlString.append(getString(R.string.Geocoding_API_KEY));
        return urlString.toString();
    }

    public Pair<LatLng, String> getLatLng(String result) {
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
            ll = new LatLng(lat, lng);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return new Pair(ll, adress);
    }


    protected class VerifyLocationTask extends AsyncTask<Void, Void, Pair<LatLng, String>> {
        private ProgressDialog progressDialog;
        EditText adressView;

        VerifyLocationTask(EditText adr) {
            adressView = adr;
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
        protected Pair<LatLng, String> doInBackground(Void... params) {

            Pair<LatLng, String> ll = null;
            JSONParser jParser = new JSONParser();
            String json = null;
            try {
                json = jParser.getJSONFromUrl(makeGeocodingRequestURL(adressView.getText().toString()));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (json != null) {
                ll = getLatLng(json);
            }

            return ll;
        }

        @Override
        protected void onPostExecute(Pair<LatLng, String> result) {
            super.onPostExecute(result);
            progressDialog.hide();

            if (result != null) {
                adressView.setText(result.second);
                if (adressView.equals(selectedEventStartLocation)) {
                    selectedEventStartLatLng = result.first;
                } else {
                    selectedEventEndLatLng = result.first;
                }
            } else {
                adressView.setText("L'adresse n'existe pas");
            }
        }
    }

    ;
}
