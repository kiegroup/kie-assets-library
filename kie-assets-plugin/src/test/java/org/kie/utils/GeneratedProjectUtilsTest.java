/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.utils;

import java.nio.file.Path;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.kie.model.ProjectDefinition;
import org.kie.model.ProjectStructure;

public class GeneratedProjectUtilsTest {
    @Rule
    public ExpectedException exceptionGrabber = ExpectedException.none();

    @Test
    public void testTargetProjectName() {
        ProjectDefinition definition = new ProjectDefinition();
        definition.setArtifactId("test-artifact-id");
        ProjectStructure structure = new ProjectStructure();
        structure.setId("test-structure-id");
        Assert.assertThat(GeneratedProjectUtils.getTargetProjectName(definition, structure), Matchers.equalTo("test-artifact-id-test-structure-id"));
    }

    @Test
    public void testTargetProjectNameMissingStructureId() {
        ProjectDefinition definition = new ProjectDefinition();
        definition.setArtifactId("test-artifact-id");
        ProjectStructure structure = new ProjectStructure();
        exceptionGrabber.expectMessage("ProjectStructure instance has to have non-empty id.");
        GeneratedProjectUtils.getTargetProjectName(definition, structure);
    }

    @Test
    public void testTargetProjectNameMissingDefinitionArtifactId() {
        ProjectDefinition definition = new ProjectDefinition();
        ProjectStructure structure = new ProjectStructure();
        structure.setId("test-structure-id");
        exceptionGrabber.expectMessage("ProjectDefinition instance has to have non-empty artifactId.");
        GeneratedProjectUtils.getTargetProjectName(definition, structure);
    }

    @Test
    public void testGetOutputDirectory() {
        ProjectDefinition definition = new ProjectDefinition();
        definition.setArtifactId("test-artifact-id");
        ProjectStructure structure = new ProjectStructure();
        structure.setId("test-structure-id");
        Path parentPath = Path.of(System.getProperty("java.io.tmpdir"));
        Assert.assertThat(GeneratedProjectUtils.getOutputDirectoryForGeneratedProject(parentPath, definition, structure),
                Matchers.equalTo(parentPath.resolve("test-artifact-id-test-structure-id")));
    }
}
