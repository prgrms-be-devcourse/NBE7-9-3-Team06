package com.backend.petplace.domain.pet.service

import com.backend.petplace.domain.pet.dto.request.CreatePetRequest
import com.backend.petplace.domain.pet.dto.request.UpdatePetRequest
import com.backend.petplace.domain.pet.dto.response.CreatePetResponse
import com.backend.petplace.domain.pet.entity.Gender
import com.backend.petplace.domain.pet.entity.Pet
import com.backend.petplace.domain.pet.repository.PetRepository
import com.backend.petplace.domain.user.entity.User
import com.backend.petplace.domain.user.repository.UserRepository
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@DataJpaTest //단위 테스트 - 통합 테스트 시 SpringBootTest 어노테이션으로 교체
@Import(PetService::class)
@Transactional
class PetServiceTest {

    @Autowired
    private lateinit var petService: PetService

    @Autowired
    private lateinit var petRepository: PetRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @PersistenceContext
    private lateinit var em: EntityManager

    private lateinit var user: User

    @BeforeEach
    fun init() {
        user = User(
            nickName = "user1",
            email = "user1@example.com",
            password = "pwd",
            address = "주소",
            addressDetail = "주소2",
            zipcode = "11111"
        )

        userRepository.save(user)
    }

    @Test
    @DisplayName("펫 생성")
    fun createPet() {
        // given
        val request = CreatePetRequest("뚜뚜", Gender.Female.toString(), LocalDate.of(2022, 1, 1), "말티즈")

        // when
        val response: CreatePetResponse = petService.createPet(user.id!!, request)
        val saved: Pet = petRepository.findById(response.id).orElseThrow()

        // then
        assertThat(saved.name).isEqualTo("뚜뚜")
        assertThat(saved.gender).isEqualTo(Gender.Female)
        assertThat(saved.birthDate).isEqualTo(LocalDate.of(2022, 1, 1))
        assertThat(saved.type).isEqualTo("말티즈")
        assertThat(saved.user?.id).isEqualTo(user.id)
    }

    @Test
    @DisplayName("펫 수정")
    fun updatePet() {
        // given
        val createRequest = CreatePetRequest("뚜뚜", Gender.Female.toString(), LocalDate.of(2022, 1, 1), "말티즈")

        val createResponse = petService.createPet(user.id!!, createRequest)
        val saved: Pet = petRepository.findById(createResponse.id).orElseThrow()

        // when
        val updateRequest = UpdatePetRequest("두두", Gender.Male.toString(), LocalDate.of(2021, 1, 1), "웰시코기")

        petService.updatePet(user.id!!, saved.id!!, updateRequest)

        em.flush()
        em.clear()

        val updated: Pet = petRepository.findById(saved.id!!).orElseThrow()

        // then
        assertThat(updated.name).isEqualTo("두두")
        assertThat(updated.gender).isEqualTo(Gender.Male)
        assertThat(updated.birthDate).isEqualTo(LocalDate.of(2021, 1, 1))
        assertThat(updated.type).isEqualTo("웰시코기")
        assertThat(updated.user?.id).isEqualTo(user.id)
    }

    @Test
    @DisplayName("펫 삭제")
    fun deletePet() {
        // given
        val request = CreatePetRequest("뚜뚜", Gender.Female.toString(), LocalDate.of(2022, 1, 1), "말티즈")

        val response = petService.createPet(user.id!!, request)
        val saved: Pet = petRepository.findById(response.id).orElseThrow()

        // when
        petService.deletePet(user.id!!, saved.id!!)

        em.flush()
        em.clear()

        val deleted: Pet = petRepository.findById(saved.id!!).orElseThrow()

        // then
        assertThat(deleted.isActivated()).isFalse()
    }
}
