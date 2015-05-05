package com.example.androidclient;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * An asynchronous task that handles the Calendar API event list retrieval.
 * Placing the API calls in their own task ensures the UI stays responsive.
 */
public class CreateEventTask extends AsyncTask<Void, Void, Void> {
    private CreateEventActivity mActivity;
    private Event event;

    /**
     * Constructor.
     *
     * @param activity UpcomingEventsActivity that spawned this task.
     */
    CreateEventTask(CreateEventActivity activity, Event event) {
        this.mActivity = activity;
        this.event = event;
    }

    /**
     * Background task to call Calendar API to fetch event list.
     *
     * @param params no parameters needed for this task.
     */
    @Override
    protected Void doInBackground(Void... params) {
        try {
            addEvent(event);

            String url= makeCreateEventURLServer(event);

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);
            SharedPreferences settings = mActivity.getSharedPreferences("preferences", Context.MODE_PRIVATE);
            String login = settings.getString("login", null);
            String pass = settings.getString("pass", null);
            Log.d("LOGIN_PASS",login+" "+pass);
            String s = Base64.encodeToString((login + ":" + pass).getBytes(), Base64.DEFAULT);
            post.setHeader("Accept", "text/html");
            post.setHeader("Host", "10.0.2.2:8182");
            post.setHeader("Authorization", "Basic " + s);

            HttpResponse httpResponse = httpClient.execute(post);
        } catch (final GooglePlayServicesAvailabilityIOException availabilityException) {
            mActivity.showGooglePlayServicesAvailabilityErrorDialog(
                    availabilityException.getConnectionStatusCode());
        } catch (UserRecoverableAuthIOException userRecoverableException) {
            mActivity.startActivityForResult(
                    userRecoverableException.getIntent(),
                    UpcomingEventsActivity.REQUEST_AUTHORIZATION);

        } catch (IOException e) {
            mActivity.updateStatus("The following error occurred: " +
                    e.getMessage());
        }
        return null;
    }

    private String makeCreateEventURLServer(Event e) throws UnsupportedEncodingException {
        StringBuilder urlString = new StringBuilder();
        urlString.append("http://10.0.2.2:8182/event");
        urlString.append("?summary=");// from
        urlString.append(e.getSummary());
        urlString.append("&location=");
        urlString.append(e.getLocation());
        urlString.append("&date=");
        Date date = new Date(e.getStart().getDateTime().getValue());
        DateFormat SERVER_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        urlString .append(URLEncoder.encode(SERVER_DATE_FORMAT.format(date), "UTF-8"));
        return urlString.toString();
    }

    private void addEvent(Event e) throws IOException {
        // Insert the new event
        Event createdEvent =  mActivity.mService.events().insert("primary", e).execute();
    }

    @Override
    protected void onPostExecute(Void result)
    {
        Toast.makeText(mActivity,"L'évènement a bien été créé",Toast.LENGTH_SHORT).show();
        mActivity.createThisEvent.setEnabled(false);
    }

    private String makeCreatedEventURLServer(Event e)
    {
        StringBuilder urlString = new StringBuilder();
        urlString.append("http://10.0.2.2/event");
        urlString.append("?summary=");// from
        urlString.append(event.getSummary());
        urlString.append("&location=");
        urlString.append(event.getLocation());
        urlString.append("&date=");// to
        urlString .append(event.getStart().getDate().toString());
        return urlString.toString();
    }
}