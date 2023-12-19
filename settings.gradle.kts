dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

    plugins {
        kotlin("ksp") version "1.9.21" // Gantilah dengan versi terbaru
    }
}

rootProject.name = "PrakMobileUAS"
include(":app")
 