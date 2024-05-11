package com.mangbaam.dripthecoffee.model

data class Range <out T> (
    val from: T,
    val to: T,
    val desc: String? = null,
)

typealias IntRange = Range<Int>
