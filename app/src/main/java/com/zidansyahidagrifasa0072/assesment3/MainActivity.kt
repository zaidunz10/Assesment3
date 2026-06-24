package com.zidansyahidagrifasa0072.assesment3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.zidansyahidagrifasa0072.assesment3.ui.navigation.NavGraph
import com.zidansyahidagrifasa0072.assesment3.ui.theme.Assesment3Theme // Sesuaikan nama theme-mu
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Assesment3Theme {
                // 1. Buat NavController di sini
                val navController = rememberNavController()


                NavGraph(navController = navController)
            }
        }
    }
}