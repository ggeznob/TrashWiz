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
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import com.example.trashwiz.R
import com.example.trashwiz.db.AppDatabase
import com.example.trashwiz.entity.CategoriesEntity
import com.example.trashwiz.entity.ClassificationRuleEntity
import com.example.trashwiz.entity.GarbageEntity
import com.example.trashwiz.entity.RegionEntity
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import androidx.compose.ui.graphics.Color
import com.example.trashwiz.MainActivity

val dl_regular = FontFamily(Font(R.font.dl_regular))
val ec_regular = FontFamily(Font(R.font.ec_regular))

@Composable
fun MainScreen(activity: ComponentActivity, navController: NavController, context: Context?) {
    var selectedRegion by remember { mutableStateOf("BeiJing") }
    val regionOptions = listOf("BeiJing", "ShangHai", "GuangZhou", "ShenZhen")
    var expanded by remember { mutableStateOf(false) }
    var regionName = ""
    var cateName = ""
    var cateDesc = ""
    var queryText by remember { mutableStateOf("") }
    var ctx = context
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
                        MainActivity.regionName = selectedRegion
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
                    val db = AppDatabase.getDatabase(activity)
                    val classificationRuleDao = db.classificationRuleDao()
                    val garbageDao = db.garbageDao()
                    val regionDao = db.regionDao()
                    val categoriesDao = db.categoriesDao()
                    var s = queryText
                    if (TextUtils.isEmpty(s)) {
                        Toast.makeText(activity,"the keyword must not be empty",Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    navController.navigate("result_screen/"+queryText)
//
//                    Toast.makeText(ctx,queryText,Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("查询结果")
            }

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

    fun getClassification(activity: ComponentActivity,garbageEntity: GarbageEntity) {


    }


}
