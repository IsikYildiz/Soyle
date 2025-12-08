package com.example.soyle.ui.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.soyle.data.language.LanguageImpl
import com.example.soyle.data.ocr.OcrImpl
import com.example.soyle.data.tts.Tts
import com.example.soyle.data.tts.TtsImpl

// Context alır
class MainViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {

            // Bağımlılıkları somut sınıfları kullanarak oluştur
            val ocr = OcrImpl()
            val language = LanguageImpl()
            val tts = TtsImpl(context) as Tts // TtsImpl, Tts arayüzünü uygular

            // Use Case'i oluştur
            val detectTextAndSpeak = DetectTextAndSpeak(ocr, language, tts)

            // ViewModel'i bağımlılıklarla oluştur ve döndür
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(detectTextAndSpeak, tts) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}