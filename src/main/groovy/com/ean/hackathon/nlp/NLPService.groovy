package com.ean.hackathon.nlp

import java.text.SimpleDateFormat

class NLPService {
    private final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd")
    def NLPClient client
    def Parser parser

    public NLPService() {
        client = new NLPClient()
        parser = new Parser()
    }

    public Map processRawText(String text) {
        def result = [:]
        Map jsonEntities = client.getNLPEntities(text)
        if (jsonEntities.containsKey("datetime")) {
            result.putAll(processDates(jsonEntities.datetime))
        }
        if (jsonEntities.containsKey("location")) {
            result.putAll(processLocations(jsonEntities.location))
        }
        if (jsonEntities.containsKey("local_search_query")) {
            result.putAll(processHotelNames(jsonEntities.local_search_query))
        }
        result
    }

    static Map processHotelNames(def hotelNames) {
        hotelNames ? [hotelName: hotelNames.max { it.confidence }.value] : [:]
    }

    static Map processLocations(List locations) {
        locations ? [location: locations.max { it.confidence }.value] : [:]
    }

    Map processDates(List dates) {
        if (!dates.isEmpty()) {
            def intervals = dates.findAll { it.type == "interval" }
            if (!intervals.isEmpty()) {
                def highestConfidence = intervals.max { it.confidence }
                return [
                        from: FORMAT.parse(highestConfidence.from.value),
                        to: FORMAT.parse(highestConfidence.to.value)
                ]
            } else {
                def dateValues = dates.collect { FORMAT.parse(it.value) }
                def min = dateValues.min()
                def max = dateValues.max()
                if (min == max) {
                    // look for a duration

                    return [
                        from: min,
                        to: new Date().plus(7)
                    ]
                }
                return [
                    from: min,
                    to: max
                ]
            }
        }

        [:]
    }

    public static void main(String[] args) {
        println new NLPService().processRawText(
            "I'd like to stay in London from next Tuesday to next Sunday. I'll be bringing my wife with me."
        )
        // I'm looking for a hotel in New York next week. I'd like to stay for three days.

        // Show me just the Hilton.

    }
}
