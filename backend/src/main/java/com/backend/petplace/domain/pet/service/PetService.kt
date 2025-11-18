package com.backend.petplace.domain.pet.service

import com.backend.petplace.domain.pet.dto.request.CreatePetRequest
import com.backend.petplace.domain.pet.dto.request.UpdatePetRequest
import com.backend.petplace.domain.pet.dto.response.CreatePetResponse
import com.backend.petplace.domain.pet.dto.response.UpdatePetResponse
import com.backend.petplace.domain.pet.entity.Pet
import com.backend.petplace.domain.pet.repository.PetRepository
import com.backend.petplace.domain.user.entity.User
import com.backend.petplace.domain.user.repository.UserRepository
import com.backend.petplace.global.exception.BusinessException
import com.backend.petplace.global.response.ErrorCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PetService(
    private val petRepository: PetRepository,
    private val userRepository: UserRepository
) {

    private fun findUser(userId: Long): User =
        userRepository.findById(userId)
            .orElseThrow { BusinessException(ErrorCode.NOT_FOUND_MEMBER) }

    private fun validateUser(userId: Long?, validationId: Long?) {
        if (userId != validationId) {
            throw BusinessException(ErrorCode.MEMBER_ACCESS_DENIED)
        }
    }

    @Transactional
    fun createPet(userId: Long, request: CreatePetRequest): CreatePetResponse {
        val user = findUser(userId)
        val pet = Pet.createPet(user, request)
        pet.assignUser(user)

        petRepository.save(pet)
        return CreatePetResponse.from(pet)
    }

    @Transactional
    fun updatePet(userId: Long, id: Long, request: UpdatePetRequest): UpdatePetResponse {
        val user = findUser(userId)
        val pet = petRepository.findById(id)
            .orElseThrow { BusinessException(ErrorCode.NOT_FOUND_PET) }

        validateUser(user.id, pet.user?.id)

        pet.updatePet(request)
        return UpdatePetResponse.from(pet)
    }

    @Transactional
    fun deletePet(userId: Long, id: Long) {
        val user = findUser(userId)
        val pet = petRepository.findById(id)
            .orElseThrow { BusinessException(ErrorCode.NOT_FOUND_PET) }

        validateUser(user.id, pet.user?.id)

        if (pet.isActivated()) {
            pet.unActivated()
        } else {
            throw BusinessException(ErrorCode.ALREADY_DELETED)
        }
    }
}
