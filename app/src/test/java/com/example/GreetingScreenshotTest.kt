package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.local.entity.AccountabilityPartnerEntity
import com.example.ui.components.AccountabilityPartnerCard
import com.example.ui.theme.TheVaultTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun partner_card_screenshot() {
    val mockPartner = AccountabilityPartnerEntity(
      isLinked = true,
      partnerEmail = "alexandra.sterling@vaultcapital.org",
      partnerName = "Alexandra Sterling",
      sharedStreak = 7,
      userCheckedInToday = true,
      partnerCheckedInToday = false
    )
    composeTestRule.setContent {
      TheVaultTheme {
        AccountabilityPartnerCard(
          partner = mockPartner,
          onOpenPartnerModal = {},
          onQuickCheckIn = {},
          onQuickNudge = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/partner_card.png")
  }
}

