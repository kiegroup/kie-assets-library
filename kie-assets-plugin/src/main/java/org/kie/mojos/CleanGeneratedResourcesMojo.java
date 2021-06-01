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
package org.kie.mojos;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.kie.model.ProjectDefinition;
import org.kie.model.ProjectStructure;
import org.kie.utils.FileFilteringUtils;
import org.kie.utils.GeneratedProjectUtils;
import org.kie.utils.MaskedMavenMojoException;
import org.kie.utils.ThrowingBiConsumer;

/**
 * Goal which cleans requested artifacts from generated project. Follows after {@linkplain GenerateProjectMojo}.
 */
@Mojo(name = "clean-generated-resources", defaultPhase = LifecyclePhase.PROCESS_SOURCES, requiresProject = false)
public class CleanGeneratedResourcesMojo extends AbstractMojoDefiningParameters {

    /**
     * Parameter to affect cleanup behavior, if true, also src/main/* directory is cleaned with one exception - src/main/resources is kept.
     */
    @Parameter(defaultValue = "false", property = "clean.src.main")
    private boolean wipeSrcMain;

    /**
     * Parameter to affect cleanup behavior, if true, also src/main/resources directory is cleaned. Requires {@linkplain #wipeSrcMain} to be set to true.
     */
    @Parameter(defaultValue = "false", property = "clean.src.main.resources")
    private boolean wipeSrcMainResources;

    /**
     * Parameter to affect cleanup behavior, if true, also src/test/* directory is cleaned with one exception - src/test/resources is kept.
     */
    @Parameter(defaultValue = "true", property = "clean.src.test")
    private boolean wipeSrcTest;

    /**
     * Parameter to affect cleanup behavior, if true, also src/test/resources directory is cleaned. Requires {@linkplain #wipeSrcTest} to be set to true.
     */
    @Parameter(defaultValue = "false", property = "clean.src.test.resources")
    private boolean wipeSrcTestResources;

    @Override
    public void execute() throws MojoExecutionException {
        try {
            cleanupGeneratedResources();
        } catch (MaskedMavenMojoException e) {
            throw new MojoExecutionException("Couldn't cleanup generated resources.", e);
        }
    }

    /**
     * Process the cleanup based on the configuration.
     * <ul>
     * <li>generated project directory denoted by {@linkplain GeneratedProjectUtils#getOutputDirectoryForArchetype(Path, ProjectDefinition, ProjectStructure)}</li>
     * <li>&lt;delete-resources&gt; {@linkplain Resource} specifications within {@linkplain AbstractMojoDefiningParameters#projectStructures}.</li>
     * <li>considers {@linkplain #wipeSrcMain} and {@linkplain #wipeSrcMainResources}</li>
     * <li>considers {@linkplain #wipeSrcTest} and {@linkplain #wipeSrcTestResources}</li>
     * </ul>
     */
    private void cleanupGeneratedResources() {
        getLog().info("Deleting resources");
        getActiveSetup().apply(cleanupAction());
    }

    /**
     * Method that for given definition and structure processes the cleanup for all the active configurations.
     * <p>
     * A BiConsumer implementation to be used together with {@linkplain AbstractMojoDefiningParameters#getActiveSetup()},
     * passed through method {@linkplain AbstractMojoDefiningParameters.ActiveSetup#apply(BiConsumer)}.
     *
     * @return BiConsumer action over {@linkplain ProjectDefinition} and {@linkplain ProjectStructure}.
     */
    private ThrowingBiConsumer cleanupAction() {
        return (definition, structure) -> {
            Path outputDirectoryForArchetype = GeneratedProjectUtils.getOutputDirectoryForArchetype(outputDirectory.toPath(), definition, structure);
            List<Path> files = new ArrayList<>();
            if (wipeSrcMain) {
                getLog().debug("Cleaning src/main directory in " + outputDirectoryForArchetype);
                Resource srcMainJavaResource = new Resource();
                srcMainJavaResource.setDirectory("src/main");
                srcMainJavaResource.getIncludes().add("**/*");
                if (!wipeSrcMainResources) {
                    srcMainJavaResource.getExcludes().add("resources/*");
                    srcMainJavaResource.getExcludes().add("resources/**/*");
                }
                files.addAll(FileFilteringUtils.filterFilesStartingAtPath(outputDirectoryForArchetype.resolve(srcMainJavaResource.getDirectory()), srcMainJavaResource));
            }
            if (wipeSrcTest) {
                getLog().debug("Cleaning src/test directory in " + outputDirectoryForArchetype);
                Resource srcTestJavaResource = new Resource();
                srcTestJavaResource.setDirectory("src/test");
                srcTestJavaResource.getIncludes().add("*");
                srcTestJavaResource.getIncludes().add("**/*");
                if (!wipeSrcTestResources) {
                    srcTestJavaResource.getExcludes().add("resources/*");
                    srcTestJavaResource.getExcludes().add("resources/**/*");
                }
                files.addAll(FileFilteringUtils.filterFilesStartingAtPath(outputDirectoryForArchetype.resolve(srcTestJavaResource.getDirectory()), srcTestJavaResource));
            }
            for (Resource resource : structure.getDeleteResources()) {
                files.addAll(FileFilteringUtils.filterFilesStartingAtPath(outputDirectoryForArchetype.resolve(resource.getDirectory()), resource));
            }
            for (Path f : files) {
                getLog().debug(String.format("Cleaning up resource %s", f));
                Files.delete(f);
            }
        };
    }
}
