package com.app.waxly.ui.location

import android.Manifest
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.waxly.model.entities.Shop // <-- ¡Importación importante!
import com.app.waxly.viewmodel.LocationSearchViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationSearchScreen(
    vm: LocationSearchViewModel = viewModel()
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val locationPermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    )

    LaunchedEffect(locationPermissionsState.allPermissionsGranted) {
        if (locationPermissionsState.allPermissionsGranted) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    vm.loadNearbyShops(location)
                }
                .addOnFailureListener {
                    Log.e("LocationScreen", "Error al obtener ubicación.", it)
                    vm.loadNearbyShops(null)
                }
        }
    }

    when {
        locationPermissionsState.allPermissionsGranted -> {
            ShopsContent(vm = vm)
        }
        locationPermissionsState.shouldShowRationale -> {
            PermissionDeniedContent(
                rationaleText = "Para mostrarte tiendas cercanas y calcular la distancia, necesitamos tu permiso de ubicación. Por favor, actívalo.",
                buttonText = "Dar Permiso",
                onClick = { locationPermissionsState.launchMultiplePermissionRequest() }
            )
        }
        else -> {
            PermissionDeniedContent(
                rationaleText = "Esta función requiere acceso a tu ubicación para encontrar tiendas de vinilos cerca de ti.",
                buttonText = "Activar Ubicación",
                onClick = { locationPermissionsState.launchMultiplePermissionRequest() }
            )
        }
    }
}

@Composable
fun ShopsContent(vm: LocationSearchViewModel) {
    var selectedShop by remember { mutableStateOf<Shop?>(null) }

    val shops by vm.shops.collectAsState()
    val isLoading by vm.isLoading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Tiendas de Vinilos Cercanas",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            AnimatedVisibility(visible = selectedShop != null, enter = fadeIn(), exit = fadeOut()) {
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

            if (shops.isEmpty() && !isLoading) {
                Text(
                    text = "No se encontraron tiendas cerca de tu ubicación.",
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(shops, key = { it.id }) { shop -> // Usar una key mejora el rendimiento
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

@Composable
fun PermissionDeniedContent(rationaleText: String, buttonText: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = rationaleText,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onClick) {
            Text(buttonText)
        }
    }
}

@Composable
private fun ShopMap(shop: Shop, modifier: Modifier = Modifier) {
    val shopLocation = LatLng(shop.latitude, shop.longitude)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(shopLocation, 15f)
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
