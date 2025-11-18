package com.backend.petplace.domain.email.repository

import com.backend.petplace.domain.email.entity.EmailAuthCode
import org.springframework.data.jpa.repository.JpaRepository

interface EmailAuthCodeRepository: JpaRepository<EmailAuthCode, String> {
    fun findByEmailAndAuthCode(email: String, authCode: String): EmailAuthCode?
}