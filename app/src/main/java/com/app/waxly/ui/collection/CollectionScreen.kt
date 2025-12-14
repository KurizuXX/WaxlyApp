package com.app.waxly.ui.collection

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.app.waxly.model.entities.MyCollection
import com.app.waxly.model.entities.Vinyl
import com.app.waxly.model.local.AppDatabase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun CollectionScreen() {
    val context = LocalContext.current
    val db = remember { AppDatabase.get(context) }
    val vinylDao = remember { db.vinylDao() }
    val myCollectionDao = remember { db.myCollectionDao() }

    var all by remember { mutableStateOf(emptyList<Vinyl>()) }
    LaunchedEffect(Unit) {
        vinylDao.getAll().collectLatest { list -> all = list.distinctBy { it.id } }
    }

    val defaults = remember(all) {
        listOf("Blonde", "Salad Days").mapNotNull { t -> all.find { it.title == t } }
    }

    var collected by remember { mutableStateOf(emptyList<Vinyl>()) }
    LaunchedEffect(Unit) {
        myCollectionDao.getCollectedVinyls().collectLatest { list ->
            collected = list.distinctBy { it.id }
        }
    }

    var query by rememberSaveable { mutableStateOf("") }
    var results by remember { mutableStateOf(emptyList<Vinyl>()) }
    LaunchedEffect(query) {
        if (query.isBlank()) {
            results = emptyList()
        } else {
            vinylDao.searchVinyls("%$query%").collectLatest { list ->
                results = list.distinctBy { it.id }
            }
        }
    }

    val scope = rememberCoroutineScope()

    val listToShow = remember(defaults, collected, query, results) {
        val list = if (query.isBlank()) {
            defaults + collected
        } else {
            results.filter { r -> collected.none { it.id == r.id } }
        }
        list.distinctBy { it.id }
    }

    Column(Modifier.fillMaxSize().padding(horizontal = 12.dp)) {
        Spacer(Modifier.height(8.dp))
        Text("COLECCIÓN", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
        Spacer(Modifier.height(10.dp))

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

        if (listToShow.isEmpty()) {
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
                items(
                    count = listToShow.size,
                    key = { i -> listToShow[i].id }
                ) { i ->
                    val v = listToShow[i]
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                enabled = query.isNotBlank(),
                                onClick = {
                                    scope.launch {
                                        myCollectionDao.insert(MyCollection(vinylId = v.id))
                                        query = ""
                                    }
                                }
                            )
                    ) {
                        VinylRowCompact(v)
                    }
                }
            }
        }
    }
}

@Composable
private fun VinylRowCompact(v: Vinyl) {
    val context = LocalContext.current
    val resId = remember(v.coverName) {
        context.resources.getIdentifier(v.coverName, "drawable", context.packageName)
    }
    val finalRes = if (resId != 0) resId else android.R.drawable.ic_menu_report_image

    Image(
        painter = painterResource(id = finalRes),
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