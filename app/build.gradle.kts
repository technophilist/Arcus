import java.util.Properties

plugins {
    id("com.android.application")
    id("com.google.dagger.hilt.android")
    kotlin("android")
    kotlin("kapt")
}

android {
    namespace = "com.example.justweather"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.justweather"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        // load the api key from local properties file and make it
        // available as a build config field
        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())
        val apiKey = properties.getProperty("OPEN_WEATHER_MAP_API_KEY")
        buildConfigField(
            type = "String",
            name = "OPEN_WEATHER_MAP_API_KEY",
            value = "\"$apiKey\""
        )
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.7"
    }
    packagingOptions {
        resources {
            resources.excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
}

dependencies {
    // core
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.core:core-ktx:1.10.1")
    testImplementation("junit:junit:4.12")

    // compose
    val composeBom = platform("androidx.compose:compose-bom:2023.03.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.7.1")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
    implementation("androidx.navigation:navigation-compose:2.5.3")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    // hilt
    implementation("com.google.dagger:hilt-android:2.44")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
    kapt("com.google.dagger:hilt-compiler:2.44")

    // logging
    implementation("com.jakewharton.timber:timber:5.0.1")

    // coroutines support for Task<T>
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.4")

    // location services
    implementation("com.google.android.gms:play-services-location:21.0.1")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")

    // retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")

    // moshi
    implementation("com.squareup.moshi:moshi:1.15.0")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:1.15.0") // Make's moshi not depend on reflection + generates Kotlin code using Kotlin Poet

    // junit
    testImplementation("junit:junit:4.13.2")
}