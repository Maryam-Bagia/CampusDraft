package com.example.campusdraft

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class annotated with @HiltAndroidApp to trigger Hilt's code generation
 * and initialise the dependency injection component hierarchy.
 */
@HiltAndroidApp
class CampusDraftApplication : Application()
