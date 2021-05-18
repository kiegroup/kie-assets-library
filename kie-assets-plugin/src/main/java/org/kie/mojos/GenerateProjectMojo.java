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
import org.kie.utils.MaskedMavenMojoException;
import org.kie.utils.ThrowingBiConsumer;
import org.kie.utils.GeneratedProjectUtils;
import org.kie.model.ConfigSet;
import org.kie.model.ProjectDefinition;
import org.kie.model.ProjectStructure;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
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
        } catch (MaskedMavenMojoException e) {
            throw new MojoExecutionException("Error while generating projects from archetype.", e);
        }
    }

    /**
     * Using {@linkplain AbstractMojoDefiningParameters#getActiveSetup()}
     * generates the directory structure for the current combination of definition and structure.
     */
    private void generateProjectsAtTargetLocation() {
        getLog().info("Importing resources");
        getActiveSetup().apply(generateProjects());
    }

    /**
     * Method that for given definition and structure generates the directory structure for all the active configurations.
     * <p>
     * A BiConsumer implementation to be used together with {@linkplain AbstractMojoDefiningParameters#getActiveSetup()},
     * passed through method {@linkplain AbstractMojoDefiningParameters.ActiveSetup#apply(BiConsumer)}.
     *
     * @return BiConsumer action over {@linkplain ProjectDefinition} and {@linkplain ProjectStructure}.
     */
    private ThrowingBiConsumer generateProjects() {
        return (definition, structure) -> {
            getLog().info("active only"+activeStructureIds);
            getLog().info("Invoked with "+definition.getId()+", strucutre:"+structure.getId());
            generateFromArchetype(definition, structure);
            addPomDependencies(definition, structure);
            setFinalNameInPom(definition, structure);
        };
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

        getLog().info("Inside invoker "+definition.getId()+", strucutre:"+projectStructure.getId());
        InvocationRequest request = new DefaultInvocationRequest();
        request.setGoals(Collections.singletonList("archetype:generate"));
        request.setUserSettingsFile(mavenSession.getRequest().getUserSettingsFile());
        request.setLocalRepositoryDirectory(mavenSession.getRequest().getLocalRepositoryPath());
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
            throw new MojoExecutionException("Error during archetype generation. See previous errors in log.", result.getExecutionException());
        }
    }

    /**
     * Manipulate the POM files of generated project. Add dependencies defined by {@linkplain ConfigSet#getDependencies()} in:
     * <ul>
     *     <li>{@linkplain ProjectDefinition#getConfig()}</li>
     *     <li>{@linkplain ProjectStructure#getCommonConfig()}</li>
     *     <li>{@linkplain ProjectStructure#getConfigSets()}
     *     with {@linkplain ConfigSet#getId()} matching one of {@linkplain #activeConfigSets}</li>
     * </ul>
     * @param definition project definition to get references from
     * @param structure  project structure to get config-set with matching ids from
     * @throws MojoExecutionException on error during file manipulation
     */
    private void addPomDependencies(ProjectDefinition definition, ProjectStructure structure) throws MojoExecutionException {
        Path pomFile = getPathToPom(definition, structure);
        manipulatePom(pomFile, project -> {
            project.getDependencies().addAll(definition.getConfig().getDependencies());
            project.getDependencies().addAll(structure.getCommonConfig().getDependencies());
            project.getDependencies().addAll(
                    structure.getConfigSets().stream()
                            .filter(it -> activeConfigSets.contains(it.getId()))
                            .flatMap(it -> it.getDependencies().stream())
                            .collect(Collectors.toList())
            );
        });
    }

    /**
     * Allow setting finalName from ProjectDefinition to pom build configuration.
     * @param definition project definition to take finalName from
     * @param structure project structure to use for pom location resolution
     * @throws MojoExecutionException
     */
    private void setFinalNameInPom(ProjectDefinition definition, ProjectStructure structure) throws MojoExecutionException {
        if (definition.getFinalName()==null || definition.getFinalName().isEmpty()) {
            getLog().debug("No finalName specified, not changing build configuration.");
            return;
        }
        Path pathToPom = getPathToPom(definition, structure);
        manipulatePom(pathToPom, project -> project.getBuild().setFinalName(definition.getFinalName()));
    }

    /**
     * Get path to pom file for given definition : structure pair.
     * @param definition definition to use for path resolution
     * @param structure structure to use for path resolution
     * @return path to pom.xml
     */
    private Path getPathToPom(ProjectDefinition definition, ProjectStructure structure) {
        Path projectDir = GeneratedProjectUtils.getOutputDirectoryForArchetype(outputDirectory.toPath(), definition, structure);
        Path pomFile = projectDir.resolve("pom.xml");
        return pomFile;
    }

    /**
     * Get Maven Model from given pomFile.
     * @param pomFile path to pom.xml
     * @return
     * @throws MojoExecutionException
     */
    private Model getPomModel(Path pomFile) throws MojoExecutionException {
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
        return model;
    }

    /**
     * Method that accepts path to pom file and operation to be applied on the MavenProject
     * instance coming from loading it.
     * @param pathToPom Path to the pom to load and save to after changes.
     * @param manipulator consumer that receives {@linkplain MavenProject} instance.
     * @throws MojoExecutionException when error during manipulation occurs.
     */
    private void manipulatePom(Path pathToPom, Consumer<MavenProject> manipulator) throws MojoExecutionException {
        Model model = getPomModel(pathToPom);
        try (
                FileOutputStream fileWriter = new FileOutputStream(pathToPom.toFile());
        ) {
            MavenProject project = new MavenProject(model);
            manipulator.accept(project);
            MavenXpp3Writer mavenWriter = new MavenXpp3Writer();
            mavenWriter.write(fileWriter, model);
        } catch (IOException e) {
            throw new MojoExecutionException("Error while saving manipulated pom: " + pathToPom, e);
        }
    }
}
