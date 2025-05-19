package com.example.trashwiz.ui
import com.example.trashwiz.R

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.OutlinedTextField

val dl_regular = FontFamily(Font(R.font.dl_regular))
val ec_regular = FontFamily(Font(R.font.ec_regular))

@Composable
fun MainScreen(navController: NavController) {
    var selectedRegion by remember { mutableStateOf("BeiJing") }
    val regionOptions = listOf("BeiJing", "ShangHai", "GuangZhou", "ShenZhen")
    var expanded by remember { mutableStateOf(false) }

    var queryText by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        // 背景图片
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
            // Row for TrashWiz text and dropdown menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("TrashWiz", style = MaterialTheme.typography.headlineMedium, fontFamily = ec_regular)

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
                label = { Text("Enter the object's name", fontFamily = dl_regular) },
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
}
