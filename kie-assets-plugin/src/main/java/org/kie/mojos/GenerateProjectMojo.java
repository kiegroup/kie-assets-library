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

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.Formatter;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.apache.maven.model.Model;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.kie.model.ConfigSet;
import org.kie.model.ProjectDefinition;
import org.kie.model.ProjectGeneration;
import org.kie.model.ProjectStructure;
import org.kie.utils.CliUtils;
import org.kie.utils.GeneratedProjectUtils;
import org.kie.utils.MaskedMavenMojoException;
import org.kie.utils.PomManipulationUtils;
import org.kie.utils.ThrowingBiConsumer;

/**
 * Goal which generates project structure using provided generation method.
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
            throw new MojoExecutionException("Error while generating projects.", e);
        }
    }

    /**
     * Using {@linkplain AbstractMojoDefiningParameters#getActiveMojoSetup()}
     * generates the directory structure for the current combination of definition and structure.
     */
    private void generateProjectsAtTargetLocation() {
        getLog().info("Importing resources");
        getActiveMojoSetup().apply(generateProjects());
    }

    /**
     * Method that for given definition and structure generates the directory structure for all the active configurations.
     * <p>
     * A BiConsumer implementation to be used together with {@linkplain AbstractMojoDefiningParameters#getActiveMojoSetup()},
     * passed through method {@linkplain ActiveMojoSetup#apply(BiConsumer)}.
     *
     * @return BiConsumer action over {@linkplain ProjectDefinition} and {@linkplain ProjectStructure}.
     */
    private ThrowingBiConsumer generateProjects() {
        return (definition, structure) -> {
            getLog().info("Active definition expressions:" + activeDefinitions);
            getLog().info("Active structure expressions:" + activeStructures);
            getLog().info("About to generate using definition '" + definition.getId() + "' and structure '" + structure.getId() + "'");
            generateProjectBasedOnConfiguration(definition, structure);
            addPomDependencies(definition, structure);
            setFinalNameInPom(definition, structure);
            addPomProperties(definition, structure);
            addMavenConfigFile(definition, structure);
            writeModuleIntoGroupingPom(definition, structure);
        };
    }

    /**
     * Writes a module denoting the generated projects for given definition and structure combination into a grouping project.
     * Grouping project is created per ProjectStructure, so grouping all ProjectDefinitions that were generated with given structure.
     * <p />
     * The grouping project is named to indicate for which structure the project was created:
     * 
     * <pre>
     * [structure.id]-grouping-project-pom.xml
     * </pre>
     * 
     * So it needs to be executed using the -f maven option to execute the file directly.
     * <p />
     * The file is located in {@linkplain #outputDirectory} folder, so all modules are referenced just by the module name
     * (no relative paths in <module></module> element are needed).
     *
     * @param definition ProjectDefinition to process
     * @param structure ProjectStructure to process
     * @throws MojoExecutionException
     */
    private void writeModuleIntoGroupingPom(ProjectDefinition definition, ProjectStructure structure) throws MojoExecutionException {
        String moduleName = GeneratedProjectUtils.getTargetProjectName(definition, structure);
        Path root = outputDirectory.toPath();
        if (!Files.isDirectory(root.resolve(moduleName))) {
            throw new MojoExecutionException(String.format("Could not find directory with name %s in directory %s.", moduleName, outputDirectory.getAbsolutePath()));
        }
        Model m = getGroupingPomModelStub(structure);
        PomManipulationUtils.manipulatePom(m, mavenProject -> {
            mavenProject.getModules().add(moduleName);
        });
    }

    /**
     * Make a pom project stub for the grouping pom or reuse existing pom.
     *
     * @param structure structure to base the stub on
     * @return Model denoting the new pom to be created.
     */
    private Model getGroupingPomModelStub(ProjectStructure structure) throws MojoExecutionException {
        String groupingProjectName = structure.getId() + "-grouping-project";
        Path pathToGroupingPom = outputDirectory.toPath().resolve(groupingProjectName + "-pom.xml");
        Model m = null;
        if (!Files.exists(pathToGroupingPom)) {
            m = new Model();
            m.setPomFile(pathToGroupingPom.toFile());
            m.setGroupId(project.getGroupId());
            m.setArtifactId(groupingProjectName);
            m.setVersion(project.getVersion());
            m.setModelVersion(project.getModelVersion());
            m.setPackaging("pom");
            m.setDescription("Grouping pom project for modules of ProjectStructure with id " + structure.getId());
        } else {
            m = PomManipulationUtils.loadPomModel(pathToGroupingPom);
        }
        return m;
    }

    /**
     * Based on provided configuration decide which project generation method is being used.
     * 
     * @param definition project definition
     * @param structure project structure
     * @throws MavenInvocationException
     * @throws MojoExecutionException
     */
    private void generateProjectBasedOnConfiguration(ProjectDefinition definition, ProjectStructure structure) throws MavenInvocationException, MojoExecutionException, IOException {
        switch (structure.getGenerate().getType()) {
            case ARCHETYPE:
                generateFromArchetype(definition, structure);
                break;
            case QUARKUS_CLI:
                generateUsingQuarkusCli(definition, structure);
                break;
            case MAVEN_PLUGIN:
                generateUsingMavenPlugin(definition, structure);
                break;
        }
    }

    /**
     * Generates project using archetype defined in {@linkplain ProjectStructure#getGenerate()} ()} providing properties from
     * {@linkplain ProjectDefinition#getGroupId()}, {@linkplain ProjectDefinition#getArtifactId()}, {@linkplain ProjectDefinition#getPackageName()}
     *
     * @param definition
     * @param projectStructure
     * @throws MavenInvocationException upon maven archetype:generate run failure
     */
    private void generateFromArchetype(ProjectDefinition definition, ProjectStructure projectStructure) throws MavenInvocationException, MojoExecutionException, IOException {
        InvocationRequest request = getInvocationRequestForArchetypeGeneration(definition, projectStructure);
        ensureLocalRepoExists(request);
        Invoker invoker = new DefaultInvoker();
        invoker.setWorkingDirectory(outputDirectory);
        InvocationResult result = invoker.execute(request);
        if (result.getExitCode() != 0) {
            throw new MojoExecutionException("Error during archetype generation. See previous errors in log.", result.getExecutionException());
        }
    }

    /**
     * Ensures that a local repository passed to Maven Invoker exists. Otherwise Maven Invoker throws an exception.
     *
     * @param request
     * @throws IOException if an I/O error occurs during creation of directories
     */
    private void ensureLocalRepoExists(InvocationRequest request) throws IOException {
        File localRepo = request.getLocalRepositoryDirectory(null);
        if (localRepo != null) {
            Files.createDirectories(localRepo.toPath()); // Invoker doesn't handle this automatically
        }
    }

    /**
     * Get maven invoker invocation request for maven archetype generation.
     * 
     * @param definition
     * @param projectStructure
     * @return
     */
    InvocationRequest getInvocationRequestForArchetypeGeneration(ProjectDefinition definition, ProjectStructure projectStructure) {
        InvocationRequest request = new DefaultInvocationRequest();
        request.setGoals(Collections.singletonList("archetype:generate"));
        determineUserSettings(projectStructure).ifPresent(request::setUserSettingsFile);
        determineLocalRepo(projectStructure).ifPresent(request::setLocalRepositoryDirectory);
        Properties properties = new Properties();
        properties.setProperty("interactiveMode", "false");
        properties.setProperty("groupId", definition.getGroupId());
        properties.setProperty("artifactId", GeneratedProjectUtils.getTargetProjectName(definition, projectStructure));
        properties.setProperty("package", definition.getPackageName());
        properties.setProperty("archetypeVersion", projectStructure.getGenerate().getArchetype().getVersion());
        properties.setProperty("archetypeGroupId", projectStructure.getGenerate().getArchetype().getGroupId());
        properties.setProperty("archetypeArtifactId", projectStructure.getGenerate().getArchetype().getArtifactId());
        properties.putAll(projectStructure.getGenerate().getProperties());
        request.setProperties(properties);
        return request;
    }

    /**
     * Execute Quarkus CLI command as a process.
     * 
     * @param definition
     * @param structure
     * @throws MojoExecutionException
     */
    private void generateUsingQuarkusCli(ProjectDefinition definition, ProjectStructure structure) throws MojoExecutionException {
        new CliUtils(getLog()).executeCliCommand(getQuarkusCliCreateAppCommand(definition, structure), outputDirectory);
    }

    /**
     * Execute Maven plugin command as a process.
     * 
     * @param definition
     * @param structure
     * @throws MojoExecutionException
     */
    private void generateUsingMavenPlugin(ProjectDefinition definition, ProjectStructure structure) throws MojoExecutionException {
        new CliUtils(getLog()).executeCliCommand(getMavenPluginCreateAppCommand(definition, structure), outputDirectory);
    }

    /**
     * Get string Quarkus CLI command to generate new project based on provided configuration.
     * 
     * @param definition
     * @param structure
     * @return string command
     */
    String getQuarkusCliCreateAppCommand(ProjectDefinition definition, ProjectStructure structure) {
        Formatter formatter = new Formatter();
        formatter
                .format("%s run quarkus@quarkusio", getJbangExecutable())
                .format(" create app")
                .format(" %s:%s", definition.getGroupId(), GeneratedProjectUtils.getTargetProjectName(definition, structure))
                .format(" --package-name %s", definition.getPackageName())
                .format(" --batch-mode");
        if (structure.getGenerate().getQuarkusExtensions() != null && !structure.getGenerate().getQuarkusExtensions().isEmpty()) {
            formatter.format(" -x %s", structure.getGenerate().getQuarkusExtensions());
        }
        if (structure.getGenerate().getQuarkusConfigFile() != null) {
            formatter.format(" --config=%s", structure.getGenerate().getQuarkusConfigFile().getAbsolutePath());
        }
        if (structure.getGenerate().getQuarkusPlatformGav() != null) {
            formatter.format(" --platform-bom %s:%s:%s",
                    structure.getGenerate().getQuarkusPlatformGav().getGroupId(),
                    structure.getGenerate().getQuarkusPlatformGav().getArtifactId(),
                    structure.getGenerate().getQuarkusPlatformGav().getVersion());
        }
        if (structure.getGenerate().getProperties() != null && !structure.getGenerate().getProperties().isEmpty()) {
            throw new RuntimeException("Quarkus CLI does not support additional custom properties.");
        }
        if (structure.getGenerate().getSettingsFile() != null) {
            throw new RuntimeException("Quarkus CLI does not support custom settingsFile.");
        }
        if (structure.getGenerate().useSeparateRepository()) {
            throw new RuntimeException("Quarkus CLI does not support separate repositories.");
        }
        return formatter.toString();
    }

    /**
     * Get path to Jbang executable.
     * 
     * @return string jbang executable
     */
    private String getJbangExecutable() {
        String propertyJbangExecutable = "jbang.executable";
        if (project != null && project.getProperties().containsKey(propertyJbangExecutable)) {
            String executable = project.getProperties().get(propertyJbangExecutable).toString();
            getLog().info("Using custom jbang executable '" + executable + "'");
            return executable;
        }
        return "jbang";
    }

    /**
     * Get string maven plugin command to generate new app based on provided configuration.
     * 
     * @param definition
     * @param structure
     * @return string command
     */
    String getMavenPluginCreateAppCommand(ProjectDefinition definition, ProjectStructure structure) {
        Formatter formatter = new Formatter();
        formatter
                .format("mvn %s:%s:%s:%s",
                        structure.getGenerate().getMavenPluginConfig().getGroupId(),
                        structure.getGenerate().getMavenPluginConfig().getArtifactId(),
                        structure.getGenerate().getMavenPluginConfig().getVersion(),
                        structure.getGenerate().getMavenPluginConfig().getGoal())
                .format(" --batch-mode")
                .format(" -DprojectGroupId=%s", definition.getGroupId())
                .format(" -DprojectArtifactId=%s", GeneratedProjectUtils.getTargetProjectName(definition, structure))
                .format(" -DpackageName=%s", definition.getPackageName());
        determineUserSettings(structure).ifPresent(file -> formatter.format(" -s %s", file.getAbsolutePath()));
        determineLocalRepo(structure).ifPresent(file -> formatter.format(" -Dmaven.repo.local=%s", file.getAbsolutePath()));
        if (structure.getGenerate().getQuarkusExtensions() != null && !structure.getGenerate().getQuarkusExtensions().isEmpty()) {
            formatter.format(" -Dextensions=%s", structure.getGenerate().getQuarkusExtensions());
        }
        if (structure.getGenerate().getQuarkusConfigFile() != null) {
            formatter.format(" -Dquarkus.tools.config=%s", structure.getGenerate().getQuarkusConfigFile().getAbsolutePath());
        }
        if (structure.getGenerate().getQuarkusPlatformGav() != null) {
            formatter
                    .format(" -DplatformGroupId=%s", structure.getGenerate().getQuarkusPlatformGav().getGroupId())
                    .format(" -DplatformArtifactId=%s", structure.getGenerate().getQuarkusPlatformGav().getArtifactId())
                    .format(" -DplatformVersion=%s", structure.getGenerate().getQuarkusPlatformGav().getVersion());
        }
        // append other properties from pom.xml, e.g. noCode, ...
        for (Map.Entry property : structure.getGenerate().getProperties().entrySet()) {
            formatter.format(" -D%s=%s", property.getKey(), property.getValue());
        }
        return formatter.toString();
    }

    /**
     * Manipulate the POM files of generated project. Add dependencies defined by {@linkplain ConfigSet#getDependencies()}.
     * 
     * @param definition project definition to get references from
     * @param structure project structure to get config-set with matching ids from
     * @throws MojoExecutionException on error during file manipulation
     */
    private void addPomDependencies(ProjectDefinition definition, ProjectStructure structure) throws MojoExecutionException {
        Path pomFile = getPathToPom(definition, structure);
        PomManipulationUtils.manipulatePom(PomManipulationUtils.loadPomModel(pomFile), project -> project.getDependencies().addAll(
                getActiveMojoSetup().getActiveConfigSetResolver().apply(definition, structure).stream()
                        .flatMap(it -> it.getDependencies().stream())
                        .collect(Collectors.toList())));
    }

    /**
     * Manipulate the POM files of generated project. Add properties defined by {@linkplain ConfigSet#getProperties()}.
     * 
     * @param definition project definition to get references from
     * @param structure project structure to get config-set with matching ids from
     * @throws MojoExecutionException on error during file manipulation
     */
    private void addPomProperties(ProjectDefinition definition, ProjectStructure structure) throws MojoExecutionException {
        Path pomFile = getPathToPom(definition, structure);
        PomManipulationUtils.manipulatePom(PomManipulationUtils.loadPomModel(pomFile), project -> getActiveMojoSetup().getActiveConfigSetResolver().apply(definition, structure).stream()
                .flatMap(it -> it.getProperties().entrySet().stream())
                .forEach(it -> project.getProperties().put(it.getKey(), it.getValue())));
    }

    /**
     * Allow setting finalName from ProjectDefinition to pom build configuration.
     * 
     * @param definition project definition to take finalName from
     * @param structure project structure to use for pom location resolution
     * @throws MojoExecutionException
     */
    private void setFinalNameInPom(ProjectDefinition definition, ProjectStructure structure) throws MojoExecutionException {
        if (definition.getFinalName() == null || definition.getFinalName().isEmpty()) {
            getLog().debug("No finalName specified, not changing build configuration.");
            return;
        }
        Path pathToPom = getPathToPom(definition, structure);
        PomManipulationUtils.manipulatePom(PomManipulationUtils.loadPomModel(pathToPom), project -> project.getBuild().setFinalName(definition.getFinalName()));
    }

    /**
     * Get path to pom file for given definition : structure pair.
     * 
     * @param definition definition to use for path resolution
     * @param structure structure to use for path resolution
     * @return path to pom.xml
     */
    private Path getPathToPom(ProjectDefinition definition, ProjectStructure structure) {
        Path projectDir = GeneratedProjectUtils.getOutputDirectoryForGeneratedProject(outputDirectory.toPath(), definition, structure);
        Path pomFile = projectDir.resolve("pom.xml");
        return pomFile;
    }

    /**
     * Adds .mvn/maven.config file in under the generated project root directory.
     *
     * @param definition project definition to use for path resolution
     * @param structure project structure to determine user settings, local repository and for path resolution
     * @throws IOException if an I/O error occurs when creating the file
     */
    private void addMavenConfigFile(ProjectDefinition definition, ProjectStructure structure) throws IOException {
        Formatter content = new Formatter();
        determineUserSettings(structure).ifPresent(file -> content.format("-s %s%n", file.getAbsolutePath()));
        determineLocalRepo(structure).ifPresent(file -> content.format("-Dmaven.repo.local=%s%n", file.getAbsolutePath()));
        Path projectDir = GeneratedProjectUtils.getOutputDirectoryForGeneratedProject(outputDirectory.toPath(), definition, structure);
        Path mvnDir = projectDir.resolve(".mvn");
        Files.createDirectories(mvnDir);
        Path mavenConfigFile = mvnDir.resolve("maven.config");
        Files.writeString(mavenConfigFile, content.toString(), StandardCharsets.UTF_8, StandardOpenOption.CREATE);
    }

    /**
     * Determines user settings to use for project generation. By default, it will use the same user settings the main build (the one running
     * kie-assets-library plugin) is using. If a custom {@linkplain ProjectGeneration#settingsFile} is provided
     * in the {@linkplain ProjectStructure}, it will override the settings from the main build. localRepository fragment is ignored as this
     * is controlled by {@linkplain ProjectGeneration#useSeparateRepository()}.
     *
     * @param structure project structure to determine user settings
     * @return user settings file to use for the project generation
     */
    private Optional<File> determineUserSettings(ProjectStructure structure) {
        File settingsXml = null;
        if (mavenSession != null && mavenSession.getRequest() != null) { // for tests
            settingsXml = mavenSession.getRequest().getUserSettingsFile();
        }
        File customSettingsXml = structure.getGenerate().getSettingsFile();
        if (customSettingsXml != null) {
            settingsXml = customSettingsXml;
        }
        return Optional.ofNullable(settingsXml);
    }

    /**
     * Determines local repository to use for project generation. By default, it will use the same local repository the main build (the one running
     * kie-assets-library plugin) is using. If a {@linkplain ProjectGeneration#useSeparateRepository()} is set to 'true'
     * in the {@linkplain ProjectStructure}, it will override the local repository from the main build.
     *
     * @param structure project structure to determine local repository
     * @return local repository to use for the project generation
     */
    private Optional<File> determineLocalRepo(ProjectStructure structure) {
        File localRepo = null;
        if (mavenSession != null && mavenSession.getRequest() != null) { // for tests
            localRepo = mavenSession.getRequest().getLocalRepositoryPath();
        }
        if (structure.getGenerate().useSeparateRepository()) {
            localRepo = GeneratedProjectUtils.getLocalMavenRepoForProject(outputDirectory.toPath(), structure).toFile();
        }
        return Optional.ofNullable(localRepo);
    }
}
