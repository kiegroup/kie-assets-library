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

import org.apache.maven.shared.invoker.InvocationRequest;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class GenerateProjectMojoCustomSettingsAndSeparateRepoTest extends AbstractMojoTest<GenerateProjectMojo> {

    public GenerateProjectMojoCustomSettingsAndSeparateRepoTest() {
        super("generate-project");
    }

    @Rule
    public ExpectedException exceptionGrabber = ExpectedException.none();

    @Test
    public void testGenerateCommandsWithCustomSettingsSeparateRepo() throws Exception {
        GenerateProjectMojo myMojo = getMojo("target/test-classes/generate-project/", "project-structure-generate-custom-settings-separate-repo.xml");

        myMojo.getActiveMojoSetup().apply(((definition, structure) -> {
            switch (structure.getGenerate().getType()) {
                case MAVEN_PLUGIN:
                    Assert.assertThat(myMojo.getMavenPluginCreateAppCommand(definition, structure), Matchers.stringContainsInOrder(
                            String.format(" -s %s", structure.getGenerate().getSettingsFile().getAbsolutePath()),
                            String.format(" -Dmaven.repo.local=%s", new File(myMojo.outputDirectory, structure.getId() + "-local-repo").getAbsolutePath())));
                    break;
                case ARCHETYPE:
                    InvocationRequest request = myMojo.getInvocationRequestForArchetypeGeneration(definition, structure);
                    Assert.assertThat(request.getUserSettingsFile(), Matchers.equalTo(structure.getGenerate().getSettingsFile()));
                    Assert.assertThat(request.getLocalRepositoryDirectory(null), Matchers.equalTo(new File(myMojo.outputDirectory, structure.getId() + "-local-repo")));
                    break;
            }
        }));
    }

    @Test
    public void testGenerateUsingQuarkusCLIWithCustomSettingsSeparateRepo() throws Exception {
        GenerateProjectMojo myMojo = getMojo("target/test-classes/generate-project/", "project-structure-generate-custom-settings-separate-repo.xml");

        myMojo.getActiveMojoSetup().apply(((definition, structure) -> {
            switch (structure.getGenerate().getType()) {
                case QUARKUS_CLI:
                    exceptionGrabber.expect(RuntimeException.class);
                    if (structure.getId().equals("C-settingsFile")) {
                        exceptionGrabber.expectMessage("Quarkus CLI does not support custom settingsFile.");
                    } else {
                        exceptionGrabber.expectMessage("Quarkus CLI does not support separate repositories.");
                    }
                    myMojo.getQuarkusCliCreateAppCommand(definition, structure);
                    break;
            }
        }));
    }
}
