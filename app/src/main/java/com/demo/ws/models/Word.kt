package com.demo.ws.models

import java.util.*
import kotlin.collections.ArrayList

data class Word(
    var name: String,
    var synonyms: ArrayList<Synonym>) {
}

data class Synonym(
    val id: UUID,
    var name: String) {

    constructor(name: String): this(id = UUID.randomUUID(), name)
}