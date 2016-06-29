package com.ean.hackathon.content

/**
 * Created by phari on 6/29/16.
 */
class PropertyCache {

    public static List<String> getHotels(String city, String country) {
        if (city.toUpperCase().equals("NEW YORK")) {
            return [ "19248" ]
        }

        return []
    }
}
