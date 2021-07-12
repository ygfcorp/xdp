package com.kakao.xdp.flow.domain.recipe

import com.kakao.xdp.commons.system.randomUUID
import com.kakao.xdp.flow.domain.AbstractRepositoryTest
import com.kakao.xdp.flow.domain.processor.ProcessorGroup
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime

class RecipeRepositoryTest(@Autowired private val cut: RecipeRepository) : AbstractRepositoryTest() {
    @Test
    fun insertAndFind() {
        val startNode =
            nodeStart {
                processorGroup { ProcessorGroup("Jdbc Source", "559a5e5a-1aef-3205-9740-2606c3ae998e") }
                node {
                    processorGroup { ProcessorGroup("Transformer", "d29685d8-0961-30bf-b4c3-fe91aa89df6e") }
                    node {
                        processorGroup { ProcessorGroup("S3 Writer", "4e894aa7-0d17-307a-9ce9-f415131c9e0b") }
                    }
                }
            }

        val dag = RecipeDAG(startNode)

        val now = LocalDateTime.now()
        val recipe = Recipe(
            id = randomUUID(),
            name = "Data Transfer from JDBC to Elastic Search",
            dag = dag,
            description = "Moving data from your JDBC compatible source to Elastic Search.",
            status = RecipeStatus.ACTIVE,
            createdAt = now,
            updatedAt = now
        )

        cut.insert(recipe)

        val found = cut.findById(recipe.id)

        Assertions.assertNotNull(found)
    }
}