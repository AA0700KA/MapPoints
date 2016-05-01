package com.smartagencysm.threepoints.rest;


public class ResultResponse {

    private Route[] routes;

    public String getPoint() {
        return this.routes[0].overviewPolyline.points;
    }

    public String getStartAddress() {
        return this.routes[0].legs[0].startAddress;
    }

    public String getEndAddress() {
        return this.routes[0].legs[0].endAddress;
    }

    public String getDistance() {
        return this.routes[0].legs[0].distance.text;
    }

    public String getDuration() {
        return this.routes[0].legs[0].duration.text;
    }

    private class Route{
        protected OverviewPolyline overviewPolyline;
        protected Leg[] legs;
    }

    private class OverviewPolyline {
        protected String points;
    }

    private class Leg {
        protected Distance distance;
        protected Duration duration;
        protected String endAddress;
        protected String startAddress;
    }

    private class Distance {
        protected String text;
    }

    private class Duration {
        protected String text;
    }

}
