package com.example.pixelbit.domain.util

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class EmailValidatorImplTest {

    private lateinit var emailValidator: EmailValidator

    @Before
    fun setUp() {
        emailValidator = EmailValidatorImpl()
    }

    // Valid email tests
    @Test
    fun `test valid simple email returns true`() {
        val validEmail = "test@example.com"
        assertThat(emailValidator.isValid(validEmail)).isTrue()
    }

    @Test
    fun `test valid email with dots in local part returns true`() {
        val validEmail = "test.user@example.com"
        assertThat(emailValidator.isValid(validEmail)).isTrue()
    }

    @Test
    fun `test valid email with underscores returns true`() {
        val validEmail = "test_user@example.com"
        assertThat(emailValidator.isValid(validEmail)).isTrue()
    }

    @Test
    fun `test valid email with hyphens returns true`() {
        val validEmail = "test-user@example.com"
        assertThat(emailValidator.isValid(validEmail)).isTrue()
    }

    @Test
    fun `test valid email with numbers returns true`() {
        val validEmail = "user123@example.com"
        assertThat(emailValidator.isValid(validEmail)).isTrue()
    }

    @Test
    fun `test valid email with multiple dots in domain returns false`() {
        val validEmail = "test@mail.example.com"
        assertThat(emailValidator.isValid(validEmail)).isFalse()
    }

    @Test
    fun `test valid email with two letter TLD returns true`() {
        val validEmail = "test@example.co"
        assertThat(emailValidator.isValid(validEmail)).isTrue()
    }

    @Test
    fun `test valid email with three letter TLD returns true`() {
        val validEmail = "test@example.org"
        assertThat(emailValidator.isValid(validEmail)).isTrue()
    }

    @Test
    fun `test valid email with mixed special characters returns true`() {
        val validEmail = "user.name-123@example.com"
        assertThat(emailValidator.isValid(validEmail)).isTrue()
    }

    // Invalid email tests
    @Test
    fun `test empty string returns false`() {
        assertThat(emailValidator.isValid("")).isFalse()
    }

    @Test
    fun `test email without at symbol returns false`() {
        val invalidEmail = "testexample.com"
        assertThat(emailValidator.isValid(invalidEmail)).isFalse()
    }

    @Test
    fun `test email without domain returns false`() {
        val invalidEmail = "test@"
        assertThat(emailValidator.isValid(invalidEmail)).isFalse()
    }

    @Test
    fun `test email without local part returns false`() {
        val invalidEmail = "@example.com"
        assertThat(emailValidator.isValid(invalidEmail)).isFalse()
    }

    @Test
    fun `test email without TLD returns false`() {
        val invalidEmail = "test@example"
        assertThat(emailValidator.isValid(invalidEmail)).isFalse()
    }

    @Test
    fun `test email with spaces returns false`() {
        val invalidEmail = "test user@example.com"
        assertThat(emailValidator.isValid(invalidEmail)).isFalse()
    }

    @Test
    fun `test email with multiple at symbols returns false`() {
        val invalidEmail = "test@@example.com"
        assertThat(emailValidator.isValid(invalidEmail)).isFalse()
    }

    @Test
    fun `test email with capital letters in domain returns false`() {
        // Based on the regex pattern [a-z]+ (lowercase only)
        val invalidEmail = "test@Example.com"
        assertThat(emailValidator.isValid(invalidEmail)).isFalse()
    }

    @Test
    fun `test email with special characters not in pattern returns false`() {
        val invalidEmail = "test#user@example.com"
        assertThat(emailValidator.isValid(invalidEmail)).isFalse()
    }

    @Test
    fun `test email with plus sign returns false`() {
        // Pattern doesn't include + symbol
        val invalidEmail = "test+user@example.com"
        assertThat(emailValidator.isValid(invalidEmail)).isFalse()
    }

    @Test
    fun `test email domain without dot returns false`() {
        val invalidEmail = "test@examplecom"
        assertThat(emailValidator.isValid(invalidEmail)).isFalse()
    }

    @Test
    fun `test email with only domain no TLD returns false`() {
        val invalidEmail = "test@example."
        assertThat(emailValidator.isValid(invalidEmail)).isFalse()
    }

    @Test
    fun `test plain text returns false`() {
        val invalidEmail = "notanemail"
        assertThat(emailValidator.isValid(invalidEmail)).isFalse()
    }

    @Test
    fun `test email with parentheses returns false`() {
        val invalidEmail = "test(user)@example.com"
        assertThat(emailValidator.isValid(invalidEmail)).isFalse()
    }

    @Test
    fun `test email with brackets returns false`() {
        val invalidEmail = "test[user]@example.com"
        assertThat(emailValidator.isValid(invalidEmail)).isFalse()
    }

    // Edge cases
    @Test
    fun `test single character local part returns true`() {
        val validEmail = "a@example.com"
        assertThat(emailValidator.isValid(validEmail)).isTrue()
    }

    @Test
    fun `test long email address returns true`() {
        val validEmail = "verylongemailaddresswithmanycharacters123@example.com"
        assertThat(emailValidator.isValid(validEmail)).isTrue()
    }

    @Test
    fun `test email with numbers in domain returns false`() {
        // Pattern uses [a-z]+ which doesn't include numbers
        val invalidEmail = "test@example123.com"
        assertThat(emailValidator.isValid(invalidEmail)).isFalse()
    }

    @Test
    fun `test multiple valid emails`() {
        val validEmails = listOf(
            "user@example.com",
            "john.doe@company.org",
            "admin_123@site.net",
            "contact-us@example.co"
        )

        validEmails.forEach { email ->
            assertThat(emailValidator.isValid(email)).isTrue()
        }
    }

    @Test
    fun `test multiple invalid emails`() {
        val invalidEmails = listOf(
            "",
            "notanemail",
            "@example.com",
            "test@",
            "test @example.com",
            "test@example",
            "test@@example.com"
        )

        invalidEmails.forEach { email ->
            assertThat(emailValidator.isValid(email)).isFalse()
        }
    }

    @Test
    fun `test email with whitespace at ends returns false`() {
        val invalidEmails = listOf(
            " test@example.com",
            "test@example.com ",
            " test@example.com "
        )

        invalidEmails.forEach { email ->
            assertThat(emailValidator.isValid(email)).isFalse()
        }
    }
}
