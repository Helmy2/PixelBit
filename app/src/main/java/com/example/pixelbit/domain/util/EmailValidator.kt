package com.example.pixelbit.domain.util

interface EmailValidator {
    fun isValid(email: String): Boolean
}

class EmailValidatorImpl : EmailValidator {
    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()

    override fun isValid(email: String): Boolean {
        return emailPattern.matches(email)
    }
}