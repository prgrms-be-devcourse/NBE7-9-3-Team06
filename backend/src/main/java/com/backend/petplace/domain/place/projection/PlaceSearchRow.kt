package com.backend.petplace.domain.place.projection

interface PlaceSearchRow {
    val id: Long
    val name: String
    val category2: String
    val latitude: Double
    val longitude: Double
    val distanceMeters: Int
    val averageRating: Double?
    val address: String
}
