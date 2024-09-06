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
        versionCode = 1
        versionName = "1.0"
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
        implementation("org.apache.commons:commons-compress:1.24.0")
        implementation("com.android.support:multidex:1.0.3")
        coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.2")
    }
}