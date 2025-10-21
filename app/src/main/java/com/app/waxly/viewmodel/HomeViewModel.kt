package com.app.waxly.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.MutableStateFlowimport
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random


class HomeViewModel : ViewModel() {
    // Seed fijo para mantener el orden aleatorio estable
    private val _seed = MutableStateFlow(Random.nextLong())
    val seed = _seed.asStateFlow()
}