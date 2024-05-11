package com.mangbaam.dripthecoffee.model

data class Recipe(
    val author: String?,
    val cups: Int,
    val isHot: Boolean,
    val videoUrl: String?,
    val beanWeight: Int,
    val waterTemperature: IntRange,
    val pours: List<Pour>,
) {
    data class Pour(
        val water: Int,
        val seconds: Int? = null,
        val desc: String? = null,
    )
}
