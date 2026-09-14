package com.example.data.model

enum class MembershipTier(
    val id: String,
    val title: String,
    val tierLabel: String,
    val monthlyPrice: String,
    val annualPrice: String? = null,
    val priceSummary: String,
    val badgeName: String,
    val description: String,
    val perks: List<String>,
    val level: Int
) {
    FREE(
        id = "FREE",
        title = "The Vault",
        tierLabel = "Free Tier",
        monthlyPrice = "$0",
        annualPrice = null,
        priceSummary = "Free Forever",
        badgeName = "THE VAULT",
        description = "Core wealth fundamentals and daily discipline ledger",
        perks = listOf(
            "Core 50/30/20 Bank Ledger Slips",
            "Daily Safe Spend Calculation & Tracker",
            "Wealth Curriculum Lessons 1 through 7",
            "Daily Accountability Partner Check-in & Nudges"
        ),
        level = 0
    ),
    PRO(
        id = "PRO",
        title = "Vault Pro",
        tierLabel = "Pro Tier",
        monthlyPrice = "$7.99/mo",
        annualPrice = null,
        priceSummary = "$7.99/mo",
        badgeName = "VAULT PRO",
        description = "Advanced wealth curriculum & compounding simulator tools",
        perks = listOf(
            "Everything in Free Tier",
            "Complete 30-Day Master Wealth Curriculum (Lessons 8-30)",
            "Interactive Compounding Investing Simulator",
            "Multi-Goal Capital Bullion Reserves & Goal Visualizers",
            "Full CSV Bank Statement Import & Export"
        ),
        level = 1
    ),
    PREMIUM(
        id = "PREMIUM",
        title = "Vault+",
        tierLabel = "Premium Tier",
        monthlyPrice = "$19.99/mo",
        annualPrice = "$149/yr",
        priceSummary = "$19.99/mo or $149/yr",
        badgeName = "VAULT+",
        description = "Executive financial autonomy, personalized roadmap & VIP concierge",
        perks = listOf(
            "Everything in Vault Pro",
            "Personalized Financial Roadmap (FI Progress & Timeline)",
            "Monthly Progress Review & Certified Executive Report Card",
            "Priority Support Concierge Desk (<2h VIP Response)",
            "Executive Private Hub Early Access & Masterminds",
            "Unlimited AI Wealth Curator Deep Counsel"
        ),
        level = 2
    );

    companion object {
        fun fromId(id: String?): MembershipTier {
            return when (id?.uppercase()) {
                "PRO" -> PRO
                "PREMIUM", "VAULT+", "VAULT_PLUS" -> PREMIUM
                else -> FREE
            }
        }
    }
}
