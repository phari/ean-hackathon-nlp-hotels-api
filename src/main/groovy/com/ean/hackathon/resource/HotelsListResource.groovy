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
    private PropertyCache propertyCache

    @GET
    @Timed
    @Path('/')
    Response getListOfHotels(@QueryParam("searchText") String freeText) {
        String cityFound = Parser.identifyCity(freeText);

        if (cityFound) {
            LocalDate twoDaysLater = new LocalDate().plusDays(2)
            LocalDate threeDaysLater = new LocalDate().plusDays(3)

            HotelListReq hotelListReq = new HotelListReq(
                    checkIn: twoDaysLater,
                    checkOut: threeDaysLater,
                    adults: 2,
                    children: 0,
                    currency: "USD",
                    locale: "en-US",
                    city: cityFound
            )

            String data = rapidAPIRESTClient.getListOfHotels(hotelListReq);

        } else {
            Response.status(404).build()
        }

    }

}