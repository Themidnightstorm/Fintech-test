package com.example.util

import com.example.data.local.entity.SlipType
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID

data class ParsedCsvTransaction(
    val id: String = UUID.randomUUID().toString(),
    val rawDate: String,
    val timestamp: Long,
    val description: String,
    val amount: Double,
    val balance: Double? = null,
    val slipType: SlipType,
    val category: String,
    val isIncluded: Boolean = true
)

object CsvTransactionParser {

    private val DATE_FORMATS = listOf(
        SimpleDateFormat("yyyy-MM-dd", Locale.US),
        SimpleDateFormat("MM/dd/yyyy", Locale.US),
        SimpleDateFormat("dd/MM/yyyy", Locale.US),
        SimpleDateFormat("yyyy/MM/dd", Locale.US),
        SimpleDateFormat("MM-dd-yyyy", Locale.US),
        SimpleDateFormat("dd-MM-yyyy", Locale.US),
        SimpleDateFormat("MMM dd, yyyy", Locale.US),
        SimpleDateFormat("dd MMM yyyy", Locale.US),
        SimpleDateFormat("yyyyMMdd", Locale.US)
    )

    private val SAVINGS_KEYWORDS = listOf(
        "transfer", "saving", "save", "investment", "vanguard", "fidelity",
        "schwab", "robinhood", "deposit", "roth", "ira", "401k", "401(k)",
        "brokerage", "treasury", "bond", "yield", "crypto", "wealth",
        "capital", "stock", "etf", "dividend", "direct deposit", "paycheck",
        "salary", "income", "wire in", "interest", "reserves", "vault"
    )

    private val EXPENSES_KEYWORDS = listOf(
        "grocery", "groceries", "supermarket", "trader joe", "whole foods",
        "kroger", "safeway", "costco", "walmart", "aldi", "rent", "mortgage",
        "electric", "water", "gas", "utility", "utilities", "power",
        "internet", "wifi", "verizon", "at&t", "t-mobile", "comcast", "xfinity",
        "insurance", "geico", "progressive", "allstate", "state farm", "health",
        "medical", "rx", "pharmacy", "cvs", "walgreens", "doctor", "hospital",
        "dental", "tuition", "loan", "bill", "toll", "auto loan", "car payment",
        "tax", "irs", "housing", "hoa", "garbage", "sewer", "storage"
    )

    private val WANTS_KEYWORDS = listOf(
        "restaurant", "cafe", "coffee", "starbucks", "dunkin", "mcdonald",
        "chipotle", "uber", "lyft", "doordash", "grubhub", "seamless", "bar",
        "pub", "brewery", "cocktail", "wine", "liquor", "cinema", "movie",
        "theatre", "netflix", "spotify", "hulu", "disney", "hbo", "apple",
        "amazon", "shopping", "target", "nordstrom", "zara", "nike", "flight",
        "airline", "delta", "united", "hotel", "airbnb", "resort", "vacation",
        "entertainment", "steam", "playstation", "xbox", "nintendo", "spa",
        "massage", "gym", "fitness", "dining", "patisserie", "clothing",
        "apparel", "gaming", "concert", "ticket", "luxury", "boutique"
    )

    fun parseCsvStream(inputStream: InputStream): List<ParsedCsvTransaction> {
        val reader = BufferedReader(InputStreamReader(inputStream))
        val lines = reader.readLines()
        return parseCsvLines(lines)
    }

    fun parseCsvContent(csvContent: String): List<ParsedCsvTransaction> {
        val lines = csvContent.lines().filter { it.isNotBlank() }
        return parseCsvLines(lines)
    }

