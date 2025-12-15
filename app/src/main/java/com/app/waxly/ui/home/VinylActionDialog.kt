package com.app.waxly.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.waxly.model.entities.Vinyl

@Composable
fun VinylActionDialog(
    vinyl: Vinyl,
    onAddToCollection: () -> Unit,
    onAddToWantlist: () -> Unit,
    onDismiss: () -> Unit
) {
    // Estados locales para feedback visual
    var addedToCollection by remember { mutableStateOf(false) }
    var addedToWantlist by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        title = {
            Text(
                text = vinyl.title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = vinyl.artist,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                vinyl.year?.let {
                    Text(
                        text = "Año: $it",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(Modifier.height(6.dp))

                // Botón agregar a colección
                Button(
                    onClick = {
                        addedToCollection = true
                        onAddToCollection()
                    },
                    enabled = !addedToCollection,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (addedToCollection) "Agregado a colección ✓"
                        else "Agregar a colección"
                    )
                }

                // Botón agregar a wantlist
                OutlinedButton(
                    onClick = {
                        addedToWantlist = true
                        onAddToWantlist()
                    },
                    enabled = !addedToWantlist,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (addedToWantlist) "Agregado a wantlist ✓"
                        else "Agregar a wantlist"
                    )
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}
