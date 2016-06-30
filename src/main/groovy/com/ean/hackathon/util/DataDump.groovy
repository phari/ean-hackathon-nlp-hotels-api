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
                    .getConnection("jdbc:postgresql://localhost:5432/eanrapidcontent",
                    "eanrapidcontent", "eanrapidcontent");

            JsonSlurper slurper = new JsonSlurper();

            //File file = new File('/Users/phari/Downloads/propertycontent.jsonl');
        /*if (file.exists()) {
            file.eachLine { line ->
                if (line) {
                    def jsonObj = slurper.parseText(line);

                    long propertyId = Integer.parseInt(jsonObj.property_id);
                    def address = jsonObj.address;
                    def city = null;
                    if (address) {
                        city = address.city;
                    }

                    String insertSQL = "INSERT INTO ean_rapid_hotels (hotel_id, city, json_str) VALUES (?, ?, ?);";
                    PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);
                    preparedStatement.setLong(1, propertyId)
                    preparedStatement.setString(2, city)
                    preparedStatement.setString(3, line)

                    preparedStatement.executeUpdate();
                    preparedStatement.close();
                }
            }
        }*/

            connection.close();
        } catch (Exception e) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }

        System.out.println("Records created successfully");


    }



}
