package com.ean.hackathon.nlp

/**
 * Created by phari on 6/29/16.
 */
class Parser {

    public static String identifyCity(String freeText) {
        if (freeText.toUpperCase().contains("NEW YORK")) {
            return "New York"
        }

        return null
    }
}
