package org.kie.mojos;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.invoker.*;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.kie.GeneratedProjectUtils;
import org.kie.model.ConfigSet;
import org.kie.model.ProjectDefinition;
import org.kie.model.ProjectStructure;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

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
     *
     * @throws MavenInvocationException up on failure during the project generation
     */
    private void generateProjectsAtTargetLocation() throws MavenInvocationException, MojoExecutionException {
        for (ProjectDefinition definition : projectDefinitions) {
            for (ProjectStructure structure : projectStructures) {
                generateFromArchetype(definition, structure);
                addPomDependencies(definition, structure);
            }
        }
    }

    /**
     * Generates project using archetype defined in {@linkplain ProjectStructure#getArchetype()} providing properties from
     * {@linkplain ProjectDefinition#getGroupId()}, {@linkplain ProjectDefinition#getArtifactId()}, {@linkplain ProjectDefinition#getPackageName()}
     *
     * @param definition
     * @param projectStructure
     * @throws MavenInvocationException upon maven archetype:generate run failure
     */
    private void generateFromArchetype(ProjectDefinition definition, ProjectStructure projectStructure) throws MavenInvocationException, MojoExecutionException {
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
        if (result.getExitCode() != 0) {
            throw new MojoExecutionException("Error during archetype generation.", result.getExecutionException());
        }
    }

    /**
     * Manipulate the POM files of generated project. Add dependencies based on matching {@linkplain ConfigSet#getId()}
     * of {@linkplain ProjectStructure#getConfigSets()} with values defined by {@linkplain ProjectDefinition#getConfigSetReferences()}.
     *
     * @param definition project definition to get references from
     * @param structure  project structure to get config-set with matching ids from
     * @throws MojoExecutionException on error during file manipulation
     */
    private void addPomDependencies(ProjectDefinition definition, ProjectStructure structure) throws MojoExecutionException {
        Path projectDir = GeneratedProjectUtils.getOutputDirectoryForArchetype(outputDirectory.toPath(), definition, structure);
        Path pomFile = projectDir.resolve("pom.xml");
        Model model = null;
        try (
                FileInputStream fileReader = new FileInputStream(pomFile.toFile());
        ) {
            MavenXpp3Reader mavenReader = new MavenXpp3Reader();
            model = mavenReader.read(fileReader);
            model.setPomFile(pomFile.toFile());
        } catch (IOException | XmlPullParserException e) {
            throw new MojoExecutionException("Error while opening generated pom: " + pomFile, e);
        }
        try (
                FileOutputStream fileWriter = new FileOutputStream(pomFile.toFile());
        ) {
            MavenProject project = new MavenProject(model);
            project.getDependencies().addAll(
                    structure.getConfigSets().stream().filter(
                            it -> definition.getConfigSetReferences().contains(it.getId())
                    ).flatMap(it -> it.getDependencies().stream()).collect(Collectors.toList())
            );
            MavenXpp3Writer mavenWriter = new MavenXpp3Writer();
            mavenWriter.write(fileWriter, model);
        } catch (IOException e) {
            throw new MojoExecutionException("Error while saving manipulated pom: " + pomFile, e);
        }
    }
}
