package com.example.trashwiz.ui

import android.Manifest
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
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.core.vision.ImageProcessingOptions
import org.tensorflow.lite.task.core.vision.preprocessing.NormalizeOp
import org.tensorflow.lite.task.vision.classifier.ImageClassifier
import org.tensorflow.lite.task.vision.classifier.Classifications
import org.tensorflow.lite.task.vision.classifier.ImageClassifier.ImageClassifierOptions
import java.io.ByteArrayOutputStream
import java.io.IOException
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
                Log.d("Cameraaaa", "onClick")
                imageCapture?.takePicture(
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageCapturedCallback() {
                        override fun onCaptureSuccess(image: ImageProxy) {
                            Log.d("Cameraaaa", "onCaptureSuccess")
                            Log.d("Cameraaaa", "image format: ${image.format}, planes: ${image.planes.size}")

                            val bitmap = imageProxyToBitmap(image)
                            image.close()

                            val itemName = analyzeImage(bitmap, context)
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

// ImageProxy 转 Bitmap
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

// 使用 TFLite 模型进行推理并返回分类名称
fun analyzeImage(bitmap: Bitmap, context: Context): String {
    val modelName = "model.tflite"
    val resultText: String

    try {
        // ⚠️ 设置归一化：mean=127.5, std=127.5 -> 把像素从[0,255]归一化为[-1,1]
        val normalizeOp = NormalizeOp(127.5f, 127.5f)

        // 构造归一化选项
        val baseOptions = BaseOptions.builder().build()

        val options = ImageClassifierOptions.builder()
            .setBaseOptions(baseOptions)
            .setMaxResults(1)
            .setScoreThreshold(0.3f)
            .setImageProcessingOptions(
                ImageProcessingOptions.builder()
                    .addPreprocessingOp(normalizeOp)
                    .build()
            )
            .build()

        val imageClassifier = ImageClassifier.createFromFileAndOptions(context, modelName, options)
        val tensorImage = TensorImage.fromBitmap(bitmap)
        val results: List<Classifications> = imageClassifier.classify(tensorImage)

        val topResult = results.firstOrNull()?.categories?.maxByOrNull { it.score }
        resultText = topResult?.label ?: "Unknown"
        Log.d("TFLite", "识别结果：$resultText")
    } catch (e: IOException) {
        Log.e("TFLite", "模型加载失败：${e.message}")
        return "Unknown"
    } catch (e: Exception) {
        Log.e("TFLite", "推理失败：${e.message}")
        return "Unknown"
    }

    return resultText
}
