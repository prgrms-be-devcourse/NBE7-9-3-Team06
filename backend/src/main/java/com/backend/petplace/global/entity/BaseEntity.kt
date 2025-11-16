package com.backend.petplace.global.entity

import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
open class BaseEntity {
    @CreatedDate
    // var인 이유
    // 두괄식: val과 JPA가 호환되지 않는다
    // 논리적으론 createdDate는 한번 정하면 안 바뀜
    // 그러나 JPA/Hibernate가 DB에서 값을 읽어올 때도 필드에 주입하려 한다.
    // Kotlin은 val이 읽기 전용이라, setter가 없으면 JPA가 값을 넣을 수 없다.
    // 단, set을 private으로 하여 JPA/Hibernate만 접근 가능하게 하면 외부에서 변경하는 것을 막을 수 있다.
    var createdDate: LocalDateTime? = null
        private set

    @LastModifiedDate
    var modifiedDate: LocalDateTime? = null
        private set

    protected fun readModifiedDate(): LocalDateTime? = modifiedDate

    protected var activated: Boolean = true

    fun unActivated() {
        this.activated = false
    }

    // Java에서 사용 가능한 getter
    fun isActivated(): Boolean = activated
}
