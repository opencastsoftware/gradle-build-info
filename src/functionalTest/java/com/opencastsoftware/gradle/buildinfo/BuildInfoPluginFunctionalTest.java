/*
 * SPDX-FileCopyrightText:  © 2022-2023 Opencast Software Europe Ltd <https://opencastsoftware.com>
 * SPDX-License-Identifier: Apache-2.0
 */
package com.opencastsoftware.gradle.buildinfo;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class BuildInfoPluginFunctionalTest {
    private File getBuildFile(File projectDir) {
        return new File(projectDir, "build.gradle");
    }

    private File getSettingsFile(File projectDir) {
        return new File(projectDir, "settings.gradle");
    }

    private Path getGeneratedSrcDir(File projectDir) {
        return projectDir.toPath()
                .resolve("build")
                .resolve("generated")
                .resolve("sources")
                .resolve("buildinfo")
                .resolve("java")
                .resolve("main");
    }

    private Path getBuildInfoJavaFile(File projectDir, String packageFolder, String className) {
        return getGeneratedSrcDir(projectDir)
                .resolve(packageFolder)
                .resolve(className + ".java");
    }

    private BuildResult runBuildTask(File projectDir, String taskName) {
        GradleRunner runner = GradleRunner.create();
        runner.forwardOutput();
        runner.withPluginClasspath();
        runner.withArguments("generateBuildInfo");
        runner.withProjectDir(projectDir);
        return runner.build();
    }

    @Test
    void generatesBuildInfoCode(@TempDir File projectDir) throws IOException {
        List<String> packageSegments = Arrays.asList("com", "opencastsoftware", "buildinfo");
        String packageName = packageSegments.stream().collect(Collectors.joining("."));
        String packageFolder = packageSegments.stream().collect(Collectors.joining(File.separator));

        writeString(getSettingsFile(projectDir), "");
        writeString(getBuildFile(projectDir),
                "plugins {\n" +
                        "  id('java')\n" +
                        "  id('com.opencastsoftware.gradle.buildinfo')\n" +
                        "}\n" +
                        "repositories {\n" +
                        "  mavenCentral()\n" +
                        "}\n" +
                        "buildInfo {\n" +
                        "  packageName = \"" + packageName + "\"\n" +
                        "  className = \"BuildInfoTest\"\n" +
                        "  properties = [gradleVersion: \"7.4.1\"]" +
                        "}");

        runBuildTask(projectDir, "generateBuildInfo");

        Path buildInfoJava = getGeneratedSrcDir(projectDir)
                .resolve(packageFolder)
                .resolve("BuildInfoTest.java");

        assertTrue(Files.exists(buildInfoJava));

        assertEquals(
                "package com.opencastsoftware.buildinfo;\n\n" +
                        "import java.lang.String;\n\n" +
                        "public final class BuildInfoTest {\n" +
                        "  public static final String gradleVersion = \"7.4.1\";\n" +
                        "}\n",
                new String(Files.readAllBytes(buildInfoJava)));
    }

    @Test
    void generatesBuildInfoCodeWithDefaults(@TempDir File projectDir) throws IOException {
        List<String> packageSegments = Arrays.asList("com", "opencastsoftware", "buildinfo");
        String packageName = packageSegments.stream().collect(Collectors.joining("."));
        String packageFolder = packageSegments.stream().collect(Collectors.joining(File.separator));

        writeString(getSettingsFile(projectDir), "");
        writeString(getBuildFile(projectDir),
                "plugins {\n" +
                        "  id('java')\n" +
                        "  id('com.opencastsoftware.gradle.buildinfo')\n" +
                        "}\n" +
                        "repositories {\n" +
                        "  mavenCentral()\n" +
                        "}\n" +
                        "buildInfo {\n" +
                        "  packageName = \"" + packageName + "\"\n" +
                        "}");

        runBuildTask(projectDir, "generateBuildInfo");

        Path buildInfoJava = getBuildInfoJavaFile(projectDir, packageFolder, "BuildInfo");

        assertTrue(Files.exists(buildInfoJava));

        assertEquals(
                "package com.opencastsoftware.buildinfo;\n\n" +
                        "public final class BuildInfo {\n" +
                        "}\n",
                new String(Files.readAllBytes(buildInfoJava)));

    }

    @Test
    void generatesBuildInfoCodeWithDifferentPackage(@TempDir File projectDir) throws IOException {
        List<String> packageSegments = Arrays.asList("io", "github", "davidgregory084");
        String packageName = packageSegments.stream().collect(Collectors.joining("."));
        String packageFolder = packageSegments.stream().collect(Collectors.joining(File.separator));

        writeString(getSettingsFile(projectDir), "");
        writeString(getBuildFile(projectDir),
                "plugins {\n" +
                        "  id('java')\n" +
                        "  id('com.opencastsoftware.gradle.buildinfo')\n" +
                        "}\n" +
                        "repositories {\n" +
                        "  mavenCentral()\n" +
                        "}\n" +
                        "buildInfo {\n" +
                        "  packageName = \"" + packageName + "\"\n" +
                        "}");

        runBuildTask(projectDir, "generateBuildInfo");

        Path buildInfoJava = getBuildInfoJavaFile(projectDir, packageFolder, "BuildInfo");

        assertTrue(Files.exists(buildInfoJava));

        assertEquals(
                "package io.github.davidgregory084;\n\n" +
                        "public final class BuildInfo {\n" +
                        "}\n",
                new String(Files.readAllBytes(buildInfoJava)));
    }

    @Test
    void generatesNothingWhenPackageIsUndefined(@TempDir File projectDir) throws IOException {
        writeString(getSettingsFile(projectDir), "");
        writeString(getBuildFile(projectDir),
                "plugins {\n" +
                        "  id('java')\n" +
                        "  id('com.opencastsoftware.gradle.buildinfo')\n" +
                        "}\n" +
                        "repositories {\n" +
                        "  mavenCentral()\n" +
                        "}");

        runBuildTask(projectDir, "generateBuildInfo");

        try (Stream<Path> entries = Files.list(getGeneratedSrcDir(projectDir))) {
            assertFalse(entries.findFirst().isPresent());
        }
    }

    private void writeString(File file, String string) throws IOException {
        try (Writer writer = new FileWriter(file)) {
            writer.write(string);
        }
    }
}
