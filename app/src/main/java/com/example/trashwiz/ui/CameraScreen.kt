package com.example.trashwiz.ui

import android.content.Context
import android.graphics.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import org.tensorflow.lite.Interpreter
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executors

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
                imageCapture?.takePicture(
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageCapturedCallback() {
                        override fun onCaptureSuccess(image: ImageProxy) {
                            val bitmap = imageProxyToBitmap(image)
                            image.close()

                            val itemName = analyzeImage(bitmap, context)
                            Log.d("itemname___", "itemName")
                            Log.d("itemname___", itemName)
                            val encoded = URLEncoder.encode(itemName, StandardCharsets.UTF_8.toString())
                            Log.d("itemname___", "encoded")
                            Log.d("itemname___", encoded)
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

fun loadLabelMap(context: Context): Map<Int, String> {
    val json = context.assets.open("labels.json").bufferedReader().use { it.readText() }
    val jsonObject = org.json.JSONObject(json)
    val labelMap = mutableMapOf<Int, String>()
    jsonObject.keys().forEach { key ->
        labelMap[key.toInt()] = jsonObject.getString(key)
    }
    return labelMap
}

fun imageProxyToBitmap(image: ImageProxy): Bitmap {
    if (image.format == ImageFormat.JPEG && image.planes.size == 1) {
        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    if (image.format == ImageFormat.YUV_420_888 && image.planes.size == 3) {
        val yBuffer = image.planes[0].buffer
        val uBuffer = image.planes[1].buffer
        val vBuffer = image.planes[2].buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)

        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, image.width, image.height), 100, out)
        val imageBytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    throw IllegalArgumentException("Unsupported image format: ${image.format}, planes: ${image.planes.size}")
}

fun loadModelFile(context: Context, modelName: String): MappedByteBuffer {
    val fileDescriptor = context.assets.openFd(modelName)
    val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
    val fileChannel = inputStream.channel
    return fileChannel.map(FileChannel.MapMode.READ_ONLY, fileDescriptor.startOffset, fileDescriptor.declaredLength)
}

fun analyzeImage(bitmap: Bitmap, context: Context): String {
    return try {
        val interpreter = Interpreter(loadModelFile(context, "model.tflite"))
        val labelMap = loadLabelMap(context)

        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
        val input = Array(1) { Array(224) { Array(224) { FloatArray(3) } } }
        for (y in 0 until 224) {
            for (x in 0 until 224) {
                val pixel = resizedBitmap.getPixel(x, y)
                input[0][y][x][0] = (Color.red(pixel) - 127.5f) / 127.5f
                input[0][y][x][1] = (Color.green(pixel) - 127.5f) / 127.5f
                input[0][y][x][2] = (Color.blue(pixel) - 127.5f) / 127.5f
            }
        }

        val output = Array(1) { FloatArray(40) }
        interpreter.run(input, output)

        val maxIndex = output[0].indices.maxByOrNull { output[0][it] } ?: -1
        val confidence = output[0][maxIndex]
        val label = labelMap[maxIndex] ?: "Unknown Category"

        // 只在置信度较高时返回，否则返回“未知”
        if (confidence > 0.01f) label
        else "Unrecognizable"
    } catch (e: Exception) {
        Log.e("TFLite", "Recognition Failed: ${e.message}")
        "Recognition Failed"
    }
}

