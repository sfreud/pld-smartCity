package com.example.androidclient;

import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.util.DateTime;

import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.google.api.services.calendar.model.Event;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * An asynchronous task that handles the Calendar API event list retrieval.
 * Placing the API calls in their own task ensures the UI stays responsive.
 */
public class EventFetchTask extends AsyncTask<Void, Void, Void> {
    private UpcomingEventsActivity mActivity;

    /**
     * Constructor.
     * @param activity UpcomingEventsActivity that spawned this task.
     */
    EventFetchTask(UpcomingEventsActivity activity) {
        this.mActivity = activity;
    }

    /**
     * Background task to call Calendar API to fetch event list.
     * @param params no parameters needed for this task.
     */
    @Override
    protected Void doInBackground(Void... params) {
        try {
            mActivity.clearEvents();
            //addEvent();
            mActivity.updateEventList(fetchEventsFromCalendar());

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

    /**
     * Fetch a list of the next 10 events from the primary calendar.
     * @return List of Strings describing returned events.
     * @throws IOException
     */
    private List<String> fetchEventsFromCalendar() throws IOException {
        // List the next 10 events from the primary calendar.
        DateTime now = new DateTime(System.currentTimeMillis());
        List<String> eventStrings = new ArrayList<String>();
        /* Calendar.Events events() : An accessor for creating requests from the Events collection. return :
         * Calendar.Events.List list(java.lang.String calendarId) : Returns events on the specified calendar.
          Create a request for the method "events.list". This request holds the parameters needed by the calendar server.
           After setting any optional parameters, call the AbstractGoogleClientRequest.execute() method to invoke the remote operation.
         * Calendar.Events.List setMaxResults(java.lang.Integer maxResults) : Maximum number of events returned on one result page.
         * Calendar.Events.List setOrderBy(java.lang.String orderBy) : The order of the events returned in the result.
         * Calendar.Events.List setSingleEvents(java.lang.Boolean singleEvents) : Whether to expand recurring events into instances
           and only return single one-off events and instances of recurring events, but not the underlying recurring events themselves.
        */
        Events events = mActivity.mService.events().list("primary")
                .setMaxResults(10)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        List<Event> items = events.getItems();

        for (Event event : items) {
            DateTime start = event.getStart().getDateTime();
            if (start == null) {
                // All-day events don't have start times, so just use
                // the start date.
                start = event.getStart().getDate();
            }
            String location = event.getLocation();

            if (location == null) {
                location = "No location found";
            }
            eventStrings.add(
                    String.format("%s (%s) Location : %s", event.getSummary(), start,location));
        }
        return eventStrings;
    }

    private void addEvent() throws IOException    {
        Event event = new Event();
        event.setSummary("Test Création Evenement");
        event.setLocation("Somewhere");

        Date startDate = new Date();
        Date endDate = new Date(startDate.getTime() + 3600000);
        DateTime start = new DateTime(startDate, TimeZone.getTimeZone("UTC"));
        event.setStart(new EventDateTime().setDateTime(start));
        DateTime end = new DateTime(endDate, TimeZone.getTimeZone("UTC"));
        event.setEnd(new EventDateTime().setDateTime(end));

        // Insert the new event
        Event createdEvent = mActivity.mService.events().insert("primary", event).execute();
        Log.v("EVENT_CREATE","L'identifiant de l'évènement créé est "+createdEvent.getId());
    }

}