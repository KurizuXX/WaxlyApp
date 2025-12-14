package com.app.waxly.ui.home

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.app.waxly.model.entities.Vinyl

@Composable
fun VinylActionDialog(
    vinyl: Vinyl,
    onAddToCollection: () -> Unit,
    onAddToWantlist: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("¿Qué quieres hacer con este vinilo?")
        },
        text = {
            Text("${vinyl.artist} — ${vinyl.title}")
        },
        confirmButton = {
            Button(onClick = onAddToCollection) {
                Text("Agregar a colección")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onAddToWantlist) {
                Text("Agregar a wantlist")
            }
        }
    )
}