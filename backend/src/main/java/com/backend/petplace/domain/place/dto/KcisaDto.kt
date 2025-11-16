package com.backend.petplace.domain.place.dto

data class KcisaDto(
    val response: Response?
) {
    data class Response(
        val header: Header?,
        val body: Body?
    )

    data class Header(
        val resultCode: String?,
        val resultMsg: String?
    )

    data class Body(
        val items: Items?,
        val numOfRows: String?,
        val pageNo: String?,
        val totalCount: String?
    )

    data class Items(
        val item: List<Item>?
    )

    data class Item(
        val title: String?,
        val issuedDate: String?,
        val category1: String?,
        val category2: String?,
        val category3: String?,
        val description: String?,
        val tel: String?,
        val url: String?,
        val address: String?,
        val coordinates: String?,
        val charge: String?
    )
}
