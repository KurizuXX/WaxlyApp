package com.app.waxly.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.app.waxly.model.entities.Vinyl
import com.app.waxly.model.local.AppDatabase
import kotlinx.coroutines.flow.collectLatest
import kotlin.random.Random

// Home con 3 carruseles: cada uno toma 6 elementos de una lista mezclada
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val db = remember { AppDatabase.get(context) }
    val flow = remember { db.vinylDao().getAll() }

    var all by remember { mutableStateOf(emptyList<Vinyl>()) }
    var query by remember { mutableStateOf("") }

    // Carga reactiva desde Room
    LaunchedEffect(Unit) { flow.collectLatest { all = it } }

    // Semilla guardada para que el orden aleatorio sea estable entre recomposiciones
    var shuffleSeed by rememberSaveable { mutableLongStateOf(Random.nextLong()) }
    val shuffled = remember(all, shuffleSeed) { all.shuffled(Random(shuffleSeed)) }

    // Bloques de 6
    val topSelling   = remember(shuffled) { shuffled.take(6) }
    val mostValuable = remember(shuffled) { shuffled.drop(6).take(6) }
    val mostCollected= remember(shuffled) { shuffled.drop(12).take(6) }

    val scroll = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
            .verticalScroll(scroll)
    ) {
        Spacer(Modifier.height(8.dp))
        SearchBarSimple(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))

        if (all.isEmpty()) {
            Text("Cargando vinilos…", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(8.dp))
        } else {
            Section("Los vinilos más vendidos de la semana", topSelling)
            Section("Los vinilos más valiosos de la semana", mostValuable)
            Section("Los vinilos más coleccionados de la semana", mostCollected)
        }
        Spacer(Modifier.height(24.dp)) // margen para que no lo tape la bottom bar
    }
}

// Barra de búsqueda visual (no filtra aquí; la usamos de UI nada más)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBarSimple(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Buscar") },
        placeholder = { Text("Buscar en Waxly") },
        singleLine = true,
        shape = RoundedCornerShape(28.dp),
        modifier = modifier
    )
}

// Sección con título + carrusel horizontal
@Composable
private fun Section(title: String, items: List<Vinyl>) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
        modifier = Modifier.padding(vertical = 6.dp)
    )
    LazyRow(
        contentPadding = PaddingValues(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items.take(6)) { v -> VinylSmallCard(v) }
    }
    Spacer(Modifier.height(16.dp))
}

// Tarjeta compacta: cuadrado con carátula + texto
@Composable
private fun VinylSmallCard(vinyl: Vinyl) {
    Column(modifier = Modifier.width(160.dp), horizontalAlignment = Alignment.Start) {
        Image(
            painter = painterResource(id = vinyl.coverRes),
            contentDescription = "${vinyl.artist} - ${vinyl.title}",
            modifier = Modifier.fillMaxWidth().aspectRatio(1f).clip(RoundedCornerShape(12.dp))
        )
        Spacer(Modifier.height(8.dp))
        Text(vinyl.title, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(vinyl.artist, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}