package com.mortex.helparticles.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.mortex.helparticles.di.AppContainer
import com.mortex.helparticles.di.ArticleApp
import com.mortex.helparticles.presentation.navigation.HelpNavHost
import com.mortex.helparticles.presentation.ui.theme.HelpArticlesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            HelpArticlesTheme {
                val container = (application as ArticleApp).container
                CompositionLocalProvider(LocalAppContainer provides container) {
                    Scaffold(modifier = Modifier.Companion.fillMaxSize()) { innerPadding ->

                        val navController = rememberNavController()


                        // NavHost here
                        HelpNavHost(
                            padding = innerPadding,
                            navController = navController,
                            container = LocalAppContainer.current
                        )
                    }

                }
            }
        }
    }


}

val LocalAppContainer = staticCompositionLocalOf<AppContainer> {
    error("AppContainer not provided")
}