package org.kie.utils;

import org.kie.model.ProjectDefinition;
import org.kie.model.ProjectStructure;

import java.nio.file.Path;

/**
 * Utility class providing methods related to generated projects' location.
 */
public class GeneratedProjectUtils {
    /**
     * Derive name of target project based on provided parameters.<p/>
     *
     * Concatenates artifactId from &lt;project-definition&gt; and id from &lt;project-structure&gt; configuration elements separated by dash '-'.
     * <p/>
     *
     * e.g.
     * <pre>
     *     &lt;project-definition&gt;
     *         &lt;groupId&gt;org.test&lt;/groupId&gt;
     *         &lt;artifactId&gt;project-name&lt;/artifactId&gt;
     *     &lt;/project-definition&gt;
     * </pre>
     * <pre>
     *     &lt;project-structure&gt;
     *         &lt;id&gt;quarkus&lt;/id&gt;
     *         &lt;archetype&gt;...&lt;/archetype&gt;
     *     &lt;/project-structure&gt;
     * </pre>
     * Returns "project-name-quarkus"
     *
     * @param targetProject {@linkplain ProjectDefinition} denoting target projects details
     * @param projectStructure {@linkplain ProjectStructure} denoting generation type used
     * @return
     */
    public static String getTargetProjectName(ProjectDefinition targetProject, ProjectStructure projectStructure) {
        return targetProject.getArtifactId() + getSuffixForProject(projectStructure);
    }

    /**
     * Extracts suffix from provided {@linkplain ProjectStructure}
     * @param projectStructure {@linkplain ProjectStructure} instance
     * @return string in form "-id", where id is {@linkplain ProjectStructure#getId()}
     */
    public static String getSuffixForProject(ProjectStructure projectStructure) {
        String suffix = projectStructure.getId();
        return "-" + suffix;
    }

    /**
     * Denote output directory Path based on provided attributes.<br/>
     * Name of the resulting directory is formed by {@linkplain #getTargetProjectName(ProjectDefinition, ProjectStructure)}
     * @param outputDirectory directory which serves as parent to the newly created directory.
     * @param targetProject properties of the newly created project
     * @param projectStructure properties with the project structure
     * @return Path denoting the newly created project.
     */
    public static Path getOutputDirectoryForArchetype(Path outputDirectory, ProjectDefinition targetProject, ProjectStructure projectStructure) {
        return outputDirectory.resolve(GeneratedProjectUtils.getTargetProjectName(targetProject, projectStructure));
    }
}
