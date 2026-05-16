package com.bookapp.shared.presentation.booklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bookapp.shared.domain.model.Book
import com.bookapp.shared.domain.model.Resource
import com.bookapp.shared.domain.usecase.DeleteBookUseCase
import com.bookapp.shared.domain.usecase.GetBooksUseCase
import com.bookapp.shared.domain.usecase.RefreshBooksUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BookListState(
    val books: List<Book> = emptyList(),
    val filteredBooks: List<Book> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val searchQuery: String = ""
)

sealed interface BookListEvent {
    data object Refresh : BookListEvent
    data class DeleteBook(val bookId: Int) : BookListEvent
    data class SearchQueryChanged(val query: String) : BookListEvent
    data object DismissError : BookListEvent
}

class BookListViewModel(
    private val getBooksUseCase: GetBooksUseCase,
    private val deleteBookUseCase: DeleteBookUseCase,
    private val refreshBooksUseCase: RefreshBooksUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(BookListState())
    val state: StateFlow<BookListState> = _state.asStateFlow()

    init {
        observeBooks()
    }

    private fun observeBooks() {
        getBooksUseCase()
            .onEach { result ->
                when (result) {
                    is Resource.Loading -> _state.update { it.copy(isLoading = true, error = null) }
                    is Resource.Success -> {
                        _state.update { current ->
                            current.copy(
                                books = result.data,
                                filteredBooks = filterBooks(result.data, current.searchQuery),
                                isLoading = false,
                                error = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(isLoading = false, error = result.message) }
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: BookListEvent) {
        when (event) {
            is BookListEvent.Refresh -> refresh()
            is BookListEvent.DeleteBook -> deleteBook(event.bookId)
            is BookListEvent.SearchQueryChanged -> updateSearch(event.query)
            is BookListEvent.DismissError -> _state.update { it.copy(error = null) }
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }
            val result = refreshBooksUseCase()
            if (result is Resource.Error) {
                _state.update { it.copy(isRefreshing = false, error = result.message) }
            } else {
                _state.update { it.copy(isRefreshing = false) }
            }
        }
    }

    private fun deleteBook(id: Int) {
        viewModelScope.launch {
            val result = deleteBookUseCase(id)
            if (result is Resource.Error) {
                _state.update { it.copy(error = result.message) }
            }
        }
    }

    private fun updateSearch(query: String) {
        _state.update { current ->
            current.copy(
                searchQuery = query,
                filteredBooks = filterBooks(current.books, query)
            )
        }
    }

    private fun filterBooks(books: List<Book>, query: String): List<Book> {
        if (query.isBlank()) return books
        return books.filter {
            it.title.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true)
        }
    }
}
