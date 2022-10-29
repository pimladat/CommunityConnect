package com.example.jetnews.data.service;

import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

class RetrofitClient {
    private val retrofit: Retrofit = Retrofit.Builder()
        // Retrofit requires a base URL, but this will be replaced when making API call
        .baseUrl("https://url_not_used/")
        // TODO: XmlConverter is deprecated, should find a suitable replacement
        .addConverterFactory(SimpleXmlConverterFactory.create())
        .build();

    fun get(): Retrofit {
        return retrofit;
    }
}
