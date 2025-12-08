package com.example.soyle.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.soyle.R
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import androidx.activity.viewModels

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(applicationContext)
    }
    private lateinit var viewFinder: PreviewView
    private lateinit var statusText: TextView
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraProvider: ProcessCameraProvider

    // İzinler
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                startCamera()
                viewModel.readText("Kamera izni verildi.")
            } else {
                viewModel.readText("Kamera izni olmadan uygulama çalışamaz.")
                Toast.makeText(this, "Kamera izni olmadan uygulama çalışamaz.", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lifecycleScope.launch {
            viewModel.initializeTts() // TTS'i başlat
        }

        viewFinder = findViewById(R.id.viewFinder)
        statusText = findViewById(R.id.statusText)
        cameraExecutor = Executors.newSingleThreadExecutor()

        if (allPermissionsGranted()) {
            startCamera() // İzin varsa kamerayı başlat
        } else { // İzin yoksa izin iste
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            viewModel.readText("Uygulama, fotoğraf çekmek için kamera izninize ihtiyaç duymaktadır.")
        }

        observeUiState()

        findViewById<View>(R.id.touch_overlay).setOnClickListener {
            handleTapAction()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkTtsStatus()
    }

    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(
        baseContext, Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    // Kamera başlatma
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

            // Fotoğraf çekme
            imageCapture = ImageCapture.Builder()
                .setTargetResolution(Size(1080, 1920))
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .build()

            // Kamera seçimi
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
            } catch (exc: Exception) {
                Log.e("CameraX", "Use case bağlama başarısız oldu", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    // Ekrana dokunulduğunda yapılacak işlemler
    private fun handleTapAction() {
        val currentState = viewModel.uiState.value

        if (currentState is UiState.Processing || currentState is UiState.Error) {
            return
        }

        if (currentState is UiState.Speaking) {
            // Konuşuyorsa durdurma talebini gönder
            viewModel.handleUserTap(null)
        } else if (imageCapture != null) {
            // Hazırsa veya başlangıçtaysa fotoğraf çekimini başlat
            takePhoto()
        } else {
            Toast.makeText(this, "Kamera başlatılmadı, lütfen bekleyin.", Toast.LENGTH_SHORT).show()
            viewModel.readText("Kamera başlatılmadı, lütfen bekleyin.")
        }
    }

    // Fotoğraf çekme
    private fun takePhoto() {
        val imageCapture = this.imageCapture ?: return

        imageCapture.takePicture(
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)

                    val bitmap = imageProxyToBitmap(image)
                    viewModel.handleUserTap(bitmap)

                    image.close()
                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)
                    Log.e("CameraX", "Fotoğraf çekimi başarısız oldu: ${exception.message}", exception)
                    viewModel.readText("Fotoğraf çekimi başarısız oldu.")
                }
            }
        )
    }

    // Bitmap formatına dönüştürme fonksiyonu
    private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
        return image.toBitmap()
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    UiState.Initial -> statusText.text = "Başlamak için ekrana dokunun."
                    UiState.Processing -> statusText.text = "Fotoğraf işleniyor, lütfen bekleyin..."
                    is UiState.Speaking -> {
                        statusText.text = "Metin okunuyor: ${state.text.take(30)}..."
                        lifecycleScope.launch {
                            while(viewModel.uiState.value is UiState.Speaking && viewModel.isTtsSpeaking){
                                viewModel.checkTtsStatus()
                                kotlinx.coroutines.delay(100)
                            }
                        }
                    }
                    UiState.Ready -> statusText.text = "İşlem tamamlandı, yeni çekim için dokunun."
                    is UiState.Error -> statusText.text = "Hata: ${state.message}"
                }
            }
        }
    }

    // Uygulama kapatıldığında yapılacaklar
    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        viewModel.shutdownTts() // Tts kapatılır
    }
}