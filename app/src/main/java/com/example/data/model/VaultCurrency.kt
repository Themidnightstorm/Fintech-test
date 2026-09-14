package com.example.data.model

import java.text.DecimalFormat
import kotlin.math.round

/**
 * Supported currencies for The Vault with real-world reference conversion parity.
 * Base parity is pegged relative to USD (1.00 USD).
 * - USD ($): US Dollar (1.00)
 * - EUR (€): Euro (0.92)
 * - GBP (£): British Pound (0.79)
 * - RUB (₽): Russian Ruble (90.00)
 * - JPY (¥): Japanese Yen (155.00)
 * - CAD (CA$): Canadian Dollar (1.36)
 * - AUD (AU$): Australian Dollar (1.52)
 */
enum class VaultCurrency(
    val code: String,
    val symbol: String,
    val displayName: String,
    val countryOrRegion: String,
    val example: String,
    val rateToUsd: Double,
    val hasDecimals: Boolean = true
) {
    USD(
        code = "USD",
        symbol = "$",
        displayName = "US Dollar",
        countryOrRegion = "United States",
        example = "$1,250.00",
        rateToUsd = 1.00,
        hasDecimals = true
    ),
    EUR(
        code = "EUR",
        symbol = "€",
        displayName = "Euro",
        countryOrRegion = "European Union",
        example = "€1,150.00",
        rateToUsd = 0.92,
        hasDecimals = true
    ),
    GBP(
        code = "GBP",
        symbol = "£",
        displayName = "British Pound",
        countryOrRegion = "United Kingdom",
        example = "£987.50",
        rateToUsd = 0.79,
        hasDecimals = true
    ),
    RUB(
        code = "RUB",
        symbol = "₽",
        displayName = "Russian Ruble",
        countryOrRegion = "Russia",
        example = "₽112,500.00",
        rateToUsd = 90.00,
        hasDecimals = true
    ),
    JPY(
        code = "JPY",
        symbol = "¥",
        displayName = "Japanese Yen",
        countryOrRegion = "Japan",
        example = "¥193,750",
        rateToUsd = 155.00,
        hasDecimals = false
    ),
    CAD(
        code = "CAD",
        symbol = "CA$",
        displayName = "Canadian Dollar",
        countryOrRegion = "Canada",
        example = "CA$1,700.00",
        rateToUsd = 1.36,
        hasDecimals = true
    ),
    AUD(
        code = "AUD",
        symbol = "AU$",
        displayName = "Australian Dollar",
        countryOrRegion = "Australia",
        example = "AU$1,900.00",
        rateToUsd = 1.52,
        hasDecimals = true
    );

    /**
     * Converts a monetary amount from this currency into a target currency.
     */
    fun convertTo(amount: Double, target: VaultCurrency): Double {
        if (this == target) return amount
        if (amount == 0.0) return 0.0
        val inUsd = amount / this.rateToUsd
        val converted = inUsd * target.rateToUsd
        return if (target.hasDecimals) {
            round(converted * 100.0) / 100.0
        } else {
            round(converted)
        }
    }

    /**
     * Formats the monetary amount with the currency symbol and appropriate decimal places.
     */
    fun format(amount: Double, decimals: Boolean = true): String {
        val df = if (hasDecimals && decimals) DecimalFormat("#,##0.00") else DecimalFormat("#,##0")
        val numStr = df.format(amount)
        return "$symbol$numStr"
    }

    /**
     * Formats large amounts rounded to integer without decimals.
     */
    fun formatCompact(amount: Double): String {
        val df = DecimalFormat("#,##0")
        val numStr = df.format(amount)
        return "$symbol$numStr"
    }

    companion object {
        fun fromCode(code: String?): VaultCurrency {
            if (code == null) return USD
            return entries.find { it.code.equals(code.trim(), ignoreCase = true) } ?: USD
        }

        fun convert(amount: Double, from: VaultCurrency, to: VaultCurrency): Double {
            return from.convertTo(amount, to)
        }
    }
}

