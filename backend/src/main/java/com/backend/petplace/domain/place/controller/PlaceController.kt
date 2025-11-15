package com.backend.petplace.domain.place.controller

import com.backend.petplace.domain.place.dto.response.PlaceDetailResponse
import com.backend.petplace.domain.place.dto.response.PlaceSearchResponse
import com.backend.petplace.domain.place.entity.Category2Type
import com.backend.petplace.domain.place.service.PlaceService
import com.backend.petplace.global.response.ApiResponse
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Validated
@RestController
@RequestMapping("api/v1/places")
class PlaceController(
    private val placeService: PlaceService
) : PlaceSpecification {

    @GetMapping("/search")
    override fun searchPlaces(
        @RequestParam @NotNull lat: Double,
        @RequestParam @NotNull lon: Double,
        @RequestParam(required = false) radiusKm: Int?,
        @RequestParam(required = false) category2: List<Category2Type>?,
        @RequestParam(required = false) keyword: String?
    ): ResponseEntity<ApiResponse<List<PlaceSearchResponse>>> {

        val results = placeService.searchPlaces(lat, lon, radiusKm, category2, keyword)
        return ResponseEntity.ok(ApiResponse.success(results))
    }

    @GetMapping("/{placeId}")
    override fun getPlaceDetail(
        @PathVariable @Positive placeId: Long
    ): ResponseEntity<ApiResponse<PlaceDetailResponse>> {
        val detail = placeService.getPlaceDetail(placeId)
        return ResponseEntity.ok(ApiResponse.success(detail))
    }
}
