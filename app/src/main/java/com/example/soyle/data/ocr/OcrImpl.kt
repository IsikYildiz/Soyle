package com.example.soyle.data.ocr

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await

// Görüntüden metin algılama işlemi yapar ve algılanan metni döndürür.
class OcrImpl() : Ocr{
    override suspend fun detectText(bitmap: Bitmap): String {
        // Metin algılama modeli
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        val image = InputImage.fromBitmap(bitmap, 0) //Bitmap işlem için dönüştürlür

        try{
            // Metin algılanır
            val result = recognizer.process(image).await()
            val resultText = result.text

            return resultText // Algılanan metin döndürülür (eğer yoksa boş bir string döndürülür)
        }
        catch (e: Exception){
            e.printStackTrace()
            return "" // Hata durumunda da boş bir string döndürülür
        }
    }
}