    fun parseCsvLines(lines: List<String>): List<ParsedCsvTransaction> {
        val result = mutableListOf<ParsedCsvTransaction>()
        if (lines.isEmpty()) return result

        var headerRowIndex = -1
        var dateCol = -1
        var descCol = -1
        var amountCol = -1
        var balanceCol = -1

        // 1. Scan for header line
        for (i in lines.indices) {
            val tokens = splitCsvLine(lines[i])
            val lowerTokens = tokens.map { it.lowercase().trim() }
            val hasDate = lowerTokens.any { it.contains("date") }
            val hasDesc = lowerTokens.any { it.contains("desc") || it.contains("payee") || it.contains("merchant") || it.contains("name") || it.contains("memo") || it.contains("title") }
            val hasAmount = lowerTokens.any { it.contains("amount") || it.contains("debit") || it.contains("credit") || it.contains("total") }

            if (hasDate && (hasDesc || hasAmount)) {
                headerRowIndex = i
                dateCol = lowerTokens.indexOfFirst { it.contains("date") }
                descCol = lowerTokens.indexOfFirst { it.contains("desc") || it.contains("payee") || it.contains("merchant") || it.contains("name") || it.contains("memo") || it.contains("title") }
                amountCol = lowerTokens.indexOfFirst { it.contains("amount") || it.contains("debit") || it.contains("credit") || it.contains("total") }
                balanceCol = lowerTokens.indexOfFirst { it.contains("balance") }
                break
            }
        }

        val startRow = if (headerRowIndex >= 0) headerRowIndex + 1 else 0

        // If no header found, default to standard standard bank layout: Date=0, Description=1, Amount=2, Balance=3
        if (dateCol == -1) dateCol = 0
        if (descCol == -1) descCol = 1
        if (amountCol == -1) amountCol = 2
        if (balanceCol == -1) balanceCol = 3

        for (i in startRow until lines.size) {
            val line = lines[i].trim()
            if (line.isBlank()) continue

            val tokens = splitCsvLine(line)
            if (tokens.isEmpty()) continue

            val rawDate = tokens.getOrNull(dateCol)?.trim() ?: "Today"
            val rawDesc = tokens.getOrNull(descCol)?.trim() ?: "Bank Transaction"
            val rawAmount = tokens.getOrNull(amountCol)?.trim() ?: "0.00"
            val rawBalance = if (balanceCol >= 0 && balanceCol < tokens.size) tokens[balanceCol].trim() else null

            val parsedAmount = parseAmount(rawAmount)
            if (parsedAmount == null || parsedAmount <= 0.0) {
                // If amount is 0 or unparseable, check if another column had the amount
                val fallbackAmount = tokens.mapNotNull { parseAmount(it) }.firstOrNull { it > 0.0 } ?: continue
                val (slip, category) = categorize(rawDesc)
                val timestamp = parseDateToTimestamp(rawDate)
                val balanceVal = rawBalance?.let { parseAmount(it) }
                result.add(
                    ParsedCsvTransaction(
                        rawDate = rawDate,
                        timestamp = timestamp,
                        description = rawDesc.ifBlank { "Bank Ledger Entry" },
                        amount = fallbackAmount,
                        balance = balanceVal,
                        slipType = slip,
                        category = category,
                        isIncluded = true
                    )
                )
                continue
            }

            val (slip, category) = categorize(rawDesc)
            val timestamp = parseDateToTimestamp(rawDate)
            val balanceVal = rawBalance?.let { parseAmount(it) }

            result.add(
                ParsedCsvTransaction(
                    rawDate = rawDate,
                    timestamp = timestamp,
                    description = rawDesc.ifBlank { "Bank Ledger Entry" },
                    amount = parsedAmount,
                    balance = balanceVal,
                    slipType = slip,
                    category = category,
                    isIncluded = true
                )
            )
        }

        return result
    }

