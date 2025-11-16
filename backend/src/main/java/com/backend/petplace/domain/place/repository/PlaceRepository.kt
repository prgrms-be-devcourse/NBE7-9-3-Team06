package com.backend.petplace.domain.place.repository

import com.backend.petplace.domain.place.entity.Place
import com.backend.petplace.domain.place.projection.PlaceSearchRow
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PlaceRepository : JpaRepository<Place, Long> {

    fun findByUniqueKey(uniqueKey: String): Optional<Place>

    @Query(
        value = """
        SELECT
          p.id              AS id,
          p.name            AS name,
          p.category2       AS category2,
          p.latitude        AS latitude,
          p.longitude       AS longitude,
          ST_Distance_Sphere(POINT(:lon, :lat), POINT(p.longitude, p.latitude)) AS distanceMeters,
          p.average_rating  AS averageRating,
          p.address         AS address
        FROM place p
        WHERE
          p.latitude  BETWEEN :minLat AND :maxLat
          AND p.longitude BETWEEN :minLon AND :maxLon
          AND ST_Distance_Sphere(POINT(:lon, :lat), POINT(p.longitude, p.latitude)) <= :radiusMeters
          AND (:category2Count = 0 OR p.category2 IN (:category2List))
          AND (:keyword IS NULL OR p.name LIKE CONCAT('%', :keyword, '%'))
        ORDER BY distanceMeters ASC
        LIMIT :limit OFFSET :offset
        """,
        nativeQuery = true
    )
    fun searchWithinRadius(
        @Param("lat") lat: Double,
        @Param("lon") lon: Double,
        @Param("minLat") minLat: Double,
        @Param("maxLat") maxLat: Double,
        @Param("minLon") minLon: Double,
        @Param("maxLon") maxLon: Double,
        @Param("radiusMeters") radiusMeters: Int,
        @Param("category2List") category2List: List<String>,
        @Param("category2Count") category2Count: Int,
        @Param("keyword") keyword: String?,
        @Param("limit") limit: Int,
        @Param("offset") offset: Int
    ): List<PlaceSearchRow>
}