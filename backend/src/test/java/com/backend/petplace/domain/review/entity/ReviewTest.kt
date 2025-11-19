package com.backend.petplace.domain.review.entity

import com.backend.petplace.domain.place.entity.Category1Type
import com.backend.petplace.domain.place.entity.Category2Type
import com.backend.petplace.domain.place.entity.Place
import com.backend.petplace.domain.user.entity.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ReviewTest {

    @Test
    @DisplayName("createNewReview: 필수 필드를 포함하여 새로운 Review 엔티티 생성")
    fun createNewReview() {

        // given
        val mockUser = User(
            email = "testuser@example.com",
            password = "testpassword123",
            nickName = "TestUser",
            address = "서울 강남구",
            zipcode = "01234",
            addressDetail = "101호" // Nullable 필드
        )

        val mockPlace = Place(
            id = 1L,
            uniqueKey = "PLACE_UK_1", // 필수 필드
            name = "TestPlace",
            category1 = Category1Type.ETC,
            category2 = Category2Type.ETC,
            latitude = 37.5,
            longitude = 127.0,
            address = "123 Test St"
        )

        val rating = 5
        val content = "This is a test review."
        val imageUrl = "reviews/test-image.jpg"

        // when
        val newReview = Review.createNewReview(mockUser, mockPlace, content, rating, imageUrl)

        // then
        assertThat(newReview).isNotNull()

        assertThat(newReview.id).isNull() // 아직 저장되지 않았으므로 ID는 null이어야 함

        assertThat(newReview.user).isEqualTo(mockUser)
        assertThat(newReview.place).isEqualTo(mockPlace)
        assertThat(newReview.content).isEqualTo(content)
        assertThat(newReview.rating).isEqualTo(rating)
        assertThat(newReview.imageUrl).isEqualTo(imageUrl)
    }
}