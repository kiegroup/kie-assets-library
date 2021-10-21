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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.kie.model.ProjectDefinition;
import org.kie.model.ProjectStructure;
import org.kie.mojos.AbstractMojoDefiningParameters;
import org.kie.mojos.AbstractMojoTest;
import org.kie.mojos.ActiveMojoSetup;

import static org.hamcrest.MatcherAssert.assertThat;

public abstract class AbstractMojoActiveConfigurationsTest<T extends AbstractMojoDefiningParameters> extends AbstractMojoTest<T> {

    public AbstractMojoActiveConfigurationsTest(String goal) {
        super(goal);
    }

    @Test
    public void testActiveSetup2And2Combination() throws Exception {
        T myMojo = getMojo("target/test-classes/active-setup/", "active-setup-2-2-combination.xml");

        ActiveMojoSetup activeMojoSetup = myMojo.getActiveMojoSetup();
        assertThat("Wrongly parsed active-definition-ids", activeMojoSetup.getActiveDefinitions(), Matchers.containsInAnyOrder("first", "second"));
        assertThat("Wrongly parsed active-structure-ids", activeMojoSetup.getActiveStructures(), Matchers.containsInAnyOrder("A", "B"));

        assertThat(activeMojoSetup.getProjectDefinitions(), Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.not(Matchers.empty())));
        assertThat(activeMojoSetup.getProjectDefinitions(), Matchers.hasItems(
                Matchers.hasProperty("id",
                        Matchers.in(Arrays.asList("first", "second", "third")))));
        assertThat(activeMojoSetup.getProjectStructures(), Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.not(Matchers.empty())));
        assertThat(activeMojoSetup.getProjectStructures(), Matchers.hasItems(
                Matchers.hasProperty("id",
                        Matchers.in(Arrays.asList("A", "B", "C")))));
        ActiveConfigurationCheck check = new ActiveConfigurationCheck();
        activeMojoSetup.apply(check.checkActiveConfigurationAction());
        assertThat(check.definitionIds, Matchers.hasItems(
                Matchers.in(Arrays.asList("first", "second"))));
        assertThat(check.structureIds, Matchers.hasItems(
                Matchers.in(Arrays.asList("A", "B"))));
    }

    @Test
    public void testActiveSetup2DefinitionsOnly() throws Exception {
        T myMojo = getMojo("target/test-classes/active-setup/", "active-setup-2-definitions.xml");

        ActiveMojoSetup activeMojoSetup = myMojo.getActiveMojoSetup();
        assertThat("Wrongly parsed active-definition-ids", activeMojoSetup.getActiveDefinitions(), Matchers.containsInAnyOrder("first", "second"));
        assertThat("Wrongly parsed active-structure-ids", activeMojoSetup.getActiveStructures(), Matchers.empty());

        assertThat(activeMojoSetup.getProjectDefinitions(), Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.not(Matchers.empty())));
        assertThat(activeMojoSetup.getProjectDefinitions(), Matchers.hasItems(
                Matchers.hasProperty("id",
                        Matchers.in(Arrays.asList("first", "second", "third")))));
        assertThat(activeMojoSetup.getProjectStructures(), Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.not(Matchers.empty())));
        assertThat(activeMojoSetup.getProjectStructures(), Matchers.hasItems(
                Matchers.hasProperty("id",
                        Matchers.in(Arrays.asList("A", "B", "C")))));
        ActiveConfigurationCheck check = new ActiveConfigurationCheck();
        activeMojoSetup.apply(check.checkActiveConfigurationAction());
        assertThat(check.definitionIds, Matchers.hasItems(
                Matchers.in(Arrays.asList("first", "second"))));
        assertThat(check.structureIds, Matchers.hasItems(
                Matchers.in(Arrays.asList("A", "B", "C"))));
    }

    @Test
    public void testActiveSetup2StructuresOnly() throws Exception {
        T myMojo = getMojo("target/test-classes/active-setup/", "active-setup-2-structures.xml");

        ActiveMojoSetup activeMojoSetup = myMojo.getActiveMojoSetup();
        assertThat("Wrongly parsed activeDefinitions", activeMojoSetup.getActiveDefinitions(), Matchers.empty());
        assertThat("Wrongly parsed activeStructures", activeMojoSetup.getActiveStructures(), Matchers.containsInAnyOrder("A", "B"));

        assertThat(activeMojoSetup.getProjectDefinitions(), Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.not(Matchers.empty())));
        assertThat(activeMojoSetup.getProjectDefinitions(), Matchers.hasItems(
                Matchers.hasProperty("id",
                        Matchers.in(Arrays.asList("first", "second", "third")))));
        assertThat(activeMojoSetup.getProjectStructures(), Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.not(Matchers.empty())));
        assertThat(activeMojoSetup.getProjectStructures(), Matchers.hasItems(
                Matchers.hasProperty("id",
                        Matchers.in(Arrays.asList("A", "B", "C")))));
        ActiveConfigurationCheck check = new ActiveConfigurationCheck();
        activeMojoSetup.apply(check.checkActiveConfigurationAction());
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
