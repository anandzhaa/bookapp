package com.bookapp.ui.screens.booklist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bookapp.shared.domain.model.Book
import com.bookapp.shared.presentation.booklist.BookListEvent
import com.bookapp.shared.presentation.booklist.BookListViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookListScreen(
    onBookClick: (Int) -> Unit,
    onAddBookClick: () -> Unit,
    isDarkMode: Boolean = false,
    onToggleDarkMode: () -> Unit = {},
    viewModel: BookListViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val pullRefreshState = rememberPullToRefreshState()

    // Show errors in snackbar
    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onEvent(BookListEvent.DismissError)
        }
    }

    // Wire pull-to-refresh
    LaunchedEffect(pullRefreshState.isRefreshing) {
        if (pullRefreshState.isRefreshing) {
            viewModel.onEvent(BookListEvent.Refresh)
        }
    }
    LaunchedEffect(state.isRefreshing) {
        if (!state.isRefreshing) pullRefreshState.endRefresh()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("BookShelf", fontWeight = FontWeight.Bold)
                },
                actions = {
                    IconButton(onClick = onToggleDarkMode) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Filled.Brightness7 else Icons.Filled.Brightness4,
                            contentDescription = "Toggle dark mode"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddBookClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Book")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .nestedScroll(pullRefreshState.nestedScrollConnection)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Search bar
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.onEvent(BookListEvent.SearchQueryChanged(it)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Search books…") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                when {
                    state.isLoading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    state.filteredBooks.isEmpty() -> EmptyState()
                    else -> {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = state.filteredBooks,
                                key = { it.id }
                            ) { book ->
                                SwipeableBookCard(
                                    book = book,
                                    onClick = { onBookClick(book.id) },
                                    onDelete = { viewModel.onEvent(BookListEvent.DeleteBook(book.id)) }
                                )
                            }
                            item { Spacer(Modifier.height(72.dp)) } // FAB clearance
                        }
                    }
                }
            }

            PullToRefreshContainer(
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableBookCard(
    book: Book,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dismissState = rememberDismissState(
        confirmValueChange = { value ->
            if (value == DismissValue.DismissedToStart) {
                onDelete()
                true
            } else false
        }
    )

    SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.EndToStart),
        background = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.error),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete",
                    tint = Color.White,
                    modifier = Modifier.padding(end = 24.dp)
                )
            }
        },
        dismissContent = {
            BookCard(book = book, onClick = onClick)
        }
    )
}

@Composable
private fun BookCard(book: Book, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Book icon placeholder
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.MenuBook,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = book.description.ifBlank { "No description available" },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "${book.pageCount} pages",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Filled.MenuBook,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "No books yet",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
            Text(
                text = "Tap + to add your first book",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
            )
        }
    }
}
