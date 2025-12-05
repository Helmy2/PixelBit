package com.example.pixelbit.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Address(
    val id: String = "",
    val street: String = "",
    val city: String = "",
    val default: Boolean = false
)
