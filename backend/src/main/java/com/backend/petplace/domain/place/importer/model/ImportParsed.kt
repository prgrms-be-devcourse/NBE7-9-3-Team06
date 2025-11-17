package com.backend.petplace.domain.place.importer.model

import com.backend.petplace.domain.place.entity.Category1Type
import com.backend.petplace.domain.place.entity.Category2Type

data class ImportParsed(
  val name: String,
  val category1: Category1Type,
  val category2: Category2Type,
  val openingHours: String?,
  val closedDays: String?,
  val parking: Boolean?,
  val petAllowed: Boolean?,
  val petRestriction: String?,
  val tel: String?,
  val url: String?,
  val postalCode: String?,
  val address: String?,
  val latitude: Double?,
  val longitude: Double?,
  val rawDescription: String?,
  val uniqueKey: String
)
