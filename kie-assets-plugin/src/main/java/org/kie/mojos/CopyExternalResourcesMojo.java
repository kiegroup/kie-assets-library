package org.kie.mojos;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.codehaus.plexus.util.StringUtils;
import org.kie.utils.MaskedMavenMojoException;
import org.kie.utils.ThrowingBiConsumer;
import org.kie.utils.FileFilteringUtils;
import org.kie.utils.GeneratedProjectUtils;
import org.kie.model.ConfigSet;
import org.kie.model.Package;
import org.kie.model.ProjectDefinition;
import org.kie.model.ProjectStructure;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
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
        } catch (MaskedMavenMojoException e) {
            throw new MojoExecutionException("Error when copying external resources into target location.", e);
        }
    }

    /**
     * Copy resources specified as {@linkplain ConfigSet#getCopyResources()} by one of:
     * <ul>
     *     <li>{@linkplain ProjectDefinition#getConfig()}</li>
     *     <li>{@linkplain ProjectStructure#getCommonConfig()}</li>
     *     <li>{@linkplain ProjectStructure#getConfigSets()}
     *     with {@linkplain ConfigSet#getId()} matching one of {@linkplain #activeConfigSets}</li>
     * </ul>
     * The target location is denoted by {@linkplain GeneratedProjectUtils#getOutputDirectoryForArchetype(Path, ProjectDefinition, ProjectStructure)}.
     */
    private void copyExternalResources() {
        getLog().info("Importing resources");
        getActiveSetup().apply(copyResourcesAction());
    }

    /**
     * Method that for given definition and structure processes the copy resources for all the active configurations.
     * <p>
     * A BiConsumer implementation to be used together with {@linkplain AbstractMojoDefiningParameters#getActiveSetup()},
     * passed through method {@linkplain AbstractMojoDefiningParameters.ActiveSetup#apply(BiConsumer)}.
     *
     * @return BiConsumer action over {@linkplain ProjectDefinition} and {@linkplain ProjectStructure}.
     */
    private ThrowingBiConsumer copyResourcesAction() {
        return (definition, structure) -> {
            List<Resource> resources = new ArrayList<>();
            resources.addAll(definition.getConfig().getCopyResources());
            resources.addAll(structure.getCommonConfig().getCopyResources());
            resources.addAll(
                    structure.getConfigSets().stream()
                            .filter(it->activeConfigSets.contains(it.getId()))
                            .flatMap(it->it.getCopyResources().stream())
                            .collect(Collectors.toList())
            );

            for (Resource resource : resources) {
                List<Path> files = new ArrayList<>();
                files.addAll(FileFilteringUtils.filterFilesStartingAtPath(Paths.get(resource.getDirectory()), resource));
                for (Path f : files) {
                    Path dest = GeneratedProjectUtils.getOutputDirectoryForArchetype(outputDirectory.toPath(), definition, structure)
                            .resolve(StringUtils.isBlank(resource.getTargetPath()) ? structure.getResourcesFolder() : resource.getTargetPath())
                            .resolve(f.getFileName());
                    getLog().debug(String.format("Copying %s to %s", f, dest));
                    Files.copy(f, dest);
                }
            }
        };
    }

    /**
     * Copy source packages {@linkplain ConfigSet#getCopySources()} specified by one of:
     * <ul>
     *     <li>{@linkplain ProjectDefinition#getConfig()}</li>
     *     <li>{@linkplain ProjectStructure#getCommonConfig()}</li>
     *     <li>{@linkplain ProjectStructure#getConfigSets()}
     *     with {@linkplain ConfigSet#getId()} matching one of {@linkplain #activeConfigSets}</li>
     * </ul>
     */
    private void copyExternalSources() {
        getActiveSetup().apply(copySourcesAction());
    }

    /**
     * Method that for given definition and structure processes the copy source packages for all the active configurations.
     * <p>
     * A BiConsumer implementation to be used together with {@linkplain AbstractMojoDefiningParameters#getActiveSetup()},
     * passed through method {@linkplain AbstractMojoDefiningParameters.ActiveSetup#apply(BiConsumer)}.
     *
     * @return BiConsumer action over {@linkplain ProjectDefinition} and {@linkplain ProjectStructure}.
     */
    private ThrowingBiConsumer copySourcesAction() {
        return (definition, structure) -> {
            List<Package> filteredPackages = new ArrayList<>();
            filteredPackages.addAll(definition.getConfig().getCopySources());
            filteredPackages.addAll(structure.getCommonConfig().getCopySources());
            filteredPackages.addAll(
                    structure.getConfigSets().stream()
                            .filter(it -> activeConfigSets.contains(it.getId()))
                            .flatMap(it -> it.getCopySources().stream())
                            .collect(Collectors.toList())
            );
            for (Package pack : filteredPackages) {
                List<Path> files = new ArrayList<>();
                Resource resource = new Resource();
                resource.getIncludes().addAll(Arrays.asList("*", "**/*"));
                Path currentPath = Paths.get(mavenSession.getCurrentProject().getBasedir().getPath());
                Path rootPath = currentPath.resolve(pack.getFilesystemRoot());
                files.addAll(FileFilteringUtils.filterFilesStartingAtPath(rootPath.resolve(Paths.get("src", pack.getType(), pack.getLanguage(), fromPackageToPath(pack))), resource));
                for (Path f : files) {
                    Path dest = GeneratedProjectUtils.getOutputDirectoryForArchetype(outputDirectory.toPath(), definition, structure)
                            .resolve("src").resolve(pack.getType()).resolve(pack.getLanguage()).resolve(fromPackageToPath(pack))
                            .resolve(f.getFileName());
                    getLog().debug(String.format("Copying %s to %s", f, dest));
                    Files.createDirectories(dest.getParent());
                    Files.copy(f, dest);
                }
            }
        };
    }

    /**
     * Translate package name to folder structure.
     * @param pack package name separated by dots
     * @return folder structure
     */
    private String fromPackageToPath(Package pack) {
        return pack.getName().replace('.', '/');
    }
}
