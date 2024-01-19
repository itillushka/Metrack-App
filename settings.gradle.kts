pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        // Scanbot SDK maven repos:
        maven { url = uri("https://nexus.scanbot.io/nexus/content/repositories/releases/") }
        maven { url = uri("https://nexus.scanbot.io/nexus/content/repositories/snapshots/") }
    }
}

rootProject.name = "Metrack"
include(":app")
 