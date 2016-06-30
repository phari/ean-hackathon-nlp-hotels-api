package com.ean.hackathon.content

import groovy.json.JsonSlurper

import java.util.regex.Pattern

public class PropertyCache {
    private static final Pattern PROPERTY_ID_PATTERN = Pattern.compile("\\{\"property_id\":\"(\\d+)\",\"name\":");
    private static PropertyCache INSTANCE;
    private Map<Integer, Object> properties = [:]

    public static PropertyCache instance() {
        if (INSTANCE != null) {
            return INSTANCE;
        } else {
            return INSTANCE = new PropertyCache();
        }

    }

    private PropertyCache() {
        JsonSlurper slurper = new JsonSlurper()
        properties = PropertyCache.getClassLoader().getResourceAsStream("propertycatalog.jsonl").readLines()
                .collectEntries {
            [propertyId(it), slurper.parseText(it)]
        }
    }

    public static List<String> getHotels(String city, String country) {
        if (city.toUpperCase().equals("NEW YORK")) {
            return [ "3333", "19", "3476" ]
        }
    }

    public Object findById(String propertyId) {
        return properties.getOrDefault(Integer.parseInt(propertyId), "property not found");
    }

    public Map<Integer, Object> findByCityName(String cityName) {
        properties.findAll {
            it.value.toString().matches(/.*(?i)${cityName}.*/)
        }
    }

    private static Integer propertyId(String line) {
        def matcher = PROPERTY_ID_PATTERN.matcher(line)
        return Integer.parseInt(matcher[0][1]);
    }

    // demo
    public static void main(String[] args) {
        println PropertyCache.instance().findByCityName("new yorK")
    }

}
