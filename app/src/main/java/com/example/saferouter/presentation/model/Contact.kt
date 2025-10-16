package com.example.saferouter.presentation.model

data class Contact(
    val nombre: String = "",
    val telefono: String = ""
) {
    constructor() : this("", "")
}