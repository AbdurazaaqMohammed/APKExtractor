plugins {
    id("com.android.application")
}

android {
    namespace = "io.github.abdurazaaqmohammed.ApkExtractor"
    compileSdk = 35

    defaultConfig {
        applicationId = "io.github.abdurazaaqmohammed.ApkExtractor"
        minSdk = 4
        targetSdk = 35
        versionCode = 3
        versionName = "1.2"
        multiDexEnabled = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = false
    }
    dependencies {
        implementation("com.android.support:multidex:1.0.3")
        coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.2")
    }
    dependenciesInfo {
        // Disables dependency metadata when building APKs.
        includeInApk = false
        // Disables dependency metadata when building Android App Bundles.
        includeInBundle = false
    }
}