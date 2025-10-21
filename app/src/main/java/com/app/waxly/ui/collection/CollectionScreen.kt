package com.app.waxly.ui.collection

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.app.waxly.model.entities.MyCollection
import com.app.waxly.model.entities.Vinyl
import com.app.waxly.model.local.AppDatabase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// Pantalla de colección:
// - 2 discos predeterminados + lo que el usuario agregue
// - buscador funcional: al hacer click en un resultado lo agrega
@Composable
fun CollectionScreen() {
    val context = LocalContext.current
    val db = remember { AppDatabase.get(context) }
    val vinylDao = remember { db.vinylDao() }
    val myCollectionDao = remember { db.myCollectionDao() }

    // Todos los vinilos (para resolver default y buscar)
    var all by remember { mutableStateOf(emptyList<Vinyl>()) }
    LaunchedEffect(Unit) { vinylDao.getAll().collectLatest { all = it.distinctBy { v -> v.id } } }

    // Dos por defecto (si existen)
    val defaults = remember(all) {
        val wanted = listOf("Blonde", "Salad Days")
        wanted.mapNotNull { t -> all.find { it.title == t } }
    }

    // Lo que ya está en mi colección (reactivo)
    var collected by remember { mutableStateOf(emptyList<Vinyl>()) }
    LaunchedEffect(Unit) {
        myCollectionDao.getCollectedVinyls().collectLatest { collected = it.distinctBy { v -> v.id } }
    }

    // Búsqueda en Room
    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf(emptyList<Vinyl>()) }
    LaunchedEffect(query) {
        if (query.isBlank()) results = emptyList()
        else vinylDao.search("%$query%").collectLatest { results = it.distinctBy { v -> v.id } }
    }

    val scope = rememberCoroutineScope()

    // Si no hay búsqueda: mostrar defaults + colección (sin duplicados)
    val showing = remember(defaults, collected, query, results) {
        if (query.isBlank()) (defaults + collected).distinctBy { it.id } else results
    }

    Column(Modifier.fillMaxSize().padding(horizontal = 12.dp)) {
        Spacer(Modifier.height(8.dp))
        Text("COLECCIÓN", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
        Spacer(Modifier.height(10.dp))

        // Buscador redondeado estilo pill
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Buscar") },
            placeholder = { Text("Busca para agregar a tu colección") },
            singleLine = true,
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        )

        Spacer(Modifier.height(12.dp))

        if (showing.isEmpty()) {
            Text(
                if (query.isBlank()) "Cargando colección..." else "Sin resultados para \"$query\"",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(8.dp)
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(showing) { v ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            // Solo agrego al tocar si estoy buscando (evito tocar los defaults)
                            .clickable(
                                enabled = query.isNotBlank(),
                                onClick = { scope.launch { myCollectionDao.insert(MyCollection(vinylId = v.id)) } }
                            )
                    ) { VinylRowCompact(v) }
                }
            }
        }
    }
}

// Item vertical compacto (imagen + textos)
@Composable
private fun VinylRowCompact(v: Vinyl) {
    Image(
        painter = painterResource(id = v.coverRes),
        contentDescription = "${v.artist} - ${v.title}",
        modifier = Modifier.size(84.dp).clip(RoundedCornerShape(10.dp))
    )
    Spacer(Modifier.width(14.dp))
    Column(Modifier.fillMaxWidth()) {
        Text(v.title, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(v.artist, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(v.year?.toString() ?: "-", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}