package com.example.trashwiz.ui

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import java.util.concurrent.Executors
import androidx.core.content.ContextCompat
import androidx.compose.ui.unit.dp
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun CameraScreen(navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val executor = remember { Executors.newSingleThreadExecutor() }

    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.weight(1f),
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    imageCapture = ImageCapture.Builder().build()

                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner, cameraSelector, preview, imageCapture
                        )
                    } catch (e: Exception) {
                        Toast.makeText(ctx, "Camera init failed", Toast.LENGTH_SHORT).show()
                        Log.e("CameraScreen", "Binding failed", e)
                    }
                }, ContextCompat.getMainExecutor(ctx))

                previewView
            }
        )

        Button(
            onClick = {
                Log.d("anaaaaaaaaaa", "onclick")
                imageCapture?.takePicture(

                    ContextCompat.getMainExecutor(context),

                    object : ImageCapture.OnImageCapturedCallback() {

                        override fun onCaptureSuccess(image: ImageProxy) {
                            Log.d("anaaaaaaaaaa", "onCaptureSuccess")
//                            val bitmap = imageProxyToBitmap(image, context)
                            image.close()

                            // 👉 空函数调用（后面再加模型处理）
//                            analyzeImage(bitmap)
                            Log.d("anaaaaaaaaaa", "close")
                            // 👉 默认 itemName（可以改成你喜欢的）
                            val itemName = "Can"

                            // 👉 跳转并传入 itemName
                            val encoded = URLEncoder.encode(itemName, StandardCharsets.UTF_8.toString())
                            navController.navigate("result_screen/$encoded")

                        }

                        override fun onError(exception: ImageCaptureException) {
                            Toast.makeText(context, "Capture failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Picture and Analyze")
        }
    }
}

// 🟡 空函数占位：之后在这里调用 Lite 模型进行分类识别
fun analyzeImage(bitmap: Bitmap) {
    // 以后你会在这里添加模型推理逻辑
    Log.d("analyzeImage", "识别函数被调用啦（模型后面再加）")
}

// ❗需要你自己实现的函数：ImageProxy 转 Bitmap
fun imageProxyToBitmap(image: ImageProxy, context: Context) {
    Log.d("imageProxyToBitmap", "识别函数被调用啦（模型后面再加）")
}
