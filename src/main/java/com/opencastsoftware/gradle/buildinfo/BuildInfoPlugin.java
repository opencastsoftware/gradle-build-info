/*
 * SPDX-FileCopyrightText:  © 2022-2023 Opencast Software Europe Ltd <https://opencastsoftware.com>
 * SPDX-License-Identifier: Apache-2.0
 */
package com.opencastsoftware.gradle.buildinfo;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.plugins.JavaBasePlugin;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.jvm.tasks.Jar;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BuildInfoPlugin implements Plugin<Project> {
    private static final String JAVAPOET_MAVEN_COORD = "com.squareup:javapoet:" + BuildInfo.javaPoetVersion;

    public void apply(Project project) {
        project.getPlugins().withType(JavaBasePlugin.class, javaPlugin -> {
            Configuration buildInfo = configureConfigurations(project);
            BuildInfoExtension extension = createExtension(project);
            Path destDir = getDestinationDirectory(project);
            SourceSet mainSourceSet = configureSourceSet(project, destDir);
            configureBuildInfoTask(project, buildInfo, extension, mainSourceSet, destDir);
        });
    }

    private Configuration configureConfigurations(Project project) {
        return project.getConfigurations()
                .create("buildInfo", config -> {
                    config.setVisible(false);
                    config.setCanBeConsumed(false);
                    config.setCanBeResolved(true);
                    config.setDescription(
                            "Dependencies of the gradle-build-info plugin.");
                    config.defaultDependencies(
                            deps -> deps.add(project.getDependencies().create(JAVAPOET_MAVEN_COORD)));
                });
    }

    private BuildInfoExtension createExtension(Project project) {
        return project.getExtensions().create("buildInfo", BuildInfoExtension.class);
    }

    private Path getDestinationDirectory(Project project) {
        Path buildDir = project.getBuildDir().toPath();

        List<String> destDirFolders = Arrays.asList(
                "generated", "sources", "buildinfo", "java", "main");

        String destDirName = destDirFolders.stream()
                .collect(Collectors.joining(File.separator));

        return buildDir.resolve(destDirName);
    }

    private SourceSet configureSourceSet(Project project, Path destDir) {
        SourceSetContainer sourceSets = project.getExtensions()
                .getByType(SourceSetContainer.class);

        SourceSet mainSourceSet = sourceSets
                .getByName(SourceSet.MAIN_SOURCE_SET_NAME);

        SourceDirectorySet mainJavaSources = mainSourceSet.getJava();

        Set<File> mainSourceDirs = mainJavaSources.getSrcDirs();
        mainSourceDirs.add(destDir.toFile());
        mainJavaSources.setSrcDirs(mainSourceDirs);

        return mainSourceSet;
    }

    private void configureBuildInfoTask(
            Project project,
            Configuration buildInfo,
            BuildInfoExtension extension,
            SourceSet mainSourceSet,
            Path destDir) {
        TaskContainer tasks = project.getTasks();

        TaskProvider<GenerateBuildInfo> generateBuildInfoTask = tasks.register(
                "generateBuildInfo",
                GenerateBuildInfo.class,
                task -> {
                    task.getTaskClasspath()
                            .from(buildInfo);

                    task.getPackageName()
                            .set(extension.getPackageName());

                    task.getClassName()
                            .set(extension.getClassName().convention("BuildInfo"));

                    task.getProperties()
                            .set(extension.getProperties().convention(Collections.emptyMap()));

                    task.getOutputDirectory()
                            .set(destDir.toFile());
                });

        tasks.getByName(mainSourceSet.getCompileJavaTaskName())
                .dependsOn(generateBuildInfoTask);

        tasks.withType(Jar.class).configureEach(task -> {
            if (task.getName().equals(mainSourceSet.getSourcesJarTaskName())) {
                task.dependsOn(generateBuildInfoTask);
            }
        });
    }
}
