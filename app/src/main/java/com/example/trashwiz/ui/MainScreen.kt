package com.example.trashwiz.ui

import android.content.Context
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.trashwiz.R
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import androidx.compose.ui.graphics.Color
import com.example.trashwiz.MainActivity

// Define custom font families
val dl_regular = FontFamily(Font(R.font.dl_regular))
val ec_regular = FontFamily(Font(R.font.ec_regular))

@Composable
fun MainScreen(activity: ComponentActivity, navController: NavController, context: Context?) {
    // Holds the selected region, default is "BeiJing"
    var selectedRegion by remember { mutableStateOf("BeiJing") }
    // List of available regions
    val regionOptions = listOf("BeiJing", "ShangHai", "GuangZhou", "ShenZhen")
    // Controls dropdown menu visibility
    var expanded by remember { mutableStateOf(false) }
    // User input for query
    var queryText by remember { mutableStateOf("") }
//    var ctx = context
    Box(modifier = Modifier.fillMaxSize()) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.main_screen),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top bar with app title and region selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // App title text
                Text("TrashWiz", style = MaterialTheme.typography.headlineMedium, fontFamily = ec_regular, color = Color.Black)
                // Region dropdown button and menu
                Box {
                    OutlinedButton(onClick = { expanded = true }) {
                        Text("Region: $selectedRegion", fontFamily = dl_regular, color = Color.Black)
                        // Save selected region to global variable
                        MainActivity.regionName = selectedRegion
                    }
                    // Dropdown options for region selection
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        regionOptions.forEach { region ->
                            DropdownMenuItem(
                                text = { Text(region) },
                                onClick = {
                                    selectedRegion = region
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Spacer to push UI elements downward
            Spacer(modifier = Modifier.weight(1f))

            // Text field for user to input query
            OutlinedTextField(
                value = queryText,
                onValueChange = { queryText = it },
                label = { Text("Enter the object's name", fontFamily = dl_regular, color = Color.Black) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        // Navigate to result screen with encoded query text
                        if (queryText.isNotBlank()) {
                            val encoded = URLEncoder.encode(queryText, StandardCharsets.UTF_8.toString())
                            navController.navigate("result_screen/$encoded")
                        }
                    }
                )
            )

            // Button to perform search
            Button(
                onClick = {
                    var s = queryText
                    // Show warning if input is empty
                    if (TextUtils.isEmpty(s)) {
                        Toast.makeText(activity,"the keyword must not be empty",Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    // Navigate to result screen
                    navController.navigate("result_screen/"+queryText)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Search Result")
            }

            // Button to navigate to camera screen
            Button(
                onClick = {
                    navController.navigate("camera")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Identify Trash")
            }
        }
    }
}
