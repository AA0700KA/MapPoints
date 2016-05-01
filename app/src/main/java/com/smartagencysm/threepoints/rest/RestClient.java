package com.smartagencysm.threepoints.rest;

import org.androidannotations.annotations.rest.Get;
import org.androidannotations.annotations.rest.Rest;

@Rest(converters = MessageConvertor.class, rootUrl = "https://maps.googleapis.com")
public interface RestClient {

    @Get("/maps/api/directions/json?origin={origin}&destination={destination}&sensor={sensor}&language={language}/")
    ResultResponse getPath(String origin, String destination, boolean sensor, String language);

}
