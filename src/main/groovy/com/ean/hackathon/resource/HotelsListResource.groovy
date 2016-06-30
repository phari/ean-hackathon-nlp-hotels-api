package com.ean.hackathon.resource

import com.codahale.metrics.annotation.Timed
import com.ean.hackathon.content.PropertyCache
import com.ean.hackathon.dao.PropertyCatalogDAO
import com.ean.hackathon.model.HotelInfo
import com.ean.hackathon.model.HotelListReq
import com.ean.hackathon.nlp.Parser
import com.ean.hackathon.rapid.RapidAPIRESTClient
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import java.time.LocalDate

@Path('/hotels/list')
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
class HotelsListResource {

    private RapidAPIRESTClient rapidAPIRESTClient
    private PropertyCatalogDAO propertyCatalogDAO
    private Parser parser

    public HotelsListResource(RapidAPIRESTClient rapidAPIRESTClient,
                              PropertyCatalogDAO propertyCatalogDAO,
                              Parser parser) {
        this.rapidAPIRESTClient = rapidAPIRESTClient
        this.propertyCatalogDAO = propertyCatalogDAO
        this.parser = parser
    }

    @GET
    @Timed
    @Produces(MediaType.APPLICATION_JSON)
    @Path('/')
    Response getListOfHotels(@QueryParam("searchText") String freeText) {
        String city = parser.identifyCity(freeText);

        if (city) {
            LocalDate twoDaysLater = LocalDate.now().plusDays(2)
            LocalDate threeDaysLater = LocalDate.now().plusDays(3)

            //Map<Integer, Object> hotelIdMap = propertyCache.findByCityName(city)
            List<Integer> hotelIdList = propertyCatalogDAO.findIdsByCityName(city)

            HotelListReq hotelListReq = new HotelListReq(
                    checkIn: twoDaysLater,
                    checkOut: threeDaysLater,
                    adults: 2,
                    children: 2,
                    currency: "USD",
                    locale: "en-US",
                    city: city,
                    hotelIds: hotelIdList
            )

            List availableList = rapidAPIRESTClient.getListOfHotels(hotelListReq);
            def hotelResultMap = [:]
            availableList.each {
                int propertyId = Integer.parseInt(it.property_id)
                hotelResultMap.put(propertyId, new HotelInfo(propertyId: propertyId, availability: it.rooms))
            }

            List<String> propertyInfoJsonList = propertyCatalogDAO.getJsonForIds(hotelResultMap.keySet() as List)
            JsonSlurper slurper = new JsonSlurper()
            propertyInfoJsonList.each {
                def jsonObj = slurper.parseText(it)
                hotelResultMap.get(Integer.parseInt(jsonObj.property_id)).propertyInfo = jsonObj
            }

            def listResp = [ origReq: hotelListReq, results: hotelResultMap.values()]

            Response.ok(listResp).build()

        } else {
            Response.status(404).build()
        }

    }

}