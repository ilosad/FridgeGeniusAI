plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.losad.fridgegenius"
    compileSdk = 36   // 🔥 중요: 36으로 통일

    defaultConfig {
        applicationId = "com.losad.fridgegenius"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        // ✅ API KEY (gradle.properties에서 주입)
        buildConfigField(
            "String",
            "OPENAI_API_KEY",
            "\"${project.findProperty("OPENAI_API_KEY") ?: ""}\""
        )
        buildConfigField(
            "String",
            "GEMINI_API_KEY",
            "\"${project.findProperty("GEMINI_API_KEY") ?: ""}\""
        )

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    /* ---------- Core ---------- */
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.1")

    /* ---------- Activity / Compose ---------- */
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    debugImplementation(libs.androidx.compose.ui.tooling)

    /* ---------- Navigation ---------- */
    implementation(libs.androidx.navigation.compose)

    /* 🔥🔥 Hilt (중요 포인트) ---------- */
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // 🔥 hiltViewModel() 쓰기 위해 반드시 필요
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    /* ---------- Room ---------- */
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    /* ---------- Network ---------- */
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.retrofit)
    implementation(libs.retrofit.moshi)

    /* ---------- JSON ---------- */
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    ksp(libs.moshi.ksp)

    /* ---------- Image ---------- */
    implementation(libs.coil.compose)

    /* ---------- ML Kit ---------- */
    implementation(libs.mlkit.image.labeling)

    implementation("com.google.mlkit:image-labeling:17.0.9")
    implementation("androidx.compose.material:material-icons-extended")
}
