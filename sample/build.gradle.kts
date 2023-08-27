plugins {
    kotlin("android")
    id("com.android.application")
    id("kotlin-android")
}

android {

    val minSdkVersion: Int = (properties["min_sdk_version"] as String).toInt()
    val compileSdkVersion: Int = (properties["compile_sdk_version"] as String).toInt()

    namespace = "org.vizhev.sample"
    compileSdk = compileSdkVersion

    defaultConfig {
        applicationId = "org.vizhev.coribs.sample"
        minSdk = minSdkVersion
        targetSdk = compileSdkVersion

        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
        viewBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    /*compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }*/

    composeOptions {
        //kotlinCompilerVersion "$kotlin_version"
        kotlinCompilerExtensionVersion = "${properties["compose_compiler_version"]}"
    }

    kotlinOptions {
        jvmTarget = "1.8"
        languageVersion = "1.9"
    }
}

dependencies {

    implementation (project(":coribs"))

    // Android
    implementation ("androidx.appcompat:appcompat:${properties["appcompat_version"]}")
    implementation ("androidx.compose.ui:ui:${properties["compose_version"]}")
    implementation ("androidx.compose.ui:ui-tooling:${properties["compose_version"]}")
    implementation ("androidx.compose.foundation:foundation:${properties["compose_version"]}")
    implementation ("androidx.compose.material:material:${properties["compose_version"]}")


    // Tests
    testImplementation ("junit:junit:4.13.2")
    androidTestImplementation ("androidx.test.ext:junit:1.1.3")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.4.0")
}