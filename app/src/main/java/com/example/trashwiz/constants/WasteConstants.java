package com.example.trashwiz.constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WasteConstants {
    public static final List<String> cates = Arrays.asList(
            "Other Waste",
            "Kitchen Waste",
            "Recyclable Waste",
            "Hazardous Waste"
    );

    public static List<String> getCates() {
        return cates;
    }

    public static final String garbage = "{\n" +
            "  \"0\": \"Other Waste/Disposable Lunch Box\",\n" +
            "  \"1\": \"Other Waste/Contaminated Plastic\",\n" +
            "  \"2\": \"Other Waste/Cigarette Butt\",\n" +
            "  \"3\": \"Other Waste/Toothpick\",\n" +
            "  \"4\": \"Other Waste/Broken Flowerpot and Tableware\",\n" +
            "  \"5\": \"Other Waste/Bamboo Chopsticks\",\n" +
            "  \"6\": \"Kitchen Waste/Leftover Food\",\n" +
            "  \"7\": \"Kitchen Waste/Large Bones\",\n" +
            "  \"8\": \"Kitchen Waste/Fruit Peel\",\n" +
            "  \"9\": \"Kitchen Waste/Fruit Pulp\",\n" +
            "  \"10\": \"Kitchen Waste/Tea Leaves\",\n" +
            "  \"11\": \"Kitchen Waste/Vegetable Leaves and Roots\",\n" +
            "  \"12\": \"Kitchen Waste/Eggshell\",\n" +
            "  \"13\": \"Kitchen Waste/Fish Bone\",\n" +
            "  \"14\": \"Recyclable Waste/Power Bank\",\n" +
            "  \"15\": \"Recyclable Waste/Bag\",\n" +
            "  \"16\": \"Recyclable Waste/Cosmetic Bottle\",\n" +
            "  \"17\": \"Recyclable Waste/Plastic Toy\",\n" +
            "  \"18\": \"Recyclable Waste/Plastic Bowl or Basin\",\n" +
            "  \"19\": \"Recyclable Waste/Plastic Hanger\",\n" +
            "  \"20\": \"Recyclable Waste/Express Paper Bag\",\n" +
            "  \"21\": \"Recyclable Waste/Plug and Cable\",\n" +
            "  \"22\": \"Recyclable Waste/Old Clothes\",\n" +
            "  \"23\": \"Recyclable Waste/Aluminum Can\",\n" +
            "  \"24\": \"Recyclable Waste/Pillow\",\n" +
            "  \"25\": \"Recyclable Waste/Stuffed Toy\",\n" +
            "  \"26\": \"Recyclable Waste/Shampoo Bottle\",\n" +
            "  \"27\": \"Recyclable Waste/Glass Cup\",\n" +
            "  \"28\": \"Recyclable Waste/Leather Shoes\",\n" +
            "  \"29\": \"Recyclable Waste/Cutting Board\",\n" +
            "  \"30\": \"Recyclable Waste/Cardboard Box\",\n" +
            "  \"31\": \"Recyclable Waste/Seasoning Bottle\",\n" +
            "  \"32\": \"Recyclable Waste/Wine Bottle\",\n" +
            "  \"33\": \"Recyclable Waste/Metal Food Can\",\n" +
            "  \"34\": \"Recyclable Waste/Pot\",\n" +
            "  \"35\": \"Recyclable Waste/Oil Container\",\n" +
            "  \"36\": \"Recyclable Waste/Beverage Bottle\",\n" +
            "  \"37\": \"Hazardous Waste/Dry Battery\",\n" +
            "  \"38\": \"Hazardous Waste/Ointment\",\n" +
            "  \"39\": \"Hazardous Waste/Expired Medicine\"\n" +
            "}";

    public static final String CATE_DESCS = "其他垃圾（Residual Waste）\n" +
            "Non-recyclable waste with minimal harm but no reuse value, including contaminated tissues, plastic bags, and packaging. Typically processed via landfill or incineration.\n" +
            "厨余垃圾（Kitchen Waste）\n" +
            "Organic waste from food residues, peels, eggshells, and expired food. Suitable for composting or biodegradation to reduce landfill burden.\n" +
            "可回收物（Recyclable Waste）\n" +
            "Materials like metals, paper, glass, and clean plastics that can be reprocessed into new products. Proper sorting reduces resource consumption and environmental pollution.\n" +
            "有害垃圾（Hazardous Waste）\n" +
            "Toxic substances including batteries, expired medicines, and chemical products. Requires specialized disposal to prevent health risks and ecosystem damage.";
}
