package com.example.trashwiz


import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.UiDevice
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResultScreenUITest {

    private lateinit var device: UiDevice

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val permissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(android.Manifest.permission.CAMERA)

    @Before
    fun setup() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    }


    @Test
    fun testSearchResultNavigationForTrulyValue() {
        val testQuery = "Fish Bone"

        // 输入搜索内容
        composeTestRule.onNodeWithTag("query_input")
            .performTextInput(testQuery)

        // 点击 Search Result 按钮
        composeTestRule.onNodeWithTag("search_button")
            .performClick()

        // 等待导航完成（确保 UI 稳定）
        composeTestRule.waitForIdle()

        // 然后再断言传参
        composeTestRule.onNodeWithText(testQuery, substring = true).assertExists()


        // 断言跳转的发生（你知道 ResultScreen 会展示一个 "Back to Main" 按钮）
        composeTestRule.onNodeWithText("Back to Main").assertExists()

        // 等待 UI 和 LiveData 数据加载
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithTag("category_name").fetchSemanticsNodes().isNotEmpty() &&
                    composeTestRule.onAllNodesWithTag("category_desc").fetchSemanticsNodes().isNotEmpty()
        }

// 验证分类名称是否正确（根据你的数据库内容替换断言文字）
        composeTestRule.onNodeWithTag("category_name")
            .assertExists()
            .assertTextContains("Kitchen Waste") // 举例：你知道 Fish Bone 属于哪个分类

// 验证分类描述是否存在（内容视数据库）
        composeTestRule.onNodeWithTag("category_desc")
            .assertExists()
            .assertTextContains("Suitable for composting or biodegradation to reduce landfill burden.", substring = true) // 举例：你知道分类描述的一部分


        // 点击 back to main 按钮
        composeTestRule.onNodeWithText("Back to Main")
            .performClick()

        // 等待导航完成（确保 UI 稳定）
        composeTestRule.waitForIdle()

        // 断言跳转的发生（你知道返回的 MainScreen 会展示一个 "priture and analyze" 按钮）
        composeTestRule.onNodeWithTag("search_button").assertExists()


    }

    @Test
    fun testSearchResultButtonForWrongVlue() {
        val testQuery = "banana"

        // 输入搜索内容
        composeTestRule.onNodeWithTag("query_input")
            .performTextInput(testQuery)

        // 点击 Search Result 按钮
        composeTestRule.onNodeWithTag("search_button")
            .performClick()

        // 等待导航完成（确保 UI 稳定）
        composeTestRule.waitForIdle()


        // 然后再断言传参
        composeTestRule.onNodeWithText(testQuery, substring = true).assertExists()


        // 等待弹窗出现
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText("Sorry, no data on this waste and its classification was found.").fetchSemanticsNodes().isNotEmpty()
        }

        // 断言弹窗确实显示
        composeTestRule.onNodeWithText("Sorry, no data on this waste and its classification was found.").assertExists()

        // 点击弹窗的“OK”按钮
        composeTestRule.onNodeWithText("OK").performClick()

        // 等待跳转回主页面
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithTag("search_button").fetchSemanticsNodes().isNotEmpty()
        }

        // 断言回到了 MainScreen（通过主页面按钮确认）
        composeTestRule.onNodeWithTag("search_button").assertExists()
    }



}
