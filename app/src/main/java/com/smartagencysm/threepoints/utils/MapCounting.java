package com.smartagencysm.threepoints.utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class MapCounting {

    private static double distanceBetweenMarkers(Marker myLocation, Marker otherPointLocation) {

        LatLng myLatLng = myLocation.getPosition();
        LatLng otherLatLng = otherPointLocation.getPosition();

        final int earthRadius = 6371;
        double radians = Math.acos(Math.sin(myLatLng.latitude) * Math.sin(otherLatLng.latitude)
                + Math.cos(myLatLng.latitude) * Math.cos(otherLatLng.latitude)
                * Math.cos(myLatLng.longitude - otherLatLng.longitude));

        return radians * earthRadius;
    }

    private static double[] getDistancesArray(Marker myLocationMarker, Marker[] markers) {

        int lengthDistances = 0;

        for (Marker marker : markers) {
            if (marker != null) {
                lengthDistances++;
            }
        }

        if (lengthDistances == 0) {
            return null;
        }

        double[] distances = new double[lengthDistances];

        int index = 0;
        for (Marker marker : markers) {
            if (marker != null) {
                distances[index] = distanceBetweenMarkers(myLocationMarker, marker);
            }
            index++;
        }

        return distances;
    }

    public static int minIndex(Marker myLocationMarker, Marker[] markers) {
        double[] distances = getDistancesArray(myLocationMarker, markers);

        if (distances == null) {
            return -1;
        }

        int minIndex = 0;
        double min = distances[0];

        for (int i = 0; i < distances.length; i++) {
            if (distances[i] < min) {
                min = distances[i];
                minIndex = i;
            }
        }
        Log.i("MIN", "Min value: " + min);

        return minIndex;
    }

}
