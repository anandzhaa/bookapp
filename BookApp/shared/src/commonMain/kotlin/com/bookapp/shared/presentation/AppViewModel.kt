package com.bookapp.shared.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

abstract class AppViewModel : ViewModel() {
    protected val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCleared() {
        scope.cancel()
        super.onCleared()
    }
}
