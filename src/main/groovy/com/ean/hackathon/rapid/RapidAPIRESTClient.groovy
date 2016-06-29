package com.ean.hackathon.rapid

import com.ean.hackathon.model.HotelListReq
import org.apache.http.HttpHost
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.util.EntityUtils

/**
 * Created by phari on 6/29/16.
 */
class RapidAPIRESTClient {

    private final HttpHost httpHost = new HttpHost("https://api.staging.ean")
    private final CloseableHttpClient httpClient
    private final String APIKEY = "1biibclk4qfpb59ognph3ldtov"
    private final String secretKey = "8vvepaj00dkd5"

    public RapidAPIRESTClient(HttpClient httpClient) {
        this.httpClient = httpClient
    }

    public String getListOfHotels(HotelListReq hotelListReq) {
        try {
            String reqPath = "/property"
            URIBuilder uriBuilder = new URIBuilder()
                    .setPath(reqPath)
                    .setParameter("arrival", hotelListReq.checkIn)
                    .setParameter("departure", hotelListReq.checkOut)
                    .setParameter("currency", hotelListReq.currency)
                    .setParameter("language", hotelListReq.locale)

            hotelListReq.hotelIds.each { it ->
                uriBuilder.setParameter("property_id", it)
            }

            URI requestURI = uriBuilder.build()
            HttpGet httpGetReq = new HttpGet(requestURI)
            String authHeader = "EAN APIKey=${APIKEY},Signature=${getSignature()}"
            httpGetReq.addHeader("Authorization", authHeader)
            httpGetReq.addHeader("X-Forwarded-For", "214.23.14.213")
            CloseableHttpResponse response = httpClient.execute(httpHost, httpGetReq)

            String responseBody = EntityUtils.toString(response.getEntity());

        } catch (Exception ex) {
            throw new RuntimeException(ex)
        }
    }

    private getSignature() {
        return "e5e92864dafb0fd19e3d2c08396aa0b7"
    }

}
