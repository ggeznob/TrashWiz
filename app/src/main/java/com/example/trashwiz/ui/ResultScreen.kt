package com.example.trashwiz.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun ResultScreen(navController: NavController, itemName: String) {
    val coroutineScope = rememberCoroutineScope()
    var category by remember { mutableStateOf("Loading...") }
    var description by remember { mutableStateOf("Loading...") }

    // Simulate loaded data
    category = "Recyclable Waste"
    description = "Cans made of aluminum or tinplate are recyclable. Please empty the contents before disposing and do not crush them, as intact cans are easier to process during recycling."

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp) // general side padding
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // itemName at top, with extra top spacing
            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Item Recognized: $itemName",
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 26.sp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Category: $category",
                style = MaterialTheme.typography.headlineSmall.copy(fontSize = 22.sp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // description starts near the middle, auto-wrapping
            Text(
                text = "Description: $description",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.weight(1f)) // Push button to bottom

            Button(
                onClick = { navController.navigate("main") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("Back to Main")
            }
        }
    }
}
