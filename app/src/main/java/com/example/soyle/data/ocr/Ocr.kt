package com.example.soyle.data.ocr

import android.graphics.Bitmap

interface Ocr{
    // Metni algılar
    suspend fun detectText(image: Bitmap) : String
}