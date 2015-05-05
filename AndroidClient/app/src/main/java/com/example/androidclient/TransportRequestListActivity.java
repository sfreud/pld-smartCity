package com.example.androidclient;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.Calendar;
import java.util.Date;
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
        int ltrSize = ltr.size();
        String[] transportRequests = new String[ltrSize];
        for (int i=0;i<ltrSize;++i) {
            TransportRequest tr = ltr.get(i);
            String trString = "";
            trString += tr.getEventSummary()+"\n";
            trString +=(new Date(tr.getEventBeginTime())).toString()+"\n";
            trString +=tr.getStartAddress()+"\n";
            trString += tr.getEventAddress();
            transportRequests[i]=trString;
        }

        transportRequestListView.setAdapter(new ArrayAdapter<String>(this,
                R.layout.simple_transport_request_list,
                android.R.id.text1, transportRequests));

        Button selectedTRToUpdate = (Button) findViewById(R.id.selectedTRToUpdate);
        selectedTRToUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = transportRequestListView.getCheckedItemPosition();
                if(position!=-1) {
                    TransportRequest tr = ltr.get(position);
                    Intent intent = new Intent(TransportRequestListActivity.this, UpdateTransportRequestActivity.class);
                    intent.putExtra("transportRequestID", tr.getId());
                    startActivity(intent);
                }
            }
        });

        Button getSelectedTRMap = (Button) findViewById(R.id.getSelectedTRMap);
        getSelectedTRMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransportRequest tr = ltr.get(transportRequestListView.getCheckedItemPosition());
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


        /*transportRequestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });*/

    }
}
