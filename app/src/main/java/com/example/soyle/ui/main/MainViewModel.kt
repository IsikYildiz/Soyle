package com.example.soyle.ui.main

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soyle.data.tts.Tts
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Metni ve dil kodunu tutan yeni Speaking durumu
sealed class UiState {
    object Initial : UiState()
    object Processing : UiState() // Fotoğraf çekildi OCR yapılıyor
    data class Speaking(val text: String, val language: String) : UiState() // Metin ve dil eklendi
    object Ready : UiState() // İşlem bitti yeni çekime hazır
    data class Error(val message: String) : UiState()
}

class MainViewModel(
    private val detectTextAndReadUseCase: DetectTextAndSpeak,
    private val tts: Tts
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    val isTtsSpeaking: Boolean
        get() = tts.isSpeaking()

    fun handleUserTap(imageBitmap: Bitmap?) {
        if (_uiState.value is UiState.Processing || _uiState.value is UiState.Error) {
            return
        }

        if (_uiState.value is UiState.Speaking) {
            // Eğer konuşuyorsa durdur
            stopReading()
            _uiState.value = UiState.Ready
            return
        }

        // Konuşmuyorsa ve hazırsa yeni işleme başla
        imageBitmap?.let { bitmap ->
            viewModelScope.launch {
                _uiState.value = UiState.Processing

                try {
                    // Use case'den metni ve dili al
                    val result = detectTextAndReadUseCase(bitmap)

                    // TTS'i başlat ve durumu Speaking olarak ayarla
                    readText(result.text, result.languageCode)
                    _uiState.value = UiState.Speaking(result.text, result.languageCode)

                } catch (e: Exception) {
                    val errorMessage = e.message ?: "Bilinmeyen Hata"
                    _uiState.value = UiState.Error(errorMessage)
                    readText("Fotoğrafta okunabilir bir metin bulunamadı.", "tr")
                    viewModelScope.launch {
                        kotlinx.coroutines.delay(2000)
                        if (_uiState.value is UiState.Error) {
                            _uiState.value = UiState.Ready
                        }
                    }
                }
            }
        }
    }

    private fun stopReading() {
        tts.stop()
    }

    // Herhangi bir metni sesli okuma
    fun readText(text: String, languageCode: String = "tr") {
        viewModelScope.launch {
            tts.stop() // Mevcut konuşmayı kes

            tts.speak(text, languageCode) // Konuş
        }
    }

    // Tts oluşturma
    suspend fun initializeTts() {
        tts.initialize()
    }

    // Tts kapatma
    fun shutdownTts() {
        tts.shutdown()
    }

    // TTS'in bittiğini kontrol eden fonksiyon
    fun checkTtsStatus() {
        if (_uiState.value is UiState.Speaking && !tts.isSpeaking()) {
            _uiState.value = UiState.Ready
        }
    }
}