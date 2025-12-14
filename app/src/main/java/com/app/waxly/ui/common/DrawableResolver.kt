package com.app.waxly.ui.common

import android.content.Context

fun drawableIdByName(context: Context, name: String): Int {
    // Devuelve 0 si no existe, por eso hacemos fallback en UI
    return context.resources.getIdentifier(name, "drawable", context.packageName)
}