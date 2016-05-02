package com.smartagencysm.threepoints.utils;



import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.smartagencysm.threepoints.rest.RestClient;
import com.smartagencysm.threepoints.rest.ResultResponse;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MapCounting {

    public static double distanceBetweenMarkers(Marker myLocation, Marker otherPointLocation, RestClient api) {

        ExecutorService executorService = Executors.newFixedThreadPool(5);

        LatLng myLatLng = myLocation.getPosition();
        LatLng otherLatLng = otherPointLocation.getPosition();

        String origin = myLatLng.latitude + "," + myLatLng.longitude;
        String distination = otherLatLng.latitude + "," + otherLatLng.longitude;

        Callable<Double> callable = getCallable(api, origin, distination);
        Future<Double> future = executorService.submit(callable);

        double result = 0;

        try {
            result = future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static Callable<Double> getCallable(final RestClient api, final String origin, final String distination) {
        return new Callable<Double>() {
            @Override
            public Double call() throws Exception {
                ResultResponse result = api.getPath(origin, distination, true, "ru");

                String distance = result.getDistance();
                double dist = Double.valueOf(distance.substring(0, distance.indexOf(" ")));

                return dist;
            }
        };
    }

    private static double[] getDistancesArray(Marker myLocationMarker, Marker[] markers, RestClient api) {

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
                distances[index] = distanceBetweenMarkers(myLocationMarker, marker, api);
            }
            index++;
        }

        return distances;
    }

    public static int minIndex(Marker myLocationMarker, Marker[] markers, RestClient api) {
        double[] distances = getDistancesArray(myLocationMarker, markers, api);

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
