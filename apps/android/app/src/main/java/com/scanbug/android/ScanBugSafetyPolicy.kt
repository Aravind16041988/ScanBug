package com.scanbug.android

/**
 * Conservative local guard for manually requested UI actions.
 *
 * This is a heuristic, not a guarantee that an action is safe. The user must
 * still inspect the screen and only test apps/actions they are authorized to use.
 */
internal object ScanBugSafetyPolicy {
    private val blockedTerms = listOf(
        "pay", "payment", "transfer", "delete", "remove", "purchase", "buy",
        "logout", "log out", "sign out", "confirm", "submit", "send",
        "withdraw", "close account", "place order"
    )

    fun isPotentiallyDestructive(label: String): Boolean {
        val normalized = label.lowercase().replace(Regex("\\s+"), " ").trim()
        return blockedTerms.any { term ->
            Regex("(^|[^a-z])" + Regex.escape(term) + "([^a-z]|$)").containsMatchIn(normalized)
        }
    }
}
