/*
 * SPDX-FileCopyrightText:  Copyright 2022-2023 Opencast Software Europe Ltd
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

import static org.junit.jupiter.api.Assertions.assertEquals;

class BuildInfoPluginFunctionalTest {
    @TempDir
    File projectDir;

    private File getBuildFile() {
        return new File(projectDir, "build.gradle");
    }

    private File getSettingsFile() {
        return new File(projectDir, "settings.gradle");
    }

    private Path getGeneratedSrcDir() {
        return projectDir.toPath()
                .resolve("build")
                .resolve("generated")
                .resolve("sources")
                .resolve("buildinfo")
                .resolve("java")
                .resolve("main");
    }

    private Path getBuildInfoJavaFile(String packageFolder, String className) {
        return getGeneratedSrcDir()
                .resolve(packageFolder)
                .resolve(className + ".java");
    }

    private BuildResult runBuildTask(String taskName) {
        GradleRunner runner = GradleRunner.create();
        runner.forwardOutput();
        runner.withPluginClasspath();
        runner.withArguments("generateBuildInfo");
        runner.withProjectDir(projectDir);
        return runner.build();
    }

    @Test
    void generatesBuildInfoCode() throws IOException {
        List<String> packageSegments = Arrays.asList("com", "opencastsoftware", "buildinfo");
        String packageName = packageSegments.stream().collect(Collectors.joining("."));
        String packageFolder = packageSegments.stream().collect(Collectors.joining(File.separator));

        writeString(getSettingsFile(), "");
        writeString(getBuildFile(),
                "plugins {\n" +
                        "  id('java')\n" +
                        "  id('com.opencastsoftware.gradle.buildinfo')\n" +
                        "}\n" +
                        "buildInfo {\n" +
                        "  packageName = \"" + packageName + "\"\n" +
                        "  className = \"BuildInfoTest\"\n" +
                        "  properties = [gradleVersion: \"7.4.1\"]" +
                        "}");

        runBuildTask("generateBuildInfo");

        Path buildInfoJava = getGeneratedSrcDir()
                .resolve(packageFolder)
                .resolve("BuildInfoTest.java");

        assert (Files.exists(buildInfoJava));
        assertEquals(
                "package com.opencastsoftware.buildinfo;\n\n" +
                        "import java.lang.String;\n\n" +
                        "public final class BuildInfoTest {\n" +
                        "  public static final String gradleVersion = \"7.4.1\";\n" +
                        "}\n",
                new String(Files.readAllBytes(buildInfoJava)));
    }

    @Test
    void generatesBuildInfoCodeWithDefaults() throws IOException {
        List<String> packageSegments = Arrays.asList("com", "opencastsoftware", "buildinfo");
        String packageName = packageSegments.stream().collect(Collectors.joining("."));
        String packageFolder = packageSegments.stream().collect(Collectors.joining(File.separator));

        writeString(getSettingsFile(), "");
        writeString(getBuildFile(),
                "plugins {\n" +
                        "  id('java')\n" +
                        "  id('com.opencastsoftware.gradle.buildinfo')\n" +
                        "}\n" +
                        "buildInfo {\n" +
                        "  packageName = \"" + packageName + "\"\n" +
                        "}");

        runBuildTask("generateBuildInfo");

        Path buildInfoJava = getBuildInfoJavaFile(packageFolder, "BuildInfo");

        assert (Files.exists(buildInfoJava));

        assertEquals(
                "package com.opencastsoftware.buildinfo;\n\n" +
                        "public final class BuildInfo {\n" +
                        "}\n",
                new String(Files.readAllBytes(buildInfoJava)));

    }

    @Test
    void generatesBuildInfoCodeWithDifferentPackage() throws IOException {
        List<String> packageSegments = Arrays.asList("io", "github", "davidgregory084");
        String packageName = packageSegments.stream().collect(Collectors.joining("."));
        String packageFolder = packageSegments.stream().collect(Collectors.joining(File.separator));

        writeString(getSettingsFile(), "");
        writeString(getBuildFile(),
                "plugins {\n" +
                        "  id('java')\n" +
                        "  id('com.opencastsoftware.gradle.buildinfo')\n" +
                        "}\n" +
                        "buildInfo {\n" +
                        "  packageName = \"" + packageName + "\"\n" +
                        "}");

        runBuildTask("generateBuildInfo");

        Path buildInfoJava = getBuildInfoJavaFile(packageFolder, "BuildInfo");

        assert (Files.exists(buildInfoJava));

        assertEquals(
                "package io.github.davidgregory084;\n\n" +
                        "public final class BuildInfo {\n" +
                        "}\n",
                new String(Files.readAllBytes(buildInfoJava)));
    }

    private void writeString(File file, String string) throws IOException {
        try (Writer writer = new FileWriter(file)) {
            writer.write(string);
        }
    }
}
