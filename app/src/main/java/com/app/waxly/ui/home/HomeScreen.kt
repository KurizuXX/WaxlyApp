package com.app.waxly.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.app.waxly.model.entities.MyCollection
import com.app.waxly.model.entities.MyWantlist
import com.app.waxly.model.entities.Vinyl
import com.app.waxly.model.local.AppDatabase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val db = remember { AppDatabase.get(context) }

    val vinylDao = remember { db.vinylDao() }
    val collectionDao = remember { db.myCollectionDao() }
    val wantlistDao = remember { db.myWantlistDao() }

    val scope = rememberCoroutineScope()

    var all by remember { mutableStateOf<List<Vinyl>>(emptyList()) }
    var selectedVinyl by remember { mutableStateOf<Vinyl?>(null) }

    LaunchedEffect(Unit) {
        vinylDao.getAll().collectLatest { all = it }
    }

    if (all.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Cargando vinilos...", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }

    // Orden aleatorio estable
    val seed = rememberSaveable { Random.nextLong() }
    val shuffled = remember(all) { all.shuffled(Random(seed)) }

    val topSelling = shuffled.take(6)
    val mostValuable = shuffled.drop(6).take(6)
    val mostCollected = shuffled.drop(12).take(6)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 12.dp)
            .padding(top = 8.dp)
    ) {
        Section("Los vinilos más vendidos de la semana", topSelling) { selectedVinyl = it }
        Section("Los vinilos más valiosos de la semana", mostValuable) { selectedVinyl = it }
        Section("Los vinilos más coleccionados de la semana", mostCollected) { selectedVinyl = it }

        Spacer(Modifier.height(24.dp))
    }

    selectedVinyl?.let { vinyl ->
        VinylActionDialog(
            vinyl = vinyl,
            onAddToCollection = {
                scope.launch {
                    collectionDao.insert(MyCollection(vinylId = vinyl.id))
                    selectedVinyl = null
                }
            },
            onAddToWantlist = {
                scope.launch {
                    wantlistDao.insert(MyWantlist(vinylId = vinyl.id))
                    selectedVinyl = null
                }
            },
            onDismiss = { selectedVinyl = null }
        )
    }
}

@Composable
private fun Section(
    title: String,
    items: List<Vinyl>,
    onVinylClick: (Vinyl) -> Unit
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
        modifier = Modifier.padding(vertical = 6.dp)
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items, key = { it.id }) { v ->
            VinylCard(v = v, onClick = { onVinylClick(v) })
        }
    }

    Spacer(Modifier.height(16.dp))
}

@Composable
private fun VinylCard(
    v: Vinyl,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    // Convertimos coverName -> drawable id
    val resId = remember(v.coverName) {
        context.resources.getIdentifier(v.coverName, "drawable", context.packageName)
    }

    // Si no existe, usamos un fallback seguro
    val finalResId = if (resId != 0) resId else android.R.drawable.ic_menu_report_image

    Column(
        modifier = Modifier
            .width(160.dp)
            .clickable(onClick = onClick)
    ) {
        Image(
            painter = painterResource(id = finalResId),
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