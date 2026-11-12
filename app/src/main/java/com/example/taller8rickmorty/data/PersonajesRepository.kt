package com.example.taller8rickmorty.data

import kotlinx.coroutines.CancellationException

class PersonajesRepository(private val api: RickMortyApi) {
    suspend fun obtenerPersonajes(): Result<PersonajesResponse> =
        try {
            Result.success(api.obtenerPersonajes())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
}
