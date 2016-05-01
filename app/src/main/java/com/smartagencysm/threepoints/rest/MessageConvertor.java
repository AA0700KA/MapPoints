package com.smartagencysm.threepoints.rest;


import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;

import org.springframework.http.converter.json.GsonHttpMessageConverter;

public class MessageConvertor extends GsonHttpMessageConverter {

    public MessageConvertor() {
        setGson(new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create());
    }

}
