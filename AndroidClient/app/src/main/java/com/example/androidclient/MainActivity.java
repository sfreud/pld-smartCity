package com.example.androidclient;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class MainActivity extends ActionBarActivity {
    Button getEventsListButton = null;
    Button createAnEventButton = null;
    Button getTransportRequestListButton = null;
    ListView transportRequestListView;
    TextView t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(R.string.application_title);

        t1 = (TextView) findViewById(R.id.textView1);

        transportRequestListView = (ListView) findViewById(R.id.transportRequestListView);

        getEventsListButton = (Button) findViewById(R.id.getEventsListButton);
        getEventsListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UpcomingEventsActivity.class);
                startActivity(intent);
            }
        });

        createAnEventButton = (Button) findViewById(R.id.createAnEventButton);
        createAnEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateEventActivity.class);
                startActivity(intent);
            }
        });

        getTransportRequestListButton = (Button) findViewById(R.id.getTransportRequestListButton);
        getTransportRequestListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransportRequestDAO trDAO = new TransportRequestDAO(getApplicationContext());
                trDAO.open();
                final List<TransportRequest> ltr = trDAO.selectAll();
                trDAO.close();
                List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
                HashMap<String, String> element;
                for (TransportRequest tr : ltr) {
                    element = new HashMap<String, String>();
                    element.put("summary", tr.getEventSummary());
                    element.put("startTime", (new Date(tr.getEventBeginTime())).toString());
                    element.put("startLocation", tr.getStartAddress());
                    element.put("eventLocation", tr.getEventAddress());
                    list.add(element);
                }
                ListAdapter adapter = new SimpleAdapter(MainActivity.this,
                        list,
                        R.layout.simple_transport__request_list,
                        new String[]{"summary", "startTime", "startLocation", "eventLocation"},
                        new int[]{R.id.eventSummary, R.id.eventStartTime, R.id.startLocation, R.id.eventLocation});
                transportRequestListView.setAdapter(adapter);
                transportRequestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        TransportRequest tr = ltr.get(position);
                        Context ctx = MainActivity.this;
                        Calendar cal = Calendar.getInstance();
                        //cal.setTimeInMillis((long));
                        cal.add(Calendar.SECOND, 10);
                        Intent intent = new Intent(ctx, AlarmReceiver.class);
                        Intent mapIntent = new Intent(ctx,MyMapActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("eventSummary",tr.getEventSummary());
                        bundle.putString("startAdress",tr.getStartAddress());
                        bundle.putDouble("startLat",tr.getStartLat());
                        bundle.putDouble("startLng",tr.getStartLng());
                        bundle.putString("endAdress",tr.getEventAddress());
                        bundle.putDouble("endLat",tr.getEventLat());
                        bundle.putDouble("endLng",tr.getEventLng());
                        intent.putExtra(SelectedEventActivity.START_END_LATLNG, bundle);
                        mapIntent.putExtra(SelectedEventActivity.START_END_LATLNG, bundle);
                        PendingIntent sender = PendingIntent.getBroadcast(ctx, 0, intent, 0);

                        // Get the AlarmManager service
                        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
                        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);

                        startActivity(mapIntent);
                    }
                });
            }
        });

        //SendingGetRequestTask sg = new SendingGetRequestTask();
        //sg.execute();

       /* AuthenficationRequestTask art = new AuthenficationRequestTask();
        art.execute();*/
    }


    protected class SendingGetRequestTask extends AsyncTask<Void, Void, String> {
        String contentAsString = "";

        @Override
        protected String doInBackground(Void... params) {
            InputStream inputStream = null;
            String result = "";
            try {

                // create HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                String url = "http://10.0.2.2:8182"/*"http://www.android.com"*/;
                // make GET request to the given URL
                HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

                // receive response as inputStream
                inputStream = httpResponse.getEntity().getContent();

                // convert inputstream to string
                if (inputStream != null)
                    result = getStringFromInputStream(inputStream);
                else
                    result = "Did not work!";

            } catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());
            }
            contentAsString = result;
            Log.d("ENDING", "Requete get terminee");
            Log.d("Page Web", contentAsString);
        /*try {

            URL url = new URL("http://www.android.com");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            contentAsString = getStringFromInputStream(in);
            urlConnection.disconnect();
            Log.d("ENDING", "Requete get terminee");
            Log.d("Page Web",contentAsString);
        } catch (Exception e) {
            contentAsString = e.toString();
            e.printStackTrace();
        }*/

            return contentAsString;
        }

        @Override
        protected void onPostExecute(String result) {
            t1.setText(contentAsString);
        }

        // convert InputStream to String
        private String getStringFromInputStream(InputStream is) {

            BufferedReader br = null;
            StringBuilder sb = new StringBuilder();

            String line;
            try {

                br = new BufferedReader(new InputStreamReader(is));
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return sb.toString();
        }
    }

    ;

    protected class AuthenficationRequestTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            InputStream inputStream = null;
            String result = "";
            try {

                String login = "loginB";
                String pwd = "secret";
                String url = "http://10.0.2.2:8183/trace";
                HttpPost post = new HttpPost(url);
                HttpClient client = new DefaultHttpClient();

                Log.v("AUTHENTICATION URL = ", url);
                post.addHeader("Authorization", "Basic " + getCredentials(login, pwd));
                HttpResponse httpResponse = client.execute(post);

                inputStream = httpResponse.getEntity().getContent();

                // convert inputstream to string
                if (inputStream != null)
                    result = getStringFromInputStream(inputStream);
                else
                    result = "Did not work!";

                Log.v("SERVER RESPONSE DATA = ", result);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        private String getCredentials(String u, String p) {
            Log.v("USER NAME = ", u);
            Log.v("PASSWORD = ", p);
            return (Base64.encodeToString((u + ":" + p).getBytes(), Base64.DEFAULT));
        }

        // convert InputStream to String
        private String getStringFromInputStream(InputStream is) {

            BufferedReader br = null;
            StringBuilder sb = new StringBuilder();

            String line;
            try {

                br = new BufferedReader(new InputStreamReader(is));
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return sb.toString();
        }
    }

    ;


}