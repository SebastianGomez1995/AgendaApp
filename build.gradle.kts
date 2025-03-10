plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("androidx.navigation.safeargs") version "2.7.7" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}