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
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UpdateTransportRequestActivity extends Activity {

    protected static DateFormat USER_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy:kk:mm");

    private TransportRequest transportRequestToUpdate;
    EditText eventSummaryToUpdate;
    EditText eventStartTimeToUpdate;
    EditText eventStartLocationToUpdate;
    EditText eventEndLocationToUpdate;
    LatLng eventStartLatLngToUpdate;
    LatLng eventEndLatLngToUpdate;
    Button updateTransportRequest;
    Button verifyLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_transport_request);

        Intent intent = getIntent();
        final long transportRequestID = intent.getLongExtra("transportRequestID",-1);

        if(transportRequestID!=-1) {
            final TransportRequestDAO trDAO = new TransportRequestDAO(getApplicationContext());
            trDAO.open();
            transportRequestToUpdate = trDAO.select(transportRequestID);
            trDAO.close();

            eventStartLatLngToUpdate = new LatLng(transportRequestToUpdate.getStartLat(),transportRequestToUpdate.getStartLng());
            eventEndLatLngToUpdate  = new LatLng(transportRequestToUpdate.getEventLat(),transportRequestToUpdate.getEventLng());

            eventSummaryToUpdate = (EditText) findViewById(R.id.event_summary_to_update);
            eventSummaryToUpdate.setText(transportRequestToUpdate.getEventSummary());

            eventStartTimeToUpdate = (EditText) findViewById(R.id.event_startTime_to_update);
            eventStartTimeToUpdate.setText(USER_DATE_FORMAT.format(new Date(transportRequestToUpdate.getEventBeginTime())));

            eventStartLocationToUpdate = (EditText) findViewById(R.id.eventStartLocation_to_update);
            eventStartLocationToUpdate.setText(transportRequestToUpdate.getStartAddress());

            eventEndLocationToUpdate = (EditText) findViewById(R.id.eventEndLocation_to_update);
            eventEndLocationToUpdate.setText(transportRequestToUpdate.getEventAddress());

            updateTransportRequest = (Button) findViewById(R.id.updateTransportRequest);
            updateTransportRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean errorsInDate = false;
                    Date eventStartDate = null;
                    try{
                        eventStartDate = USER_DATE_FORMAT.parse(eventStartTimeToUpdate.getText().toString());
                    } catch (ParseException e) {
                        errorsInDate = true;
                        Toast.makeText(getApplicationContext(), getString(R.string.wrongEnteredDate), Toast.LENGTH_LONG).show();
                    }
                    if(!errorsInDate){
                        transportRequestToUpdate.setEventSummary(eventSummaryToUpdate.getText().toString());
                        transportRequestToUpdate.setEventBeginTime(eventStartDate.getTime());
                        transportRequestToUpdate.setEventAddress(eventEndLocationToUpdate.getText().toString());
                        transportRequestToUpdate.setEventLat(eventEndLatLngToUpdate.latitude);
                        transportRequestToUpdate.setEventLng(eventEndLatLngToUpdate.longitude);
                        transportRequestToUpdate.setStartAddress(eventStartLocationToUpdate.getText().toString()) ;
                        transportRequestToUpdate.setStartLat(eventStartLatLngToUpdate.latitude);
                        transportRequestToUpdate.setStartLng(eventStartLatLngToUpdate.longitude);
                        trDAO.open();
                        trDAO.update(transportRequestToUpdate);
                        trDAO.close();
                        Toast.makeText(getApplicationContext(), getString(R.string.modifiedTransportRequest), Toast.LENGTH_LONG).show();
                    }
                }
            });

            verifyLocations = (Button) findViewById(R.id.verify_locations2);
            verifyLocations.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VerifyLocationTask2 vlt = new VerifyLocationTask2(eventStartLocationToUpdate);
                    vlt.execute();
                    VerifyLocationTask2 vlt2 = new VerifyLocationTask2(eventEndLocationToUpdate);
                    vlt2.execute();
                }
            });


        } else{
            //Display an error
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("eventSummaryToUpdate",eventSummaryToUpdate.getText().toString());
        savedInstanceState.putString("eventStartTimeToUpdate",eventStartTimeToUpdate.getText().toString());
        savedInstanceState.putString("eventStartLocationToUpdate",eventStartLocationToUpdate.getText().toString());
        savedInstanceState.putString("eventEndLocationToUpdate",eventEndLocationToUpdate.getText().toString());
        savedInstanceState.putDouble("eventStartLatLngToUpdateLat", eventStartLatLngToUpdate.latitude);
        savedInstanceState.putDouble("eventStartLatLngToUpdateLng", eventStartLatLngToUpdate.longitude);
        savedInstanceState.putDouble("eventEndLatLngToUpdateLat",eventEndLatLngToUpdate.latitude);
        savedInstanceState.putDouble("eventEndLatLngToUpdateLng",eventEndLatLngToUpdate.longitude);
        savedInstanceState.putLong("transportRequestToUpdateId", transportRequestToUpdate.getId());
        savedInstanceState.putString("transportRequestToUpdateEventId", transportRequestToUpdate.getEventID());

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if(savedInstanceState!=null) {
            eventSummaryToUpdate.setText(savedInstanceState.getString("eventSummaryToUpdate"));
            eventStartTimeToUpdate.setText(savedInstanceState.getString("eventStartTimeToUpdate"));
            eventStartLocationToUpdate.setText(savedInstanceState.getString("eventStartLocationToUpdate"));
            eventEndLocationToUpdate.setText(savedInstanceState.getString("eventEndLocationToUpdate"));
            eventStartLatLngToUpdate = new LatLng(savedInstanceState.getDouble("eventStartLatLngToUpdateLat"), savedInstanceState.getDouble("eventStartLatLngToUpdateLng"));
            eventEndLatLngToUpdate = new LatLng(savedInstanceState.getDouble("eventEndLatLngToUpdateLat"), savedInstanceState.getDouble("eventEndLatLngToUpdateLng"));
            long transportRequestToUpdateId = savedInstanceState.getLong("transportRequestToUpdateId");
            String transportRequestToUpdateEventId = savedInstanceState.getString("transportRequestToUpdateEventId");
            transportRequestToUpdate = new TransportRequest();
            transportRequestToUpdate.setId(transportRequestToUpdateId);
            transportRequestToUpdate.setEventID(transportRequestToUpdateEventId);
        }
    }

    public String makeGeocodingRequestURL(String adress) throws UnsupportedEncodingException {
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/geocode/json?address=");
        urlString.append(URLEncoder.encode(adress, "UTF-8"));
        urlString.append("&key=");
        urlString.append(getString(R.string.Geocoding_API_KEY));
        return urlString.toString();
    }

    protected class VerifyLocationTask2 extends AsyncTask<Void, Void, Pair<LatLng, String>> {
        private ProgressDialog progressDialog;
        EditText adressView;

        VerifyLocationTask2(EditText adr) {
            adressView = adr;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(UpdateTransportRequestActivity.this);
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
                ll = SelectedEventActivity.getLatLng(json);
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
                if (adressView.equals(eventStartLocationToUpdate)) {
                    eventStartLatLngToUpdate = result.first;
                } else {
                    eventEndLatLngToUpdate = result.first;
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
