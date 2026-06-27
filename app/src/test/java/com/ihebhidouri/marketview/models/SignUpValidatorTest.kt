package com.ihebhidouri.marketview.models

import org.junit.Assert.assertNull
import org.junit.Assert.assertNotNull
import org.junit.Test

class SignUpValidatorTest {

    private val validator = SignUpValidator()

    // Username tests

    @Test
    fun username_blank_returnsError() {
        val result = validator.validateUsername("")
        assertNotNull(result)
    }

    @Test
    fun username_tooShort_returnsError() {
        val result = validator.validateUsername("abc")
        assertNotNull(result)
    }

    @Test
    fun username_exactlyFiveChars_returnsNull() {
        val result = validator.validateUsername("abcde")
        assertNull(result)
    }

    @Test
    fun username_specialChars_returnsError() {
        val result = validator.validateUsername("user@name")
        assertNotNull(result)
    }

    @Test
    fun username_validWithUnderscore_returnsNull() {
        val result = validator.validateUsername("user_123")
        assertNull(result)
    }

    // Password tests

    @Test
    fun password_tooShort_returnsError() {
        val result = validator.validatePassword("Ab1@")
        assertNotNull(result)
    }

    @Test
    fun password_noUppercase_returnsError() {
        val result = validator.validatePassword("abcdefg1@")
        assertNotNull(result)
    }

    @Test
    fun password_noLowercase_returnsError() {
        val result = validator.validatePassword("ABCDEFG1@")
        assertNotNull(result)
    }

    @Test
    fun password_noDigit_returnsError() {
        val result = validator.validatePassword("Abcdefgh@")
        assertNotNull(result)
    }

    @Test
    fun password_noSpecialChar_returnsError() {
        val result = validator.validatePassword("Abcdefg1")
        assertNotNull(result)
    }

    @Test
    fun password_valid_returnsNull() {
        val result = validator.validatePassword("Test@1234")
        assertNull(result)
    }

    // Full validation tests

    @Test
    fun validate_validUsernameAndPassword_noErrors() {
        assertNull(validator.validateUsername("validuser"))
        assertNull(validator.validatePassword("Test@1234"))
    }

    @Test
    fun validate_invalidUsernameAndPassword_bothReturnErrors() {
        assertNotNull(validator.validateUsername("ab"))
        assertNotNull(validator.validatePassword("weak"))
    }
}