package com.opencastsoftware.gradle.buildinfo;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskProvider;

public class BuildInfoPlugin implements Plugin<Project> {
    public void apply(Project project) {
        project.getPlugins().withType(JavaPlugin.class, javaPlugin -> {
            BuildInfoExtension extension = project.getExtensions()
                    .create("buildInfo", BuildInfoExtension.class);

            Path buildDir = project.getBuildDir().toPath();

            List<String> destDirFolders = Arrays.asList(
                    "generated", "sources", "buildinfo", "java", "main");

            String destDirName = destDirFolders.stream().collect(Collectors.joining(File.separator));

            Path destDir = buildDir.resolve(destDirName);

            SourceSetContainer sourceSets = project.getExtensions()
                    .getByType(SourceSetContainer.class);

            SourceDirectorySet mainJavaSources = sourceSets
                    .getByName(SourceSet.MAIN_SOURCE_SET_NAME)
                    .getJava();

            Set<File> mainSourceDirs = mainJavaSources.getSrcDirs();
            mainSourceDirs.add(destDir.toFile());
            mainJavaSources.setSrcDirs(mainSourceDirs);

            TaskProvider<GenerateBuildInfo> generateBuildInfoTask = project.getTasks()
                    .register("generateBuildInfo", GenerateBuildInfo.class, task -> {
                        task.getPackageName()
                                .set(extension.getPackageName());

                        task.getClassName()
                                .set(extension.getClassName().convention("BuildInfo"));

                        task.getProperties()
                                .set(extension.getProperties().convention(Collections.emptyMap()));

                        task.getOutputDirectory()
                                .set(destDir.toFile());
                    });

            project.getTasks()
                    .getByName(JavaPlugin.COMPILE_JAVA_TASK_NAME)
                    .dependsOn(generateBuildInfoTask);
        });

    }
}
