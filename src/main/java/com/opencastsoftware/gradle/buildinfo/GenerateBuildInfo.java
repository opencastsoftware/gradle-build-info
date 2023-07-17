/*
 * SPDX-FileCopyrightText:  © 2022-2023 Opencast Software Europe Ltd <https://opencastsoftware.com>
 * SPDX-License-Identifier: Apache-2.0
 */
package com.opencastsoftware.gradle.buildinfo;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.*;
import org.gradle.workers.WorkQueue;
import org.gradle.workers.WorkerExecutor;

import javax.inject.Inject;

public abstract class GenerateBuildInfo extends DefaultTask {
    @InputFiles
    abstract public ConfigurableFileCollection getTaskClasspath();

    @Input
    @Optional
    abstract public Property<String> getPackageName();

    @Input
    abstract public Property<String> getClassName();

    @Input
    abstract public MapProperty<String, String> getProperties();

    @OutputDirectory
    abstract public RegularFileProperty getOutputDirectory();

    @Inject
    abstract public WorkerExecutor getWorkerExecutor();

    @TaskAction
    public void generate() {
        WorkQueue workQueue = getWorkerExecutor().classLoaderIsolation(workerSpec -> {
            workerSpec.getClasspath().from(getTaskClasspath());
        });

        workQueue.submit(GenerateBuildInfoAction.class, parameters -> {
            parameters.getPackageName().set(getPackageName());
            parameters.getClassName().set(getClassName());
            parameters.getProperties().set(getProperties());
            parameters.getOutputDirectory().set(getOutputDirectory());
        });
    }
}
