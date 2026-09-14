package com.example

import com.example.data.local.entity.SavingsGoalEntity
import com.example.data.model.FinancialRoadmapData
import com.example.data.model.VaultCurrency
import com.example.ui.viewmodel.InvestingSimState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun `daily safe spend calculation adheres to 30 percent savings rule`() {
    val monthlyIncome = 6500.00
    val fixedOverhead = 2930.00 // 2100 rent + 550 car + 280 utilities
    val savingsTarget = monthlyIncome * 0.30 // 1950.00
    val discretionaryPool = monthlyIncome - fixedOverhead - savingsTarget // 1620.00
    val dailySafeSpend = discretionaryPool / 30.0 // 54.00

    assertEquals(1950.00, savingsTarget, 0.001)
    assertEquals(1620.00, discretionaryPool, 0.001)
    assertEquals(54.00, dailySafeSpend, 0.001)
  }

  @Test
  fun `net worth equation matches total assets minus total debt`() {
    val savings = 18500.00
    val debt = 4200.00
    val checking = 1500.00

    // Fixed income: net worth is savings - debt
    val netWorthFixed = savings - debt
    assertEquals(14300.00, netWorthFixed, 0.001)

    // Variable income with liquid checking: assets (savings + checking) - debt
    val netWorthVariable = (savings + checking) - debt
    assertEquals(15800.00, netWorthVariable, 0.001)
  }

  @Test
  fun `compounding simulator formula calculates valid future value and gains`() {
    val sim = InvestingSimState(
      initialCapital = 10000.00,
      monthlyDeposit = 500.00,
      years = 10,
      stocksWeight = 1.0f,
      realEstateWeight = 0.0f,
      goldWeight = 0.0f,
      bondsWeight = 0.0f
    )

    assertEquals(0.10, sim.weightedAnnualReturn, 0.001)
    val totalDeposited = 10000.00 + (500.00 * 12 * 10) // 70,000.00
    assertEquals(70000.00, sim.totalDeposited, 0.001)

    // With 10% annual return compounded monthly over 10 years, future value must exceed total deposits
    assertTrue(sim.futureNetWorth > sim.totalDeposited)
    assertTrue(sim.compoundGain > 0.0)
  }

  @Test
  fun `financial independence target uses 25x annual expenses rule`() {
    val fixedOverhead = 2500.00
    val monthlyIncome = 6000.00
    val estimatedMonthlyLiving = (fixedOverhead + (monthlyIncome * 0.20)).coerceAtLeast(1200.0) // 2500 + 1200 = 3700
    val annualLivingExpenses = estimatedMonthlyLiving * 12.0 // 44,400

    val fiTarget = annualLivingExpenses * 25.0 // 1,110,000
    assertEquals(1110000.00, fiTarget, 0.01)

    val roadmap = FinancialRoadmapData.calculate(
      netWorth = 111000.00, // exactly 10%
      monthlyIncome = monthlyIncome,
      fixedExpensesTotal = fixedOverhead,
      totalDebt = 0.0,
      savingsBalance = 25000.0,
      primaryGoal = SavingsGoalEntity(1, "Reserves", 25000.0, 25000.0),
      allGoals = emptyList(),
      currency = VaultCurrency.USD
    )

    assertEquals(10.0f, roadmap.fiProgressPercent, 0.5f)
  }
}
