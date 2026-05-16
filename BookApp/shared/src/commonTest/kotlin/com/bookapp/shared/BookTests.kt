package com.bookapp.shared

import com.bookapp.shared.domain.model.Book
import com.bookapp.shared.domain.model.Resource
import com.bookapp.shared.domain.repository.BookRepository
import com.bookapp.shared.domain.usecase.AddBookUseCase
import com.bookapp.shared.domain.usecase.DeleteBookUseCase
import com.bookapp.shared.domain.usecase.GetBooksUseCase
import com.bookapp.shared.presentation.booklist.BookListEvent
import com.bookapp.shared.presentation.booklist.BookListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

// ─── Fakes ───────────────────────────────────────────────────────────────────

val sampleBook = Book(
    id = 1,
    title = "Clean Code",
    description = "A handbook of agile software craftsmanship",
    pageCount = 431,
    excerpt = "Writing clean code is what you must do...",
    publishDate = "2008-08-01T00:00:00Z"
)

class FakeBookRepository : BookRepository {
    private val books = mutableListOf(sampleBook)
    var shouldFail = false

    override fun getBooks() = flowOf(
        if (shouldFail) Resource.Error("Network error")
        else Resource.Success(books.toList())
    )

    override suspend fun getBookById(id: Int): Resource<Book> {
        return books.find { it.id == id }
            ?.let { Resource.Success(it) }
            ?: Resource.Error("Book not found")
    }

    override suspend fun addBook(book: Book): Resource<Book> {
        if (shouldFail) return Resource.Error("Add failed")
        val added = book.copy(id = books.size + 1)
        books.add(added)
        return Resource.Success(added)
    }

    override suspend fun deleteBook(id: Int): Resource<Unit> {
        if (shouldFail) return Resource.Error("Delete failed")
        books.removeAll { it.id == id }
        return Resource.Success(Unit)
    }

    override suspend fun refreshBooks(): Resource<Unit> =
        if (shouldFail) Resource.Error("Refresh failed") else Resource.Success(Unit)
}

// ─── AddBookUseCase Tests ─────────────────────────────────────────────────────

class AddBookUseCaseTest {
    private val repo = FakeBookRepository()
    private val useCase = AddBookUseCase(repo)

    @Test
    fun `returns error when title is blank`() = runTest {
        val result = useCase(
            title = "  ",
            description = "desc",
            pageCount = 100,
            excerpt = "",
            publishDate = "2024-01-01T00:00:00Z"
        )
        assertIs<Resource.Error>(result)
        assertEquals("Title cannot be empty", result.message)
    }

    @Test
    fun `returns error when pageCount is zero`() = runTest {
        val result = useCase(
            title = "Valid Title",
            description = "desc",
            pageCount = 0,
            excerpt = "",
            publishDate = "2024-01-01T00:00:00Z"
        )
        assertIs<Resource.Error>(result)
    }

    @Test
    fun `returns success when valid input`() = runTest {
        val result = useCase(
            title = "New Book",
            description = "Great read",
            pageCount = 200,
            excerpt = "First lines...",
            publishDate = "2024-01-01T00:00:00Z"
        )
        assertIs<Resource.Success<Book>>(result)
        assertEquals("New Book", result.data.title)
    }
}

// ─── DeleteBookUseCase Tests ──────────────────────────────────────────────────

class DeleteBookUseCaseTest {
    private val repo = FakeBookRepository()
    private val useCase = DeleteBookUseCase(repo)

    @Test
    fun `deletes existing book successfully`() = runTest {
        val result = useCase(sampleBook.id)
        assertIs<Resource.Success<Unit>>(result)
    }

    @Test
    fun `returns error when repository fails`() = runTest {
        repo.shouldFail = true
        val result = useCase(sampleBook.id)
        assertIs<Resource.Error>(result)
    }
}

// ─── BookListViewModel Tests ──────────────────────────────────────────────────

@OptIn(ExperimentalCoroutinesApi::class)
class BookListViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repo: FakeBookRepository
    private lateinit var viewModel: BookListViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repo = FakeBookRepository()
        viewModel = BookListViewModel(
            getBooksUseCase = GetBooksUseCase(repo),
            deleteBookUseCase = DeleteBookUseCase(repo),
            refreshBooksUseCase = com.bookapp.shared.domain.usecase.RefreshBooksUseCase(repo)
        )
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state loads books`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        val state = viewModel.state.value
        assertTrue(state.filteredBooks.isNotEmpty())
        assertEquals(sampleBook.title, state.filteredBooks.first().title)
    }

    @Test
    fun `search filters books by title`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.onEvent(BookListEvent.SearchQueryChanged("Clean"))
        val state = viewModel.state.value
        assertTrue(state.filteredBooks.all { it.title.contains("Clean", ignoreCase = true) })
    }

    @Test
    fun `search with no match returns empty list`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.onEvent(BookListEvent.SearchQueryChanged("ZZZ_NO_MATCH"))
        assertTrue(viewModel.state.value.filteredBooks.isEmpty())
    }
}
