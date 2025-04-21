package com.example.trashwiz.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.trashwiz.ui.theme.TrashWizTheme
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun MainScreen(navController: NavController) {
    var selectedRegion by remember { mutableStateOf("BeiJing") }
    val regionOptions = listOf("BeiJing", "ShangHai", "GuangZhou", "ShenZhen")
    var expanded by remember { mutableStateOf(false) }

    var queryText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Row for TrashWiz text and dropdown menu
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("TrashWiz", style = MaterialTheme.typography.headlineMedium)

            Box {
                OutlinedButton(onClick = { expanded = true }) {
                    Text("Current Region: $selectedRegion")
                }
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

        Spacer(modifier = Modifier.weight(1f))

        OutlinedTextField(
            value = queryText,
            onValueChange = { queryText = it },
            label = { Text("Enter the object's name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    if (queryText.isNotBlank()) {
                        val encoded = URLEncoder.encode(queryText, StandardCharsets.UTF_8.toString())
                        navController.navigate("result_screen/$encoded")
                    }
                }
            )
        )

        Button(
            onClick = {
                navController.navigate("camera")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("📷")
        }
    }
}
