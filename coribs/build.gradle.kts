plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    compileSdk = 33

    defaultConfig {
        minSdk = 22
        targetSdk = 33

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        languageVersion = "${properties["kotlin_version"]}"
    }
}

dependencies {
    // Android
    implementation("androidx.appcompat:appcompat:${properties["appcompat_version"]}")
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")
    implementation("androidx.transition:transition-ktx:1.4.1")
    implementation("com.google.android.material:material:1.6.1")

    implementation ("androidx.compose.ui:ui:${properties["compose_version"]}")
    implementation ("androidx.compose.ui:ui-tooling:${properties["compose_version"]}")

    // Tests
    //testImplementation ("junit:junit:4.13.2")
    //androidTestImplementation ("androidx.test.ext:junit:1.1.3")
    //androidTestImplementation ("androidx.test.espresso:espresso-core:3.4.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
}
