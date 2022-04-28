package com.opencastsoftware.gradle.buildinfo;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

class BuildInfoPluginTest {
    @Test
    void pluginRegistersSuccessfully() {
        Project project = ProjectBuilder.builder().build();
        project.getPlugins().apply("com.opencastsoftware.gradle.buildinfo");
        assertNotNull(project.getPluginManager().hasPlugin("com.opencastsoftware.gradle.buildinfo"));
    }
}
