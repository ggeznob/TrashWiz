package com.example.trashwiz

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.trashwiz.ui.analyzeImage
import com.example.trashwiz.ui.loadLabelMap
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AnalyseImagePreprocessingAndPostprocessingTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun testImagePreprocessing() {
        // Subtest 1: Image preprocessing
        val bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888).apply {
            eraseColor(Color.rgb(100, 150, 200))  // Use a fixed pixel value
        }

        val result = analyzeImage(bitmap, context)

        // Check that the result is not null or "Recognition Failed"
        assertNotNull(result)
        assertNotEquals("Recognition Failed", result)
    }

    @Test
    fun testModelOutputPostprocessing() {
        // Subtest 2: Model output postprocessing
        val bitmap = Bitmap.createBitmap(224, 224, Bitmap.Config.ARGB_8888).apply {
            eraseColor(Color.rgb(128, 128, 128))  // Neutral gray color
        }

        val result = analyzeImage(bitmap, context)

        // We expect the result to be a valid label or "Unrecognizable"
        assertTrue(
            result != "Recognition Failed"
        )
    }

    @Test
    fun testLabelMapLoading() {
        // Subtest 3: Label map loading
        val labelMap = loadLabelMap(context)

        assertNotNull(labelMap)
        assertTrue(labelMap.isNotEmpty())
        assertTrue(labelMap.containsKey(0))  // For example, key = 0 should exist
        assertTrue(labelMap[0]?.isNotBlank() == true)
    }
}

