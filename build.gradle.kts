plugins {
    `java-gradle-plugin`
    `maven-publish`
    id("com.gradle.plugin-publish") version "0.21.0"
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
    id("me.qoomon.git-versioning") version "5.2.0"
}

repositories {
    mavenCentral()
}

group = "com.opencastsoftware.gradle"
description = "A Gradle plugin for generating build info as Java code."

version = "0.0.0-SNAPSHOT"

gitVersioning.apply {
    refs {
        branch(".+") {
            describeTagPattern = "v(?<version>.*)".toPattern()
            version = "\${describe.tag.version:-0.0.0}-\${describe.distance}-\${commit.short}-SNAPSHOT"
        }
        tag("v(?<version>.*)") {
            version = "\${ref.version}"
        }
    }
    rev {
        describeTagPattern = "v(?<version>.*)".toPattern()
        version = "\${describe.tag.version:-0.0.0}-\${describe.distance}-\${commit.short}-SNAPSHOT"
    }
}

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

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            pom {
                name.set("Gradle Build Info Plugin")
                description.set(project.description)
                url.set("https://github.com/opencastsoftware/gradle-build-info")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("DavidGregory084")
                        name.set("David Gregory")
                        email.set("david.gregory@opencastsoftware.com")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/opencastsoftware/gradle-build-info.git")
                    developerConnection.set("scm:git:git@github.com:opencastsoftware/gradle-build-info.git")
                    url.set("https://github.com/opencastsoftware/gradle-build-info")
                }
            }
        }
    }
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
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
