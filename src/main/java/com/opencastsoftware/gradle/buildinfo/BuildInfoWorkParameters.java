/*
 * SPDX-FileCopyrightText:  © 2023 Opencast Software Europe Ltd <https://opencastsoftware.com>
 * SPDX-License-Identifier: Apache-2.0
 */
package com.opencastsoftware.gradle.buildinfo;

import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.workers.WorkParameters;

public interface BuildInfoWorkParameters extends WorkParameters {
    @Input
    @Optional
    abstract public Property<String> getPackageName();

    @Input
    abstract public Property<String> getClassName();

    @Input
    abstract public MapProperty<String, String> getProperties();

    @OutputDirectory
    abstract public RegularFileProperty getOutputDirectory();
}
