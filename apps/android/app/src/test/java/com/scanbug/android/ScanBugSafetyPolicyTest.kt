package com.scanbug.android

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ScanBugSafetyPolicyTest {
    @Test
    fun blocksSensitiveAndDestructiveLabels() {
        listOf("Pay now", "TRANSFER", "Delete account", "Please submit", "Sign out", "Place order")
            .forEach { assertTrue("Expected '$it' to be blocked", ScanBugSafetyPolicy.isPotentiallyDestructive(it)) }
    }

    @Test
    fun allowsOrdinaryNavigationLabels() {
        listOf("Continue", "History", "Display", "Settings")
            .forEach { assertFalse("Expected '$it' to be allowed", ScanBugSafetyPolicy.isPotentiallyDestructive(it)) }
    }

    @Test
    fun matchesWholeWordsRatherThanSubstrings() {
        assertFalse(ScanBugSafetyPolicy.isPotentiallyDestructive("Display"))
        assertTrue(ScanBugSafetyPolicy.isPotentiallyDestructive("Delete item"))
    }

    @Test
    fun normalizesCaseAndWhitespace() {
        assertTrue(ScanBugSafetyPolicy.isPotentiallyDestructive("  SIGN   OUT  "))
    }
}
