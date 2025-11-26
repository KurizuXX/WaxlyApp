package com.app.waxly.viewmodel

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.waxly.model.entities.Shop // Asegúrate de que esta ruta sea correcta
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LocationSearchViewModel : ViewModel() {

    // --- ESTADO EXPUESTO A LA UI USANDO StateFlow ---

    // Cambiamos 'searchResults' por '_shops' (privado) y 'shops' (público)
    private val _shops = MutableStateFlow<List<Shop>>(emptyList())
    val shops: StateFlow<List<Shop>> = _shops.asStateFlow()

    // Cambiamos 'isLoading'
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // --- LÓGICA DEL VIEWMODEL ---

    fun loadNearbyShops(userLocation: Location?) {
        _isLoading.value = true // Actualizamos el valor del StateFlow
        viewModelScope.launch {
            Log.d("LocationViewModel", "Iniciando carga de tiendas...")
            delay(1000) // Simula espera de red

            val allShops = getHardcodedShops()

            if (userLocation == null) {
                Log.w("LocationViewModel", "Ubicación del usuario es nula. Mostrando lista sin calcular distancia.")
                _shops.value = allShops
            } else {
                Log.d("LocationViewModel", "Calculando distancia desde: Lat=${userLocation.latitude}, Lon=${userLocation.longitude}")
                val shopsWithDistance = allShops.map { shop ->
                    val shopLocation = Location("").apply {
                        latitude = shop.latitude
                        longitude = shop.longitude
                    }
                    val distanceInKm = userLocation.distanceTo(shopLocation) / 1000.0
                    shop.copy(distance = distanceInKm)
                }
                _shops.value = shopsWithDistance.sortedBy { it.distance }
            }

            Log.d("LocationViewModel", "Carga completada. ${_shops.value.size} resultados encontrados.")
            _isLoading.value = false
        }
    }

    fun showNoResultsFound(): Boolean {
        return !_isLoading.value && _shops.value.isEmpty()
    }

    private fun getHardcodedShops(): List<Shop> {
        // Lista completa de tiendas (igual que antes)
        return listOf(
            Shop(id = 1, name = "Needle & Groove Records", address = "123 Main St, Santiago, Chile", latitude = -33.4489, longitude = -70.6693, distance = 0.0),
            Shop(id = 2, name = "Sonar Discos", address = "Paseo Las Palmas 2225, Providencia, Chile", latitude = -33.4181, longitude = -70.6030, distance = 0.0),
            Shop(id = 3, name = "Discomanía", address = "Av. Providencia 2124, Providencia, Chile", latitude = -33.4194, longitude = -70.6083, distance = 0.0),
            Shop(id = 4, name = "Eureka Records", address = "Defensa 1281, Buenos Aires, Argentina", latitude = -34.6283, longitude = -58.3725, distance = 0.0),
            Shop(id = 5, name = "Exiles Records", address = "Honduras 5270, Buenos Aires, Argentina", latitude = -34.5870, longitude = -58.4320, distance = 0.0),
            Shop(id = 6, name = "La Roma Records", address = "Álvaro Obregón 200, Ciudad de México, México", latitude = 19.4172, longitude = -99.1600, distance = 0.0),
            Shop(id = 7, name = "Retroactivo Records", address = "Jalapa 125, Ciudad de México, México", latitude = 19.4165, longitude = -99.1633, distance = 0.0),
            Shop(id = 8, name = "Discos Marcapasos", address = "C. del Conde Duque, 16, Madrid, España", latitude = 40.4265, longitude = -3.7095, distance = 0.0),
            Shop(id = 9, name = "Babel Discos", address = "C. de la Palma, 19, Madrid, España", latitude = 40.4258, longitude = -3.7041, distance = 0.0),
            Shop(id = 10, name = "Amoeba Music", address = "6200 Hollywood Blvd, Los Angeles, CA, USA", latitude = 34.1018, longitude = -118.3260, distance = 0.0)
        )
    }
}
