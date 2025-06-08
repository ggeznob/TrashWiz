package com.example.trashwiz

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.trashwiz.ui.CameraScreen
import com.example.trashwiz.ui.MainScreen
import com.example.trashwiz.ui.ResultScreen
import com.example.trashwiz.ui.theme.TrashWizTheme
import androidx.activity.compose.rememberLauncherForActivityResult
class MainActivity : ComponentActivity() {
    companion object{
        // Global variable to store the selected region name
        var regionName = "";
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TrashWizTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    // Create navigation controller
                    val navController = rememberNavController()
                    val context = LocalContext.current
                    val activity = context as? Activity

                    // State to track if permission is granted
                    var permissionGranted by remember { mutableStateOf(false) }
                    // State to ensure permission is only requested once
                    var permissionRequested by remember { mutableStateOf(false) }

                    // Permission launcher for requesting camera access
                    val permissionLauncher = rememberLauncherForActivityResult(
                        ActivityResultContracts.RequestPermission()
                    ) { isGranted ->
                        permissionGranted = isGranted
                        // Show message and close app if permission is denied
                        if (!isGranted) {
                            Toast.makeText(
                                context,
                                "Camera access was not granted. The application will now exit.",
                                Toast.LENGTH_LONG
                            ).show()
                            activity?.finish()
                        }
                    }

                    // Launch camera permission request only once
                    LaunchedEffect(Unit) {
                        if (!permissionRequested) {
                            permissionRequested = true
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }

                    // Show navigation host only if permission is granted
                    if (permissionGranted) {
                        NavHost(navController = navController, startDestination = "main") {
                            // Main screen route
                            composable("main") {
                                MainScreen(this@MainActivity,navController,this@MainActivity)
                            }
                            // Camera screen route
                            composable("camera") {
                                CameraScreen(navController)
                            }

                            // Result screen route with item name argument
                            composable(
                                route = "result_screen/{itemName}",
                                arguments = listOf(navArgument("itemName") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val itemName = backStackEntry.arguments?.getString("itemName") ?: ""
                                ResultScreen(this@MainActivity,navController = navController, itemName = itemName)
                            }
                        }
                    }
                }
            }
        }
    }
}
