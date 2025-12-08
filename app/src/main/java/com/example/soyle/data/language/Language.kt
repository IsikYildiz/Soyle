package com.example.soyle.data.language

interface Language {
    // Bir metnin dilini algılar
    suspend fun identifyLanguage(text: String): String
}