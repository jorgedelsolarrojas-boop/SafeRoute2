package com.example.saferouter.presentation.model

data class Perfil(
    val name: String = "",
    val lastname: String = "",
    val age: Int = 0,
    val email: String = "",
    val imageUrl: String = "",
    val contacts: List<Contact> = emptyList()
)
