package com.backend.petplace.domain.place.entity

import com.backend.petplace.global.entity.BaseEntity
import jakarta.persistence.*

@Entity
@Table(
    name = "place",
    indexes = [
        Index(name = "idx_place_lat", columnList = "latitude"),
        Index(name = "idx_place_lon", columnList = "longitude"),
        Index(name = "idx_place_category2", columnList = "category2"),
        Index(name = "idx_place_name", columnList = "name")
    ]
)
class Place(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 100, unique = true)
    var uniqueKey: String,

    @Column(nullable = false, length = 200)
    var name: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    var category1: Category1Type,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    var category2: Category2Type,

    @Column(length = 500)
    var openingHours: String? = null,

    @Column(length = 300)
    var closedDays: String? = null,

    var parking: Boolean? = null,

    var petAllowed: Boolean? = null,

    @Column(length = 300)
    var petRestriction: String? = null,

    @Column(length = 30)
    var tel: String? = null,

    @Column(length = 500)
    var url: String? = null,

    @Column(length = 10)
    var postalCode: String? = null,

    @Column(length = 300)
    var address: String? = null,

    @Column(nullable = false)
    var latitude: Double,

    @Column(nullable = false)
    var longitude: Double,

    @Column(length = 1000)
    var rawDescription: String? = null,

    // @Builder.Default 대체: 기본값 설정
    var averageRating: Double = 0.0,

    var totalReviewCount: Int = 0

) : BaseEntity() {

    fun updateReviewStats(newRating: Int) {
        val totalScore = this.averageRating * this.totalReviewCount
        this.totalReviewCount++
        this.averageRating = (totalScore + newRating) / this.totalReviewCount
    }

    fun apply(
        name: String,
        c1: Category1Type,
        c2: Category2Type,
        openingHours: String?,
        closedDays: String?,
        parking: Boolean?,
        petAllowed: Boolean?,
        petRestriction: String?,
        tel: String?,
        url: String?,
        postalCode: String?,
        address: String?,
        lat: Double,
        lng: Double,
        rawDescription: String?
    ) {
        this.name = name
        this.category1 = c1
        this.category2 = c2
        this.openingHours = openingHours
        this.closedDays = closedDays
        this.parking = parking
        this.petAllowed = petAllowed
        this.petRestriction = petRestriction
        this.tel = tel
        this.url = url
        this.postalCode = postalCode
        this.address = address
        this.latitude = lat
        this.longitude = lng
        this.rawDescription = rawDescription
    }
}
