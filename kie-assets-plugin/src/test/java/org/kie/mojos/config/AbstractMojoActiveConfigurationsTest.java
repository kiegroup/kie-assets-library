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
package org.kie.mojos.config;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

import org.apache.maven.plugin.testing.MojoRule;
import org.hamcrest.Matchers;
import org.hamcrest.io.FileMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.kie.model.ProjectDefinition;
import org.kie.model.ProjectStructure;
import org.kie.mojos.AbstractMojoDefiningParameters;

import static org.hamcrest.MatcherAssert.assertThat;

public abstract class AbstractMojoActiveConfigurationsTest<T extends AbstractMojoDefiningParameters> {

    private String goal;

    @Rule
    public MojoRule rule = new MojoRule() {
        @Override
        protected void before() throws Throwable {
        }

        @Override
        protected void after() {
        }
    };

    public AbstractMojoActiveConfigurationsTest(String goal) {
        this.goal = goal;
    }

    @Test
    public void testActiveSetup2And2Combination() throws Exception {
        File projectDir = new File("target/test-classes/active-setup/");
        assertThat("Project directory does not exist", projectDir, FileMatchers.anExistingDirectory());
        File pomFile = new File(projectDir, "active-setup-2-2-combination.xml");
        assertThat("Tested xml file does not exist", pomFile, FileMatchers.anExistingFile());

        T myMojo = (T) rule.lookupMojo(goal, pomFile);

        AbstractMojoDefiningParameters.ActiveSetup activeSetup = myMojo.getActiveSetup();
        assertThat("Wrongly parsed active-definition-ids", activeSetup.getActiveDefinitions(), Matchers.containsInAnyOrder("first", "second"));
        assertThat("Wrongly parsed active-structure-ids", activeSetup.getActiveStructures(), Matchers.containsInAnyOrder("A", "B"));

        assertThat(activeSetup.getProjectDefinitions(), Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.not(Matchers.empty())));
        assertThat(activeSetup.getProjectDefinitions(), Matchers.hasItems(
                Matchers.hasProperty("id",
                        Matchers.in(Arrays.asList("first", "second", "third")))));
        assertThat(activeSetup.getProjectStructures(), Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.not(Matchers.empty())));
        assertThat(activeSetup.getProjectStructures(), Matchers.hasItems(
                Matchers.hasProperty("id",
                        Matchers.in(Arrays.asList("A", "B", "C")))));
        ActiveConfigurationCheck check = new ActiveConfigurationCheck();
        activeSetup.apply(check.checkActiveConfigurationAction());
        assertThat(check.definitionIds, Matchers.hasItems(
                Matchers.in(Arrays.asList("first", "second"))));
        assertThat(check.structureIds, Matchers.hasItems(
                Matchers.in(Arrays.asList("A", "B"))));
    }

    @Test
    public void testActiveSetup2DefinitionsOnly() throws Exception {
        File projectDir = new File("target/test-classes/active-setup/");
        assertThat("Project directory does not exist", projectDir, FileMatchers.anExistingDirectory());
        File pomFile = new File(projectDir, "active-setup-2-definitions.xml");
        assertThat("Tested xml file does not exist", pomFile, FileMatchers.anExistingFile());

        T myMojo = (T) rule.lookupMojo(goal, pomFile);

        AbstractMojoDefiningParameters.ActiveSetup activeSetup = myMojo.getActiveSetup();
        assertThat("Wrongly parsed active-definition-ids", activeSetup.getActiveDefinitions(), Matchers.containsInAnyOrder("first", "second"));
        assertThat("Wrongly parsed active-structure-ids", activeSetup.getActiveStructures(), Matchers.empty());

        assertThat(activeSetup.getProjectDefinitions(), Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.not(Matchers.empty())));
        assertThat(activeSetup.getProjectDefinitions(), Matchers.hasItems(
                Matchers.hasProperty("id",
                        Matchers.in(Arrays.asList("first", "second", "third")))));
        assertThat(activeSetup.getProjectStructures(), Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.not(Matchers.empty())));
        assertThat(activeSetup.getProjectStructures(), Matchers.hasItems(
                Matchers.hasProperty("id",
                        Matchers.in(Arrays.asList("A", "B", "C")))));
        ActiveConfigurationCheck check = new ActiveConfigurationCheck();
        activeSetup.apply(check.checkActiveConfigurationAction());
        assertThat(check.definitionIds, Matchers.hasItems(
                Matchers.in(Arrays.asList("first", "second"))));
        assertThat(check.structureIds, Matchers.hasItems(
                Matchers.in(Arrays.asList("A", "B", "C"))));
    }

    @Test
    public void testActiveSetup2StructuresOnly() throws Exception {
        File projectDir = new File("target/test-classes/active-setup/");
        assertThat("Project directory does not exist", projectDir, FileMatchers.anExistingDirectory());
        File pomFile = new File(projectDir, "active-setup-2-structures.xml");
        assertThat("Tested xml file does not exist", pomFile, FileMatchers.anExistingFile());

        T myMojo = (T) rule.lookupMojo(goal, pomFile);

        AbstractMojoDefiningParameters.ActiveSetup activeSetup = myMojo.getActiveSetup();
        assertThat("Wrongly parsed active-definition-ids", activeSetup.getActiveDefinitions(), Matchers.empty());
        assertThat("Wrongly parsed active-structure-ids", activeSetup.getActiveStructures(), Matchers.containsInAnyOrder("A", "B"));

        assertThat(activeSetup.getProjectDefinitions(), Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.not(Matchers.empty())));
        assertThat(activeSetup.getProjectDefinitions(), Matchers.hasItems(
                Matchers.hasProperty("id",
                        Matchers.in(Arrays.asList("first", "second", "third")))));
        assertThat(activeSetup.getProjectStructures(), Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.not(Matchers.empty())));
        assertThat(activeSetup.getProjectStructures(), Matchers.hasItems(
                Matchers.hasProperty("id",
                        Matchers.in(Arrays.asList("A", "B", "C")))));
        ActiveConfigurationCheck check = new ActiveConfigurationCheck();
        activeSetup.apply(check.checkActiveConfigurationAction());
        assertThat(check.definitionIds, Matchers.hasItems(
                Matchers.in(Arrays.asList("first", "second"))));
        assertThat(check.structureIds, Matchers.hasItems(
                Matchers.in(Arrays.asList("A", "B"))));
    }

    private static class ActiveConfigurationCheck {
        private Set<String> definitionIds = new HashSet<>();
        private Set<String> structureIds = new HashSet<>();

        private BiConsumer<ProjectDefinition, ProjectStructure> checkActiveConfigurationAction() {
            return (definition, structure) -> {
                definitionIds.add(definition.getId());
                structureIds.add(structure.getId());
            };
        }
    }

}
