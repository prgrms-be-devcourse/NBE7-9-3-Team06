package com.backend.petplace.global.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import java.util.*

@Configuration
class EmailConfig {
    @Value("\${spring.mail.host}")
    private lateinit var host: String

    @Value("\${spring.mail.port}")
    private var port: Int = 0

    @Value("\${spring.mail.username}")
    private lateinit var username: String

    @Value("\${spring.mail.password}")
    private lateinit var password: String

    @Value("\${spring.mail.properties.mail.smtp.auth}")
    private var auth: Boolean = false

    @Value("\${spring.mail.properties.mail.smtp.starttls.enable}")
    private var starttlsEnable: Boolean = false

    @Value("\${spring.mail.properties.mail.smtp.starttls.required}")
    private var starttlsRequired: Boolean = false

    @Value("\${spring.mail.properties.mail.smtp.connection timeout}")
    private var connectionTimeout: Int = 0

    @Value("\${spring.mail.properties.mail.smtp.timeout}")
    private var timeout: Int = 0

    @Value("\${spring.mail.properties.mail.smtp.write timeout}")
    private var writeTimeout: Int = 0

    @Bean
    fun javaMailSender(): JavaMailSender {
        val mailSender = JavaMailSenderImpl()
        mailSender.host = host
        mailSender.port = port
        mailSender.username = username
        mailSender.password = password
        mailSender.defaultEncoding = "UTF-8"
        mailSender.javaMailProperties = getMailProperties()
        return mailSender
    }

    private fun getMailProperties(): Properties {
        val properties = Properties()
        properties["mail.smtp.auth"] = auth.toString()
        properties["mail.smtp.starttls.enable"] = starttlsEnable.toString()
        properties["mail.smtp.starttls.required"] = starttlsRequired.toString()
        properties["mail.smtp.connection timeout"] = connectionTimeout.toString()
        properties["mail.smtp.timeout"] = timeout.toString()
        properties["mail.smtp.write timeout"] = writeTimeout.toString()
        return properties
    }
}