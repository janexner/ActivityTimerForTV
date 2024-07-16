plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.exner.tools.activitytimerfortv"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.exner.tools.activitytimerfortv"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
        vectorDrawables {
            useSupportLibrary = true
        }

    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            vcsInfo.include = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug {
            isDebuggable = true
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildToolsVersion = "34.0.0"

    applicationVariants.all {
        val variant = this
        variant.outputs
            .map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
            .forEach { output ->
                val outputFileName = "Activity_Timer_${variant.baseName}_${variant.versionName}_${variant.versionCode}.apk"
                println("OutputFileName: $outputFileName")
                output.outputFileName = outputFileName
            }
    }

}

kotlin {
    jvmToolchain(17)

    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
    sourceSets.test {
        kotlin.srcDir("build/generated/ksp/test/kotlin")
    }
}

dependencies {

    implementation(libs.androidx.appcompat)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.tv.foundation)
    implementation(libs.androidx.tv.material)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.datastore.preferences)

    // suggested by Dependency Analyser Plugin
    implementation(libs.androidx.activity)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.animation)
    implementation(libs.androidx.foundation.layout)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.material.icons.core)
    implementation(libs.androidx.runtime.saveable)
    implementation(libs.androidx.runtime)
    implementation(libs.androidx.ui.geometry)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.text)
    implementation(libs.androidx.ui.unit)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.datastore.core)
    implementation(libs.androidx.datastore.preferences.core)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.lifecycle.common)
    implementation(libs.androidx.lifecycle.livedata.core)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.navigation.common)
    implementation(libs.androidx.navigation.runtime)
    implementation(libs.androidx.room.common)
    implementation(libs.androidx.sqlite)
    implementation(libs.dagger)
    implementation(libs.hilt.core)
    implementation(libs.javax.inject)
    implementation(libs.kotlinx.coroutines.core)

    // hilt
    implementation(libs.hilt.android)
    ksp(libs.dagger.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Room components
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)
    implementation(libs.room.ktx)

    // compose destinations
    implementation(libs.compose.destinations.core)
    ksp(libs.compose.destinations.ksp)

    // retrofit
    implementation(libs.squareup.retrofit)
    implementation(libs.squareup.retrofit.converter.gson)

    // nearby
    implementation(libs.play.services.nearby)
    implementation(libs.accompanist.permissions)
}

hilt {
    enableAggregatingTask = true
}

ksp {
    arg("room.schemaLocation", "$projectDir/src/main/assets/schemas")
    arg("room.incremental"   , "true")
}
