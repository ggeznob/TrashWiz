package com.example.trashwiz

import android.Manifest
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
import androidx.compose.ui.platform.LocalLifecycleOwner
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TrashWizTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    val context = LocalContext.current

                    // 👇 权限状态（只请求一次）
                    var permissionRequested by remember { mutableStateOf(false) }

                    // 👇 权限请求器
                    val permissionLauncher = rememberLauncherForActivityResult(
                        ActivityResultContracts.RequestPermission()
                    ) { isGranted ->
                        if (!isGranted) {
                            Toast.makeText(context, "必须允许摄像头权限才能使用拍照功能", Toast.LENGTH_LONG).show()
                        }
                    }

                    // 👇 第一次启动时请求权限
                    LaunchedEffect(Unit) {
                        if (!permissionRequested) {
                            permissionRequested = true
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }

                    NavHost(navController = navController, startDestination = "main") {
                        composable("main") {
                            MainScreen(navController)
                        }
                        composable("camera") {
                            CameraScreen(navController)
                        }
                        composable(
                            route = "result_screen/{itemName}",
                            arguments = listOf(navArgument("itemName") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val itemName = backStackEntry.arguments?.getString("itemName") ?: ""
                            ResultScreen(navController = navController, itemName = itemName)
                        }
                    }
                }
            }
        }
    }
}
