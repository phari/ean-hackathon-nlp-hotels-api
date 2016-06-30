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
            result.putAll(processLocation(jsonEntities.location))
        }
        result
    }

    static Map processLocation(List locations) {
        if (!locations.isEmpty()) {
            def highestConfidence = locations.max { it.confidence }
            return [
                    location: highestConfidence.value
            ]
        }

        return [:]
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

        return [:]
    }

    public static void main(String[] args) {
        println new NLPService().processRawText("I want to stay in New York for two days. I'll check in July seventh " +
                "and check out on July fourteenth.")
    }
}
