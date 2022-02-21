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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Model;
import org.apache.maven.plugin.MojoExecutionException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class PomManipulationUtilsTest {

    @Rule
    public ExpectedException exceptionGrabber = ExpectedException.none();

    @Test
    public void loadSimplePomModel() throws MojoExecutionException {
        Model pomModel = PomManipulationUtils.loadPomModel(Path.of(getClass().getResource("/pom-manipulation-utils/simple-pom.xml").getFile()).toAbsolutePath());
        Assert.assertThat(pomModel.getGroupId(), Matchers.equalTo("org.acme"));
        Assert.assertThat(pomModel.getArtifactId(), Matchers.equalTo("simple-pom"));
    }

    @Test
    public void loadInvalidPomModel() throws MojoExecutionException {
        exceptionGrabber.expect(MojoExecutionException.class);
        exceptionGrabber.expectMessage("Error while opening generated pom: ");
        PomManipulationUtils.loadPomModel(Path.of(getClass().getResource("/pom-manipulation-utils/invalid-pom.xml").getFile()).toAbsolutePath());
    }

    @Test
    public void manipulateSimplePomModel() throws MojoExecutionException, IOException {
        Path pomFile = Path.of(getClass().getResource("/pom-manipulation-utils/simple-pom.xml").getFile()).toAbsolutePath();
        Model pomModel = PomManipulationUtils.loadPomModel(pomFile);
        Assert.assertThat(pomModel.getGroupId(), Matchers.equalTo("org.acme"));
        Assert.assertThat(pomModel.getArtifactId(), Matchers.equalTo("simple-pom"));
        Assert.assertThat(pomModel.getProperties(), Matchers.aMapWithSize(0));
        File tmpCpy = File.createTempFile("pom-manipulation-test", null);
        FileUtils.copyFile(pomFile.toFile(), tmpCpy);
        PomManipulationUtils.manipulatePom(PomManipulationUtils.loadPomModel(tmpCpy.toPath()), it -> it.getProperties().put("test.prop", "value"));
        Model pomModelAfterSave = PomManipulationUtils.loadPomModel(tmpCpy.toPath());
        Assert.assertThat(pomModelAfterSave.getProperties(), Matchers.aMapWithSize(1));
        Assert.assertThat(pomModelAfterSave.getProperties(), Matchers.hasEntry("test.prop", "value"));
    }

}
