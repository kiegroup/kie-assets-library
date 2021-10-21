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

import java.nio.file.Path;

import org.kie.model.ProjectDefinition;
import org.kie.model.ProjectStructure;

/**
 * Utility class providing methods related to generated projects' location.
 */
public class GeneratedProjectUtils {
    /**
     * Derive name of target project based on provided parameters.
     * <p/>
     * <p>
     * Concatenates artifactId from &lt;project-definition&gt; and id from &lt;project-structure&gt; configuration elements separated by dash '-'.
     * <p/>
     * <p>
     * e.g.
     * 
     * <pre>
     *     &lt;project-definition&gt;
     *         &lt;groupId&gt;org.test&lt;/groupId&gt;
     *         &lt;artifactId&gt;project-name&lt;/artifactId&gt;
     *     &lt;/project-definition&gt;
     * </pre>
     * 
     * <pre>
     *     &lt;project-structure&gt;
     *         &lt;id&gt;quarkus&lt;/id&gt;
     *         &lt;archetype&gt;...&lt;/archetype&gt;
     *     &lt;/project-structure&gt;
     * </pre>
     * 
     * Returns "project-name-quarkus"
     *
     * @param targetProject {@linkplain ProjectDefinition} denoting target projects details
     * @param projectStructure {@linkplain ProjectStructure} denoting generation type used
     * @throws IllegalArgumentException when one of arguments is null, or one of
     *         {@linkplain ProjectDefinition#getArtifactId()}
     *         and/or {@linkplain ProjectStructure#getId()} returns null or an empty String
     * @return
     */
    public static String getTargetProjectName(ProjectDefinition targetProject, ProjectStructure projectStructure) {
        if (targetProject == null || targetProject.getArtifactId() == null || targetProject.getArtifactId().isEmpty()) {
            throw new IllegalArgumentException("ProjectDefinition instance has to have non-empty artifactId.");
        }
        return targetProject.getArtifactId() + getSuffixForProject(projectStructure);
    }

    /**
     * Extracts suffix from provided {@linkplain ProjectStructure}
     *
     * @param projectStructure {@linkplain ProjectStructure} instance
     * @throws IllegalArgumentException when {@linkplain ProjectStructure} is null or {@linkplain ProjectStructure#getId()}
     *         returns null or empty string.
     * @return string in form "-id", where id is {@linkplain ProjectStructure#getId()}
     */
    public static String getSuffixForProject(ProjectStructure projectStructure) {
        if (projectStructure == null || projectStructure.getId() == null || projectStructure.getId().isEmpty()) {
            throw new IllegalArgumentException("ProjectStructure instance has to have non-empty id.");
        }
        String suffix = projectStructure.getId();
        return "-" + suffix;
    }

    /**
     * Denote output directory Path based on provided attributes.<br/>
     * Name of the resulting directory is formed by {@linkplain #getTargetProjectName(ProjectDefinition, ProjectStructure)}
     *
     * @param outputDirectory directory which serves as parent to the newly created directory.
     * @param targetProject properties of the newly created project
     * @param projectStructure properties with the project structure
     * @return Path denoting the newly created project.
     */
    public static Path getOutputDirectoryForGeneratedProject(Path outputDirectory, ProjectDefinition targetProject, ProjectStructure projectStructure) {
        return outputDirectory.resolve(GeneratedProjectUtils.getTargetProjectName(targetProject, projectStructure));
    }
}
