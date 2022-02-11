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

import java.util.Collections;

import org.apache.maven.shared.invoker.InvocationRequest;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.kie.model.Artifact;
import org.kie.model.MavenPluginConfig;
import org.kie.model.ProjectDefinition;
import org.kie.model.ProjectStructure;
import org.kie.utils.GeneratedProjectUtils;

public class GenerateProjectCommandTest extends AbstractMojoTest<GenerateProjectMojo> {

    public GenerateProjectCommandTest() {
        super("generate-project");
    }

    @Rule
    public ExpectedException exceptionGrabber = ExpectedException.none();

    @Test
    public void testGenerateCommands() throws Exception {
        GenerateProjectMojo myMojo = getMojo("target/test-classes/generate-project/", "project-structure-generate.xml");

        myMojo.getActiveMojoSetup().apply(((definition, structure) -> {
            switch (structure.getGenerate().getType()) {
                case MAVEN_PLUGIN:
                    assertMavenPluginCommand(myMojo.getMavenPluginCreateAppCommand(definition, structure), definition, structure);
                    break;
                case QUARKUS_CLI:
                    assertQuarkusCliCommand(myMojo.getQuarkusCliCreateAppCommand(definition, structure), definition, structure);
                    break;
                case ARCHETYPE:
                    assertArchetypeCommand(myMojo.getInvocationRequestForArchetypeGeneration(definition, structure), definition, structure);
                    break;
            }
        }));
    }

    private void assertQuarkusCliCommand(String command, ProjectDefinition definition, ProjectStructure structure) {
        Artifact quarkusPlatformGav = structure.getGenerate().getQuarkusPlatformGav();
        Assert.assertThat(command, Matchers.stringContainsInOrder(
                "jbang run quarkus@quarkusio",
                " create app",
                String.format(" %s:%s", definition.getGroupId(), definition.getArtifactId()),
                String.format(" --package-name %s", definition.getPackageName()),
                String.format(" -x %s", structure.getGenerate().getQuarkusExtensions()),
                String.format(" --config=%s", structure.getGenerate().getQuarkusConfigFile().getAbsolutePath()),
                String.format(" --platform-bom %s:%s:%s", quarkusPlatformGav.getGroupId(), quarkusPlatformGav.getArtifactId(), quarkusPlatformGav.getVersion())));
    }

    private void assertMavenPluginCommand(String command, ProjectDefinition definition, ProjectStructure structure) {
        MavenPluginConfig mavenPluginConfig = structure.getGenerate().getMavenPluginConfig();
        Artifact quarkusPlatformGav = structure.getGenerate().getQuarkusPlatformGav();
        Assert.assertThat(
                command,
                Matchers.stringContainsInOrder(
                        "mvn",
                        String.format(" %s:%s:%s:%s", mavenPluginConfig.getGroupId(), mavenPluginConfig.getArtifactId(), mavenPluginConfig.getVersion(), mavenPluginConfig.getGoal()),
                        String.format(" -DprojectGroupId=%s", definition.getGroupId()),
                        String.format(" -DprojectArtifactId=%s", definition.getArtifactId()),
                        String.format(" -DpackageName=%s", definition.getPackageName()),
                        String.format(" -Dextensions=%s", structure.getGenerate().getQuarkusExtensions()),
                        String.format(" -Dquarkus.tools.config=%s", structure.getGenerate().getQuarkusConfigFile().getAbsolutePath()),
                        String.format(" -DplatformGroupId=%s", quarkusPlatformGav.getGroupId()),
                        String.format(" -DplatformArtifactId=%s", quarkusPlatformGav.getArtifactId()),
                        String.format(" -DplatformVersion=%s", quarkusPlatformGav.getVersion())));
    }

    private void assertArchetypeCommand(InvocationRequest request, ProjectDefinition definition, ProjectStructure structure) {
        Assert.assertThat(request.getGoals(), Matchers.equalTo(Collections.singletonList("archetype:generate")));
        Assert.assertThat(request.getUserSettingsFile(), Matchers.equalTo(null));
        Assert.assertThat(request.getLocalRepositoryDirectory(null), Matchers.equalTo(null));
        Assert.assertThat(request.getProperties(), Matchers.hasEntry("interactiveMode", "false"));
        Assert.assertThat(request.getProperties(), Matchers.hasEntry("groupId", definition.getGroupId()));
        Assert.assertThat(request.getProperties(), Matchers.hasEntry("artifactId", GeneratedProjectUtils.getTargetProjectName(definition, structure)));
        Assert.assertThat(request.getProperties(), Matchers.hasEntry("package", definition.getPackageName()));
        Assert.assertThat(request.getProperties(), Matchers.hasEntry("archetypeVersion", structure.getGenerate().getArchetype().getVersion()));
        Assert.assertThat(request.getProperties(), Matchers.hasEntry("archetypeGroupId", structure.getGenerate().getArchetype().getGroupId()));
        Assert.assertThat(request.getProperties(), Matchers.hasEntry("archetypeArtifactId", structure.getGenerate().getArchetype().getArtifactId()));
        Assert.assertThat(request.getProperties(), Matchers.hasEntry("interactiveMode", "false"));
    }
}