    fun categorize(description: String): Pair<SlipType, String> {
        val lower = description.lowercase().trim()

        for (kw in SAVINGS_KEYWORDS) {
            if (lower.contains(kw)) {
                val cat = when {
                    lower.contains("investment") || lower.contains("vanguard") || lower.contains("fidelity") || lower.contains("stock") || lower.contains("etf") -> "Investment"
                    lower.contains("roth") || lower.contains("ira") || lower.contains("401k") -> "Retirement"
                    lower.contains("deposit") || lower.contains("paycheck") || lower.contains("salary") -> "Income Reserve"
                    else -> "Savings Transfer"
                }
                return Pair(SlipType.SAVINGS, cat)
            }
        }

        for (kw in EXPENSES_KEYWORDS) {
            if (lower.contains(kw)) {
                val cat = when {
                    lower.contains("grocery") || lower.contains("supermarket") || lower.contains("trader joe") || lower.contains("costco") || lower.contains("whole foods") -> "Groceries"
                    lower.contains("rent") || lower.contains("mortgage") || lower.contains("housing") -> "Housing"
                    lower.contains("electric") || lower.contains("water") || lower.contains("utility") || lower.contains("gas") || lower.contains("power") -> "Utilities"
                    lower.contains("internet") || lower.contains("wifi") || lower.contains("verizon") || lower.contains("t-mobile") -> "Telecom"
                    lower.contains("insurance") || lower.contains("geico") || lower.contains("allstate") -> "Insurance"
                    lower.contains("health") || lower.contains("pharmacy") || lower.contains("cvs") || lower.contains("medical") -> "Healthcare"
                    else -> "Essential Need"
                }
                return Pair(SlipType.EXPENSES, cat)
            }
        }

        for (kw in WANTS_KEYWORDS) {
            if (lower.contains(kw)) {
                val cat = when {
                    lower.contains("restaurant") || lower.contains("cafe") || lower.contains("coffee") || lower.contains("starbucks") || lower.contains("dining") -> "Dining Out"
                    lower.contains("uber") || lower.contains("lyft") -> "Ride Share"
                    lower.contains("netflix") || lower.contains("spotify") || lower.contains("cinema") || lower.contains("entertainment") -> "Entertainment"
                    lower.contains("amazon") || lower.contains("target") || lower.contains("shopping") || lower.contains("clothing") -> "Shopping"
                    lower.contains("flight") || lower.contains("airline") || lower.contains("hotel") || lower.contains("airbnb") -> "Travel & Leisure"
                    else -> "Discretionary Want"
                }
                return Pair(SlipType.WANTS, cat)
            }
        }

        // Default fallback heuristic:
        return Pair(SlipType.WANTS, "Discretionary Want")
    }

    private fun parseAmount(raw: String): Double? {
        val clean = raw.replace("$", "")
            .replace("€", "")
            .replace("£", "")
            .replace("¥", "")
            .replace("₹", "")
            .replace("A$", "")
            .replace("C$", "")
            .replace("CHF", "")
            .replace(",", "")
            .replace(" ", "")
            .replace("(", "-")
            .replace(")", "")
            .trim()
        val num = clean.toDoubleOrNull() ?: return null
        return Math.abs(num)
    }

    private fun parseDateToTimestamp(dateStr: String): Long {
        val trimmed = dateStr.trim()
        for (format in DATE_FORMATS) {
            try {
                val parsed = format.parse(trimmed)
                if (parsed != null) return parsed.time
            } catch (_: Exception) {}
        }
        return System.currentTimeMillis()
    }

    private fun splitCsvLine(line: String): List<String> {
        val tokens = mutableListOf<String>()
        var cur = StringBuilder()
        var inQuotes = false
        for (ch in line) {
            when (ch) {
                '"' -> inQuotes = !inQuotes
                ',' -> {
                    if (inQuotes) {
                        cur.append(ch)
                    } else {
                        tokens.add(cur.toString().trim())
                        cur = StringBuilder()
                    }
                }
                else -> cur.append(ch)
            }
        }
        tokens.add(cur.toString().trim())
        return tokens
    }

    fun getSampleCsvContent(): String {
        return """
Date,Description,Amount,Balance
2026-08-20,Vanguard S&P 500 ETF Transfer,1250.00,48920.00
2026-08-20,Whole Foods Market Grocery,142.80,47670.00
2026-08-19,Starbucks Reserve Roastery,18.50,47527.20
2026-08-19,Metropolitan Electric & Gas Utility,165.00,47362.20
2026-08-18,Uber Executive Ride,44.20,47318.00
2026-08-17,High-Yield Vault Automatic Savings Deposit,500.00,46818.00
2026-08-16,Nordstrom Luxury Apparel,195.00,46623.00
2026-08-15,CVS Health & Pharmacy Prescription,32.40,46590.60
2026-08-14,The Capital Grille Restaurant,185.00,46405.60
2026-08-13,Monthly Residential Rent & HOA,2400.00,44005.60
2026-08-12,Fidelity Roth IRA Contribution,400.00,43605.60
2026-08-11,Trader Joe's Organic Groceries,98.50,43507.10
        """.trimIndent()
    }
}
