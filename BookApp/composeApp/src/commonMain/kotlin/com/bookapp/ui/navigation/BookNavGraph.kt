package com.bookapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bookapp.ui.screens.addbook.AddBookScreen
import com.bookapp.ui.screens.bookdetail.BookDetailScreen
import com.bookapp.ui.screens.booklist.BookListScreen
import com.bookapp.ui.screens.splash.SplashScreen

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object BookList : Screen("book_list")
    data object AddBook : Screen("add_book")
    data object BookDetail : Screen("book_detail/{bookId}") {
        fun createRoute(bookId: Int) = "book_detail/$bookId"
    }
}

@Composable
fun BookNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Splash.route,
    isDarkMode: Boolean = false,
    onToggleDarkMode: () -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashComplete = {
                    navController.navigate(Screen.BookList.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.BookList.route) {
            BookListScreen(
                onBookClick = { bookId ->
                    navController.navigate(Screen.BookDetail.createRoute(bookId))
                },
                onAddBookClick = {
                    navController.navigate(Screen.AddBook.route)
                },
                isDarkMode = isDarkMode,
                onToggleDarkMode = onToggleDarkMode
            )
        }

        composable(Screen.AddBook.route) {
            AddBookScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.BookDetail.route,
            arguments = listOf(navArgument("bookId") { type = NavType.IntType })
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getInt("bookId") ?: return@composable
            BookDetailScreen(
                bookId = bookId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
