package com.example.androidclient;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class TransportRequestListActivity extends Activity {

    private ListView transportRequestListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport_request_list);

        transportRequestListView = (ListView) findViewById(R.id.transportRequestListView);

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
        ListAdapter adapter = new SimpleAdapter(TransportRequestListActivity.this,
                list,
                R.layout.simple_transport__request_list,
                new String[]{"summary", "startTime", "startLocation", "eventLocation"},
                new int[]{R.id.eventSummary, R.id.eventStartTime, R.id.startLocation, R.id.eventLocation});
        transportRequestListView.setAdapter(adapter);
        transportRequestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TransportRequest tr = ltr.get(position);
                Context ctx = TransportRequestListActivity.this;
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
}
