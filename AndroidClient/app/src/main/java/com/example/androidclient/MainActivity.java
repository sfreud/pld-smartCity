package com.example.androidclient;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.services.calendar.CalendarScopes;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class MainActivity extends ActionBarActivity {
    private Button getEventsListButton = null, createAnEventButton = null, getTransportRequestListButton = null;
    private ListView transportRequestListView;
    private TextView /*t1,*/t2;
    private static final String[] SCOPES = {CalendarScopes.CALENDAR, CalendarScopes.CALENDAR_READONLY};
    private EditText uname, pass;
    private LinearLayout llog, lopt;
    private static String urlBegin = "http://10.0.2.2:8182/";


    private static final String PREF_ACCOUNT_NAME = "accountName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        uname = (EditText) findViewById(R.id.editTextUname);
        t2 = (TextView) findViewById(R.id.textView2);
        pass = (EditText)findViewById(R.id.editTextPassword);

        lopt = (LinearLayout) findViewById(R.id.layoutEvents);
        llog = (LinearLayout) findViewById(R.id.layoutLogin);


        //Check whether it's the app first launch or not. If it is, we send the retrieved
        //events from google calendar to our own server.
        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
        if(settings.getString("login", null)==null){
            String login = "";
            //pop a windows asking for authentication
            t2.setText(getString(R.string.firstLaunch));
            AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
            Account[] list = manager.getAccounts();
            if(list.length!=0)
                login = list[0].name;
           // Log.d("account",login);

            llog.setVisibility(View.VISIBLE);
            lopt.setVisibility(View.INVISIBLE);

            uname.setText(login);
            //String login ="";

        }
        else{
            t2.setText(getString(R.string.welcomeBack));
        }
        setTitle(R.string.application_title);

        //t1 = (TextView) findViewById(R.id.textView1);


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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.disconnectItem) {
            SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.remove("login");
            editor.commit();
            llog.setVisibility(View.VISIBLE);
            lopt.setVisibility(View.INVISIBLE);
            t2.setText(getString(R.string.firstLaunch));
            return true;
        }

        return super.onOptionsItemSelected(item);
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


    public void connectToExistingAccount(View view)
    {
        if(uname.getText().toString().equals("")|| pass.getText().toString().equals("")){
            Toast.makeText(this, getString(R.string.incoherentCredentials), Toast.LENGTH_SHORT).show();
        }
        else if(uname.getText().toString().contains(":")){
            Toast.makeText(this, getString(R.string.semicolonInUname), Toast.LENGTH_SHORT).show();
        }
        else{
            t2.setText(getString(R.string.connecting));
            new RegisterTask().execute(uname.getText().toString(),pass.getText().toString(),"login");
        }
    }

    public void register(View view) {
        if(uname.getText().toString().equals("")|| pass.getText().toString().equals("")){
            Toast.makeText(this, getString(R.string.incoherentCredentials), Toast.LENGTH_SHORT).show();
        }
        else if(uname.getText().toString().contains(":")){
            Toast.makeText(this, getString(R.string.semicolonInUname), Toast.LENGTH_SHORT).show();
        }
        else{
            t2.setText(getString(R.string.connecting));
            new RegisterTask().execute(uname.getText().toString(), pass.getText().toString(), "register");
        }
    }

    private class RegisterTask extends AsyncTask<String, Void, String> {
        //Asynchronous call to the server. Sends events retrieved on google calendar. Should be used
        //once at app first launch, to sync' our server with gcalendar, and each time the user creates
        //another event from the app thereafter.
        String requestType;
        @Override
        protected String doInBackground(String... params) {
            InputStream inputStream = null;
            int count = params.length;
            String uname = params[0], pass = params[1];
            requestType = params[2];
            HttpClient httpclient = new DefaultHttpClient();
            String url = urlBegin+params[2];
            String result = "";

            // make GET request to the given URL
            HttpResponse httpResponse = null;
            try {
                HttpPost post = new HttpPost(url);
                String s = Base64.encodeToString((uname + ":" + pass).getBytes(), Base64.DEFAULT);
                //Log.d("Header",s);
                post.setHeader("Accept", "text/html");
                post.setHeader("Host", "10.0.2.2:8182");
                post.setHeader("Authorization", "Basic " + s);

                httpResponse = httpclient.execute(post);
                inputStream = httpResponse.getEntity().getContent();

                String line = "";
                BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
                while ((line = rd.readLine()) != null) {
                    result += line;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            //Not implemented yet, but we will probably send back either an acknoledgement code,
            //or a full itinerary formatted as  a string, or even several full itineraries.
            //Or it will be formatted differently.
            //Or we will write in a local db directly after each answer from the server and return nothing.
            try {
                //byte[] buffer = new byte[1000];
                //inputStream.read(buffer,0,1000);

                //return buffer.toString();
            } catch (NullPointerException e) {
                return "3";
                //} catch (IOException e) {
                //e.printStackTrace();
                //return e.toString();
            }
            //return inputStream.toString();
            return result;
        }
        @Override
        protected void onPostExecute(String s){
            Log.d("Result",s);
            switch(s){
                case "0":
                    if(requestType.equals("register")) {
                        Toast.makeText(getBaseContext(), getString(R.string.success), Toast.LENGTH_SHORT).show();
                    }
                    llog.setVisibility(View.GONE);
                    lopt.setVisibility(View.VISIBLE);
                    t2.setText(getString(R.string.connected));
                    SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
                    editor.putString("login",uname.getText().toString());
                    editor.commit();
                    break;
                case "1":
                    Toast.makeText(getBaseContext(), getString(R.string.unameTooLong), Toast.LENGTH_LONG).show();
                    t2.setText(R.string.firstLaunch);
                    break;
                case "2":
                    Toast.makeText(getBaseContext(), getString(R.string.semicolonInUname), Toast.LENGTH_SHORT).show();
                    t2.setText(R.string.firstLaunch);
                    break;

                case "3":
                    Toast.makeText(getBaseContext(), getString(R.string.unknownError), Toast.LENGTH_SHORT).show();
                    t2.setText(R.string.firstLaunch);
                    break;
                case "4":
                    Toast.makeText(getBaseContext(), getString(R.string.nameAlreadyUsed), Toast.LENGTH_SHORT).show();
                    t2.setText(R.string.firstLaunch);
                    break;
                default : //HTTP403. Tried to connect to an existing accout with bad credentials.
                    Toast.makeText(getBaseContext(), getString(R.string.wrongCredentials), Toast.LENGTH_SHORT).show();
                    t2.setText(R.string.firstLaunch);
                    break;
            }


        }
    }
}