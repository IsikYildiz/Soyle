package com.example.soyle.data.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import kotlinx.coroutines.CompletableDeferred
import java.util.Locale

class TtsImpl(private val context: Context) : Tts, TextToSpeech.OnInitListener {

    // TTS objesini tutacak değişken
    private var tts: TextToSpeech? = null
    private val initializationCompletion = CompletableDeferred<Boolean>() // Başlatma durumunu beklemek için
    private var isTtsReady: Boolean = false

    // Başlatma fonksiyonu
    override suspend fun initialize() {
        if (tts == null) {
            tts = TextToSpeech(context, this) // TTS objesi oluşturulur.
            this.isTtsReady = true
            initializationCompletion.await()
        }
    }

    // OnInitListener geri çağrımı
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Başarılı olursa dil ayarı yapılabilir
            this.isTtsReady = true
            initializationCompletion.complete(true)
            println("TTS motoru başarıyla başlatıldı.")
        } else {
            // Hata durumu
            this.isTtsReady = false
            initializationCompletion.complete(false)
            println("TTS başlatılırken hata oluştu.")
        }
    }

    // Konuşma fonksiyonu
    override fun speak(text: String, language: String) {
        if (!isTtsReady) {
            println("TTS motoru hazır değil, konuşma iptal edildi.")
            return
        }

        val languageCode = Locale.forLanguageTag(language)

        // Dil kontrolü
        if (tts!!.isLanguageAvailable(languageCode) >= TextToSpeech.LANG_AVAILABLE) {
            tts!!.language = languageCode
        }

        // Konuşma işlemini başlatma
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "TTS_ID")
    }

    // Konuşmayı durdurur
    override fun stop() {
        try {
            tts?.stop()
        } catch (e: android.os.DeadObjectException) {
            tts = null
            isTtsReady = false
        }
    }

    // Kaynakları serbest bırakır
    override fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        this.isTtsReady = false
    }

    // Konuşma durumunu kontrol eder
    override fun isSpeaking(): Boolean {
        return try {
            tts?.isSpeaking ?: false
        } catch (e: android.os.DeadObjectException) {
            println("Hata: TTS DeadObjectException. Kaynaklar sıfırlanıyor.")
            tts = null
            isTtsReady = false
            false
        }
    }
}