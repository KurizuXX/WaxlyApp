package com.app.waxly.ui.wantlist

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
import com.app.waxly.model.entities.MyWantlist
import com.app.waxly.model.entities.Vinyl
import com.app.waxly.model.local.AppDatabase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun WantlistScreen() {
    val context = LocalContext.current
    val db = remember { AppDatabase.get(context) }
    val vinylDao = remember { db.vinylDao() }
    val wantDao = remember { db.myWantlistDao() }

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

    var wantlist by remember { mutableStateOf(emptyList<Vinyl>()) }
    LaunchedEffect(Unit) {
        wantDao.getWantlistVinyls().collectLatest { list ->
            wantlist = list.distinctBy { it.id }
        }
    }

    val scope = rememberCoroutineScope()

    val listToShow = remember(wantlist, query, results) {
        val list = if (query.isBlank()) {
            wantlist
        } else {
            results.filter { resultVinyl -> wantlist.none { it.id == resultVinyl.id } }
        }
        list.distinctBy { it.id }
    }

    Column(Modifier.fillMaxSize().padding(horizontal = 12.dp)) {
        Spacer(Modifier.height(8.dp))
        Text("WANTLIST", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Buscar") },
            placeholder = { Text("Buscar vinilos para tu wantlist") },
            singleLine = true,
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        )

        Spacer(Modifier.height(12.dp))

        if (listToShow.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    if (query.isBlank()) "Busca discos y agrégalos a tu wantlist" 
                    else "Sin resultados para \"$query\""
                )
            }
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
                                        wantDao.insert(MyWantlist(vinylId = v.id))
                                        query = "" // limpiar tras agregar
                                    }
                                }
                            )
                    ) { VinylRowCompact(v) }
                }
            }
        }
    }
}

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
