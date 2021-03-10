package org.kie.mojos;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.kie.FileFilteringUtils;
import org.kie.GeneratedProjectUtils;
import org.kie.model.Package;
import org.kie.model.ProjectDefinition;
import org.kie.model.ProjectStructure;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Goal which copies specified resources into the newly generated project structure.<br />
 * Follows on {@linkplain GenerateProjectMojo} and {@linkplain CleanGeneratedResourcesMojo}.
 */
@Mojo(name = "copy-external-resources", defaultPhase = LifecyclePhase.PROCESS_SOURCES, requiresProject = false)
public class CopyExternalResourcesMojo extends AbstractMojoDefiningParameters {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            copyExternalResources();
            copyExternalSources();
        } catch (IOException e) {
            throw new MojoExecutionException("Error when copying external resources into target location.", e);
        }
    }

    /**
     * Copy resources specified by {@linkplain ProjectDefinition#getCopyResources()} into generated project.<br/>
     * The target location is denoted by {@linkplain GeneratedProjectUtils#getOutputDirectoryForArchetype(Path, ProjectDefinition, ProjectStructure)}.<br />
     *
     * @throws IOException upon error in Path resolution or copy operation.
     */
    private void copyExternalResources() throws IOException {
        getLog().info("Importing resources");
        for (ProjectDefinition definition : projectDefinitions) {
            for (ProjectStructure project : projectStructures) {
                List<Path> files = new ArrayList<>();
                for (Resource resource : definition.getCopyResources()) {
                    files.addAll(FileFilteringUtils.filterFilesStartingAtPath(Paths.get(resource.getDirectory()), resource));
                }
                for (Path f : files) {
                    Path dest = GeneratedProjectUtils.getOutputDirectoryForArchetype(outputDirectory.toPath(), definition, project)
                            .resolve(project.getResourcesFolder())
                            .resolve(f.getFileName());
                    getLog().debug(String.format("Copying %s to %s", f, dest));
                    Files.copy(f, dest);
                }
            }
        }
    }

    private void copyExternalSources() throws IOException {
        for (ProjectDefinition definition : projectDefinitions) {
            for (ProjectStructure structure : projectStructures) {
                List<Package> filteredPackages = structure.getCopySources().stream().filter(it -> definition.getConfigurationIds().contains(it.getId())).collect(Collectors.toList());
                for (Package pack : filteredPackages) {
                    List<Path> files = new ArrayList<>();
                    Resource resource = new Resource();
                    resource.getIncludes().addAll(Arrays.asList("*", "**/*"));
                    files.addAll(FileFilteringUtils.filterFilesStartingAtPath(Paths.get(mavenSession.getCurrentProject().getBasedir().getPath(), "src", pack.getType(), pack.getLanguage(), fromPackageToPath(pack)), resource));
                    for (Path f : files) {
                        Path dest = GeneratedProjectUtils.getOutputDirectoryForArchetype(outputDirectory.toPath(), definition, structure)
                                .resolve("src").resolve(pack.getType()).resolve(pack.getLanguage()).resolve(fromPackageToPath(pack))
                                .resolve(f.getFileName());
                        getLog().debug(String.format("Copying %s to %s", f, dest));
                        Files.createDirectories(dest.getParent());
                        Files.copy(f, dest);
                    }
                }
            }
        }
    }

    private String fromPackageToPath(Package pack) {
        return pack.getName().replace('.', '/');
    }
}
