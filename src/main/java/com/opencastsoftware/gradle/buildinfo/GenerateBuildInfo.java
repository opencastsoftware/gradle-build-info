/*
 * SPDX-FileCopyrightText:  Copyright 2022-2023 Opencast Software Europe Ltd
 * SPDX-License-Identifier: Apache-2.0
 */
package com.opencastsoftware.gradle.buildinfo;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import javax.lang.model.element.Modifier;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public abstract class GenerateBuildInfo extends DefaultTask {
    @Input
    abstract public Property<String> getPackageName();

    @Input
    abstract public Property<String> getClassName();

    @Input
    abstract public MapProperty<String, String> getProperties();

    @OutputDirectory
    abstract public RegularFileProperty getOutputDirectory();

    @TaskAction
    public void generate() throws IOException {
        String className = getClassName().get();
        String packageName = getPackageName().get();
        Map<String, String> properties = getProperties().get();
        File outputDirectory = getOutputDirectory().get().getAsFile();

        TypeSpec.Builder classBuilder = TypeSpec
                .classBuilder(className)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        for (Map.Entry<String, String> property : properties.entrySet()) {
            String propertyName = property.getKey();
            String propertyValue = property.getValue();

            FieldSpec fieldSpec = FieldSpec
                    .builder(String.class, propertyName, Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("$S", propertyValue)
                    .build();

            classBuilder.addField(fieldSpec);
        }

        JavaFile buildInfoFile = JavaFile
                .builder(packageName, classBuilder.build())
                .build();

        buildInfoFile.writeTo(outputDirectory);
    }
}
