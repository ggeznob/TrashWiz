// This is the main Application class that initializes the local database with default regions, categories, and garbage classification data on first app launch.

package com.example.trashwiz;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.Observer;

import com.example.trashwiz.constants.WasteConstants;
import com.example.trashwiz.dao.CategoriesDao;
import com.example.trashwiz.dao.ClassificationRuleDao;
import com.example.trashwiz.dao.GarbageDao;
import com.example.trashwiz.dao.RegionDao;
import com.example.trashwiz.db.AppDatabase;
import com.example.trashwiz.entity.CategoriesEntity;
import com.example.trashwiz.entity.ClassificationRuleEntity;
import com.example.trashwiz.entity.GarbageEntity;
import com.example.trashwiz.entity.RegionEntity;
import com.example.trashwiz.utils.ShareUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MyApp extends Application {
    private CategoriesDao categoriesDao;
    private ClassificationRuleDao classificationRuleDao;
    private GarbageDao garbageDao;
    private RegionDao regionDao;
    private List<String> garbage = new ArrayList<>();
    private List<String> classification = new ArrayList<>();
    @Override
    public void onCreate() {
        super.onCreate();

        // Get database instance
        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
        categoriesDao = db.categoriesDao();
        classificationRuleDao = db.classificationRuleDao();
        garbageDao = db.garbageDao();
        regionDao = db.regionDao();

        // Check if the app is launched for the first time
        if (ShareUtils.get(getApplicationContext(), ShareUtils.KEY_INIT).isEmpty()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // Insert default regions
                    regionDao.insert(new RegionEntity(0,"GuangZhou"));
                    regionDao.insert(new RegionEntity(1,"ShenZhen"));
                    regionDao.insert(new RegionEntity(2,"ShangHai"));
                    regionDao.insert(new RegionEntity(3,"BeiJing"));

                    // Insert waste categories with descriptions
                    List<String> l = WasteConstants.getCates();
                    categoriesDao.insert(new CategoriesEntity(0,l.get(0),WasteConstants.Residual_DESCS));
                    categoriesDao.insert(new CategoriesEntity(1,l.get(1),WasteConstants.Kitchen_DESCS));
                    categoriesDao.insert(new CategoriesEntity(2,l.get(2),WasteConstants.Recyclable_DESCS));
                    categoriesDao.insert(new CategoriesEntity(3,l.get(3),WasteConstants.Hazardous_DESCS));

                    try {
                        // Parse and insert garbage classification data from JSON
                        JSONObject jsonObject = new JSONObject(WasteConstants.garbage);
                        Iterator<String> it = jsonObject.keys();
                        while (it.hasNext()) {
                            String s = it.next();
                            String value = jsonObject.getString(s);
                            int slashIndex = value.indexOf('/');
                            String cate = "";
                            if (slashIndex != -1 && slashIndex < value.length() - 1) {
                                cate = (value.substring(0, slashIndex) + "");
                                Log.e("HHH", "garbage_items.item_id " + Integer.parseInt(s));
                                // Insert garbage item
                                garbageDao.insert(new GarbageEntity(Integer.parseInt(s),value.substring(slashIndex + 1)));
                            }
                            // Insert classification rules for all regions
                            classificationRuleDao.insert(new ClassificationRuleEntity(0,Integer.parseInt(s),WasteConstants.cates.indexOf(cate)));
                            classificationRuleDao.insert(new ClassificationRuleEntity(1,Integer.parseInt(s),WasteConstants.cates.indexOf(cate)));
                            classificationRuleDao.insert(new ClassificationRuleEntity(2,Integer.parseInt(s),WasteConstants.cates.indexOf(cate)));
                            classificationRuleDao.insert(new ClassificationRuleEntity(3,Integer.parseInt(s),WasteConstants.cates.indexOf(cate)));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // Mark initialization as complete
                    ShareUtils.add(getApplicationContext(),ShareUtils.KEY_INIT,"1 ");
                }
            }).start();
        }
    }
}
