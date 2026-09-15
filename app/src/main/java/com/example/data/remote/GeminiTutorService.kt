package com.example.data.remote

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale
import java.util.concurrent.TimeUnit

data class CuratorFinancialContext(
    val currencyCode: String = "USD",
    val monthlyIncome: Double = 0.0,
    val hasFixedIncome: Boolean = true,
    val checkingBalance: Double = 0.0,
    val savingsBalance: Double = 0.0,
    val totalDebt: Double = 0.0,
    val fixedExpensesTotal: Double = 0.0,
    val dailySafeSpend: Double = 0.0,
    val netWorth: Double = 0.0,
    val savingsGoals: List<String> = emptyList(),
    val recentTransactions: List<String> = emptyList()
)

data class SentinelWatchContext(
    val currencyCode: String = "USD",
    val dailySafeSpend: Double = 0.0,
    val streakDays: Int = 0,
    val checkedInToday: Boolean = false
)

class GeminiTutorService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private fun fmt(value: Double): String = String.format(Locale.US, "%.2f", value)

    private fun buildSystemPrompt(context: CuratorFinancialContext?): String {
        val basePrompt = """
            You are 'The Curator' â€” an executive private wealth advisor and personal banking concierge for 'The Vault'.
            Your mission is to provide sophisticated, crystal-clear, plain English financial counsel, tailored specifically to the user's real portfolio.
            Avoid dry academic jargon. When using financial terms (like 'index fund', 'liquidity', or 'compound interest'), explain them with clear, relatable everyday examples.
            Focus on practical, actionable private wealth discipline:
            1. Protecting capital and avoiding volatile gambles.
            2. Consistent monthly compounding and automated reserves.
            3. The 50/30/20 budget slip discipline (50% fixed overhead, 30% savings/wealth compounding, 20% safe discretionary spend).
            4. Maintaining a 3-6 month emergency reserve in high-yield vaults and eliminating high-interest liabilities.
            5. Disciplined, steady index fund allocation.
        """.trimIndent()

        if (context == null) return basePrompt

        val curr = context.currencyCode
        val goalsFormatted = if (context.savingsGoals.isNotEmpty()) {
            context.savingsGoals.joinToString("\nâ€¢ ")
        } else {
            "General Reserve Vault active"
        }

        val txFormatted = if (context.recentTransactions.isNotEmpty()) {
            context.recentTransactions.joinToString("\nâ€¢ ")
        } else {
            "No ledger entries recorded yet"
        }

        return """
            $basePrompt

            CONFIDENTIAL USER FINANCIAL DOSSIER (LIVE ACCOUNT BALANCES & LEDGER DATA):
            â€¢ Active Currency: $curr
            â€¢ Monthly Income: $curr ${fmt(context.monthlyIncome)} (${if (context.hasFixedIncome) "Fixed Salary" else "Variable / Freelance"})
            â€¢ Liquid Checking Balance: $curr ${fmt(context.checkingBalance)}
            â€¢ Total Savings Reserves: $curr ${fmt(context.savingsBalance)}
            â€¢ Outstanding Debt / Liabilities: $curr ${fmt(context.totalDebt)}
            â€¢ Total Net Worth (Savings + Checking - Debt): $curr ${fmt(context.netWorth)}
            â€¢ Daily Safe Spend Benchmark: $curr ${fmt(context.dailySafeSpend)} / day
            â€¢ Mandatory Monthly Fixed Bills / Overhead: $curr ${fmt(context.fixedExpensesTotal)} / month
            â€¢ Active Wealth & Savings Goals:
            â€¢ $goalsFormatted
            â€¢ Recent Bank Ledger Slips & CSV Imports:
            â€¢ $txFormatted

            CRITICAL DIRECTIVES:
            1. You have direct, authorized access to the user's live Vault numbers and transactions listed above.
            2. When the user asks "How much money do I have?", "What is my balance?", "What is my net worth?", "Can I afford this?", or asks about recent transactions, ALWAYS cite their specific numbers from the dossier above. NEVER tell the user that you don't have access to their accounts or balances.
            3. Address the user with polished luxury banking etiquette, calm reassurance, and clarity. Keep answers under 160 words, using clean bullet points where appropriate.
        """.trimIndent()
    }

    private fun buildSentinelPrompt(watch: SentinelWatchContext): String {
        val checkInLine = if (watch.checkedInToday) "recorded" else "STILL OPEN â€” remind the user gently"
        return """
            You are 'The Sentinel' â€” the watch on the ramparts of The Vault, an AI companion who guards the user's daily money discipline and streak.
            Voice: vigilant, terse, honorable; a castle watchman crossed with a private bank guard. You are explicitly an AI companion of The Vault; never claim to be a human being.
            Rules:
            1. Keep replies under 60 words.
            2. Reference the user's real numbers from the watch dossier below (streak, daily safe spend, check-in status).
            3. If the user confesses a budget slip, respond with steadiness, not shame: name the breach, note the wall still holds, set tomorrow's watch.
            4. Celebrate streaks and check-ins with quiet pride, not confetti.
            5. End most replies with a short watch phrase, e.g. "The watch holds." or "We hold the line tomorrow."
        """.trimIndent() + """

            WATCH DOSSIER:
            â€¢ Active Currency: ${watch.currencyCode}
            â€¢ Daily Safe Spend: ${watch.currencyCode} ${fmt(watch.dailySafeSpend)} / day
            â€¢ Current Streak: ${watch.streakDays} days
            â€¢ Today's Check-In: $checkInLine
        """.trimIndent()
    }

    private suspend fun generate(
        systemPrompt: String,
        userQuestion: String,
        conversationHistory: List<Pair<String, String>>
    ): String? = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext null
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"

            val contentsArray = JSONArray()
            for ((sender, text) in conversationHistory.takeLast(6)) {
                val role = if (sender == "USER") "user" else "model"
                val partObj = JSONObject().put("text", text)
                val contentObj = JSONObject()
                    .put("role", role)
                    .put("parts", JSONArray().put(partObj))
                contentsArray.put(contentObj)
            }

            val currentPart = JSONObject().put("text", userQuestion)
            val currentContent = JSONObject()
                .put("role", "user")
                .put("parts", JSONArray().put(currentPart))
            contentsArray.put(currentContent)

            val rootJson = JSONObject()
            rootJson.put("contents", contentsArray)

            val systemInstructionJson = JSONObject()
                .put("parts", JSONArray().put(JSONObject().put("text", systemPrompt)))
            rootJson.put("systemInstruction", systemInstructionJson)

            val generationConfig = JSONObject()
                .put("temperature", 0.6)
                .put("topP", 0.9)
            rootJson.put("generationConfig", generationConfig)

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = rootJson.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .url(url)
                .header("x-goog-api-key", apiKey)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) return@withContext null

            val responseJson = JSONObject(responseBody)
            val candidates = responseJson.optJSONArray("candidates") ?: return@withContext null
            if (candidates.length() == 0) return@withContext null
            val content = candidates.getJSONObject(0).optJSONObject("content") ?: return@withContext null
            val parts = content.optJSONArray("parts") ?: return@withContext null
            if (parts.length() == 0) return@withContext null
            parts.getJSONObject(0).optString("text", "").takeIf { it.isNotBlank() }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun askTutor(
        userQuestion: String,
        conversationHistory: List<Pair<String, String>>,
        financialContext: CuratorFinancialContext? = null
    ): String =
        generate(buildSystemPrompt(financialContext), userQuestion, conversationHistory)
            ?: getOfflineAristocraticWisdom(userQuestion, financialContext)

    suspend fun askSentinel(
        userMessage: String,
        conversationHistory: List<Pair<String, String>>,
        watch: SentinelWatchContext
    ): String =
        generate(buildSentinelPrompt(watch), userMessage, conversationHistory)
            ?: getSentinelWatchReply(userMessage, watch)

    private fun getSentinelWatchReply(message: String, watch: SentinelWatchContext): String {
        val q = message.lowercase()
        val curr = watch.currencyCode
        return when {
            q.contains("blew") || q.contains("overspent") || q.contains("slip") || q.contains("failed") ->
                "A breach, then: the line was $curr ${fmt(watch.dailySafeSpend)} and it crossed. Not a catastrophe â€” one slip against a ${watch.streakDays}-day watch. Log it, learn its shape, and we hold the line tomorrow."
            q.contains("streak") || q.contains("check") ->
                "The watch holds: ${watch.streakDays} days standing. ${if (watch.checkedInToday) "Today's check-in is recorded." else "Today's check-in is still open â€” report when ready."}"
            q.contains("spend") || q.contains("allowance") ->
                "Today's allowance stands at $curr ${fmt(watch.dailySafeSpend)}. Within it, your fixed bills and savings slips stay fully funded. Spend as a friend of the Vault."
            else ->
                "The Sentinel reports: streak ${watch.streakDays} days, daily safe spend $curr ${fmt(watch.dailySafeSpend)}. The ramparts are quiet. Send word when you need the watch."
        }
    }

    private fun getOfflineAristocraticWisdom(question: String, context: CuratorFinancialContext?): String {
        val q = question.lowercase()
        val curr = context?.currencyCode ?: "USD"
        val netWorth = context?.netWorth ?: 0.0
        val checking = context?.checkingBalance ?: 0.0
        val savings = context?.savingsBalance ?: 0.0
        val debt = context?.totalDebt ?: 0.0
        val safeSpend = context?.dailySafeSpend ?: 0.0

        return when {
            q.contains("how much") || q.contains("balance") || q.contains("my money") || q.contains("account") || q.contains("cash") -> {
                "According to your private Vault ledger:\n\n" +
                "â€¢ Liquid Checking: $curr ${fmt(checking)}\n" +
                "â€¢ Total Savings Reserves: $curr ${fmt(savings)}\n" +
                "â€¢ Outstanding Debt: $curr ${fmt(debt)}\n" +
                "â€¢ Total Net Worth: $curr ${fmt(netWorth)}\n" +
                "â€¢ Daily Safe Spend: $curr ${fmt(safeSpend)}/day\n\n" +
                "Your capital is allocated across your 50/30/20 slips."
            }
            q.contains("safe spend") || q.contains("spend today") || q.contains("daily") -> {
                "Your Daily Safe Spend is currently $curr ${fmt(safeSpend)} per day. Spending within this amount guarantees that your fixed expenses and 30% savings goals remain completely funded."
            }
            q.contains("net worth") || q.contains("worth") -> {
                "Your Net Worth is $curr ${fmt(netWorth)}. In plain English: it's what you own ($curr ${fmt(savings + checking)}) minus what you owe ($curr ${fmt(debt)})."
            }
            q.contains("transaction") || q.contains("csv") || q.contains("slip") || q.contains("recent") -> {
                if (context != null && context.recentTransactions.isNotEmpty()) {
                    val recent = context.recentTransactions.take(4).joinToString("\nâ€¢ ")
                    "Here are your latest recorded ledger entries:\n\nâ€¢ $recent\n\nAll imported bank slips are categorized automatically into Expenses, Savings, or Wants."
                } else {
                    "No transactions recorded yet in your ledger. You can import bank CSV statements or record entries manually from the dashboard."
                }
            }
            q.contains("invest") || q.contains("stock") || q.contains("market") -> {
                "Investing means buying a tiny piece of real, profitable companies. The easiest way for most people to start is buying a low-cost S&P 500 index fund (like VOO or SPY), which spreads your money across 500 top companies all at once. Invest regularly and give it years to grow!"
            }
            q.contains("save") || q.contains("emergency") || q.contains("reserve") -> {
                "An emergency fund is your personal safety net. Try to save 3 to 6 months of basic living expenses in a high-yield savings account. This keeps you safe from unexpected repairs or medical bills without needing credit cards."
            }
            q.contains("expense") || q.contains("budget") || q.contains("want") -> {
                "A great rule of thumb is the 50/30/20 budget: 50% for your needs (rent, groceries, bills), 30% for your savings and debt payments, and 20% for fun money (eating out, hobbies). This gives you balance and peace of mind!"
            }
            q.contains("debt") || q.contains("credit") -> {
                "High-interest debt (like credit cards with 20%+ APR) costs you a ton of money. Focus on paying minimums on everything, and put all extra cash toward the card with the highest interest rate until it reaches zero!"
            }
            else -> {
                "Greetings. I am The Curator, your executive private wealth advisor. Ask me anything about your balances, daily safe spend, debt elimination, or long-term compounding. What can I assist you with today?"
            }
        }
    }
}    private fun buildSystemPrompt(context: CuratorFinancialContext?): String {
        val basePrompt = """
            You are 'The Curator' — an executive private wealth advisor and personal banking concierge for 'The Vault'.
            Your mission is to provide sophisticated, crystal-clear, plain English financial counsel, tailored specifically to the user's real portfolio.
            Avoid dry academic jargon. When using financial terms (like 'index fund', 'liquidity', or 'compound interest'), explain them with clear, relatable everyday examples.
            Focus on practical, actionable private wealth discipline:
            1. Protecting capital and avoiding volatile gambles.
            2. Consistent monthly compounding and automated reserves.
            3. The 50/30/20 budget slip discipline (50% fixed overhead, 30% savings/wealth compounding, 20% safe discretionary spend).
            4. Maintaining a 3-6 month emergency reserve in high-yield vaults and eliminating high-interest liabilities.
            5. Disciplined, steady index fund allocation.
        """.trimIndent()

        if (context == null) return basePrompt

        val curr = context.currencyCode
        val goalsFormatted = if (context.savingsGoals.isNotEmpty()) {
            context.savingsGoals.joinToString("\n• ")
        } else {
            "General Reserve Vault active"
        }

        val txFormatted = if (context.recentTransactions.isNotEmpty()) {
            context.recentTransactions.joinToString("\n• ")
        } else {
            "No ledger entries recorded yet"
        }

        return """
            $basePrompt

            CONFIDENTIAL USER FINANCIAL DOSSIER (LIVE ACCOUNT BALANCES & LEDGER DATA):
            • Active Currency: $curr
            • Monthly Income: $curr ${String.format(Locale.US, "%.2f", context.monthlyIncome)} (${if (context.hasFixedIncome) "Fixed Salary" else "Variable / Freelance"})
            • Liquid Checking Balance: $curr ${String.format(Locale.US, "%.2f", context.checkingBalance)}
            • Total Savings Reserves: $curr ${String.format(Locale.US, "%.2f", context.savingsBalance)}
            • Outstanding Debt / Liabilities: $curr ${String.format(Locale.US, "%.2f", context.totalDebt)}
            • Total Net Worth (Savings + Checking - Debt): $curr ${String.format(Locale.US, "%.2f", context.netWorth)}
            • Daily Safe Spend Benchmark: $curr ${String.format(Locale.US, "%.2f", context.dailySafeSpend)} / day
            • Mandatory Monthly Fixed Bills / Overhead: $curr ${String.format(Locale.US, "%.2f", context.fixedExpensesTotal)} / month
            • Active Wealth & Savings Goals:
            • $goalsFormatted
            • Recent Bank Ledger Slips & CSV Imports:
            • $txFormatted

            CRITICAL DIRECTIVES:
            1. You have direct, authorized access to the user's live Vault numbers and transactions listed above.
            2. When the user asks "How much money do I have?", "What is my balance?", "What is my net worth?", "Can I afford this?", or asks about recent transactions, ALWAYS cite their specific numbers from the dossier above. NEVER tell the user that you don't have access to their accounts or balances.
            3. Address the user with polished luxury banking etiquette, calm reassurance, and clarity. Keep answers under 160 words, using clean bullet points where appropriate.
        """.trimIndent()
    }

    suspend fun askTutor(
        userQuestion: String,
        conversationHistory: List<Pair<String, String>>,
        financialContext: CuratorFinancialContext? = null
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getOfflineAristocraticWisdom(userQuestion, financialContext)
        }

        try {
               val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"

            val contentsArray = JSONArray()

            // Prior conversation history (last 6 turns)
            for ((sender, text) in conversationHistory.takeLast(6)) {
                val role = if (sender == "USER") "user" else "model"
                val partObj = JSONObject().put("text", text)
                val contentObj = JSONObject()
                    .put("role", role)
                    .put("parts", JSONArray().put(partObj))
                contentsArray.put(contentObj)
            }

            // Current user query
            val currentPart = JSONObject().put("text", userQuestion)
            val currentContent = JSONObject()
                .put("role", "user")
                .put("parts", JSONArray().put(currentPart))
            contentsArray.put(currentContent)

            val rootJson = JSONObject()
            rootJson.put("contents", contentsArray)

            val systemInstructionJson = JSONObject()
                .put("parts", JSONArray().put(JSONObject().put("text", buildSystemPrompt(financialContext))))
            rootJson.put("systemInstruction", systemInstructionJson)

            val generationConfig = JSONObject()
                .put("temperature", 0.6)
                .put("topP", 0.9)
            rootJson.put("generationConfig", generationConfig)

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = rootJson.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .url(url)
                .header("x-goog-api-key", apiKey)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                return@withContext getOfflineAristocraticWisdom(userQuestion, financialContext)
            }

            val responseJson = JSONObject(responseBody)
            val candidates = responseJson.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val firstCandidate = candidates.getJSONObject(0)
                val content = firstCandidate.getJSONObject("content")
                val parts = content.getJSONArray("parts")
                if (parts.length() > 0) {
                    return@withContext parts.getJSONObject(0).getString("text")
                }
            }
            getOfflineAristocraticWisdom(userQuestion, financialContext)
        } catch (e: Exception) {
            getOfflineAristocraticWisdom(userQuestion, financialContext)
        }
    }

    private fun getOfflineAristocraticWisdom(question: String, context: CuratorFinancialContext?): String {
        val q = question.lowercase()
        val curr = context?.currencyCode ?: "USD"
        val netWorth = context?.netWorth ?: 0.0
        val checking = context?.checkingBalance ?: 0.0
        val savings = context?.savingsBalance ?: 0.0
        val debt = context?.totalDebt ?: 0.0
        val safeSpend = context?.dailySafeSpend ?: 0.0

        return when {
            q.contains("how much") || q.contains("balance") || q.contains("my money") || q.contains("account") || q.contains("cash") -> {
                "According to your private Vault ledger:\n\n" +
                "• Liquid Checking: $curr ${String.format(Locale.US, "%.2f", checking)}\n" +
                "• Total Savings Reserves: $curr ${String.format(Locale.US, "%.2f", savings)}\n" +
                "• Outstanding Debt: $curr ${String.format(Locale.US, "%.2f", debt)}\n" +
                "• Total Net Worth: $curr ${String.format(Locale.US, "%.2f", netWorth)}\n" +
                "• Daily Safe Spend: $curr ${String.format(Locale.US, "%.2f", safeSpend)}/day\n\n" +
                "Your capital is allocated across your 50/30/20 slips."
            }
            q.contains("safe spend") || q.contains("spend today") || q.contains("daily") -> {
                "Your Daily Safe Spend is currently $curr ${String.format(Locale.US, "%.2f", safeSpend)} per day. Spending within this amount guarantees that your fixed expenses and 30% savings goals remain completely funded."
            }
            q.contains("net worth") || q.contains("worth") -> {
                "Your Net Worth is $curr ${String.format(Locale.US, "%.2f", netWorth)}. In plain English: it's what you own ($curr ${String.format(Locale.US, "%.2f", savings + checking)}) minus what you owe ($curr ${String.format(Locale.US, "%.2f", debt)})."
            }
            q.contains("transaction") || q.contains("csv") || q.contains("slip") || q.contains("recent") -> {
                if (context != null && context.recentTransactions.isNotEmpty()) {
                    val recent = context.recentTransactions.take(4).joinToString("\n• ")
                    "Here are your latest recorded ledger entries:\n\n• $recent\n\nAll imported bank slips are categorized automatically into Expenses, Savings, or Wants."
                } else {
                    "No transactions recorded yet in your ledger. You can import bank CSV statements or record entries manually from the dashboard."
                }
            }
            q.contains("invest") || q.contains("stock") || q.contains("market") -> {
                "Investing means buying a tiny piece of real, profitable companies. The easiest way for most people to start is buying a low-cost S&P 500 index fund (like VOO or SPY), which spreads your money across 500 top companies all at once. Invest regularly and give it years to grow!"
            }
            q.contains("save") || q.contains("emergency") || q.contains("reserve") -> {
                "An emergency fund is your personal safety net. Try to save 3 to 6 months of basic living expenses in a high-yield savings account. This keeps you safe from unexpected repairs or medical bills without needing credit cards."
            }
            q.contains("expense") || q.contains("budget") || q.contains("want") -> {
                "A great rule of thumb is the 50/30/20 budget: 50% for your needs (rent, groceries, bills), 30% for your savings and debt payments, and 20% for fun money (eating out, hobbies). This gives you balance and peace of mind!"
            }
            q.contains("debt") || q.contains("credit") -> {
                "High-interest debt (like credit cards with 20%+ APR) costs you a ton of money. Focus on paying minimums on everything, and put all extra cash toward the card with the highest interest rate until it reaches zero!"
            }
            else -> {
                "Greetings. I am The Curator, your executive private wealth advisor. Ask me anything about your balances, daily safe spend, debt elimination, or long-term compounding. What can I assist you with today?"
            }
        }
    }
}
