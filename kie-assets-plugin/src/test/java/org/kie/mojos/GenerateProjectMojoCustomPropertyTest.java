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

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class GenerateProjectMojoCustomPropertyTest extends AbstractMojoTest<GenerateProjectMojo> {

    public GenerateProjectMojoCustomPropertyTest() {
        super("generate-project");
    }

    @Rule
    public ExpectedException exceptionGrabber = ExpectedException.none();

    @Test
    public void testGenerateCommandsWithCustomProperty() throws Exception {
        GenerateProjectMojo myMojo = getMojo("target/test-classes/generate-project/", "project-structure-generate-custom-property.xml");

        myMojo.getActiveMojoSetup().apply(((definition, structure) -> {
            switch (structure.getGenerate().getType()) {
                case MAVEN_PLUGIN:
                    Assert.assertThat(myMojo.getMavenPluginCreateAppCommand(definition, structure), Matchers.containsString("-Dtwo=value2"));
                    break;
                case ARCHETYPE:
                    Assert.assertThat(myMojo.getInvocationRequestForArchetypeGeneration(definition, structure).getProperties(), Matchers.hasEntry("one", "value1"));
                    break;
            }
        }));
    }

    @Test
    public void testGenerateUsingQuarkusCLIWithCustomProperty() throws Exception {
        GenerateProjectMojo myMojo = getMojo("target/test-classes/generate-project/", "project-structure-generate-custom-property.xml");

        myMojo.getActiveMojoSetup().apply(((definition, structure) -> {
            switch (structure.getGenerate().getType()) {
                case QUARKUS_CLI:
                    exceptionGrabber.expect(RuntimeException.class);
                    exceptionGrabber.expectMessage("Quarkus CLI does not support additional custom properties.");
                    myMojo.getQuarkusCliCreateAppCommand(definition, structure);
                    break;
            }
        }));
    }
}
