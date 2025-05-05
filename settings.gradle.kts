rootProject.name = "OTP_project"

pluginManagement {
    resolutionStrategy {
        eachPlugin {
            if (requested.id.namespace == 'org.springframework.boot') {
                useModule('org.springframework.boot:spring-boot-gradle-plugin:${springBootVersion}')
            }
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
    }
    javaToolchains {
        repositories {
            gradleBuildCache() // Кэш Gradle для быстрого восстановления инструментов
            maven("https://repo.spring.io/milestone") // Репозиторий Spring Milestone
            maven("https://repo.spring.io/snapshot")   // Репозиторий Spring Snapshot
        }
    }
}