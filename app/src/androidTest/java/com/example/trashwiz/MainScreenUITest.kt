package com.example.trashwiz
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.UiDevice
import org.junit.*

import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainScreenUITest {

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
    fun testMainScreenLoadsAfterPermissionGranted() {
        // 等待 Compose 内容加载
        composeTestRule.waitForIdle()

        // region_button 存在并可点击
        composeTestRule.onNodeWithTag("region_button")
            .assertExists()
            .assertHasClickAction()

        // query_input 存在并可输入
        val queryNode = composeTestRule.onNodeWithTag("query_input")
        queryNode.assertExists()
        queryNode.performTextInput("banana")
        queryNode.assert(hasText("banana"))

        // search_button 存在并可点击
        composeTestRule.onNodeWithTag("search_button")
            .assertExists()
            .assertHasClickAction()

        // identify_button 存在并可点击
        composeTestRule.onNodeWithTag("identify_button")
            .assertExists()
            .assertHasClickAction()
    }

    @Test
    fun testCameraScreenAppearsAfterClick() {
        composeTestRule.onNodeWithTag("identify_button").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("take_picture_button").assertExists()
    }

    /*
    @Test
    fun t() {
        // 等待 Compose 内容加载
        composeTestRule.waitForIdle()


    }
*/
    @Test
    fun testAllRegionSelectionsUpdateCorrectly() {
        val regions = listOf("BeiJing", "ShangHai", "GuangZhou", "ShenZhen")

        regions.forEach { region ->
            // 点击 region 按钮，展开下拉菜单
            composeTestRule.onNodeWithTag("region_button")
                .assertExists()
                .performClick()

            // 点击对应的 region 项
            composeTestRule.onNodeWithText(region)
                .assertExists()
                .performClick()

            // 等待 Compose 更新
            composeTestRule.waitForIdle()

            // 断言 region_button 文本是 "Region: $region"
            composeTestRule.onNodeWithTag("region_button")
                .assertTextEquals("Region: $region")
        }
    }



    @Test
    fun testSearchResultNavigationForNotEmpty() {
        val testQuery = "banana"

        // 输入搜索内容
        composeTestRule.onNodeWithTag("query_input")
            .assertExists()
            .performTextInput(testQuery)

        // 点击 Search Result 按钮
        composeTestRule.onNodeWithTag("search_button")
            .assertExists()
            .performClick()

        // 等待导航完成（确保 UI 稳定）
        composeTestRule.waitForIdle()

        // 等待跳转发生（你知道 ResultScreen 会展示一个 "Back to Main" 按钮）
        composeTestRule.onNodeWithText("Back to Main").assertExists()

        // 然后再断言传参
        composeTestRule.onNodeWithText(testQuery, substring = true).assertExists()

    }

    @Test
    fun testSearchResultButtonWithEmptyInput_doesNotNavigate() {
        // 清空输入框（默认应该是空的，但可以显式设置）
        composeTestRule.onNodeWithTag("query_input")
            .performTextClearance()

        // 点击 search_button
        composeTestRule.onNodeWithTag("search_button")
            .assertExists()
            .performClick()

        // 等待 UI 稳定
        composeTestRule.waitForIdle()

        // 验证没有跳转 —— 用“ResultScreen 特有文字”判断（比如 testQuery 或“Back to Main”之类）
        composeTestRule.onNodeWithText("Back to Main")
            .assertDoesNotExist()
        // 验证没有跳转, search button 依旧存在
        composeTestRule.onNodeWithTag("search_button")
            .assertExists()

    }

}
