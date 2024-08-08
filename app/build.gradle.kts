plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.amr.weatherapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.amr.weatherapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation (libs.play.services.location)
    implementation (libs.gson)
    implementation (libs.lottie)
    implementation (libs.material.v1110)
    implementation (libs.glide)
    implementation (libs.play.services.location.v2101)
    implementation (libs.okhttp)
    implementation (libs.json)
    annotationProcessor (libs.compiler)

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}