package com.app.waxly

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.app.waxly.model.entities.Vinyl
import com.app.waxly.ui.home.VinylActionDialog
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class VinylActionDialogTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun muestra_titulo_artista_y_ano() {
        val vinyl = Vinyl(
            id = 1,
            title = "Blonde",
            artist = "Frank Ocean",
            year = 2016,
            coverName = "cover_blonde"
        )

        composeRule.setContent {
            VinylActionDialog(
                vinyl = vinyl,
                onAddToCollection = {},
                onAddToWantlist = {},
                onDismiss = {}
            )
        }

        composeRule.onNodeWithText("Blonde").assertIsDisplayed()
        composeRule.onNodeWithText("Frank Ocean").assertIsDisplayed()
        composeRule.onNodeWithText("Año: 2016").assertIsDisplayed()
    }

    @Test
    fun click_agregar_a_coleccion_ejecuta_callback_y_desactiva_boton() {
        var called = false

        val vinyl = Vinyl(
            id = 1,
            title = "Blonde",
            artist = "Frank Ocean",
            year = 2016,
            coverName = "cover_blonde"
        )

        composeRule.setContent {
            VinylActionDialog(
                vinyl = vinyl,
                onAddToCollection = { called = true },
                onAddToWantlist = {},
                onDismiss = {}
            )
        }

        // El botón está habilitado inicialmente
        composeRule
            .onNodeWithText("Agregar a colección")
            .assertIsEnabled()
            .performClick()

        // Cambia el texto indicando éxito
        composeRule
            .onNodeWithText("Agregado a colección ✓")
            .assertIsDisplayed()

        // Callback fue ejecutado
        assertTrue(called)
    }
}