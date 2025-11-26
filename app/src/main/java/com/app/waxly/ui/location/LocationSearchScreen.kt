package com.app.waxly.ui.location

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.waxly.model.entities.Shop
import com.app.waxly.viewmodel.LocationSearchViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun LocationSearchScreen(
    vm: LocationSearchViewModel = viewModel()
) {
    var selectedShop by remember { mutableStateOf<Shop?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = vm.query,
            onValueChange = { vm.onQueryChange(it) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Busca por nombre o dirección") },
            placeholder = { Text("Encuentra tiendas de vinilos cercanas...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Icono de búsqueda") },
            singleLine = true,
            shape = MaterialTheme.shapes.extraLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { vm.onSearch() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Buscar ahora")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when {
            vm.isSearching -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            else -> {
                AnimatedVisibility(
                    visible = selectedShop != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    selectedShop?.let { shop ->
                        ShopMap(
                            shop = shop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .clip(RoundedCornerShape(16.dp))
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    if (vm.showNoResultsFound()) {
                        item {
                            Text(
                                text = "No se encontraron tiendas para \"${vm.query}\".",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        items(vm.searchResults) { shop ->
                            ShopResultCard(
                                shop = shop,
                                onClick = {
                                    selectedShop = if (selectedShop?.id == shop.id) null else shop
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ShopMap(shop: Shop, modifier: Modifier = Modifier) {
    val shopLocation = LatLng(shop.latitude, shop.longitude)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(shopLocation, 15f) // Zoom inicial
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState
    ) {
        Marker(
            state = MarkerState(position = shopLocation),
            title = shop.name,
            snippet = shop.address
        )
    }
}

@Composable
private fun ShopResultCard(shop: Shop, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(shop.name, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(shop.address, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "A ${"%.1f".format(shop.distance)} km de distancia",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

