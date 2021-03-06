package com.ean.hackathon.nlp

import com.ean.hackathon.dao.PropertyCatalogDAO

/**
 * Created by phari on 6/29/16.
 */
class Parser {

    private Set<String> knownCities = new HashSet<String>()

    public Parser(def propertyCatalogDAO) {
        knownCities = propertyCatalogDAO.allCities
    }

    public Parser() {
        knownCities = ["New York", "London"]
    }

    public boolean isKnwonCity(String city) {
        return knownCities.contains(city.toLowerCase())
    }

    public String identifyCity(String freeText) {
        def location = new NLPService().processRawText(freeText)?.location
        if (location) {
            return location.toLowerCase()
        }

        String ignoreCase = freeText.toUpperCase()
        if (ignoreCase.contains("NEW YORK")) {
            return "new york"
        } else if (ignoreCase.contains("LONDON")) {
            return "london"
        } else if (ignoreCase.contains("BELLEVUE")) {
            return "bellevue"
        } else if (ignoreCase.contains("PARIS")) {
            return "paris"
        }

        return null
    }
}
