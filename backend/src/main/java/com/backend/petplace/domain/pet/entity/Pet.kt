package com.backend.petplace.domain.pet.entity

import com.backend.petplace.domain.pet.dto.request.CreatePetRequest
import com.backend.petplace.domain.pet.dto.request.UpdatePetRequest
import com.backend.petplace.domain.user.entity.User
import com.backend.petplace.global.entity.BaseEntity
import jakarta.persistence.*
import java.time.LocalDate

@Entity
class Pet (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "petId")
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "userId")
    var user: User? = null,

    @Column(name = "name")
    var name: String? = null,

    @Column(name = "gender", nullable = false)
    @Enumerated(EnumType.STRING)
    var gender: Gender? = null,

    @Column(name = "birthDate")
    var birthDate: LocalDate? = null,

    @Column(name = "type")
    var type: String? = null

) : BaseEntity() {

    companion object { //똑같은 객체 타입에 다른 주솟값을 가지더라도 이 object안에서는 같은 데이터를 공유받을 수 있습니다.
        @JvmStatic //Kotlin에는 Static이 없으므로 어노테이션을 이용하여 바로 접근
        fun createPet(user: User, request: CreatePetRequest): Pet {
            return Pet(
                user = user,
                name = request.name,
                gender = request.gender?.let { Gender.valueOf(it) },
                birthDate = request.birthDate,
                type = request.type
            )
        }
    }

    fun assignUser(newUser: User) {
        this.user?.pets?.remove(this)
        this.user = newUser
        newUser.pets.add(this)
    }

    fun updatePet(request: UpdatePetRequest) {
        if (request.isValidName()) {
            this.name = request.name
        }

        request.gender?.let {
            this.gender = Gender.valueOf(it)
        }

        request.type?.let {
            this.type = it
        }

        request.birthDate?.let {
            this.birthDate = it
        }
    }
}