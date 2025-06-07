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

@Composable
fun ResultScreen(activity: ComponentActivity,navController: NavController, itemName: String) {
    var cateName by remember { mutableStateOf("") }
    var cateDesc by remember { mutableStateOf("") }
    val isUnrecognizable = itemName.startsWith("Unrecognizable") || itemName == "Recognition Failed"

    val db = AppDatabase.getDatabase(activity)
    val classificationRuleDao = db.classificationRuleDao()
    val garbageDao = db.garbageDao()
    val regionDao = db.regionDao()
    val categoriesDao = db.categoriesDao()
    var s = itemName.replace("+", " ")
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.result_screen),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = s,
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 33.sp),
                textAlign = TextAlign.Center,
                fontFamily = ec_regular,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (!isUnrecognizable) {
                Text(
                    text = cateName,
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
                        .padding(horizontal = 12.dp),
                    fontFamily = dl_regular,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.weight(1f))

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

    garbageDao.getByKeyword(s.replace(" ", ""))
        .observe(activity, object : Observer<GarbageEntity?> {
            override fun onChanged(value: GarbageEntity?) {
                if (value == null) {
                    showTips()
                    return
                }
               var garbage = value
                regionDao.getByRegionName(MainActivity.regionName).observe(activity,
                    object : Observer<RegionEntity?>{
                        override fun onChanged(value: RegionEntity?) {
                            if (value == null) {
                                showTips()
                                return
                            }
                            classificationRuleDao.getByItemId(garbage.item_id,value!!.region_id)
                                .observe(activity, object : Observer<ClassificationRuleEntity?> {

                                    override fun onChanged(value: ClassificationRuleEntity?) {
                                        if (value == null) {
                                            showTips()
                                            return
                                        }
                                        categoriesDao.getByCateId(value!!.category_id)
                                            .observe(activity, object : Observer<CategoriesEntity?> {
                                                override fun onChanged(value: CategoriesEntity?) {
                                                    if (value == null) {
                                                        showTips()
                                                        return
                                                    }
                                                    cateName = value!!.name
                                                    cateDesc = value!!.description
                                                }
                                            })
                                    }
                                })
                        }

                    })

            }

            private fun showTips() {
                AlertDialog.Builder(activity)
                    .setTitle("Tips")
                    .setMessage("No Result")
                    .setPositiveButton("OK",null)
                    .setOnDismissListener {
                        navController.navigate("main")
                    }
                    .show();
            }
        })
}
