plugins {
    `java-gradle-plugin`
    alias(libs.plugins.gradlePluginPublish)
    alias(libs.plugins.gradleJavaConventions)
}

repositories { mavenCentral() }

group = "com.opencastsoftware.gradle"

description = "A Gradle plugin for generating build info as Java code."

java { toolchain.languageVersion.set(JavaLanguageVersion.of(11)) }

dependencies {
    implementation(libs.javaPoet)
    testImplementation(libs.junitJupiter)
}

gradlePlugin {
    website.set("https://github.com/opencastsoftware/gradle-build-info")
    vcsUrl.set("https://github.com/opencastsoftware/gradle-build-info.git")
    plugins {
        create("buildInfoPlugin") {
            id = "com.opencastsoftware.gradle.buildinfo"
            displayName = "Build Info Plugin"
            description = project.description
            implementationClass = "com.opencastsoftware.gradle.buildinfo.BuildInfoPlugin"
            tags.set(listOf("build", "info", "codegen", "code-generation", "java"))
        }
    }
}

mavenPublishing {
    pom {
        name.set("Gradle Build Info Plugin")
        description.set(project.description)
        url.set("https://github.com/opencastsoftware/gradle-build-info")
        inceptionYear.set("2022")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("repo")
            }
        }
        organization {
            name.set("Opencast Software Europe Ltd")
            url.set("https://opencastsoftware.com")
        }
        developers {
            developer {
                id.set("DavidGregory084")
                name.set("David Gregory")
                organization.set("Opencast Software Europe Ltd")
                organizationUrl.set("https://opencastsoftware.com/")
                timezone.set("Europe/London")
                url.set("https://github.com/DavidGregory084")
            }
        }
        ciManagement {
            system.set("Github Actions")
            url.set("https://github.com/opencastsoftware/gradle-build-info/actions")
        }
        issueManagement {
            system.set("GitHub")
            url.set("https://github.com/opencastsoftware/gradle-build-info/issues")
        }
        scm {
            connection.set("scm:git:https://github.com/opencastsoftware/gradle-build-info.git")
            developerConnection.set("scm:git:git@github.com:opencastsoftware/gradle-build-info.git")
            url.set("https://github.com/opencastsoftware/gradle-build-info")
        }
    }
}

tasks.withType<JavaCompile>() {
    // Target Java 8
    options.release.set(8)
}

testing {
    suites {
        val functionalTest by
            registering(JvmTestSuite::class) {
                gradlePlugin.testSourceSets(sources)
                testType.set(TestSuiteType.FUNCTIONAL_TEST)
            }
    }
}

val functionalTestImplementation: Configuration by
    configurations.getting { extendsFrom(configurations.testImplementation.get()) }

tasks.named<Task>("check") { dependsOn(testing.suites.named("functionalTest")) }
