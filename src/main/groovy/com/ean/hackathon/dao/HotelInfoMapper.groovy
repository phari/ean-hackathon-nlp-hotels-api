package com.ean.hackathon.dao

import com.ean.hackathon.model.HotelInfo
import org.skife.jdbi.v2.StatementContext
import org.skife.jdbi.v2.tweak.ResultSetMapper

import java.sql.ResultSet
import java.sql.SQLException

class HotelInfoMapper implements ResultSetMapper<HotelInfo> {

    public HotelInfo map(int index, ResultSet resultSet, StatementContext statementContext) throws SQLException {
        int hotelId = resultSet.getInt("hotel_id")
        String jsonStr = resultSet.getString("json_str")

        return new HotelInfo(propertyId: hotelId, propertyInfo: jsonStr)
    }
}
