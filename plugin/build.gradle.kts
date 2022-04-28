plugins {
    `java-gradle-plugin`
    `maven-publish`
    id("com.gradle.plugin-publish") version "0.21.0"
}

repositories {
    mavenCentral()
}

group = "com.opencastsoftware.gradle"
description = "A Gradle plugin for generating build info as Java code."

dependencies {
    implementation("com.squareup:javapoet:1.13.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
}

pluginBundle {
    website = "https://www.opencastsoftware.com"
    vcsUrl = "https://github.com/opencastsoftware/gradle-build-info.git"
    description = project.description
    tags = listOf("build", "info", "codegen", "code-generation", "java")
}

gradlePlugin {
    plugins {
        create("buildInfoPlugin") {
            id = "com.opencastsoftware.gradle.buildinfo"
            displayName = "Build Info Plugin"
            description = project.description
            implementationClass = "com.opencastsoftware.gradle.buildinfo.BuildInfoPlugin"
        }
    }
}

val functionalTestImplementation by configurations.creating {
    extendsFrom(configurations["testImplementation"])
}

val functionalTestSourceSet = sourceSets.create("functionalTest") {
}

val functionalTest by tasks.registering(Test::class) {
    testClassesDirs = functionalTestSourceSet.output.classesDirs
    classpath = functionalTestSourceSet.runtimeClasspath
    useJUnitPlatform()
}

gradlePlugin.testSourceSets(functionalTestSourceSet)

tasks.named<Task>("check") {
    dependsOn(functionalTest)
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
