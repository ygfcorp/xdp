package com.kakao.xdp.flow.domain.recipe

import com.kakao.xdp.commons.jackson.Jackson
import com.kakao.xdp.commons.server.exception.InvalidStateException
import com.kakao.xdp.commons.system.randomUUID
import com.kakao.xdp.flow.domain.processor.ProcessorGroup
import org.springframework.core.convert.converter.Converter
import org.springframework.data.annotation.Id
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import java.time.LocalDateTime

data class Recipe(@Id val id: String,
                  val name: String,
                  val dag: RecipeDAG,
                  val description: String,
                  val status: RecipeStatus,
                  val createdAt: LocalDateTime,
                  val updatedAt: LocalDateTime)

data class RecipeDAG(val node: Node) {
    companion object {
        private val writer = Jackson.mapper().writerFor(RecipeDAG::class.java)
        private val reader = Jackson.mapper().readerFor(RecipeDAG::class.java)
        fun deserialize(payload: String): RecipeDAG = reader.readValue(payload)
    }

    fun serialize() = writer.writeValueAsString(this)!!

    @WritingConverter
    class RecipeDAGToJsonStringConverter : Converter<RecipeDAG, String> {
        override fun convert(source: RecipeDAG) = source.serialize()
    }

    @ReadingConverter
    class JsonStringToRecipeDAGConverter : Converter<String, RecipeDAG> {
        override fun convert(source: String) = deserialize(source)
    }
}

enum class RecipeStatus {
    ACTIVE, INACTIVE
}

class Node(val id: String) {
    constructor() : this(randomUUID())

    internal lateinit var processorGroup: ProcessorGroup
    val nextNodes: MutableList<Node> = mutableListOf()

    fun isLast() = nextNodes.isEmpty()

    fun next(node: Node) {
        ensureNoDuplication(node)
        nextNodes.add(node)
    }

    private fun ensureNoDuplication(node: Node) {
        if (nextNodes.contains(node)) {
            throw InvalidStateException("`node` already exists")
        }
    }

    fun node(init: Node.() -> Unit): Node {
        val newNode = Node().apply(init)
        next(newNode)
        return newNode
    }

    fun processorGroup(initPg: Node.() -> ProcessorGroup) {
        processorGroup = initPg()
    }
}

fun nodeStart(init: Node.() -> Unit) = Node().apply(init)

