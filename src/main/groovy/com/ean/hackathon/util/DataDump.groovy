package com.ean.hackathon.util

import groovy.json.JsonSlurper

import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.Statement;

/**
 * Created by phari on 6/29/16.
 */
class DataDump {

    public static void main(String[] args) {

        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager
                    .getConnection("jdbc:postgresql://aoeleaninsdevpostgresdb001.karmalab.net:5432/eaninsights",
                    "eaninsightsdev", "eanInsightsDev\$");

            JsonSlurper slurper = new JsonSlurper();

            File file = new File('/Users/phari/Downloads/propertycontent.jsonl');
            if (file.exists()) {
                file.eachLine { line ->
                    if (line) {
                        def jsonObj = slurper.parseText(line);

                        long propertyId = Integer.parseInt(jsonObj.property_id);
                        def address = jsonObj.address;
                        def city = null;
                        if (address) {
                            city = address.city;
                        }

                        String insertSQL = "INSERT INTO ean_rapid_hotels (hotel_id, city, hotel_name, json_str) VALUES (?, ?, ?, ?);";
                        def obj = slurper.parseText(line)
                        String hotelName = obj.name

                        PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);
                        preparedStatement.setLong(1, propertyId)
                        preparedStatement.setString(2, city)
                        preparedStatement.setString(3, hotelName)
                        preparedStatement.setString(4, line)

                        preparedStatement.executeUpdate();
                        preparedStatement.close();
                    }
                }
            }

            connection.close();
        } catch (Exception e) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }

        System.out.println("Records created successfully");


    }



}
