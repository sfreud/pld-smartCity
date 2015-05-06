package com.example.androidclient;

import android.accounts.Account;
import android.accounts.AccountManager;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class MainActivity extends ActionBarActivity {
    private Button getEventsListButton = null, createAnEventButton = null, getTransportRequestListButton = null;
    private TextView t2;
    private EditText uname, pass;
    private LinearLayout llog, lopt;
    private static String urlBegin = "http://10.0.2.2:8182/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //instantiate views
        setContentView(R.layout.activity_main);
        uname = (EditText) findViewById(R.id.editTextUname);
        t2 = (TextView) findViewById(R.id.textView2);
        pass = (EditText)findViewById(R.id.editTextPassword);
        lopt = (LinearLayout) findViewById(R.id.layoutEvents);
        llog = (LinearLayout) findViewById(R.id.layoutLogin);


        //Check whether it's the app first launch or not. If it is, we ask to create an account or connect
        //to an existing one.
        //a single pref file for the whole app
        SharedPreferences settings = getSharedPreferences("preferences",MODE_PRIVATE);
        if(settings.getString("login", null)==null){
            String login = "";
            //pop a windows asking for authentication
            //get google account as default login
            t2.setText(getString(R.string.firstLaunch));
            AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
            Account[] list = manager.getAccounts();
            if(list.length!=0)
                login = list[0].name;
            llog.setVisibility(View.VISIBLE);
            lopt.setVisibility(View.INVISIBLE);

            uname.setText(login);
        }
        else{
            t2.setText(getString(R.string.welcomeBack));
        }

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
                Intent intent = new Intent(MainActivity.this, TransportRequestListActivity.class);
                startActivity(intent);
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
        int id = item.getItemId();

        //manually disconnect user
        if (id == R.id.disconnectItem) {
            SharedPreferences settings = getSharedPreferences("preferences",MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            //delete registered id's
            editor.remove("login");
            editor.remove("pass");
            editor.commit();
            //ask for a new authentication
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
            String uname = params[0], pass = params[1];
            requestType = params[2];
            HttpClient httpclient = new DefaultHttpClient();
            String url = urlBegin+params[2];
            String result = "";

            //create and execute an http request to the given url
            //The server sends back an acknowledgement code, which is treated in the onPostExecute(
            //method
            try {
                HttpPost post = new HttpPost(url);
                String s = Base64.encodeToString((uname + ":" + pass).getBytes(), Base64.DEFAULT);
                post.setHeader("Accept", "text/html");
                post.setHeader("Host", "10.0.2.2:8182");
                post.setHeader("Authorization", "Basic " + s);

                HttpResponse httpResponse = httpclient.execute(post);
                InputStream inputStream = httpResponse.getEntity().getContent();

                String line = "";
                BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
                while ((line = rd.readLine()) != null) {
                    result += line;
                }
            } catch (IOException e) {

            } catch (NullPointerException e) {
                return "3";
            }
            return result;
        }
        @Override
        protected void onPostExecute(String s){
            //Log.d("Result",s);
            switch(s){
                case "0":
                    if(requestType.equals("register")) {
                        Toast.makeText(getBaseContext(), getString(R.string.success), Toast.LENGTH_SHORT).show();
                    }
                    llog.setVisibility(View.GONE);
                    lopt.setVisibility(View.VISIBLE);
                    t2.setText(getString(R.string.connected));
                    SharedPreferences.Editor editor = getSharedPreferences("preferences",MODE_PRIVATE).edit();
                    editor.putString("login",uname.getText().toString());
                    editor.putString("pass",pass.getText().toString());
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