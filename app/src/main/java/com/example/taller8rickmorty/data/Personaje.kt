package com.example.taller8rickmorty.data

data class Personaje(
    val id: Int,
    val name: String?,
    val status: String?,
    val species: String?,
    val image: String?
)

data class PersonajesResponse(
    val results: List<Personaje>
)
