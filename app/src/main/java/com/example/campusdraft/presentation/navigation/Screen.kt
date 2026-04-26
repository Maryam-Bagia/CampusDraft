package com.example.campusdraft.presentation.navigation

/** All navigation destinations in the app */
sealed class Screen(val route: String) {

    /** Home / discovery screen */
    object Home : Screen("home")

    /** Form + live-preview screen for a selected application type */
    object Form : Screen("form/{applicationTypeId}") {
        fun createRoute(applicationTypeId: String) = "form/$applicationTypeId"
    }

    /** Full-screen preview and action screen (generate PDF, share, print) */
    object Preview : Screen("preview/{savedId}") {
        fun createRoute(savedId: Long) = "preview/$savedId"
    }

    /** History of all saved applications */
    object History : Screen("history")

    /** Favorited applications */
    object Favorites : Screen("favorites")
}