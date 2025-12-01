package com.mortex.helparticles.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.mortex.helparticles.di.AppContainer
import com.mortex.helparticles.presentation.navigation.HelpNavHost
import com.mortex.helparticles.presentation.ui.theme.HelpArticlesTheme
import com.mortex.helparticles.util.CachePolicy
import com.mortex.helparticles.work.WorkScheduler

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WorkScheduler.scheduleDailyPrefetch(applicationContext)

        enableEdgeToEdge()
        setContent {
            HelpArticlesTheme {
                Scaffold(modifier = Modifier.Companion.fillMaxSize()) { innerPadding ->

                    val navController = rememberNavController()

                    val container = AppContainer(
                        CachePolicy.ARTICLE_TTL_MS,
                        this@MainActivity
                    )

                    HelpNavHost(
                        padding = innerPadding,
                        navController = navController,
                        container = container
                    )
                }
            }
        }
    }
}