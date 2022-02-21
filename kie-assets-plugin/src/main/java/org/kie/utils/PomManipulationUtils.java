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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Consumer;

import org.apache.maven.Maven;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

public class PomManipulationUtils {

    /**
     * Get Maven Model from given pomFile.
     *
     * @param pomFile path to pom.xml
     * @return
     * @throws MojoExecutionException
     */
    public static Model loadPomModel(Path pomFile) throws MojoExecutionException {
        Model model = null;
        try (
                FileInputStream fileReader = new FileInputStream(pomFile.toFile());) {
            MavenXpp3Reader mavenReader = new MavenXpp3Reader();
            model = mavenReader.read(fileReader);
            model.setPomFile(pomFile.toFile());
        } catch (IOException | XmlPullParserException e) {
            throw new MojoExecutionException("Error while opening generated pom: " + pomFile, e);
        }
        return model;
    }

    /**
     * Method that accepts maven Model for given pom file and operation to be applied on the MavenProject
     * instance denoted by the Model instance.
     *
     * @param model Loaded maven pom model to manipulate and save to after changes.
     * @param manipulator consumer that receives {@linkplain MavenProject} instance.
     * @throws MojoExecutionException when error during manipulation occurs.
     */
    public static void manipulatePom(Model model, Consumer<MavenProject> manipulator) throws MojoExecutionException {
        try (
                FileOutputStream fileWriter = new FileOutputStream(model.getPomFile())) {
            MavenProject project = new MavenProject(model);
            manipulator.accept(project);
            MavenXpp3Writer mavenWriter = new MavenXpp3Writer();
            mavenWriter.write(fileWriter, model);
        } catch (IOException e) {
            throw new MojoExecutionException("Error while saving manipulated pom: " + model.getPomFile(), e);
        }
    }
}
