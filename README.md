# Söyle - Görme Engelliler İçin Anlık Metin Okuyucu / Instant Text Reader for the Visually Impaired

## Proje Ekibi / Project Team
**Işık Yıldız**, **Hasan Yıldız**

---

## 🇹🇷 Türkçe Açıklama

**Söyle**: Söyle, görme engelli insanların kağıtlarda, defterlerde, tablolarda v.b yazılanları anlaması için geliştirilmiş bir android mobil uygulamasıdır. 
Uygulama görme engelli insanların, telefonlarıyla bir fotoğraf çekerek yazılanları sesli olarak dinleyebilmesini sağlar. 

### Özellikler

* **OCR (Optik Karakter Tanıma):** Kullanıcının tek bir dokunuşla çektiği fotoğraflardaki metinleri Google ML Kit kullanarak anında algılar.
* **Otomatik Dil Algılama:** Algılanan metnin dilini otomatik olarak tanımlar (ML Kit Language Identification kullanarak).
* **Sesli Geri Bildirim (TTS):** Algılanan metni, belirlenen dilde Android Text-to-Speech (TTS) motoru aracılığıyla sesli olarak okur.
* **Basit Arayüz:** Kullanıcı etkileşimi sadece ekrana dokunma eylemi üzerine kurulmuştur.
    * **Tek Dokunuş:** Fotoğraf çekimini başlatır ve okumayı başlatır.
    * **Okuma Sırasında Dokunuş:** Mevcut okumayı durdurur ve uygulamayı yeni bir çekime hazırlar.
* **Durum Yönetimi:** Uygulamanın anlık durumu (Başlangıç, İşleniyor, Okunuyor, Hazır, Hata) kullanıcıya sesli ve metinsel olarak bildirilir.

### ⚙️ Kurulum

1.  Projeyi klonlayın.
2.  Android Studio'da açın.
3.  Gerekli tüm ML Kit ve CameraX bağımlılıklarını `build.gradle` dosyasında tanımladığınızdan emin olun.
4.  Uygulamayı cihazınıza kurun.
5.  Uygulamayı çalıştırın ve kamera izni verdiğinizden emin olun.

---

---

## 🇺🇸 English Description

**Soyle**: Soyle is an Android mobile application developed to help visually impaired people understand what's written on paper, notebooks, tables, etc. 
The application allows visually impaired people to listen to what's written by taking a photo with their phone.

### Features

* **OCR (Optical Character Recognition):** Detects text in photos taken with a single tap using Google ML Kit.
* **Automatic Language Detection:** Automatically identifies the language of the detected text using ML Kit Language Identification.
* **Audio Feedback (TTS):** Reads the detected text aloud in the identified language via the Android Text-to-Speech (TTS) engine.
* **Simple Touch Interface:** User interaction is purely based on tapping the screen.
    * **Single Tap:** Initiates photo capture and starts the reading process.
    * **Tap During Reading:** Stops the current reading and prepares the app for a new capture.
* **State Management:** The app's current status (Initial, Processing, Speaking, Ready, Error) is communicated to the user through both audio and on-screen text.

### Setup

1.  Clone the repository.
2.  Open the project in Android Studio.
3.  Ensure all necessary ML Kit and CameraX dependencies are defined in your `build.gradle` file.
4.  Upload the application into your phone.
5.  Run the application and ensure camera permissions are granted.

---
