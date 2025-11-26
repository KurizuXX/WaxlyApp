package com.app.waxly.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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

// La firma de la función ya no necesita el parámetro 'onNavigateToSearch'
@Composable
fun HomeScreen() {
    val ctx = LocalContext.current
    val db = remember { AppDatabase.get(ctx) }
    val flow = remember { db.vinylDao().getAll() }

    var all by remember { mutableStateOf<List<Vinyl>>(emptyList()) }

    // Cargamos la tabla de vinilos de Room de forma reactiva
    LaunchedEffect(Unit) {
        flow.collectLatest { all = it }
    }

    // Si aún no hay datos (seed inicial), muestro un texto
    if (all.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Cargando vinilos…", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }

    // Mantengo un orden aleatorio estable mientras la pantalla siga viva
    var seed by rememberSaveable { mutableLongStateOf(Random.nextLong()) }
    val shuffled = remember(all, seed) { all.shuffled(Random(seed)) }

    // 3 secciones de 6 cada una (si hay menos, muestra lo que haya)
    val topSelling   = remember(shuffled) { shuffled.take(6) }
    val mostValuable = remember(shuffled) { shuffled.drop(6).take(6) }
    val mostCollected= remember(shuffled) { shuffled.drop(12).take(6) }

    val scroll = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scroll)      // 👈 añade scroll vertical
            .padding(horizontal = 12.dp)
            .padding(top = 8.dp)
    ) {
        // El botón/tarjeta de búsqueda se ha eliminado de aquí.

        Section("Los vinilos más vendidos de la semana", topSelling)
        Section("Los vinilos más valiosos de la semana", mostValuable)
        Section("Los vinilos más coleccionados de la semana", mostCollected)

        // un poco de espacio para no chocar con la bottom bar
        Spacer(Modifier.height(24.dp))
    }
}

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
        items(items) { v -> VinylCard(v) }
    }

    Spacer(Modifier.height(16.dp))
}

@Composable
private fun VinylCard(v: Vinyl) {
    Column(
        modifier = Modifier.width(160.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Image(
            painter = painterResource(id = v.coverRes),
            contentDescription = "${v.artist} - ${v.title}",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(12.dp))
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = v.title,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = v.artist,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
