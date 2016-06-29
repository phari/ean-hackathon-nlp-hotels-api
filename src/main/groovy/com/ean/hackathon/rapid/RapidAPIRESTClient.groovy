package com.ean.hackathon.rapid

import com.ean.hackathon.model.HotelListReq
import groovy.json.JsonSlurper
import org.apache.http.HttpHost
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.util.EntityUtils

import java.time.format.DateTimeFormatter

/**
 * Created by phari on 6/29/16.
 */
class RapidAPIRESTClient {

    private final HttpHost httpHost
    private final CloseableHttpClient httpClient
    private final String APIKEY = "1biibclk4qfpb59ognph3ldtov"
    private final String secretKey = "8vvepaj00dkd5"
    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public RapidAPIRESTClient(HttpClient httpClient) {
        this.httpClient = httpClient

        URI endPointURI = new URI("https://api.staging.ean")
        this.httpHost = new HttpHost(endPointURI.getHost(), endPointURI.getPort(), endPointURI.getScheme())
    }

    def getListOfHotels(HotelListReq hotelListReq) {
        try {
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

            hotelListReq.hotelIds.each { it ->
                uriBuilder.addParameter("property_id", it)
            }

            URI requestURI = uriBuilder.build()
            HttpGet httpGetReq = new HttpGet(requestURI)
            httpGetReq.addHeader("Authorization", getAuthHeader())
            httpGetReq.addHeader("X-Forwarded-For", "127.0.0.1")
            httpGetReq.addHeader("Accept", "application/json")
            httpGetReq.addHeader("Customer-Ip", "127.0.0.1")
            CloseableHttpResponse response = httpClient.execute(httpHost, httpGetReq)

            def jsonResult = new JsonSlurper().parse(response.getEntity().content)
            //String responseBody = EntityUtils.toString(response.getEntity().content)
            //println "responseBody: ${responseBody}"
            //return responseBody

            jsonResult

        } catch (Exception ex) {
            throw new RuntimeException(ex)
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

    private getAuthHeader() {
        return "EAN APIKey=${APIKEY},Signature=43196bad25c7284a005fa107b3e6fab9"
    }

}
