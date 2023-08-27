plugins {
    kotlin("android")
    id("com.android.library")
}

android {
    val minSdkVersion: Int = (properties["min_sdk_version"] as String).toInt()
    val compileSdkVersion: Int = (properties["compile_sdk_version"] as String).toInt()

    namespace = "org.vizhev.coribs"
    compileSdk = compileSdkVersion

    defaultConfig {
        minSdk = minSdkVersion
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    /*compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }*/

    buildFeatures {
        viewBinding = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "${properties["compose_compiler_version"]}"
    }

    kotlinOptions {
        jvmTarget = "1.8"
        languageVersion = "1.9"
    }
}

dependencies {
    // Android
    implementation("androidx.appcompat:appcompat:${properties["appcompat_version"]}")
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")
    implementation("androidx.transition:transition-ktx:1.4.1")
    implementation("com.google.android.material:material:1.9.0")

    implementation("androidx.compose.ui:ui:${properties["compose_version"]}")
    implementation("androidx.compose.ui:ui-tooling:${properties["compose_version"]}")

     // Tests
    testImplementation ("junit:junit:4.13.2")
    androidTestImplementation ("androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
}
