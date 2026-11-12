package com.example.taller8rickmorty

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.example.taller8rickmorty.data.Personaje

private const val LISTA_ROUTE = "characters"
private const val DETALLE_ROUTE = "character/{id}"

@Composable
fun RickMortyApp(viewModel: PersonajesViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = LISTA_ROUTE) {
        composable(LISTA_ROUTE) {
            ListaPersonajes(
                viewModel = viewModel,
                onPersonajeClick = { id -> navController.navigate("character/$id") }
            )
        }
        composable(
            route = DETALLE_ROUTE,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id")
            val personaje = id?.let(viewModel::buscarPersonaje)
            DetallePersonaje(personaje = personaje, onVolver = { navController.navigateUp() })
        }
    }
}

@Composable
private fun ListaPersonajes(
    viewModel: PersonajesViewModel,
    onPersonajeClick: (Int) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Rick y Morty", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(12.dp))
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when (val currentState = state) {
                UiState.Loading -> Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(12.dp))
                    Text("Cargando personajes...")
                }
                is UiState.Success -> Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${currentState.items.size} personajes", style = MaterialTheme.typography.bodyLarge)
                        TextButton(onClick = viewModel::cargar) {
                            Text("Recargar")
                        }
                    }
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(currentState.items, key = { it.id }) { personaje ->
                            PersonajeFila(personaje, onClick = { onPersonajeClick(personaje.id) })
                        }
                    }
                }
                is UiState.Error -> Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(currentState.message)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = viewModel::cargar) {
                        Text("Reintentar")
                    }
                }
            }
        }
    }
}

@Composable
private fun PersonajeFila(personaje: Personaje, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = personaje.image,
                contentDescription = personaje.name,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = personaje.name ?: "Nombre no disponible",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Estado: ${personaje.status ?: "No disponible"}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun DetallePersonaje(personaje: Personaje?, onVolver: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (personaje == null) {
            Text("Personaje no disponible en la lista cargada.")
            Spacer(Modifier.height(8.dp))
            Text("Vuelve a la lista y carga los personajes antes de abrir el detalle.")
        } else {
            AsyncImage(
                model = personaje.image,
                contentDescription = personaje.name,
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.height(24.dp))
            Text(
                text = personaje.name ?: "Nombre no disponible",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Estado: ${personaje.status ?: "No disponible"}",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Especie: ${personaje.species ?: "No disponible"}",
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Spacer(Modifier.height(24.dp))
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onVolver) {
            Text("Volver a la lista")
        }
    }
}
