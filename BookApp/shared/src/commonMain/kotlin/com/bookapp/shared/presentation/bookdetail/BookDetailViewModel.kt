package com.bookapp.shared.presentation.bookdetail

import com.bookapp.shared.presentation.AppViewModel
import com.bookapp.shared.domain.model.Book
import com.bookapp.shared.domain.model.Resource
import com.bookapp.shared.domain.usecase.DeleteBookUseCase
import com.bookapp.shared.domain.usecase.GetBookByIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BookDetailState(
    val book: Book? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isDeleted: Boolean = false
)

sealed interface BookDetailEvent {
    data class LoadBook(val id: Int) : BookDetailEvent
    data class DeleteBook(val id: Int) : BookDetailEvent
    data object DismissError : BookDetailEvent
}

class BookDetailViewModel(
    private val getBookByIdUseCase: GetBookByIdUseCase,
    private val deleteBookUseCase: DeleteBookUseCase
) : AppViewModel() {

    private val _state = MutableStateFlow(BookDetailState())
    val state: StateFlow<BookDetailState> = _state.asStateFlow()

    fun onEvent(event: BookDetailEvent) {
        when (event) {
            is BookDetailEvent.LoadBook -> loadBook(event.id)
            is BookDetailEvent.DeleteBook -> deleteBook(event.id)
            is BookDetailEvent.DismissError -> _state.update { it.copy(error = null) }
        }
    }

    private fun loadBook(id: Int) {
        scope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = getBookByIdUseCase(id)) {
                is Resource.Success -> _state.update { it.copy(book = result.data, isLoading = false) }
                is Resource.Error -> _state.update { it.copy(error = result.message, isLoading = false) }
                else -> Unit
            }
        }
    }

    private fun deleteBook(id: Int) {
        scope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = deleteBookUseCase(id)) {
                is Resource.Success -> _state.update { it.copy(isDeleted = true, isLoading = false) }
                is Resource.Error -> _state.update { it.copy(error = result.message, isLoading = false) }
                else -> Unit
            }
        }
    }
}
