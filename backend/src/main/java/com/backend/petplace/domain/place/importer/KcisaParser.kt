package com.backend.petplace.domain.place.importer

import com.backend.petplace.domain.place.dto.KcisaDto
import com.backend.petplace.domain.place.entity.Category1Type
import com.backend.petplace.domain.place.entity.Category2Type
import com.backend.petplace.domain.place.entity.mapper.CategoryMapper
import com.backend.petplace.domain.place.importer.model.ImportParsed
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.regex.Pattern

@Component
class KcisaParser {

    fun parse(it: KcisaDto.Item): ImportParsed {
        // 1) 주소/우편번호
        var postal: String? = null
        var addr: String? = null

        it.address
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?.let { address ->
                val m = POSTAL_P.matcher(address)
                if (m.find()) {
                    postal = m.group(1)
                    addr = m.group(2)
                } else {
                    addr = address
                }
            }

        // 2) 좌표
        var lat: Double? = null
        var lng: Double? = null

        it.coordinates
            ?.let { coord ->
                val m = COORD_P.matcher(coord)
                if (m.find()) {
                    lat = m.group(2).toDouble() * if (m.group(1).equals("S", ignoreCase = true)) -1 else 1
                    lng = m.group(4).toDouble() * if (m.group(3).equals("W", ignoreCase = true)) -1 else 1
                }
            }

        // 3) description 토큰 파싱
        var opening: String? = null
        var closed: String? = null
        var petLimit: String? = null
        var parking: Boolean? = null
        var petAllowed: Boolean? = null

        val desc = it.description
        if (!desc.isNullOrBlank()) {
            val tokens = desc.split("|")
            for (raw in tokens) {
                val s = raw.trim()
                when {
                    OPENING_P.matcher(s).find() -> {
                        opening = s.replaceFirst("^\\s*운영\\s*시간\\s*:?\\s*".toRegex(), "").trim()
                    }
                    CLOSED_P.matcher(s).find() -> {
                        closed = s.replaceFirst("^\\s*휴\\s*무\\s*일\\s*:?\\s*".toRegex(), "").trim()
                    }
                    s.contains("주차가능") -> parking = true
                    s.contains("주차 불가") -> parking = false
                    s.contains("동반가능") -> petAllowed = true
                    s.contains("동반불가") -> petAllowed = false
                    PET_LIMIT_P.matcher(s).find() -> {
                        petLimit =
                            s.replaceFirst("^\\s*반려동물\\s*제한사항\\s*:?\\s*".toRegex(), "").trim()
                    }
                }
            }
        }

        // 4) tel/url
        val tel = it.tel?.replace(Regex("[^0-9-]"), "")
        val url = normalizeUrl(it.url)

        // 5) 카테고리 매핑
        val c1: Category1Type = CategoryMapper.mapCategory1(it.category1)
        val c2: Category2Type = CategoryMapper.mapCategory2(it.category2)

        // 6) uniqueKey: title + 우편번호(없으면 주소) → SHA-256 해싱
        val uniqueKey = buildUniqueKey(it.title, postal, addr)

        return ImportParsed(
            name = it.title ?: "",
            category1 = c1,
            category2 = c2,
            openingHours = opening,
            closedDays = closed,
            parking = parking,
            petAllowed = petAllowed,
            petRestriction = petLimit,
            tel = tel,
            url = url,
            postalCode = postal,
            address = addr,
            latitude = lat,
            longitude = lng,
            rawDescription = it.description,
            uniqueKey = uniqueKey
        )
    }

    companion object {
        // 좌표 패턴
        private val COORD_P: Pattern =
            Pattern.compile("([NS])\\s*([0-9.]+)\\s*,\\s*([EW])\\s*([0-9.]+)")

        // 우편번호 패턴
        private val POSTAL_P: Pattern =
            Pattern.compile("^\\((\\d{5})\\)\\s*(.+)$")

        // 운영시간 패턴
        private val OPENING_P: Pattern =
            Pattern.compile("^\\s*운영\\s*시간\\s*:?\\s*(.+)$")

        // 휴무일 패턴
        private val CLOSED_P: Pattern =
            Pattern.compile("^\\s*휴\\s*무\\s*일\\s*:?\\s*(.+)$")

        // 반려동물 제한사항 패턴
        private val PET_LIMIT_P: Pattern =
            Pattern.compile("^\\s*반려동물\\s*제한사항\\s*:?\\s*(.+)$")

        private fun normalizeTitle(t: String?): String =
            t?.trim()
                ?.lowercase()
                ?.replace("\\s+".toRegex(), " ")
                ?: ""

        private fun normalizeAddrForKey(addr: String?): String =
            addr?.trim()
                ?.lowercase()
                ?.replace("\\s+".toRegex(), " ")
                ?: ""

        private fun buildUniqueKey(title: String?, postal: String?, addr: String?): String {
            val t = normalizeTitle(title)
            val base = if (!postal.isNullOrBlank()) {
                "$t|$postal"
            } else {
                "$t|${normalizeAddrForKey(addr)}"
            }
            return sha256Hex(base)
        }

        private fun normalizeUrl(u: String?): String? {
            if (u.isNullOrBlank()) return null
            var s = u.trim()
            if (!s.matches(Regex("^(?i)https?://.*"))) {
                s = "http://$s"
            }
            return s
        }

        private fun sha256Hex(s: String): String {
            return try {
                val md = MessageDigest.getInstance("SHA-256")
                val out = md.digest(s.toByteArray(StandardCharsets.UTF_8))
                buildString {
                    for (b in out) {
                        append(String.format("%02x", b))
                    }
                }
            } catch (e: NoSuchAlgorithmException) {
                throw IllegalStateException("SHA-256 not available", e)
            }
        }
    }
}
