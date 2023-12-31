plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.sharma.notesapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.sharma.notesapp"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles (getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    packagingOptions.resources.excludes.apply {
        add("META-INF/gradle/**")
    }
}

dependencies {

    val firebaseBom = "32.2.3"
    val gson = "2.8.7"
    val hilt = "2.47"
    val lifecycle = "2.6.1"
    val lottieVersion = "3.4.0"
    val navigation = "2.7.0"
    val playIntegrity = "1.2.0"

    // Play Integrity
    implementation ("com.google.android.play:integrity:$playIntegrity")
    implementation ("com.google.android.gms:play-services-auth:20.7.0")

    // Firebase
    implementation (platform("com.google.firebase:firebase-bom:$firebaseBom"))
    implementation ("com.google.firebase:firebase-firestore-ktx")
    implementation ("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-appcheck-debug")
    implementation("com.google.firebase:firebase-appcheck-playintegrity")
    implementation("com.google.firebase:firebase-appcheck-ktx")

    // Hilt
    implementation ("com.google.dagger:hilt-android:$hilt")
    kapt ("com.google.dagger:hilt-android-compiler:$hilt")

    // lifecycle
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle")

    // navigation
    implementation ("androidx.navigation:navigation-fragment-ktx:$navigation")
    implementation ("androidx.navigation:navigation-ui-ktx:$navigation")

    // Lottie
    implementation ("com.airbnb.android:lottie:$lottieVersion")

    // Local
    implementation ("com.google.code.gson:gson:$gson")

    implementation ("androidx.core:core-ktx:1.8.0")
    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("com.google.android.material:material:1.5.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
}