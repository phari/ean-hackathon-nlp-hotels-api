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
            result.putAll(processDates(jsonEntities.datetime, jsonEntities.duration))
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

    Map processDates(List dates, List durations) {
        def result = [:]
        if (!dates.isEmpty()) {
            def intervals = dates.findAll { it.type == "interval" }
            if (!intervals.isEmpty()) {
                def highestConfidence = intervals.max { it.confidence }
                result = [
                        from: FORMAT.parse(highestConfidence.from.value),
                        to  : FORMAT.parse(highestConfidence.to.value) - 1
                ]
            } else if (durations) {
                def from = FORMAT.parse(dates[0].value)
                def duration = durations.max { it.confidence }?.value ?: 7
                result = [
                    from: from,
                    to: from + (duration - 1)
                ]
            } else {
                def dateValues = dates.collect { FORMAT.parse(it.value) }
                def min = dateValues.min()
                def max = dateValues.max()
                if (min == max) {
                    result = [
                        from: min,
                        to: new Date().plus(6)
                    ]
                } else {
                    result = [
                        from: min,
                        to  : max
                    ]
                }
            }
        }

        def now = new Date()
        if (now - result.from > 7) {
            return [to:  result.to + 7, from:  result.from + 14]
        } else if (now - result.from > 0) {
            return [to:  result.to + 7, from:  result.from + 7]
        }

        result
    }

    public static void main(String[] args) {

        "%Hilton Garden Inn%"

        println new NLPService().processRawText(
                "Hotels in London from the tenth of July to the fifteenth of July."
        )
        // I'm looking for a hotel in New York next week. I'd like to stay for three days.
        // Hotels in London from the tenth of July to the fifteenth of July.
        // I'm looking for a hotel in New York next week. I'd like to stay for three days.
        // I'd like to stay in London next week. I need something from next Tuesday to next Sunday.

        // Show me just the Hilton.

    }
}
