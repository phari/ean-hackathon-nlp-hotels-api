package com.ean.hackathon.content

import java.util.regex.Pattern

public class PropertyCache {
    private static final Pattern PROPERTY_ID_PATTERN = Pattern.compile("\\{\"property_id\":\"(\\d+)\",\"name\":");
    private static PropertyCache INSTANCE;
    private Map<Integer, String> properties = [:]

    public static PropertyCache instance() {
        if (INSTANCE != null) {
            return INSTANCE;
        } else {
            return INSTANCE = new PropertyCache();
        }

    }

    private PropertyCache() {
        properties = PropertyCache.class.getClassLoader().getResourceAsStream("propertycatalog.jsonl").readLines()
                .collectEntries {
            [propertyId(it), it]
        }
    }

    public String findById(String propertyId) {
        return properties.getOrDefault(Integer.parseInt(propertyId), "property not found");
    }

    public List<String> findByCityName(String cityName) {
        properties.findAll {
            it.value.matches(/.*(?i)${cityName}.*/)
        }.collect {
            it.value
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
