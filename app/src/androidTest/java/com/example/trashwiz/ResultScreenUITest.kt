package com.example.trashwiz


import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.UiDevice
import org.junit.*
import org.junit.runner.RunWith
import androidx.test.uiautomator.UiSelector
import org.junit.Assert.assertTrue



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

        // input search content
        composeTestRule.onNodeWithTag("query_input")
            .performTextInput(testQuery)

        // click Search Result button
        composeTestRule.onNodeWithTag("search_button")
            .performClick()

        // wait for navigation
        composeTestRule.waitForIdle()

        // then assert para
        composeTestRule.onNodeWithText(testQuery, substring = true).assertExists()


        // assert the page jump（we know ResultScreen will show "Back to Main" button）
        composeTestRule.onNodeWithText("Back to Main").assertExists()

        // wait for UI and  LiveData loads
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithTag("category_name").fetchSemanticsNodes().isNotEmpty() &&
                    composeTestRule.onAllNodesWithTag("category_desc").fetchSemanticsNodes().isNotEmpty()
        }

// Verify if the classification name is correct (replace assertion text based on your database content)
        composeTestRule.onNodeWithTag("category_name")
            .assertExists()
            .assertTextContains("Kitchen Waste") // 举例：你知道 Fish Bone 属于哪个分类

// verify if classification description exists
        composeTestRule.onNodeWithTag("category_desc")
            .assertExists()
            .assertTextContains("Suitable for composting or biodegradation to reduce landfill burden.", substring = true) // 举例：你知道分类描述的一部分


        // click 'back to main' button
        composeTestRule.onNodeWithText("Back to Main")
            .performClick()

        // wait for navigation（make sure UI stability)
        composeTestRule.waitForIdle()

        // Claiming the occurrence of jump (we know that the returned MainScreen will display a "price and analyze" button)
        composeTestRule.onNodeWithTag("search_button").assertExists()


    }

    @Test
    fun testSearchResultButtonForWrongVlue() {
        val testQuery = "banana"

        // input search input
        composeTestRule.onNodeWithTag("query_input")
            .performTextInput(testQuery)

        // click Search Result button
        composeTestRule.onNodeWithTag("search_button")
            .performClick()

        //wait for navigation
        composeTestRule.waitForIdle()


        // then assert the para
        composeTestRule.onNodeWithText(testQuery, substring = true).assertExists()



        //  wait for AlertDialog's display
        val dialogShown = device.findObject(
            UiSelector().textContains("Sorry, no data on this waste")
        ).waitForExists(5_000)

        assertTrue("Dialog was not shown!", dialogShown)

        //  click the OK button of AlertDialog
        device.findObject(UiSelector().text("OK")).click()

        // wait for jump back to main screen
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithTag("search_button").fetchSemanticsNodes().isNotEmpty()
        }

        // assert back to MainScreen（by sserting the exist of search button）
        composeTestRule.onNodeWithTag("search_button").assertExists()
    }



}
