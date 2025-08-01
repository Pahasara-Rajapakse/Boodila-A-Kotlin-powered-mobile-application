plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.booodima"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.booodima"
        minSdk = 21
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildToolsVersion = "35.0.0"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)

    //implementation(libs.firebase.database)
   // implementation(libs.firebase.firestore.ktx)
   // implementation(libs.firebase.firestore)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

   //implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
   //implementation("com.google.firebase:firebase-analytics")

}

