import java.util.Properties

plugins {
    id("com.android.application")
    id("com.google.dagger.hilt.android")
    kotlin("android")
    kotlin("kapt")
}

android {
    namespace = "com.example.arcus"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.arcus"
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
        val properties = Properties().apply {
            load(project.rootProject.file("local.properties").inputStream())
        }
        val openWeatherMapApiKey = properties.getProperty("OPEN_WEATHER_MAP_API_KEY")
        val mapBoxApiKey = properties.getProperty("MAP_BOX_ACCESS_TOKEN")
        buildConfigField(
            type = "String",
            name = "OPEN_WEATHER_MAP_API_KEY",
            value = "\"$openWeatherMapApiKey\""
        )
        buildConfigField(
            type = "String",
            name = "MAP_BOX_API_KEY",
            value = "\"$mapBoxApiKey\""
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        // To enable the use of java.time api's with minSdk < 26
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "17"
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

    // java.time support for api's < Android O
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")

    // compose
    val composeBom = platform("androidx.compose:compose-bom:2023.05.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.7.1")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.0")
    implementation("androidx.navigation:navigation-compose:2.5.3")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    // splashscreen
    implementation("androidx.core:core-splashscreen:1.0.1")

    // material theme - for adding dynamic colors in splash screen (work around)
    implementation("com.google.android.material:material:1.9.0")

    // hilt
    implementation("com.google.dagger:hilt-android:2.44")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
    kapt("com.google.dagger:hilt-compiler:2.44")

    // room
    implementation("androidx.room:room-runtime:2.5.1")
    implementation("androidx.room:room-ktx:2.5.1")
    kapt("androidx.room:room-compiler:2.5.1")

    // logging
    implementation("com.jakewharton.timber:timber:5.0.1")

    // coroutines support for Task<T>
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.4")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")

    // location services
    implementation("com.google.android.gms:play-services-location:21.0.1")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")

    // "await()" support for Task<T>
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.4")

    // retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")

    // moshi
    implementation("com.squareup.moshi:moshi:1.15.0")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:1.15.0") // Make's moshi not depend on reflection + generates Kotlin code using Kotlin Poet

    // work manager
    implementation("androidx.work:work-runtime-ktx:2.8.1")
    implementation("androidx.hilt:hilt-work:1.0.0")
    kapt("androidx.hilt:hilt-compiler:1.0.0")

    // junit
    testImplementation("junit:junit:4.13.2")

    // mockito
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.0.0")
}