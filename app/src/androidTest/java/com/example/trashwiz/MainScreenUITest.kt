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
        // wait for Compose content loads
        composeTestRule.waitForIdle()

        // region_button exists and has click action
        composeTestRule.onNodeWithTag("region_button")
            .assertExists()
            .assertHasClickAction()

        // query_input exists and works
        val queryNode = composeTestRule.onNodeWithTag("query_input")
        queryNode.assertExists()
        queryNode.performTextInput("banana")
        queryNode.assert(hasText("banana"))

        // search_button exists and clickable
        composeTestRule.onNodeWithTag("search_button")
            .assertExists()
            .assertHasClickAction()

        // identify_button exists and clickable
        composeTestRule.onNodeWithTag("identify_button")
            .assertExists()
            .assertHasClickAction()
    }

    @Test
    fun testCameraScreenAppearsAfterClick() {
        //perform clicks
        composeTestRule.onNodeWithTag("identify_button").performClick()
        //wait for jump
        composeTestRule.waitForIdle()
        //assert the occurrence of jump
        composeTestRule.onNodeWithTag("take_picture_button").assertExists()
    }


    @Test
    fun testAllRegionSelectionsUpdateCorrectly() {
        val regions = listOf("BeiJing", "ShangHai", "GuangZhou", "ShenZhen")

        regions.forEach { region ->
            // click 'region' button,Pull down and expand menu
            composeTestRule.onNodeWithTag("region_button")
                .assertExists()
                //perform click
                .performClick()

            // click related region session
            composeTestRule.onNodeWithText(region)
                .assertExists()
                .performClick()

            // wait for update of Compose
            composeTestRule.waitForIdle()

            // assert the text of region_button is: "Region: $region"
            composeTestRule.onNodeWithTag("region_button")
                .assertTextEquals("Region: $region")
        }
    }



    @Test
    fun testSearchResultNavigationForNotEmpty() {
        val testQuery = "banana"

        // input search contents
        composeTestRule.onNodeWithTag("query_input")
            .assertExists()
            .performTextInput(testQuery)

        // click Search Result button
        composeTestRule.onNodeWithTag("search_button")
            .assertExists()
            .performClick()

        // wait for navigation
        composeTestRule.waitForIdle()

        // Wait for the jump to occur (you know ResultScreen will display a "Back to Main" button)
        composeTestRule.onNodeWithText("Back to Main").assertExists()

        // Then assert the transmission of parameters
        composeTestRule.onNodeWithText(testQuery, substring = true).assertExists()

    }

    @Test
    fun testSearchResultButtonWithEmptyInput_doesNotNavigate() {
        // clear the input content
        composeTestRule.onNodeWithTag("query_input")
            .performTextClearance()

        // click search_button
        composeTestRule.onNodeWithTag("search_button")
            .assertExists()
            .performClick()

        //
        composeTestRule.waitForIdle()

        // assert there is no jump  —— using specified text of “ResultScreen to justify（like "testQuery" or"Back to Main”）
        composeTestRule.onNodeWithText("Back to Main")
            .assertDoesNotExist()
        // assert there is no jump, search button still exists
        composeTestRule.onNodeWithTag("search_button")
            .assertExists()

    }

}
