plugins {
  alias(libs.plugins.android.application)
}

import java.util.Properties
import java.io.FileInputStream

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

android {
    namespace = "com.example.aistudymentor"
    compileSdk = 36
    defaultConfig {
        applicationId = "com.example.aistudymentor"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        
        val geminiApiKey = localProperties.getProperty("GEMINI_API_KEY", "")
        buildConfigField("String", "GEMINI_API_KEY", "\"$geminiApiKey\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
      viewBinding = true
      buildConfig = true
    }
}

dependencies {
  // Core Android dependencies
  implementation(libs.androidx.core)
  implementation(libs.androidx.activity)
  implementation(libs.androidx.fragment)
  implementation(libs.androidx.lifecycle.viewmodel)
  implementation(libs.androidx.lifecycle.livedata)

  // UI 
  implementation(libs.androidx.constraintlayout)
  implementation(libs.material)

  // Tests
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.test.core)
  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.androidx.test.runner)
  androidTestImplementation(libs.androidx.test.espresso.core)

  // Navigation
  implementation(libs.androidx.navigation.fragment)
  implementation(libs.androidx.navigation.ui)
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.6.3")
  
  // Room Database
  implementation(libs.room.runtime)
  annotationProcessor(libs.room.compiler)

  // Retrofit & Gson
  implementation(libs.retrofit.core)
  implementation(libs.retrofit.converter.gson)
  implementation(libs.gson)

  // CameraX
  implementation(libs.camerax.core)
  implementation(libs.camerax.camera2)
  implementation(libs.camerax.lifecycle)
  implementation(libs.camerax.view)
  
  implementation("androidx.viewpager2:viewpager2:1.0.0")
}
