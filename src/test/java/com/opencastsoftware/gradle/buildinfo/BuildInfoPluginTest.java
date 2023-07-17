/*
 * SPDX-FileCopyrightText:  © 2022-2023 Opencast Software Europe Ltd <https://opencastsoftware.com>
 * SPDX-License-Identifier: Apache-2.0
 */
package com.opencastsoftware.gradle.buildinfo;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class BuildInfoPluginTest {
    @Test
    void pluginRegistersSuccessfully() {
        Project project = ProjectBuilder.builder().build();
        project.getPlugins().apply("com.opencastsoftware.gradle.buildinfo");
        assertNotNull(project.getPluginManager().hasPlugin("com.opencastsoftware.gradle.buildinfo"));
    }
}
