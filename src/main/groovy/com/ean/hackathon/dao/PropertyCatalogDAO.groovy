package com.ean.hackathon.dao

import com.ean.hackathon.model.HotelInfo
import org.skife.jdbi.v2.sqlobject.Bind
import org.skife.jdbi.v2.sqlobject.SqlQuery
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator
import org.skife.jdbi.v2.unstable.BindIn

@UseStringTemplate3StatementLocator
interface PropertyCatalogDAO {
    @RegisterMapper(HotelInfoMapper)
    @SqlQuery("select hotel_id, json_str from ean_rapid_hotels where city ilike :city")
    List<HotelInfo> findByCityName(@Bind("city") String city)

    @SqlQuery("select hotel_id from ean_rapid_hotels where city ilike :city")
    List<Integer> findIdsByCityName(@Bind("city") String city)

    @SqlQuery("select json_str from ean_rapid_hotels where hotel_id in (<idList>)")
    List<String> getJsonForIds(@BindIn("idList") List<Integer> idList)

    @SqlQuery("select distinct(LOWER(city)) from ean_rapid_hotels")
    Set<String> getAllCities()

}
