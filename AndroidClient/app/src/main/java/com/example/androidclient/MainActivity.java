package com.example.androidclient;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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


public class MainActivity extends ActionBarActivity {
    Button getEventsListButton= null;
    Button getMapButton = null;
    TextView t1 = null;
    TextView t2 =  null;
    TextView t3 =  null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getEventsListButton = (Button) findViewById(R.id.button);
        getMapButton = (Button) findViewById(R.id.button2);
        t1 = (TextView) findViewById(R.id.textView);
        t2 = (TextView) findViewById(R.id.textView2);
        t3 = (TextView) findViewById(R.id.textView3);

        getEventsListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UpcomingEventsActivity.class);
                startActivity(intent);
            }
        });

        getMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyMapActivity.class);
                startActivity(intent);
            }
        });

        //SendingGetRequestTask sg = new SendingGetRequestTask();
        //sg.execute();

       /* AuthenficationRequestTask art = new AuthenficationRequestTask();
        art.execute();*/
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                if(inputStream != null)
                    result = getStringFromInputStream(inputStream);
                else
                    result = "Did not work!";

            } catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());
            }
            contentAsString = result;
            Log.d("ENDING", "Requete get terminee");
            Log.d("Page Web",contentAsString);
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
            t3.setText(contentAsString);
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
    };

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
                if(inputStream != null)
                    result = getStringFromInputStream(inputStream);
                else
                    result = "Did not work!";

                Log.v("SERVER RESPONSE DATA = ", result);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        private String getCredentials(String u, String p)
        {
            Log.v("USER NAME = ",u);
            Log.v("PASSWORD = ",p);
            return(Base64.encodeToString((u + ":" + p).getBytes(),Base64.DEFAULT));
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
    };



}