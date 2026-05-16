package com.bookapp.shared.presentation.addbook

import com.bookapp.shared.presentation.AppViewModel
import com.bookapp.shared.domain.model.Resource
import com.bookapp.shared.domain.usecase.AddBookUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

data class AddBookState(
    val title: String = "",
    val description: String = "",
    val pageCount: String = "",
    val excerpt: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    // Field-level errors
    val titleError: String? = null,
    val pageCountError: String? = null
)

sealed interface AddBookEvent {
    data class TitleChanged(val value: String) : AddBookEvent
    data class DescriptionChanged(val value: String) : AddBookEvent
    data class PageCountChanged(val value: String) : AddBookEvent
    data class ExcerptChanged(val value: String) : AddBookEvent
    data object Submit : AddBookEvent
    data object DismissError : AddBookEvent
}

class AddBookViewModel(private val addBookUseCase: AddBookUseCase) : AppViewModel() {

    private val _state = MutableStateFlow(AddBookState())
    val state: StateFlow<AddBookState> = _state.asStateFlow()

    fun onEvent(event: AddBookEvent) {
        when (event) {
            is AddBookEvent.TitleChanged -> _state.update {
                it.copy(title = event.value, titleError = null)
            }
            is AddBookEvent.DescriptionChanged -> _state.update {
                it.copy(description = event.value)
            }
            is AddBookEvent.PageCountChanged -> _state.update {
                it.copy(pageCount = event.value, pageCountError = null)
            }
            is AddBookEvent.ExcerptChanged -> _state.update {
                it.copy(excerpt = event.value)
            }
            is AddBookEvent.Submit -> submitBook()
            is AddBookEvent.DismissError -> _state.update { it.copy(error = null) }
        }
    }

    private fun submitBook() {
        val current = _state.value
        var hasErrors = false

        if (current.title.isBlank()) {
            _state.update { it.copy(titleError = "Title is required") }
            hasErrors = true
        }

        val pageCountInt = current.pageCount.toIntOrNull()
        if (pageCountInt == null || pageCountInt <= 0) {
            _state.update { it.copy(pageCountError = "Enter a valid page count") }
            hasErrors = true
        }

        if (hasErrors) return

        scope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val result = addBookUseCase(
                title = current.title,
                description = current.description,
                pageCount = pageCountInt!!,
                excerpt = current.excerpt,
                publishDate = Clock.System.now().toString()
            )

            when (result) {
                is Resource.Success -> _state.update { it.copy(isLoading = false, isSuccess = true) }
                is Resource.Error -> _state.update {
                    it.copy(isLoading = false, error = result.message)
                }
                else -> _state.update { it.copy(isLoading = false) }
            }
        }
    }
}
