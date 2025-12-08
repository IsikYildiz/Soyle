package com.example.soyle.data.language

import com.google.mlkit.nl.languageid.LanguageIdentification
import kotlinx.coroutines.tasks.await

class LanguageImpl : Language {
    override suspend fun identifyLanguage(text: String): String {
        val languageIdentifier = LanguageIdentification.getClient() // Dil algılama modeli
        val language = languageIdentifier.identifyLanguage(text).await() // Dil algılanır

        return language
    }
}