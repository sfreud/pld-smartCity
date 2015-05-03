package com.example.androidclient;

public class TransportRequest {
    private long id;
    private String eventID;
    private String eventSummary;
    private String eventAddress;
    private double eventLat;
    private double eventLng;
    private String startAddress;
    private double startLat;
    private double startLng;
    private long eventBeginTime;

    public TransportRequest(String eventID, String eventSummary, String eventAddress, double eventLat, double eventLng, String startAddress, double startLat, double startLng, long eventBeginTime) {
        this.eventID = eventID;
        this.eventSummary = eventSummary;
        this.eventAddress = eventAddress;
        this.eventLat = eventLat;
        this.eventLng = eventLng;
        this.startAddress = startAddress;
        this.startLat = startLat;
        this.eventBeginTime = eventBeginTime;
        this.startLng = startLng;
    }

    public TransportRequest(long id, String eventID, String eventSummary, String eventAddress, double eventLat, double eventLng, String startAddress, double startLat, double startLng, long eventBeginTime) {
        this.id = id;
        this.eventID = eventID;
        this.eventSummary = eventSummary;
        this.eventAddress = eventAddress;
        this.eventLat = eventLat;
        this.startAddress = startAddress;
        this.eventLng = eventLng;
        this.startLat = startLat;
        this.startLng = startLng;
        this.eventBeginTime = eventBeginTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEventID() {
        return eventID;
    }

    public String getEventSummary() {
        return eventSummary;
    }

    public String getEventAddress() {
        return eventAddress;
    }

    public double getEventLat() {
        return eventLat;
    }

    public double getEventLng() {
        return eventLng;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public double getStartLat() {
        return startLat;
    }

    public double getStartLng() {
        return startLng;
    }

    public long getEventBeginTime() {
        return eventBeginTime;
    }

}