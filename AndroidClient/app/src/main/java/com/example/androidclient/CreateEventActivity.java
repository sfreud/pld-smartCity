package com.example.androidclient;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;


public class CreateEventActivity extends Activity {

    /**
     * A Calendar service object used to query or modify calendars via the
     * Calendar API. Note: Do not confuse this class with the
     * com.google.api.services.calendar.model.Calendar class.
     */
    com.google.api.services.calendar.Calendar mService;

    private GoogleAccountCredential credential;
    private TextView mStatusText;
    private EditText enteredEventSummary;
    private EditText enteredEventLocation;
    private EditText enteredEventStartDate;
    private EditText enteredEventEndDate;
    private Button createThisEvent;
    private final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    private final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

    private static DateFormat USER_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy:kk:mm");

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {CalendarScopes.CALENDAR, CalendarScopes.CALENDAR_READONLY};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        mStatusText = (TextView) findViewById(R.id.mStatusText2);

        enteredEventSummary = (EditText) findViewById(R.id.enteredEventSummary);
        enteredEventLocation = (EditText) findViewById(R.id.enteredEventLocation);
        enteredEventStartDate = (EditText) findViewById(R.id.enteredEventStartDate);
        enteredEventEndDate =  (EditText) findViewById(R.id.enteredEventEndDate);

        createThisEvent = (Button) findViewById(R.id.createThisEvent);
        createThisEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Event event = new Event();
                event.setSummary(enteredEventSummary.getText().toString());
                event.setLocation(enteredEventLocation.getText().toString());

                boolean errorsInDates = false;
                Date startDate = null, endDate = null;
                try {
                    startDate = USER_DATE_FORMAT.parse(enteredEventStartDate.getText().toString());
                } catch (ParseException e) {
                    errorsInDates = true;
                    Toast.makeText(getApplicationContext(),getString(R.string.wrongBeginDate),Toast.LENGTH_LONG).show();
                }
                try {
                    endDate = USER_DATE_FORMAT.parse(enteredEventEndDate.getText().toString());
                } catch (ParseException e1) {
                    errorsInDates = true;
                    Toast.makeText(getApplicationContext(),getString(R.string.wrongEndDate),Toast.LENGTH_LONG).show();
                }
                if(!errorsInDates) {
                    DateTime start = new DateTime(startDate, TimeZone.getTimeZone("UTC"));
                    event.setStart(new EventDateTime().setDateTime(start));
                    DateTime end = new DateTime(endDate, TimeZone.getTimeZone("UTC"));
                    event.setEnd(new EventDateTime().setDateTime(end));

                    CreateEventTask cet = new CreateEventTask(event);
                    cet.execute();
                }
            }
        });

        // Initialize credentials and calendar service.
        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
        credential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));

        mService = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("AndroidClient")
                .build();
    }

    /**
     * Called whenever this activity is pushed to the foreground, such as after
     * a call to onCreate().
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (isGooglePlayServicesAvailable()) {
            emailKnownAndDeviceOnline();
        } else {
            mStatusText.setText(getString(R.string.playServiceRequired));
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     *
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode  code indicating the result of the incoming
     *                    activity result.
     * @param data        Intent (containing result data) returned by incoming
     *                    activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode == RESULT_OK) {
                    emailKnownAndDeviceOnline();
                } else {
                    isGooglePlayServicesAvailable();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        credential.setSelectedAccountName(accountName);
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.commit();
                        emailKnownAndDeviceOnline();
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    mStatusText.setText(getString(R.string.unspecifiedAccount));
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    emailKnownAndDeviceOnline();
                } else {
                    chooseAccount();
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * If the email address isn't known yet, then call chooseAccount() method
     * so the user can pick an account.
     */
    private void emailKnownAndDeviceOnline() {
        if (credential.getSelectedAccountName() == null) {
            chooseAccount();
        } else {
            if (!isDeviceOnline()) {
                mStatusText.setText(getString(R.string.noConnection));
            }
        }
    }

    /**
     * Show a status message in the list header TextView; called from background
     * threads and async tasks that need to update the UI (in the UI thread).
     *
     * @param message a String to display in the UI header TextView.
     */
    public void updateStatus(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mStatusText.setText(message);
            }
        });
    }

    /**
     * Starts an activity in Google Play Services so the user can pick an
     * account.
     */
    private void chooseAccount() {
        startActivityForResult(
                credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
    }

    /**
     * Checks whether the device currently has a network connection.
     *
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date. Will
     * launch an error dialog for the user to update Google Play Services if
     * possible.
     *
     * @return true if Google Play Services is available and up to
     * date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        final int connectionStatusCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
            return false;
        } else if (connectionStatusCode != ConnectionResult.SUCCESS) {
            return false;
        }
        return true;
    }

    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     *
     * @param connectionStatusCode code describing the presence (or lack of)
     *                             Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                        connectionStatusCode,
                        CreateEventActivity.this,
                        REQUEST_GOOGLE_PLAY_SERVICES);
                dialog.show();
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


    private class CreateEventTask extends AsyncTask<Void, Void, Void> {
        private Event event;

        CreateEventTask(Event event) {
            this.event = event;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                addEvent(event);

                String url= makeCreateEventURLServer(event);

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost post = new HttpPost(url);
                SharedPreferences settings = getSharedPreferences("preferences", Context.MODE_PRIVATE);
                String login = settings.getString("login", null);
                String pass = settings.getString("pass", null);
                Log.d("LOGIN_PASS", login + " " + pass);
                String s = Base64.encodeToString((login + ":" + pass).getBytes(), Base64.DEFAULT);
                post.setHeader("Accept", "text/html");
                post.setHeader("Host", "10.0.2.2:8182");
                post.setHeader("Authorization", "Basic " + s);

                HttpResponse httpResponse = httpClient.execute(post);
            } catch (final GooglePlayServicesAvailabilityIOException availabilityException) {
                showGooglePlayServicesAvailabilityErrorDialog(
                        availabilityException.getConnectionStatusCode());
            } catch (UserRecoverableAuthIOException userRecoverableException) {
                startActivityForResult(
                        userRecoverableException.getIntent(),
                        UpcomingEventsActivity.REQUEST_AUTHORIZATION);

            } catch (IOException e) {
                updateStatus("The following error occurred: " +
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
            Event createdEvent =  mService.events().insert("primary", e).execute();
        }

        @Override
        protected void onPostExecute(Void result)
        {
            Toast.makeText(getApplicationContext(),getString(R.string.eventCreated),Toast.LENGTH_SHORT).show();
            createThisEvent.setEnabled(false);
        }
    }
}
