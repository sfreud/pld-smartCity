package com.example.androidclient;

import java.util.Date;

public class TransportRequest {
    private long id;
    private String eventSummary;
    private String eventAddress;
    private double eventLat;
    private double eventLng;
    private String startAddress;
    private double startLat;
    private double startLng;
    private long eventBeginTime;

    public TransportRequest(String eventSummary, String eventAddress, double eventLat, double eventLng, String startAddress, double startLat, double startLng, long eventBeginTime) {
        this.eventSummary = eventSummary;
        this.eventAddress = eventAddress;
        this.eventLat = eventLat;
        this.eventLng = eventLng;
        this.startAddress = startAddress;
        this.startLat = startLat;
        this.eventBeginTime = eventBeginTime;
        this.startLng = startLng;
    }

    public TransportRequest(long id, String eventSummary, String eventAddress, double eventLat, double eventLng, String startAddress, double startLat, double startLng, long eventBeginTime) {
        this.id = id;
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

    public String getEventSummary() {
        return eventSummary;
    }

    public void setEventSummary(String eventSummary) {
        this.eventSummary = eventSummary;
    }

    public String getEventAddress() {
        return eventAddress;
    }

    public void setEventAddress(String eventAddress) {
        this.eventAddress = eventAddress;
    }

    public double getEventLat() {
        return eventLat;
    }

    public void setEventLat(double eventLat) {
        this.eventLat = eventLat;
    }

    public double getEventLng() {
        return eventLng;
    }

    public void setEventLng(double eventLng) {
        this.eventLng = eventLng;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public double getStartLat() {
        return startLat;
    }

    public void setStartLat(double startLat) {
        this.startLat = startLat;
    }

    public double getStartLng() {
        return startLng;
    }

    public void setStartLng(double startLng) {
        this.startLng = startLng;
    }

    public long getEventBeginTime() {
        return eventBeginTime;
    }

    public void setEventBeginTime(long eventBeginTime) {
        this.eventBeginTime = eventBeginTime;
    }
}