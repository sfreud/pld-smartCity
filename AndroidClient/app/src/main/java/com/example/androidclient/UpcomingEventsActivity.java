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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class UpcomingEventsActivity extends Activity {

    public final static String SELECTED_EVENT = "com.example.androidclient.SELECTED_EVENT";
    /**
     * A Calendar service object used to query or modify calendars via the
     * Calendar API. Note: Do not confuse this class with the
     * com.google.api.services.calendar.model.Calendar class.
     */
    com.google.api.services.calendar.Calendar mService;

    GoogleAccountCredential credential;
    protected TextView mStatusText;
    protected ListView eventsListView;
    final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {CalendarScopes.CALENDAR, CalendarScopes.CALENDAR_READONLY};

    /**
     * Create the main activity.
     *
     * @param savedInstanceState previously saved instance data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_events);

        mStatusText = (TextView) findViewById(R.id.mStatusText);

        eventsListView = (ListView) findViewById(R.id.eventsListView);

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
            refreshEventList();
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
                    refreshEventList();
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
                        refreshEventList();
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    mStatusText.setText(getString(R.string.unspecifiedAccount));
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    refreshEventList();
                } else {
                    chooseAccount();
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Attempt to get a list of calendar events to display. If the email
     * address isn't known yet, then call chooseAccount() method so the user
     * can pick an account.
     */
    private void refreshEventList() {
        if (credential.getSelectedAccountName() == null) {
            chooseAccount();
        } else {
            if (isDeviceOnline()) {
                new EventFetchTask().execute();
            } else {
                mStatusText.setText(getString(R.string.noConnection));
            }
        }
    }

    /**
     * Clear any existing events from the list display and update the header
     * message; called from background threads and async tasks that need to
     * update the UI (in the UI thread).
     */
    public void clearEvents() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mStatusText.setText("Retrieving events…");
            }
        });
    }

    /**
     * Fill the event display called from
     * background threads and async tasks that need to update the UI (in the
     * UI thread).
     *
     * @param events a List of Event
     */
    public void updateEventList(final List<Event> events) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (events == null) {
                    mStatusText.setText(getString(R.string.errorRetrievingEvent));
                } else if (events.size() == 0) {
                    mStatusText.setText(getString(R.string.noEventFound));
                } else {
                    mStatusText.setText(getString(R.string.yourEvents));
                    List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
                    HashMap<String, String> element;
                    for (int i = 0; i < events.size(); i++) {
                        Event event = events.get(i);
                        element = new HashMap<String, String>();
                        element.put("summary", event.getSummary());
                        DateTime start = event.getStart().getDateTime();
                        if (start == null) {
                            // All-day events don't have start times, so just use
                            // the start date.
                            start = event.getStart().getDate();
                        }
                        Date startDate = new Date(start.getValue());
                        DateFormat USER_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy kk:mm");
                        element.put("startTime", USER_DATE_FORMAT.format(startDate));

                        String location = event.getLocation();
                        if (location == null) {
                            location = getString(R.string.noLocation);
                        }
                        element.put("location", location);
                        list.add(element);
                    }

                    ListAdapter adapter = new SimpleAdapter(UpcomingEventsActivity.this,
                            list,
                            R.layout.simple_events_list,
                            new String[]{"summary", "startTime", "location"},
                            new int[]{R.id.eventSummary, R.id.eventStartTime, R.id.eventLocation});
                    eventsListView.setAdapter(adapter);

                    eventsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Event selectedEvent = events.get(position);
                            String selectedEventID = selectedEvent.getId();
                            TransportRequestDAO trDAO = new TransportRequestDAO(getApplicationContext());
                            trDAO.open();
                            boolean eventHasAlreadyATransportRequest = trDAO.eventHasAlreadyATransportRequest(selectedEventID);
                            if(!eventHasAlreadyATransportRequest) {
                                Intent intent = new Intent(UpcomingEventsActivity.this, SelectedEventActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("eventID", selectedEvent.getId());
                                bundle.putString("summary", selectedEvent.getSummary());
                                bundle.putLong("startTime", selectedEvent.getStart().getDateTime().getValue());
                                String location = selectedEvent.getLocation();
                                if (location == null) {
                                    location = getString(R.string.noLocation);
                                }
                                bundle.putString("location", location);
                                intent.putExtra(SELECTED_EVENT, bundle);
                                startActivity(intent);
                            } else{
                                Toast.makeText(getApplicationContext(), getString(R.string.eventAlreadyRegistered), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
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
                        UpcomingEventsActivity.this,
                        REQUEST_GOOGLE_PLAY_SERVICES);
                dialog.show();
            }
        });
    }

    private class EventFetchTask extends AsyncTask<Void, Void, Void> {


        /**
         * Background task to call Calendar API to fetch event list.
         *
         * @param params no parameters needed for this task.
         */
        @Override
        protected Void doInBackground(Void... params) {
            try {
                clearEvents();
                updateEventList(fetchEventsFromCalendar());
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

        /**
         * Fetch a list of the next 10 events from the primary calendar.
         *
         * @return List of events.
         * @throws IOException
         */
        private List<Event> fetchEventsFromCalendar() throws IOException {
            // List the next 10 events from the primary calendar.
            DateTime now = new DateTime(System.currentTimeMillis());
            Events events = mService.events().list("primary")
                    .setMaxResults(10)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();

            return events.getItems();
        }
    }
}