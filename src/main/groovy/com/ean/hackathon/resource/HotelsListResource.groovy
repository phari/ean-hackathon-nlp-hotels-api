package com.ean.hackathon.resource

import com.codahale.metrics.annotation.Timed
import com.ean.hackathon.dao.PropertyCatalogDAO
import com.ean.hackathon.model.HotelInfo
import com.ean.hackathon.model.HotelListReq
import com.ean.hackathon.nlp.NLPService
import com.ean.hackathon.nlp.Parser
import com.ean.hackathon.rapid.RapidAPIException
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
import java.time.ZoneId

@Path('/hotels')
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
class HotelsListResource {

    private RapidAPIRESTClient rapidAPIRESTClient
    private PropertyCatalogDAO propertyCatalogDAO
    private Parser parser
    private NLPService nlpService

    public HotelsListResource(RapidAPIRESTClient rapidAPIRESTClient,
                              PropertyCatalogDAO propertyCatalogDAO,
                              Parser parser,
                              NLPService nlpService) {
        this.rapidAPIRESTClient = rapidAPIRESTClient
        this.propertyCatalogDAO = propertyCatalogDAO
        this.parser = parser
        this.nlpService = nlpService
    }

    @GET
    @Timed
    @Produces(MediaType.APPLICATION_JSON)
    @Path('/list')
    Response getListOfHotels(@QueryParam("searchtext") String freeText) {
        freeText = freeText.toLowerCase()
        Map processedText = nlpService.processRawText(freeText)

        //String city = parser.identifyCity(freeText);
        String city = processedText.get("location")
        println "City Found **${city}** for Query Text **${freeText}**"

        LocalDate checkInDt
        LocalDate checkOutDt

        if (city) {
            if (processedText.containsKey("from") && processedText.containsKey("to")) {
                Date checkInDate = processedText.get("from")
                checkInDt = checkInDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                Date checkOutDate = processedText.get("to")
                checkOutDt = checkOutDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                println "Dates Available **${checkInDt} - ${checkOutDt}**"
            } else {
                //No Dates, so default it for 3 days after 2 days from current date.
                checkInDt = LocalDate.now().plusDays(2)
                checkOutDt = LocalDate.now().plusDays(5)
                println "Defaulted Dates **${checkInDt} - ${checkOutDt}**"
            }

            /*List<Integer> hotelIdList = []
            if (parser.isKnwonCity(city)) {
                println "Known City"
                hotelIdList = propertyCatalogDAO.findIdsByCityName(city)
            } else {
                println "Unknown City"
                hotelIdList = propertyCatalogDAO.findIdsLikeCityName(city)
            }*/

            List<Integer> hotelIdList = []
            //if (processedText.containsKey("hotelName")) {
            //    println "Specific hotel mentioned **${processedText.get("hotelName")}**"
            //    hotelIdList = propertyCatalogDAO.findByHotelNameInCity(city, processedText.get("hotelName").toString().trim())
            //} else {
                println "No specific hotel"
                hotelIdList = propertyCatalogDAO.findIdsLikeCityName(city)
            //}

            println "Hotels Found **${hotelIdList.size()}** for location **${city}**"

            if (hotelIdList) {
                HotelListReq hotelListReq = new HotelListReq(
                        checkIn: checkInDt,
                        checkOut: checkOutDt,
                        adults: 2,
                        children: 0,
                        currency: "USD",
                        locale: "en-US",
                        city: city,
                        hotelIds: hotelIdList
                )

                handleHotelListRequest(hotelListReq, city)
            }
        } else {
            println "No hotels found in the **${city}**"
            Response.status(404).entity("No hotels found in the ${city}").build()
        }

    }

    @GET
    @Timed
    @Produces(MediaType.APPLICATION_JSON)
    @Path('/filter')
    Response findByHotelNameAndCity(@QueryParam("city") String city, @QueryParam("hotelname") String hotelName) {
        println "Requested for hotel **${hotelName}** in city **${city}**"

        List<Integer> hotelIdList = propertyCatalogDAO.findByHotelNameInCity(city, hotelName)
        println "Hotels Found **${hotelIdList.size()}** for location **${city}**"

        LocalDate twoDaysLater = LocalDate.now().plusDays(2)
        LocalDate threeDaysLater = LocalDate.now().plusDays(3)

        if (hotelIdList) {
            HotelListReq hotelListReq = new HotelListReq(
                    checkIn: twoDaysLater,
                    checkOut: threeDaysLater,
                    adults: 2,
                    children: 0,
                    currency: "USD",
                    locale: "en-US",
                    city: city,
                    hotelIds: [ hotelIdList.first() ]
            )

            handleHotelListRequest(hotelListReq, city)
        } else {
            println "No Hotels found with that name **${hotelName}** in the city **${city}**"
            Response.status(404).entity("No Hotels found with that name ${hotelName} in the city ${city}").build()
        }

    }

    Response handleHotelListRequest(HotelListReq hotelListReq, String city) {
        List availableList = null;
        try {
            availableList = rapidAPIRESTClient.getListOfHotels(hotelListReq);
        } catch (RapidAPIException exception) {
            Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(exception).build()
        }

        if (availableList) {
            println "**${availableList.size()}** Availability Found for location **${city}**"

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

            def listResp = [origReq: hotelListReq, results: hotelResultMap.values()]

            Response.ok(listResp).build()
        } else {
            println "Availability not found for hotels in the **${city}**"
            Response.status(404).entity("Availability not found for hotels in the ${city}").build()
        }
    }


}