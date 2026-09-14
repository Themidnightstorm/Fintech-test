package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.entity.SlipType
import com.example.util.CsvTransactionParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("The Vault", appName)
  }

  @Test
  fun `csv parser automatically categorizes bank transactions`() {
    val sampleCsv = """
      Date,Description,Amount,Balance
      2026-08-20,Vanguard S&P 500 ETF Transfer,1250.00,48920.00
      2026-08-20,Whole Foods Market Grocery,142.80,47670.00
      2026-08-19,Starbucks Reserve Roastery,18.50,47527.20
    """.trimIndent()

    val parsed = CsvTransactionParser.parseCsvContent(sampleCsv)
    assertEquals(3, parsed.size)

    // Transfer -> Savings
    assertEquals(SlipType.SAVINGS, parsed[0].slipType)
    assertEquals(1250.00, parsed[0].amount, 0.001)
    assertEquals(48920.00, parsed[0].balance ?: 0.0, 0.001)

    // Grocery -> Expenses
    assertEquals(SlipType.EXPENSES, parsed[1].slipType)
    assertEquals(142.80, parsed[1].amount, 0.001)

    // Restaurant / Coffee -> Wants
    assertEquals(SlipType.WANTS, parsed[2].slipType)
    assertEquals(18.50, parsed[2].amount, 0.001)
  }
}
