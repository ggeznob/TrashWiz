package com.example.trashwiz.ui

import android.app.AlertDialog
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import com.example.trashwiz.R
import com.example.trashwiz.db.AppDatabase
import com.example.trashwiz.entity.CategoriesEntity
import com.example.trashwiz.entity.ClassificationRuleEntity
import com.example.trashwiz.entity.GarbageEntity
import com.example.trashwiz.entity.RegionEntity
import androidx.compose.ui.graphics.Color
import com.example.trashwiz.MainActivity
import androidx.compose.ui.platform.testTag

@Composable
fun ResultScreen(activity: ComponentActivity,navController: NavController, itemName: String) {
    // State variables to hold category name and description
    var cateName by remember { mutableStateOf("") }
    var cateDesc by remember { mutableStateOf("") }
    // Check if the recognition failed or item is unrecognized
    val isUnrecognizable = itemName.startsWith("Unrecognizable") || itemName == "Recognition Failed"

    // Initialize Room database DAOs
    val db = AppDatabase.getDatabase(activity)
    val classificationRuleDao = db.classificationRuleDao()
    val garbageDao = db.garbageDao()
    val regionDao = db.regionDao()
    val categoriesDao = db.categoriesDao()

    // Decode the item name (handle '+' from URL encoding)
    var s = itemName.replace("+", " ")

    // Fullscreen background layout
    Box(modifier = Modifier.fillMaxSize()) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.result_screen),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Main content layout
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Display recognized item name
            Text(
                text = s,
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 33.sp),
                textAlign = TextAlign.Center,
                fontFamily = ec_regular,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Display category name and description if recognition succeeded
            if (!isUnrecognizable) {
                Text(
                    text = cateName,
                    modifier = Modifier.testTag("category_name"),
                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = 22.sp),
                    textAlign = TextAlign.Center,
                    fontFamily = dl_regular,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = cateDesc,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("category_desc")
                        .padding(horizontal = 12.dp),
                    fontFamily = dl_regular,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Button to return to main screen
            Button(
                onClick = { navController.navigate("main") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("Back to Main", fontFamily = dl_regular, color = Color.Black)
            }
        }
    }

    // Query the garbage item by keyword
    garbageDao.getByKeyword(s.replace(" ", ""))
        .observe(activity, object : Observer<GarbageEntity?> {
            override fun onChanged(value: GarbageEntity?) {
                if (value == null) {
                    // Show alert if not found
                    showTips()
                    return
                }
                var garbage = value

                // Query the current region
                regionDao.getByRegionName(MainActivity.regionName).observe(activity,
                    object : Observer<RegionEntity?>{
                        override fun onChanged(value: RegionEntity?) {
                            if (value == null) {
                                showTips()
                                return
                            }
                            // Query classification rule based on item and region
                            classificationRuleDao.getByItemId(garbage.item_id,value!!.region_id)
                                .observe(activity, object : Observer<ClassificationRuleEntity?> {

                                    override fun onChanged(value: ClassificationRuleEntity?) {
                                        if (value == null) {
                                            showTips()
                                            return
                                        }
                                        // Query category details
                                        categoriesDao.getByCateId(value!!.category_id)
                                            .observe(activity, object : Observer<CategoriesEntity?> {
                                                override fun onChanged(value: CategoriesEntity?) {
                                                    if (value == null) {
                                                        showTips()
                                                        return
                                                    }
                                                    // Set the final display content
                                                    cateName = value!!.name
                                                    cateDesc = value!!.description
                                                }
                                            })
                                    }
                                })
                        }

                    })

            }

            // Display a dialog if the recognition or query fails
            private fun showTips() {
                AlertDialog.Builder(activity)
                    .setTitle("Oops!")
                    .setMessage("Sorry, no data on this waste and its classification was found.")
                    .setPositiveButton("OK",null)
                    .setOnDismissListener {
                        navController.navigate("main")
                    }
                    .show();
            }
        })
}
