package com.backend.petplace.domain.pet.repository

import com.backend.petplace.domain.mypage.dto.MyPageUserPets
import com.backend.petplace.domain.pet.entity.Pet
import com.backend.petplace.domain.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PetRepository : JpaRepository<Pet, Long> {

    @Query(
        """
        SELECT new com.backend.petplace.domain.mypage.dto.MyPageUserPets(
            p.id, p.name, p.gender, p.birthDate, p.type
        )
        FROM Pet p
        WHERE p.user = :user AND p.activated = true
        """
    )
    fun findByUserWithActivatedPet(@Param("user") user: User): List<MyPageUserPets>
}
