package com.example.soyle.data.tts

// Metni okuma işlemi yapar
interface Tts {
    suspend fun initialize() // TTS motorunu başlatmak için
    fun speak(text: String, languageCode: String) // Metni okumaya başlar
    fun stop() // Okumayı durdurur
    fun shutdown() // Kaynakları serbest bırakmak için
    fun isSpeaking(): Boolean // Şuan bir metin okunuyor mu döndürür
}