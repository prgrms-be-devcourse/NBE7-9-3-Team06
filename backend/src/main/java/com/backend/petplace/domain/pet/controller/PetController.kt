package com.backend.petplace.domain.pet.controller

import com.backend.petplace.domain.pet.dto.request.CreatePetRequest
import com.backend.petplace.domain.pet.dto.request.UpdatePetRequest
import com.backend.petplace.domain.pet.dto.response.CreatePetResponse
import com.backend.petplace.domain.pet.dto.response.UpdatePetResponse
import com.backend.petplace.domain.pet.service.PetService
import com.backend.petplace.global.jwt.CustomUserDetails
import com.backend.petplace.global.response.ApiResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/v1/")
class PetController(
    private val petService: PetService
) : PetSpecification {

    @PostMapping("/create-pet")
    override fun createPet(
        @RequestBody @Valid request: CreatePetRequest,
        @AuthenticationPrincipal user: CustomUserDetails
    ): ResponseEntity<ApiResponse<CreatePetResponse?>> {

        val userId = user.userId
        val response = petService.createPet(userId, request)
        return ResponseEntity.ok(ApiResponse.create(response))
    }

    @PatchMapping("/update-pet/{id}")
    override fun updatePet(
        @PathVariable("id") id: Long,
        @RequestBody @Valid request: UpdatePetRequest,
        @AuthenticationPrincipal user: CustomUserDetails
    ): ResponseEntity<ApiResponse<UpdatePetResponse?>> {

        val userId = user.userId
        val response = petService.updatePet(userId, id, request)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @DeleteMapping("/delete-pet/{id}")
    override fun deletePet(
        @PathVariable("id") id: Long,
        @AuthenticationPrincipal user: CustomUserDetails
    ): ResponseEntity<ApiResponse<Void?>> {

        val userId = user.userId
        petService.deletePet(userId, id)
        return ResponseEntity.ok(ApiResponse.success<Void?>())
    }
}
