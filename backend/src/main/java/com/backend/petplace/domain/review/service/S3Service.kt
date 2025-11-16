package com.backend.petplace.domain.review.service

import com.amazonaws.HttpMethod
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import com.backend.petplace.domain.review.dto.response.PresignedUrlResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*

@Service
class S3Service (
    private val amazonS3Client: AmazonS3,

    @Value("\${cloud.aws.s3.bucket}")
    private val bucket: String

) {

    /**
     * S3에 파일을 PUT 방식으로 업로드할 수 있는 Presigned URL 생성
     */
    fun generatePresignedUrl(dirName: String?, originalFilename: String?): PresignedUrlResponse {
        val uniqueFileName = createUniqueFileName(originalFilename, dirName)
        val expiration: Date = Date(System.currentTimeMillis() + PRESIGNED_URL_EXPIRATION_MS)

        val generatePresignedUrlRequest =
            GeneratePresignedUrlRequest(bucket, uniqueFileName)
                .withMethod(HttpMethod.PUT)
                .withExpiration(expiration)

        val presignedUrl = amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest)

        // 클라이언트에게 URL과 함께, 업로드 후 서버에 알려줄 파일 경로도 전달
        return PresignedUrlResponse(
            presignedUrl = presignedUrl.toString(),
            s3FilePath = uniqueFileName
        )
    }

    fun getPublicUrl(s3Path: String?): String? {
        if (s3Path.isNullOrBlank()) {
            return null
        }
        return amazonS3Client.getUrl(bucket, s3Path).toString()
    }

    private fun createUniqueFileName(originalFilename: String?, dirName: String?): String {
        // 확장자 추출 로직 간소화
        val extension = originalFilename?.lastIndexOf('.')?.let { index ->
            if (index >= 0) originalFilename.substring(index) else ""
        } ?: ""

        // 문자열 템플릿 ("$변수") 사용으로 가독성 향상
        return "$dirName/${UUID.randomUUID()}$extension"
    }

    companion object {
        private const val PRESIGNED_URL_EXPIRATION_MS = 1000 * 60 * 10L
    }
}
