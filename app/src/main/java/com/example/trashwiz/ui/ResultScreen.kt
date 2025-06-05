package com.example.trashwiz.ui

import android.text.TextUtils
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
import org.w3c.dom.Text
import androidx.compose.ui.graphics.Color

@Composable
fun ResultScreen(activity: ComponentActivity,navController: NavController, itemName: String) {
    val coroutineScope = rememberCoroutineScope()
    var category by remember { mutableStateOf("Loading...") }
    var description by remember { mutableStateOf("Loading...") }
    var regionName by remember { mutableStateOf("") }
    var cateName by remember { mutableStateOf("") }
    var cateDesc by remember { mutableStateOf("") }
    // 判定是否为无法识别的情况
    val isUnrecognizable = itemName.startsWith("Unrecognizable") || itemName == "Recognition Failed"

    // 仅在非无法识别时填充分类和描述内容
    if (!isUnrecognizable) {
        // 调用数据库查询相应垃圾分类类别 ----------------------------------------------

        category = "Recyclable Waste"
        description = "Cans made of aluminum or tinplate are recyclable. Please empty the contents before disposing and do not crush them, as intact cans are easier to process during recycling."

        // -----------------------------------------------------------------------
    }
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
                text = "Item Recognized: $s",
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 26.sp),
                textAlign = TextAlign.Center,
                fontFamily = ec_regular,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 只有当不是无法识别时才显示分类
            if (!isUnrecognizable) {
                Text(
                    text = "Category: $cateName",
                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = 22.sp),
                    textAlign = TextAlign.Center,
                    fontFamily = dl_regular,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Description: $cateDesc",
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
                    return
                }
                classificationRuleDao.getByItemId(value.item_id)
                    .observe(activity, object : Observer<ClassificationRuleEntity?> {

                        override fun onChanged(value: ClassificationRuleEntity?) {
                            regionDao.getByRegionId(value!!.region_id)
                                .observe(activity, object : Observer<RegionEntity?> {

                                    override fun onChanged(value: RegionEntity?) {
                                        regionName = value!!.name

                                    }
                                })
                            categoriesDao.getByCateId(value!!.category_id)
                                .observe(activity, object : Observer<CategoriesEntity?> {
                                    override fun onChanged(value: CategoriesEntity?) {
                                        cateName = value!!.name
                                        cateDesc = value!!.description
                                    }
                                })
                        }
                    })
            }
        })
}
