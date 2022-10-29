package com.example.jetnews.data.service

import com.example.jetnews.model.Rss
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url

/**
 * Interface for API to get RSS news feed
 */

interface NewsApi {
    @GET
    fun getRssFeed(@Url url: String?): Call<Rss?>?
}