package org.kie.mojos;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.kie.FileFilteringUtils;
import org.kie.GeneratedProjectUtils;
import org.kie.model.ProjectDefinition;
import org.kie.model.ProjectStructure;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            cleanupGeneratedResources();
        } catch (IOException e) {
            throw new MojoExecutionException("Couldn't cleanup generated resources.", e);
        }
    }

    /**
     * Process the cleanup based on the configuration.
     * <ul>
     *     <li>generated project directory denoted by {@linkplain GeneratedProjectUtils#getOutputDirectoryForArchetype(Path, ProjectDefinition, ProjectStructure)}</li>
     *     <li>&lt;delete-resources&gt; {@linkplain Resource} specifications within {@linkplain AbstractMojoDefiningParameters#projectStructures}.</li>
     *     <li>considers {@linkplain #wipeSrcMain} and {@linkplain #wipeSrcMainResources}</li>
     *     <li>considers {@linkplain #wipeSrcTest} and {@linkplain #wipeSrcTestResources}</li>
     * </ul>
     * @throws IOException
     */
    private void cleanupGeneratedResources() throws IOException {
        getLog().info("Deleting resources");
        for (ProjectDefinition definition : projectDefinitions) {
            for (ProjectStructure structure : projectStructures) {
                Path outputDirectoryForArchetype = GeneratedProjectUtils.getOutputDirectoryForArchetype(outputDirectory.toPath(), definition, structure);
                List<Path> files = new ArrayList<>();
                if (wipeSrcMain) {
                    getLog().debug("Cleaning src/main directory in " + outputDirectoryForArchetype);
                    Resource srcMainJavaResource = new Resource();
                    srcMainJavaResource.setDirectory("src/main");
                    srcMainJavaResource.getIncludes().add("**/*");
                    if(!wipeSrcMainResources) {
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
                    if(!wipeSrcTestResources) {
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
            }
        }
    }
}
