plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("org.sonarqube") version "5.0.0.4638"
}


android {
    namespace = "com.example.projet42_androidapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.projet42_androidapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        manifestPlaceholders["appAuthRedirectScheme"] = "projet42"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    lint {
        xmlReport = true
        xmlOutput = file("build/reports/lint-results.xml")
    }

    sonarqube {
        properties {
            property("sonar.projectKey", "projet42_androidapp")
            property("sonar.organization", "projet42")
            property("sonar.host.url", "https://sonarcloud.io")
        }
    }
}

tasks.sonarqube {
    dependsOn(tasks.check)
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material.v168)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)

    implementation (libs.appauth)
    implementation (libs.gson)
    implementation (libs.okhttp)
    implementation (libs.java.jwt)
    implementation (libs.nimbus.jose.jwt)

    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    implementation ("org.osmdroid:osmdroid-android:6.1.18")
    implementation ("com.github.MKergall:osmbonuspack:6.9.0")

    implementation ("androidx.compose.material:material-icons-extended:1.6.8")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

}