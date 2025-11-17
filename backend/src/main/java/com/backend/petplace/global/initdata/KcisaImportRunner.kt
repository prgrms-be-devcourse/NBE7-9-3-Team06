package com.backend.petplace.global.initdata

import com.backend.petplace.domain.place.importer.KcisaImportService
import com.backend.petplace.global.config.ImportProperties
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(prefix = "import", name = ["enabled"], havingValue = "true")
class KcisaImportRunner(
    private val service: KcisaImportService,
    private val props: ImportProperties
) : ApplicationRunner {

    override fun run(args: ApplicationArguments) {
        val count = service.importAll()
        println("[KCISA IMPORT] imported=$count (pageSize=${props.pageSize})")
    }
}
