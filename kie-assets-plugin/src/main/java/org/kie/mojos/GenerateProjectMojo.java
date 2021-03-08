package org.kie.mojos;

import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.shared.invoker.*;
import org.kie.GeneratedProjectUtils;
import org.kie.model.ProjectDefinition;
import org.kie.model.ProjectStructure;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * Goal which generates project structure using provided archetype.
 */
@Mojo(name = "generate-project", defaultPhase = LifecyclePhase.PROCESS_SOURCES, requiresProject = false)
public class GenerateProjectMojo
        extends AbstractMojoDefiningParameters {
    public void execute()
            throws MojoExecutionException {
        Path f = outputDirectory.toPath();

        if (!Files.exists(f)) {
            try {
                Files.createDirectories(f);
            } catch (IOException e) {
                throw new MojoExecutionException("Error when creating base directory.", e);
            }
        }

        try {
            generateProjectsAtTargetLocation();
        } catch (MavenInvocationException e) {
            throw new MojoExecutionException("Error while generating projects from archetype.", e);
        }
    }

    /**
     * Based on {@linkplain AbstractMojoDefiningParameters#projectDefinitions} and {@linkplain AbstractMojoDefiningParameters#projectStructures}
     * generates the directory structure for Cartesian product of these two lists.
     * @throws MavenInvocationException up on failure during the project generation
     */
    private void generateProjectsAtTargetLocation() throws MavenInvocationException, MojoExecutionException {
        for (ProjectDefinition definition: projectDefinitions) {
            for (ProjectStructure structure : projectStructures) {
                generateFromArchetype(definition, structure);
            }
        }
    }

    /**
     * Generates project using archetype defined in {@linkplain ProjectStructure#getArchetype()} providing properties from
     * {@linkplain ProjectDefinition#getGroupId()}, {@linkplain ProjectDefinition#getArtifactId()}, {@linkplain ProjectDefinition#getPackageName()}
     * @param definition
     * @param projectStructure
     * @throws MavenInvocationException upon maven archetype:generate run failure
     */
    private void generateFromArchetype(ProjectDefinition definition,ProjectStructure projectStructure) throws MavenInvocationException, MojoExecutionException {
            InvocationRequest request = new DefaultInvocationRequest();
            request.setGoals(Collections.singletonList("archetype:generate"));
            Properties properties = new Properties();
            properties.setProperty("interactiveMode", "false");
            properties.setProperty("groupId", definition.getGroupId());
            properties.setProperty("artifactId", GeneratedProjectUtils.getTargetProjectName(definition, projectStructure));
            properties.setProperty("package", definition.getPackageName());
            properties.setProperty("archetypeVersion", projectStructure.getArchetype().getVersion());
            properties.setProperty("archetypeGroupId", projectStructure.getArchetype().getGroupId());
            properties.setProperty("archetypeArtifactId", projectStructure.getArchetype().getArtifactId());
            request.setProperties(properties);
            Invoker invoker = new DefaultInvoker();
            invoker.setWorkingDirectory(outputDirectory);
            InvocationResult result = invoker.execute(request);
            if (result.getExitCode()!=0) {
                throw new MojoExecutionException("Error during archetype generation.", result.getExecutionException());
            }
    }
}
