package com.example.soyle.ui.main

import android.graphics.Bitmap
import com.example.soyle.data.language.Language
import com.example.soyle.data.ocr.Ocr
import com.example.soyle.data.tts.Tts

// Metni ve dil kodunu tutan basit bir veri sınıfı
data class DetectedTextResult(val text: String, val languageCode: String)

// Ocr, dil algılama ve tts işlemleri için köprü görevi görür
class DetectTextAndSpeak(
    private val ocr: Ocr,
    private val language: Language,
    private val tts: Tts
) {
    suspend operator fun invoke(bitmap: Bitmap): DetectedTextResult {
        // Metni algıla
        val detectedText = ocr.detectText(bitmap)

        // Metin bulunamazsa
        if (detectedText.isBlank()) {
            tts.speak("Fotoğrafta okunabilir bir metin bulunamadı.", "tr")
            throw Exception("Metin Bulunamadı")
        }

        // Dili algıla
        val languageCode = language.identifyLanguage(detectedText)

        return DetectedTextResult(detectedText, languageCode)
    }
}