package com.backend.petplace.global.s3

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.ObjectMetadata
import com.backend.petplace.global.exception.BusinessException
import com.backend.petplace.global.response.ErrorCode
import org.springframework.beans.factory.annotation.Value
import org.springframework.cglib.core.CollectionUtils.bucket
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.util.*

@Component
class S3Uploader(
    private val amazonS3Client: AmazonS3,

    @Value("\${cloud.aws.s3.bucket}")
    private val bucket: String

) {

    fun upload(multipartFile: MultipartFile, dirName: String): String {
        // 프로퍼티 접근 문법 사용 (.originalFilename)
        val originalFilename = multipartFile.originalFilename
        validateFileName(originalFilename)

        val uniqueFileName = createUniqueFileName(originalFilename!!, dirName)
        val metadata = createObjectMetadata(multipartFile)

        try {
            amazonS3Client.putObject(
                bucket,
                uniqueFileName,
                multipartFile.inputStream,
                metadata
            )
        } catch (e: IOException) {
            throw BusinessException(ErrorCode.FAIL_TO_UPLOAD_IMAGE, e)
        }
        return amazonS3Client.getUrl(bucket, uniqueFileName).toString()
    }

    private fun validateFileName(originalFilename: String?) {
        if (originalFilename.isNullOrBlank()) {
            throw BusinessException(ErrorCode.INVALID_FILE_NAME)
        }
    }

    private fun createObjectMetadata(multipartFile: MultipartFile): ObjectMetadata {

        return ObjectMetadata().apply {
            contentLength = multipartFile.size       // .getSize() -> .size
            contentType = multipartFile.contentType  // .getContentType() -> .contentType
        }
    }

    private fun createUniqueFileName(originalFilename: String, dirName: String): String {
        // 문자열 템플릿 사용 ("$변수")
        return "$dirName/${UUID.randomUUID()}-$originalFilename"
    }
}