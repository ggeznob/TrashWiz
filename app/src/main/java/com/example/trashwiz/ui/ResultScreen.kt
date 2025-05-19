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
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.trashwiz.R

@Composable
fun ResultScreen(navController: NavController, itemName: String) {
    val coroutineScope = rememberCoroutineScope()
    var category by remember { mutableStateOf("Loading...") }
    var description by remember { mutableStateOf("Loading...") }

    // 判定是否为无法识别的情况
    val isUnrecognizable = itemName.startsWith("Unrecognizable") || itemName == "Recognition Failed"

    // 仅在非无法识别时填充分类和描述内容
    if (!isUnrecognizable) {
        category = "Recyclable Waste"
        description = "Cans made of aluminum or tinplate are recyclable. Please empty the contents before disposing and do not crush them, as intact cans are easier to process during recycling."
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.result_screen),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Item Recognized: $itemName",
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 26.sp),
                textAlign = TextAlign.Center,
                fontFamily = ec_regular
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 只有当不是无法识别时才显示分类
            if (!isUnrecognizable) {
                Text(
                    text = "Category: $category",
                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = 22.sp),
                    textAlign = TextAlign.Center,
                    fontFamily = dl_regular
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Description: $description",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    fontFamily = dl_regular
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { navController.navigate("main") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("Back to Main", fontFamily = dl_regular)
            }
        }
    }
}
