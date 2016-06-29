package com.ean.hackathon.resource

import com.codahale.metrics.annotation.Timed
import com.ean.hackathon.content.PropertyCache
import com.ean.hackathon.model.HotelListReq
import com.ean.hackathon.nlp.Parser
import com.ean.hackathon.rapid.RapidAPIRESTClient
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

    public HotelsListResource(RapidAPIRESTClient rapidAPIRESTClient) {
        this.rapidAPIRESTClient = rapidAPIRESTClient
    }

    @GET
    @Timed
    @Path('/')
    Response getListOfHotels(@QueryParam("searchText") String freeText) {
        String cityFound = Parser.identifyCity(freeText);

        if (cityFound) {
            LocalDate twoDaysLater = LocalDate.now().plusDays(2)
            LocalDate threeDaysLater = LocalDate.now().plusDays(3)

            List<String> hotelIds = PropertyCache.getHotels("New York", "US")
            HotelListReq hotelListReq = new HotelListReq(
                    checkIn: twoDaysLater,
                    checkOut: threeDaysLater,
                    adults: 2,
                    children: 2,
                    currency: "USD",
                    locale: "en-US",
                    city: cityFound,
                    hotelIds: hotelIds
            )

            def priceAvailability = rapidAPIRESTClient.getListOfHotels(hotelListReq);
            Response.status(200).entity(priceAvailability).build()

        } else {
            Response.status(404).build()
        }

    }

}