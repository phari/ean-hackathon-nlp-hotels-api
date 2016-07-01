package com.ean.hackathon.dao

import com.ean.hackathon.model.HotelInfo
import groovy.json.JsonSlurper

class FileBasedPropertyCatalogDAO {
    private final Map<String, Integer> hotelIdsByCities = new HashMap<>();
    private final Map<Integer, String> propertyInfoByIds = new HashMap<>();

    public FileBasedPropertyCatalogDAO() {
        JsonSlurper slurper = new JsonSlurper()
        //FileBasedPropertyCatalogDAO.getClassLoader().getResourceAsStream("testrapidcontent.csv").readLines()
        FileBasedPropertyCatalogDAO.getClassLoader().getResourceAsStream("rapidcontentmini.txt").readLines()
                .each {

            def jsonObj = slurper.parseText(it)
            int propertyId = Integer.parseInt(jsonObj.property_id)
            def address = jsonObj.address;
            String city = null;
            if (address) {
                city = address.city;
            }

            String hotelName = jsonObj.name

            hotelIdsByCities[city.toLowerCase()] = propertyId
            propertyInfoByIds[propertyId] = it
        }
    }


    List<HotelInfo> findByCityName(String city) {
        //Not Implemented
        return null;
    }

    List<Integer> findIdsLikeCityName(String city) {
        def result = []
        if (city) {
            String search = city.toLowerCase()
            hotelIdsByCities.each {
                if (it.key.contains(search)) {
                    result.add(it.value)
                }
            }
        }

        return result
    }

    List<Integer> findIdsByCityName(String city) {
        //Not Implemented
        return null;
    }

    List<String> getJsonForIds(List<Integer> idList) {
        def result = []
        if (idList) {
            idList.each {
                String jsonstr = propertyInfoByIds.get(it)
                result.add(jsonstr)
            }
        }

        return result
    }

    Set<String> getAllCities() {
        //Not Implemented
        return null;
    }

    List<Integer> findByHotelNameInCity(String city, String hotelName) {
        //Not Implemented
        return null;
    }

}
