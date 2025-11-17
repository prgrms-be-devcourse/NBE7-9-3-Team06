package com.backend.petplace.domain.email.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class EmailAuthCode(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val authCode: String,

    @Column(nullable = false)
    val email: String,

    @Column(nullable = false)
    val expiredAt: LocalDateTime,
) {
    companion object {
        @JvmStatic
        fun create(email: String ,authCode: String ,authCodeExpirationTime: Long): EmailAuthCode{
            return EmailAuthCode(
                email = email,
                authCode = authCode,
                expiredAt = LocalDateTime.now().plusMinutes(authCodeExpirationTime),
            )
        }
    }

    fun isExpired(): Boolean { return LocalDateTime.now().isAfter(expiredAt) }

    @Column(nullable = false)
    var verified: Boolean = false
        private set

    fun markVerifiedTrue() { this.verified = true }
}