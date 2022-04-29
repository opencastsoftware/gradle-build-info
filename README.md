# gradle-build-info

[![CI](https://github.com/opencastsoftware/gradle-build-info/actions/workflows/ci.yml/badge.svg)](https://github.com/opencastsoftware/gradle-build-info/actions/workflows/ci.yml)
[![License](https://img.shields.io/github/license/opencastsoftware/gradle-build-info)](https://opensource.org/licenses/Apache-2.0)
[![Gradle Plugin Portal](https://img.shields.io/gradle-plugin-portal/v/com.opencastsoftware.gradle.buildinfo)](https://plugins.gradle.org/plugin/com.opencastsoftware.gradle.buildinfo)
[![Maven Central](https://img.shields.io/maven-central/v/com.opencastsoftware.gradle/gradle-build-info)](https://search.maven.org/artifact/com.opencastsoftware.gradle/gradle-build-info)

**gradle-build-info** is a Gradle plugin for generating build info as Java code, inspired by [sbt-buildinfo](https://github.com/sbt/sbt-buildinfo).

## Installation

Groovy (build.gradle):
```groovy
plugins {
    id 'java'
    id 'com.opencastsoftware.gradle.buildinfo' version '0.1.0'
}
```

Kotlin (build.gradle.kts):
```kotlin
plugins {
    java
    id("com.opencastsoftware.gradle.buildinfo") version "0.1.0"
}
```

## Usage

The **gradle-build-info** plugin adds a `buildInfo` extension to your Gradle project. You can use this to configure the build info code that is generated.

Groovy (build.gradle):

```groovy
buildInfo {
  packageName = 'com.opencastsoftware.gradle.bsp'
  className = 'BuildInfo'
  properties = [version: "0.1.0", bspVersion: "2.0.0"]
}
```
or Kotlin (build.gradle.kts):
```kotlin
buildInfo {
  packageName.set("com.opencastsoftware.gradle.bsp")
  className.set("BuildInfo")
  properties.set(mapOf("version" to "0.1.0", "bspVersion" to "2.0.0"))
}
```
results in:
```java
package com.opencastsoftware.gradle.bsp;

import java.lang.String;

public final class BuildInfo {
  public static final String version = "0.1.0";
  public static final String bspVersion = "2.0.0";
}
```

The generated sources directory for this plugin (`build/generated/sources/buildinfo/java/main`) is added to the main Java source set by the plugin.

## Note

This plugin generates Java code. As a result, if you do not enable the core Gradle `java` plugin, it will not do anything.

## License

All code in this repository is licensed under the Apache License, Version 2.0. See [LICENSE](./LICENSE).
