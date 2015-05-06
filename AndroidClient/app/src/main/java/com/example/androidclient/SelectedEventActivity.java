package com.example.androidclient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;


public class SelectedEventActivity extends Activity {
    public final static String START_END_LATLNG = "com.example.androidclient.START_END_LATLNG";
    private EditText selectedEventStartLocation, selectedEventEndLocation;
    private Button verifyLocations, createTransportRequest,selectedEventGetMap;

    private LatLng selectedEventStartLatLng, selectedEventEndLatLng;
    private String eventID,summary;

    private long startTime;
    private String endLocation;
    private TransportRequest transportRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_event);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(UpcomingEventsActivity.SELECTED_EVENT);
        eventID = bundle.getString("eventID");
        summary = bundle.getString("summary");
        startTime = bundle.getLong("startTime");
        endLocation = bundle.getString("location");

        TextView selectedEventSummary = (TextView) findViewById(R.id.selected_event_summary);
        selectedEventSummary.setText(summary);
        TextView selectedEventStartTime = (TextView) findViewById(R.id.selected_event_startTime);
        selectedEventStartTime.setText((new Date(startTime)).toString());
        selectedEventStartLocation = (EditText) findViewById(R.id.selectedEventStartLocation);
        selectedEventEndLocation = (EditText) findViewById(R.id.selectedEventEndLocation);
        selectedEventEndLocation.setText(endLocation);

        verifyLocations = (Button) findViewById(R.id.verify_locations);
        verifyLocations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VerifyLocationTask vlt = new VerifyLocationTask(selectedEventStartLocation);
                vlt.execute();
                VerifyLocationTask vlt2 = new VerifyLocationTask(selectedEventEndLocation);
                vlt2.execute();
                createTransportRequest.setEnabled(true);
            }
        });

        createTransportRequest = (Button) findViewById(R.id.create_transport_request);
        createTransportRequest.setEnabled(false);
        createTransportRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            transportRequest = new TransportRequest(eventID, summary,
                    selectedEventEndLocation.getText().toString(),selectedEventEndLatLng.latitude,selectedEventEndLatLng.longitude,
                    selectedEventStartLocation.getText().toString(),selectedEventStartLatLng.latitude,selectedEventStartLatLng.longitude,
                    startTime);
            TransportRequestDAO trDAO = new TransportRequestDAO(getApplicationContext());
            trDAO.open();
            trDAO.add(transportRequest);
            trDAO.close();
            selectedEventGetMap.setEnabled(true);
            Toast.makeText(getApplicationContext(),getString(R.string.transportRequestCreated),Toast.LENGTH_SHORT).show();
            verifyLocations.setEnabled(false);
            createTransportRequest.setEnabled(false);
            createTransportRequest.setText(getString(R.string.transportRequestCreatedButton));
            }
        });

        selectedEventGetMap = (Button) findViewById(R.id.selected_event_get_Map);
        selectedEventGetMap.setEnabled(false);
        selectedEventGetMap.setOnClickListener(new View.OnClickListener() {
                                                   @Override
                                                   public void onClick(View v) {
           Intent intent2 = new Intent(SelectedEventActivity.this, MyMapActivity.class);
           Bundle bundle = new Bundle();
           bundle.putString("eventSummary",transportRequest.getEventSummary());
           bundle.putString("startAdress",transportRequest.getStartAddress());
           bundle.putDouble("startLat",transportRequest.getStartLat());
           bundle.putDouble("startLng",transportRequest.getStartLng());
           bundle.putString("endAdress",transportRequest.getEventAddress());
           bundle.putDouble("endLat",transportRequest.getEventLat());
           bundle.putDouble("endLng",transportRequest.getEventLng());
           intent2.putExtra(START_END_LATLNG, bundle);
           startActivity(intent2);
           }
        });
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

    public String makeGeocodingRequestURL(String adress) throws UnsupportedEncodingException {
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/geocode/json?address=");
        urlString.append(URLEncoder.encode(adress, "UTF-8"));
        urlString.append("&key=");
        urlString.append(getString(R.string.Geocoding_API_KEY));
        return urlString.toString();
    }

    public static Pair<LatLng, String> getLatLng(String result) {
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


    private class VerifyLocationTask extends AsyncTask<Void, Void, Pair<LatLng, String>> {
        private ProgressDialog progressDialog;
        EditText adressView;

        VerifyLocationTask(EditText adr) {
            adressView = adr;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(SelectedEventActivity.this);
            progressDialog.setMessage(getString(R.string.verifying));
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
            progressDialog.dismiss();
            if (result != null) {
                adressView.setText(result.second);
                if (adressView.equals(selectedEventStartLocation)) {
                    selectedEventStartLatLng = result.first;
                } else {
                    selectedEventEndLatLng = result.first;
                }
            } else {
                adressView.setText(getString(R.string.adressNotFound));
            }
        }
    };

    public class JSONParser {
        InputStream is = null;
        JSONObject jObj = null;
        String json = "";

        // constructor
        public JSONParser() {
        }

        public String getJSONFromUrl(String url) {
            // Making HTTP request
            try {
                // defaultHttpClient
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost post = new HttpPost(url);

                HttpResponse httpResponse = httpClient.execute(post);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }

                json = sb.toString();
                is.close();
            } catch (Exception e) {
                Log.e("Buffer Error", "Error converting result " + e.toString());
            }
            return json;

        }
    };

}
