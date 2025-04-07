package com.example.autokiosk.utils

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ValidationUtilsTest {

    @Test
    fun validEmailReturnsTrue() {
        assertTrue(ValidationUtils.isValidEmail("emem@mail.com"))
    }

    @Test
    fun invalidEmailReturnsFalse() {
        assertFalse(ValidationUtils.isValidEmail("invalid-email"))
        assertFalse(ValidationUtils.isValidEmail("emem@.com"))
        assertFalse(ValidationUtils.isValidEmail("em@com"))
        assertFalse(ValidationUtils.isValidEmail(""))
    }

    @Test
    fun validPasswordReturnsTrue() {
        assertTrue(ValidationUtils.isValidPassword("123456"))
        assertTrue(ValidationUtils.isValidPassword("password111"))
    }

    @Test
    fun invalidPasswordReturnsFalse() {
        assertFalse(ValidationUtils.isValidPassword("12345"))
        assertFalse(ValidationUtils.isValidPassword(""))
    }
}
