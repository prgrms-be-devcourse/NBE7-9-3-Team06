package com.backend.petplace.domain.review.service

import com.amazonaws.HttpMethod
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import com.backend.petplace.domain.review.dto.response.PresignedUrlResponse
import lombok.RequiredArgsConstructor
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*

@Service
@RequiredArgsConstructor
class S3Service {
    private val amazonS3Client: AmazonS3Client? = null

    @Value("\${cloud.aws.s3.bucket}")
    private val bucket: String? = null

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

        val presignedUrl = amazonS3Client!!.generatePresignedUrl(generatePresignedUrlRequest)

        // 클라이언트에게 URL과 함께, 업로드 후 서버에 알려줄 파일 경로도 전달
        return PresignedUrlResponse(presignedUrl.toString(), uniqueFileName)
    }

    fun getPublicUrl(s3Path: String?): String? {
        if (s3Path == null || s3Path.isEmpty()) {
            return null
        }
        return amazonS3Client!!.getUrl(bucket, s3Path).toString()
    }

    private fun createUniqueFileName(originalFilename: String?, dirName: String?): String {
        var extension = ""
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."))
        }
        // 디렉토리/UUID.확장자 형식으로 고유 파일명 생성
        return dirName + "/" + UUID.randomUUID() + extension
    }

    companion object {
        private val PRESIGNED_URL_EXPIRATION_MS = (1000 * 60 * 10).toLong()
    }
}
