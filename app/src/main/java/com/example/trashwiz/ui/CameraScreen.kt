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
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executors

@Composable
fun CameraScreen(navController: NavController) {
    // Get current context and lifecycle
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
//    val executor = remember { Executors.newSingleThreadExecutor() }

    // Holds reference to ImageCapture instance
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Preview camera feed
        AndroidView(
            modifier = Modifier.weight(1f),
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()

                    // Set up camera preview
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    // Set up image capture
                    imageCapture = ImageCapture.Builder().build()
                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    try {
                        // Bind camera lifecycle to the view
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

        // Button to capture image and run analysis
        Button(
            onClick = {
                imageCapture?.takePicture(
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageCapturedCallback() {
                        override fun onCaptureSuccess(image: ImageProxy) {
                            // Convert camera image to Bitmap
                            val bitmap = imageProxyToBitmap(image)
                            image.close()

                            // Analyze the image using ML model
                            val itemName = analyzeImage(bitmap, context)

                            // Encode result and navigate to result screen
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

// Load label map from assets/labels.json
fun loadLabelMap(context: Context): Map<Int, String> {
    val json = context.assets.open("labels.json").bufferedReader().use { it.readText() }
    val jsonObject = org.json.JSONObject(json)
    val labelMap = mutableMapOf<Int, String>()
    jsonObject.keys().forEach { key ->
        labelMap[key.toInt()] = jsonObject.getString(key)
    }
    return labelMap
}

// Convert ImageProxy to Bitmap format for processing
fun imageProxyToBitmap(image: ImageProxy): Bitmap {
    // Handle JPEG format directly
    if (image.format == ImageFormat.JPEG && image.planes.size == 1) {
        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    // Convert YUV_420_888 format to JPEG, then to Bitmap
    if (image.format == ImageFormat.YUV_420_888 && image.planes.size == 3) {
        val yBuffer = image.planes[0].buffer
        val uBuffer = image.planes[1].buffer
        val vBuffer = image.planes[2].buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)

        // Reconstruct NV21 byte array from YUV planes
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

// Load the TFLite model file from assets
fun loadModelFile(context: Context, modelName: String): MappedByteBuffer {
    val fileDescriptor = context.assets.openFd(modelName)
    val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
    val fileChannel = inputStream.channel
    return fileChannel.map(FileChannel.MapMode.READ_ONLY, fileDescriptor.startOffset, fileDescriptor.declaredLength)
}

// Run image through TensorFlow Lite model and return predicted label
fun analyzeImage(bitmap: Bitmap, context: Context): String {
    return try {
        val interpreter = Interpreter(loadModelFile(context, "model.tflite"))
        val labelMap = loadLabelMap(context)

        // Resize bitmap to model input size
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)

        // Prepare input tensor: [1, 224, 224, 3] normalized RGB
        val input = Array(1) { Array(224) { Array(224) { FloatArray(3) } } }
        for (y in 0 until 224) {
            for (x in 0 until 224) {
                val pixel = resizedBitmap.getPixel(x, y)
                input[0][y][x][0] = (Color.red(pixel) - 127.5f) / 127.5f
                input[0][y][x][1] = (Color.green(pixel) - 127.5f) / 127.5f
                input[0][y][x][2] = (Color.blue(pixel) - 127.5f) / 127.5f
            }
        }

        // Output tensor: [1, 40] for 40 possible labels
        val output = Array(1) { FloatArray(40) }
        interpreter.run(input, output)

        // Get the index of the highest confidence prediction
        val maxIndex = output[0].indices.maxByOrNull { output[0][it] } ?: -1
        val confidence = output[0][maxIndex]
        val label = labelMap[maxIndex] ?: "Unknown Category"

        // Return label if confidence is sufficient
        if (confidence > 0.01f) label
        else "Unrecognizable"
    } catch (e: Exception) {
        Log.e("TFLite", "Recognition Failed: ${e.message}")
        "Recognition Failed"
    }
}

