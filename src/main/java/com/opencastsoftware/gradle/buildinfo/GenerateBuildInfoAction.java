/*
 * SPDX-FileCopyrightText:  © 2022-2023 Opencast Software Europe Ltd <https://opencastsoftware.com>
 * SPDX-License-Identifier: Apache-2.0
 */
package com.opencastsoftware.gradle.buildinfo;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;
import org.gradle.internal.UncheckedException;
import org.gradle.workers.WorkAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.lang.model.element.Modifier;

import java.io.IOException;
import java.util.Map;

public abstract class GenerateBuildInfoAction implements WorkAction<BuildInfoWorkParameters> {
    private final Logger logger = LoggerFactory.getLogger(GenerateBuildInfoAction.class);

    @Override
    public void execute() {
        Property<String> packageName = getParameters().getPackageName();
        Property<String> className = getParameters().getClassName();
        MapProperty<String, String> properties = getParameters().getProperties();
        RegularFileProperty outputDirectory = getParameters().getOutputDirectory();

        if (packageName.getOrNull() == null) {
            logger.warn(
                    "The gradle-build-info plugin is enabled, but no package name has been defined for the generated code. "
                            + "No build info code will be generated.");
            return;
        }

        TypeSpec.Builder classBuilder = TypeSpec
                .classBuilder(className.get())
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        for (Map.Entry<String, String> property : properties.get().entrySet()) {
            String propertyName = property.getKey();
            String propertyValue = property.getValue();

            FieldSpec fieldSpec = FieldSpec
                    .builder(String.class, propertyName, Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("$S", propertyValue)
                    .build();

            classBuilder.addField(fieldSpec);
        }

        JavaFile buildInfoFile = JavaFile
                .builder(packageName.get(), classBuilder.build())
                .build();

        try {
            buildInfoFile.writeTo(outputDirectory.get().getAsFile());
        } catch (IOException e) {
            UncheckedException.throwAsUncheckedException(e);
        }
    }
}
