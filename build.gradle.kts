import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
        id("io.realm.kotlin") version "1.10.0"

}

group = "com.cyrax"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    jvm {
        jvmToolchain(11)
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0") // Add to use coroutines with the SDK
                implementation("io.realm.kotlin:library-base:1.10.0") // Add to only use the local database
                implementation("uk.co.caprica:vlcj:4.8.2")

                implementation("org.jetbrains.compose.ui:ui-graphics:1.1.1")
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "TroidMedia"
            packageVersion = "1.0.0"
        }
    }
}
