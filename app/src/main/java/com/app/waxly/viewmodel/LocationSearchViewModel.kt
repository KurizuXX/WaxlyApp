package com.app.waxly.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.waxly.model.entities.Shop // Asegúrate de que la importación es correcta
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LocationSearchViewModel : ViewModel() {

    var query by mutableStateOf("")
        private set

    var searchResults by mutableStateOf<List<Shop>>(emptyList())
        private set

    var isSearching by mutableStateOf(false)
        private set

    private var searchAttempted by mutableStateOf(false)

    fun onQueryChange(newQuery: String) {
        query = newQuery
        if (newQuery.isEmpty()) {
            searchAttempted = false
            searchResults = emptyList()
        }
    }

    // 👇 FUNCIÓN DE BÚSQUEDA SIMULADA. ¡NO NECESITA URL!
    fun onSearch() {
        if (query.isBlank()) return

        searchAttempted = true
        isSearching = true

        // Usamos viewModelScope para simular una pequeña espera, como si fuera una llamada de red.
        viewModelScope.launch {
            Log.d("LocationViewModel", "Iniciando búsqueda simulada para: '$query'")
            delay(1000) // Simula una espera de 1 segundo

            // --- ¡AQUÍ ESTÁ LA MAGIA! ---
            // Devolvemos una lista de tiendas de vinilo de ejemplo, sin importar lo que busques.
            // Más adelante, puedes conectar esto a una base de datos real.
            searchResults = listOf(
                Shop(
                    id = 1,
                    name = "Needle & Groove Records",
                    address = "123 Main St, Santiago, Chile",
                    distance = 2.5,
                    latitude = -33.4489, // Coordenadas de Santiago
                    longitude = -70.6693
                ),
                Shop(
                    id = 2,
                    name = "Sonar Discos",
                    address = "Paseo Las Palmas 2225, Providencia, Chile",
                    distance = 5.1,
                    latitude = -33.4181, // Coordenadas de Providencia
                    longitude = -70.6030
                ),
                Shop(
                    id = 3,
                    name = "Discomanía",
                    address = "Av. Providencia 2124, Providencia, Chile",
                    distance = 5.3,
                    latitude = -33.4194, // Coordenadas de Providencia
                    longitude = -70.6083
                )
            )
            Log.d("LocationViewModel", "Búsqueda simulada completada. ${searchResults.size} resultados encontrados.")
            isSearching = false
        }
    }

    fun showNoResultsFound(): Boolean {
        return searchAttempted && searchResults.isEmpty() && !isSearching
    }
}
