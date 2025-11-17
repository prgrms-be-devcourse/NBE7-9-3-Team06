

package com.backend.petplace.global.config.swagger

import com.backend.petplace.global.response.ApiResponse
import com.backend.petplace.global.response.ErrorCode
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.examples.Example
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.responses.ApiResponses
import org.springdoc.core.customizers.OperationCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun customOpenAPI(): OpenAPI =
        OpenAPI()
            .info(
                Info()
                    .title("PetPlace")
                    .version("1.0")
                    .description("PetPlace API 서버 문서입니다")
            )

    @Bean
    fun customize(): OperationCustomizer {
        return OperationCustomizer { operation, handlerMethod ->
            val annotation = handlerMethod.getMethodAnnotation(ApiErrorCodeExamples::class.java)
            if (annotation != null) {
                generateErrorCodeResponseExample(operation, annotation.value)
            }
            operation
        }
    }

    private fun generateErrorCodeResponseExample(
        operation: Operation,
        errorCodes: Array<out ErrorCode>
    ) {
        val responses = operation.responses

        val statusWithExampleHolders = errorCodes
            .map { errorCode ->
                ExampleHolder(
                    name = errorCode.name,
                    code = errorCode.status.value(),
                    holder = getSwaggerExample(errorCode)
                )
            }
            .groupBy { it.code }

        addExamplesToResponses(responses, statusWithExampleHolders)
    }

    private fun getSwaggerExample(errorCode: ErrorCode): Example {
        return Example().apply {
            summary = errorCode.message
            value = ApiResponse.error<Any>(errorCode)
        }
    }

    private fun addExamplesToResponses(
        responses: ApiResponses,
        statusWithExampleHolders: Map<Int, List<ExampleHolder>>
    ) {
        statusWithExampleHolders.forEach { (status, holders) ->
            val content = Content()
            val mediaType = MediaType()

            holders.forEach { exampleHolder ->
                mediaType.addExamples(exampleHolder.name, exampleHolder.holder)
            }

            content.addMediaType("application/json", mediaType)

            val apiResponse = io.swagger.v3.oas.models.responses.ApiResponse()
                .apply { this.content = content }

            responses.addApiResponse(status.toString(), apiResponse)
        }
    }
}