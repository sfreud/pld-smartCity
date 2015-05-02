package com.example.androidclient;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public class TransportRequestDAO extends DAOBase {

    public TransportRequestDAO(Context pContext) {
        super(pContext);
    }

    /**
     * @param t la requête de transport à ajouter à la base
     */
    public void add(TransportRequest t) {
        ContentValues value = new ContentValues();
        value.put(DBContract.TransportRequestContract.EVENTSUMMARY, t.getEventSummary());
        value.put(DBContract.TransportRequestContract.EVENTADDRESS, t.getEventAddress());
        value.put(DBContract.TransportRequestContract.EVENTLAT, t.getEventLat());
        value.put(DBContract.TransportRequestContract.EVENTLNG, t.getEventLng());
        value.put(DBContract.TransportRequestContract.STARTADDRESS, t.getStartAddress());
        value.put(DBContract.TransportRequestContract.STARTLAT, t.getStartLat());
        value.put(DBContract.TransportRequestContract.STARTLNG, t.getStartLng());
        value.put(DBContract.TransportRequestContract.EVENTBEGINTIME, t.getEventBeginTime());
        long tID = mDb.insert(DBContract.TransportRequestContract.TABLE_NAME, null, value);
        t.setId(tID);
    }

    /**
     * @param id l'identifiant de la requête de transport à supprimer
     */
    public void remove(long id) {
        mDb.delete(DBContract.TransportRequestContract.TABLE_NAME, DBContract.TransportRequestContract.KEY + " = ?", new String[] {String.valueOf(id)});
    }

    /**
     * @param t la requête de transport modifiée
     */
    public void update(TransportRequest t) {
        ContentValues value = new ContentValues();
        value.put(DBContract.TransportRequestContract.EVENTSUMMARY, t.getEventSummary());
        value.put(DBContract.TransportRequestContract.EVENTADDRESS, t.getEventAddress());
        value.put(DBContract.TransportRequestContract.EVENTLAT, t.getEventLat());
        value.put(DBContract.TransportRequestContract.EVENTLNG, t.getEventLng());
        value.put(DBContract.TransportRequestContract.STARTADDRESS, t.getStartAddress());
        value.put(DBContract.TransportRequestContract.STARTLAT, t.getStartLat());
        value.put(DBContract.TransportRequestContract.STARTLNG, t.getStartLng());
        value.put(DBContract.TransportRequestContract.EVENTBEGINTIME, t.getEventBeginTime());
        mDb.update(DBContract.TransportRequestContract.TABLE_NAME, value, DBContract.TransportRequestContract.KEY  + " = ?", new String[] {String.valueOf(t.getId())});
    }

    /**
     * @param id l'identifiant de la requête de transport à récupérer
     */
    public TransportRequest select(long id) {
        Cursor c = mDb.rawQuery("select * from " + DBContract.TransportRequestContract.TABLE_NAME + " where id = ?", new String[]{String.valueOf(id)});
        c.moveToFirst();
        return new TransportRequest(c.getLong(0), c.getString(1), c.getString(2), c.getDouble(3), c.getDouble(4), c.getString(5),  c.getDouble(6),  c.getDouble(7), c.getLong(8));
    }

    public List<TransportRequest> selectAll()
    {
        List<TransportRequest> ltr = new ArrayList<TransportRequest>();
        Cursor c = mDb.rawQuery("select * from " + DBContract.TransportRequestContract.TABLE_NAME,null);
        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            ltr.add(new TransportRequest(c.getLong(0), c.getString(1), c.getString(2), c.getDouble(3), c.getDouble(4), c.getString(5),  c.getDouble(6),  c.getDouble(7), c.getLong(8)));
        }
        c.close();
        return ltr;
    }
}

