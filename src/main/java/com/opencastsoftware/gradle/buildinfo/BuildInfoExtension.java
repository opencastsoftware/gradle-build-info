/*
 * SPDX-FileCopyrightText:  © 2022-2023 Opencast Software Europe Ltd <https://opencastsoftware.com>
 * SPDX-License-Identifier: Apache-2.0
 */
package com.opencastsoftware.gradle.buildinfo;

import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;

abstract public class BuildInfoExtension {
    abstract public Property<String> getPackageName();
    abstract public Property<String> getClassName();
    abstract public MapProperty<String, String> getProperties();
}
