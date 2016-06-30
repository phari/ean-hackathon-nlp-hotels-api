package com.ean.hackathon.nlp

import com.ean.hackathon.rapid.HttpClientFactory
import groovy.json.JsonSlurper
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.CloseableHttpClient

class NLPClient {

    def String host
    def CloseableHttpClient client

    public NLPClient() {
        this.host = "api.wit.ai"
        this.client = HttpClientFactory.singletonClientInstance()
    }

    def Map getNLPEntities(String text) {
        URI requestURI = "https://${host}/message?v=20160629&q=${URLEncoder.encode(text)}".toURI()
        HttpGet httpGetReq = new HttpGet(requestURI)
        httpGetReq.addHeader("Authorization", "Bearer OLYCFWAWWOCYK57H2NAZJ23VWNCWXE4D")
        CloseableHttpResponse response = client.execute(httpGetReq)

        def jsonResult = new JsonSlurper().parse(response.getEntity().content)
        response.close()

        jsonResult?.entities
    }

}
