package com.example.taller8rickmorty

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taller8rickmorty.data.Personaje
import com.example.taller8rickmorty.data.PersonajesRepository
import com.example.taller8rickmorty.data.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface UiState {
    data object Loading : UiState
    data class Success(val items: List<Personaje>) : UiState
    data class Error(val message: String) : UiState
}

class PersonajesViewModel(
    private val repository: PersonajesRepository = PersonajesRepository(RetrofitInstance.api)
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var personajesCache: List<Personaje> = emptyList()

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            _uiState.value = repository.obtenerPersonajes().fold(
                onSuccess = {
                    personajesCache = it.results
                    UiState.Success(it.results)
                },
                onFailure = {
                    UiState.Error("No se pudo cargar. Revisa tu conexión e inténtalo de nuevo.")
                }
            )
        }
    }

    fun buscarPersonaje(id: Int): Personaje? = personajesCache.firstOrNull { it.id == id }
}
