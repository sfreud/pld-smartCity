package com.example.androidclient;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;


public class SelectedEventActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_event);

        Intent intent = getIntent();
        ArrayList<CharSequence> eventToList = intent.getCharSequenceArrayListExtra(UpcomingEventsActivity.SELECTED_EVENT);
        String summary = (String) eventToList.get(0);
        String startTime = (String) eventToList.get(1);
        String location = (String) eventToList.get(2);

        TextView infosSelectedEvent = (TextView) findViewById(R.id.infos_selected_event);
        infosSelectedEvent.setText(summary+"\n"+startTime+"\n"+location);

    }
}
