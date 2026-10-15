plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jlleitschuh.gradle.ktlint")
}
android {
    namespace = "dev.devaxes.composeatlas"
    compileSdk = 36
    defaultConfig {
        applicationId = "dev.devaxes.composeatlas"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures { compose = true }
    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    lint {
        abortOnError = true
        warningsAsErrors = true
        // API 36 is this milestone's tested target; SDK-update notices are not compatibility checks.
        disable += setOf("GradleDependency", "AndroidGradlePluginVersion", "OldTargetApi")
    }
    testOptions { animationsDisabled = false }
}
kotlin { compilerOptions { jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17) } }
ktlint { version.set("1.7.1") }
dependencies {
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.compose.ui:ui:1.9.0")
    implementation("androidx.compose.ui:ui-tooling-preview:1.9.0")
    implementation("androidx.compose.foundation:foundation:1.9.0")
    implementation("androidx.compose.animation:animation:1.9.0")
    implementation("androidx.compose.material3:material3:1.3.2")
    implementation("androidx.compose.material:material-icons-core:1.7.8")
    debugImplementation("androidx.compose.ui:ui-tooling:1.9.0")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.9.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.9.0")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test:runner:1.6.2")
}
dependencyLocking {
    lockAllConfigurations()
    ignoredDependencies.add("org.jetbrains.kotlin:kotlin-stdlib-common")
}
configurations.configureEach {
    resolutionStrategy.force("org.jetbrains.kotlin:kotlin-stdlib-common:2.2.10")
}
