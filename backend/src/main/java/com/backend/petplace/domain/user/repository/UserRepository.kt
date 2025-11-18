package com.backend.petplace.domain.user.repository

import com.backend.petplace.domain.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<User, Long> {
    fun existsByNickName(name: String): Boolean

    fun existsByEmail(email: String): Boolean

    fun findByNickName(nickName: String): User?
}