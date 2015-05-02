package com.example.androidclient;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;

public class TransportRequest {


    /**
     * arrivalAddress: adresse du lieu de l'évènement
     * startAddress: adresse du lieu de départ
     * startStop : adresse de l'arrêt de départ
     * arrivalStop : adresse de l'arrêt d'arrivée
     * beginEvent : date de début de l'évènement
     */
    private String arrivalAddress;
    private String startAddress;
    private String startStop;
    private String arrivalStop;
    private DateTime beginEvent;


    public TransportRequest(Event event, String startAddress) {
        this.startAddress = startAddress;
        this.beginEvent = event.getStart().getDateTime();
        this.arrivalAddress = event.getLocation();
        this.startStop = null;
        this.arrivalStop = null;

    }

    public String getArrivalStop() {
        return arrivalStop;
    }

    public void setArrivalStop(String arrivalStop) {
        this.arrivalStop = arrivalStop;
    }

    public String getStartStop() {
        return startStop;
    }

    public void setStartStop(String startStop) {
        this.startStop = startStop;
    }

    public String getArrivalAddress() {
        return arrivalAddress;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public DateTime getBeginEvent() {
        return beginEvent;
    }

    public Object calculateTransport() {
        return null;
    }


}