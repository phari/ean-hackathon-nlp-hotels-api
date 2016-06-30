package com.ean.hackathon.model

import com.fasterxml.jackson.annotation.JsonIgnore

import java.time.LocalDate

/**
 * Created by phari on 6/29/16.
 */
class HotelListReq {

    String city

    @JsonIgnore
    List<Integer> hotelIds

    LocalDate checkIn
    LocalDate checkOut
    int adults
    int children
    String currency
    String locale

}
