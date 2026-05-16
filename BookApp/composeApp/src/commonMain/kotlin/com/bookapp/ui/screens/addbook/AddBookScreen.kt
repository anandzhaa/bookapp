package com.bookapp.ui.screens.addbook

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.bookapp.shared.presentation.addbook.AddBookEvent
import com.bookapp.shared.presentation.addbook.AddBookViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddBookViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) onNavigateBack()
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onEvent(AddBookEvent.DismissError)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Book", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(20.dp))

            BookTextField(
                value = state.title,
                onValueChange = { viewModel.onEvent(AddBookEvent.TitleChanged(it)) },
                label = "Title *",
                error = state.titleError,
                singleLine = true
            )

            Spacer(Modifier.height(16.dp))

            BookTextField(
                value = state.description,
                onValueChange = { viewModel.onEvent(AddBookEvent.DescriptionChanged(it)) },
                label = "Description",
                minLines = 3,
                maxLines = 5
            )

            Spacer(Modifier.height(16.dp))

            BookTextField(
                value = state.pageCount,
                onValueChange = { viewModel.onEvent(AddBookEvent.PageCountChanged(it)) },
                label = "Page Count *",
                error = state.pageCountError,
                keyboardType = KeyboardType.Number,
                singleLine = true
            )

            Spacer(Modifier.height(16.dp))

            BookTextField(
                value = state.excerpt,
                onValueChange = { viewModel.onEvent(AddBookEvent.ExcerptChanged(it)) },
                label = "Excerpt",
                minLines = 3,
                maxLines = 6
            )

            Spacer(Modifier.height(28.dp))

            Button(
                onClick = { viewModel.onEvent(AddBookEvent.Submit) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !state.isLoading,
                shape = MaterialTheme.shapes.medium
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Save Book", style = MaterialTheme.typography.labelLarge)
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun BookTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    error: String? = null,
    singleLine: Boolean = false,
    minLines: Int = 1,
    maxLines: Int = Int.MAX_VALUE,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        isError = error != null,
        supportingText = error?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
        singleLine = singleLine,
        minLines = minLines,
        maxLines = maxLines,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = MaterialTheme.shapes.medium
    )
}
