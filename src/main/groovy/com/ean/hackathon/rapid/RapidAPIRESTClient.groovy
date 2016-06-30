package com.ean.hackathon.rapid

import com.codahale.metrics.annotation.Timed
import com.ean.hackathon.model.HotelListReq
import groovy.json.JsonSlurper
import org.apache.http.HttpHost
import org.apache.http.HttpStatus
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.client.CloseableHttpClient

import java.security.MessageDigest
import java.time.format.DateTimeFormatter

/**
 * Created by phari on 6/29/16.
 */
class RapidAPIRESTClient {

    private final HttpHost httpHost
    private final CloseableHttpClient httpClient
    private final String API_KEY = "1biibclk4qfpb59ognph3ldtov"
    private final String SECRET_KEY = "8vvepaj00dkd5"
    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public RapidAPIRESTClient(HttpClient httpClient) {
        this.httpClient = httpClient

        URI endPointURI = new URI("https://api.staging.ean")
        this.httpHost = new HttpHost(endPointURI.getHost(), endPointURI.getPort(), endPointURI.getScheme())
    }

    @Timed
    def getListOfHotels(HotelListReq hotelListReq) {
        String reqPath = "/1/property"
        URIBuilder uriBuilder = new URIBuilder()
                .setPath(reqPath)
                .addParameter("arrival", hotelListReq.checkIn.format(FORMATTER))
                .addParameter("departure", hotelListReq.checkOut.format(FORMATTER))
                .addParameter("country", "US")
                .addParameter("currency", hotelListReq.currency)
                .addParameter("language", hotelListReq.locale)
                .addParameter("occupancy", getOccupancyQueryParam(hotelListReq))
                .addParameter("sort_type", "PREFERRED")
                .addParameter("sales_channel", "WEBSITE")
                .addParameter("sales_environment", "HOTEL_ONLY")

        int endIndex = hotelListReq.hotelIds.size() > 20 ? 19 : hotelListReq.hotelIds.size()
        hotelListReq.hotelIds.subList(0, endIndex).each { it ->
            uriBuilder.addParameter("property_id", "${it}")
        }

        URI requestURI = uriBuilder.build()
        HttpGet httpGetReq = new HttpGet(requestURI)
        httpGetReq.addHeader("Authorization", getAuthHeader())
        httpGetReq.addHeader("X-Forwarded-For", "127.0.0.1")
        httpGetReq.addHeader("Accept", "application/json")
        httpGetReq.addHeader("Customer-Ip", "127.0.0.1")

        try {
            CloseableHttpResponse response = httpClient.execute(httpHost, httpGetReq)
            int statusCode = response.getStatusLine().statusCode
            if (statusCode == HttpStatus.SC_OK) {
                def jsonResult = new JsonSlurper().parse(response.getEntity().content)
                jsonResult
            } else {
                throw new RapidAPIException(statusCode, "Error in requesting from Rapid API")
            }
        } catch (IOException e) {
            throw new RapidAPIException(e.getMessage())
        }
    }

    private String getOccupancyQueryParam(HotelListReq hotelListReq) {
        /*StringBuilder stringBuilder = new StringBuilder()
        stringBuilder.append(hotelListReq.adults)

        if (hotelListReq.children > 0) {
            stringBuilder.append("-")
            //default it to 5 years
            def dummyAges = []
            def loop = {
                dummyAges.add(5)
            }
            1.upto(hotelListReq.children, loop)
            stringBuilder.append(dummyAges.join(','))
        }

        stringBuilder.toString()*/

        return "${hotelListReq.adults}"
    }

    private String getAuthHeader() {
        return "EAN APIKey=${API_KEY},Signature=${generateSignature()}"
    }

    private String generateSignature() {
        def unixTime = System.currentTimeMillis() / 1000L;

        def values = [API_KEY, SECRET_KEY, "${unixTime}"]
        def HEXCHARS = "0123456789abcdef".toCharArray();

        MessageDigest md5digest = null
        String signature = null
        try {
            md5digest = MessageDigest.getInstance("MD5")
            for (final String val : values) {
                md5digest.update(val.getBytes("UTF-8"))
            }
            final byte[] digest = md5digest.digest()
            final char[] chars = new char[digest.length * 2]
            int c = 0
            for (final byte b : digest) {
                chars[c++] = HEXCHARS[(b >>> 4) & 0x0f]
                chars[c++] = HEXCHARS[(b      ) & 0x0f]
            }

            signature = new String(chars)
        } finally {
            md5digest.reset()
        }

        return signature
    }


}
