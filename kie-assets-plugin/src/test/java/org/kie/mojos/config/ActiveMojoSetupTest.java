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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.kie.model.ConfigSet;
import org.kie.model.ProjectDefinition;
import org.kie.model.ProjectStructure;
import org.kie.mojos.ActiveMojoSetup;

public class ActiveMojoSetupTest {

    @Rule
    public ExpectedException exceptionGrabber = ExpectedException.none();

    @Test
    public void testEmpty() {
        ActiveMojoSetup activeMojoSetup = new ActiveMojoSetup()
                .setActiveDefinitions(Collections.emptySet())
                .setActiveStructures(Collections.emptySet())
                .setProjectDefinitions(Collections.emptyList())
                .setProjectStructures(Collections.emptyList())
                .setActiveConfigSets(Collections.emptySet())
                .setReusableConfigSets(Collections.emptyList());
        activeMojoSetup.apply((definition, structure) -> {
            Assert.fail("No active setup should be configured.");
        });
    }

    @Test
    public void testProductSingleCombination() {
        ActiveMojoSetup activeMojoSetup = new ActiveMojoSetup()
                .setProjectDefinitions(Collections.singletonList(getDefinition("id1")))
                .setProjectStructures(Collections.singletonList(getStructure("id2")));
        final AtomicInteger counter = new AtomicInteger();
        activeMojoSetup.apply((definition, structure) -> {
            counter.incrementAndGet();
            Assert.assertThat(definition.getId(), Matchers.equalTo("id1"));
            Assert.assertThat(structure.getId(), Matchers.equalTo("id2"));
        });
        Assert.assertThat(counter.get(), Matchers.equalTo(1));
    }

    @Test
    public void testProductMultipleCombination() {
        ActiveMojoSetup activeMojoSetup = new ActiveMojoSetup()
                .setProjectDefinitions(Arrays.asList(getDefinition("d1"), getDefinition("d2"), getDefinition("d3")))
                .setProjectStructures(Arrays.asList(getStructure("s1"), getStructure("s2"), getStructure("s3")));
        List<String> combinations = new ArrayList<>();
        activeMojoSetup.apply((definition, structure) -> {
            combinations.add(definition.getId() + "|" + structure.getId());
        });
        Assert.assertThat(combinations.size(), Matchers.equalTo(9));
        Assert.assertThat(combinations, Matchers.contains(
                "d1|s1", "d1|s2", "d1|s3",
                "d2|s1", "d2|s2", "d2|s3",
                "d3|s1", "d3|s2", "d3|s3"));
    }

    @Test
    public void testSingleActiveDefinition() {
        ActiveMojoSetup activeMojoSetup = new ActiveMojoSetup()
                .setProjectDefinitions(Arrays.asList(getDefinition("d1"), getDefinition("d2"), getDefinition("d3")))
                .setProjectStructures(Arrays.asList(getStructure("s1"), getStructure("s2"), getStructure("s3")))
                .setActiveDefinitions(Collections.singleton("d2"));
        List<String> combinations = new ArrayList<>();
        activeMojoSetup.apply((definition, structure) -> {
            combinations.add(definition.getId() + "|" + structure.getId());
        });
        Assert.assertThat(combinations.size(), Matchers.equalTo(3));
        Assert.assertThat(combinations, Matchers.contains(
                "d2|s1", "d2|s2", "d2|s3"));
    }

    @Test
    public void testSingleActiveStructure() {
        ActiveMojoSetup activeMojoSetup = new ActiveMojoSetup()
                .setProjectDefinitions(Arrays.asList(getDefinition("d1"), getDefinition("d2"), getDefinition("d3")))
                .setProjectStructures(Arrays.asList(getStructure("s1"), getStructure("s2"), getStructure("s3")))
                .setActiveStructures(Collections.singleton("s3"));
        List<String> combinations = new ArrayList<>();
        activeMojoSetup.apply((definition, structure) -> {
            combinations.add(definition.getId() + "|" + structure.getId());
        });
        Assert.assertThat(combinations.size(), Matchers.equalTo(3));
        Assert.assertThat(combinations, Matchers.contains(
                "d1|s3",
                "d2|s3",
                "d3|s3"));
    }

    @Test
    public void testSingleActiveCombination() {
        ActiveMojoSetup activeMojoSetup = new ActiveMojoSetup()
                .setProjectDefinitions(Arrays.asList(getDefinition("d1"), getDefinition("d2"), getDefinition("d3")))
                .setProjectStructures(Arrays.asList(getStructure("s1"), getStructure("s2"), getStructure("s3")))
                .setActiveDefinitions(Collections.singleton("d1"))
                .setActiveStructures(Collections.singleton("s3"));
        List<String> combinations = new ArrayList<>();
        activeMojoSetup.apply((definition, structure) -> {
            combinations.add(definition.getId() + "|" + structure.getId());
        });
        Assert.assertThat(combinations.size(), Matchers.equalTo(1));
        Assert.assertThat(combinations, Matchers.contains("d1|s3"));
    }

    @Test
    public void testNegationSingleActiveDefinition() {
        ActiveMojoSetup activeMojoSetup = new ActiveMojoSetup()
                .setProjectDefinitions(Arrays.asList(getDefinition("d1"), getDefinition("d2"), getDefinition("d3")))
                .setProjectStructures(Arrays.asList(getStructure("s1"), getStructure("s2"), getStructure("s3")))
                .setActiveDefinitions(Collections.singleton("!d2"));
        List<String> combinations = new ArrayList<>();
        activeMojoSetup.apply((definition, structure) -> {
            combinations.add(definition.getId() + "|" + structure.getId());
        });
        Assert.assertThat(combinations.size(), Matchers.equalTo(6));
        Assert.assertThat(combinations, Matchers.contains(
                "d1|s1", "d1|s2", "d1|s3",
                "d3|s1", "d3|s2", "d3|s3"));
    }

    @Test
    public void testNegationSingleActiveStructure() {
        ActiveMojoSetup activeMojoSetup = new ActiveMojoSetup()
                .setProjectDefinitions(Arrays.asList(getDefinition("d1"), getDefinition("d2"), getDefinition("d3")))
                .setProjectStructures(Arrays.asList(getStructure("s1"), getStructure("s2"), getStructure("s3")))
                .setActiveStructures(Collections.singleton("!s3"));
        List<String> combinations = new ArrayList<>();
        activeMojoSetup.apply((definition, structure) -> {
            combinations.add(definition.getId() + "|" + structure.getId());
        });
        Assert.assertThat(combinations.size(), Matchers.equalTo(6));
        Assert.assertThat(combinations, Matchers.contains(
                "d1|s1", "d1|s2",
                "d2|s1", "d2|s2",
                "d3|s1", "d3|s2"));
    }

    @Test
    public void testNegationTwoActiveDefinitions() {
        ActiveMojoSetup activeMojoSetup = new ActiveMojoSetup()
                .setProjectDefinitions(Arrays.asList(getDefinition("d1"), getDefinition("d2"), getDefinition("d3")))
                .setProjectStructures(Arrays.asList(getStructure("s1"), getStructure("s2"), getStructure("s3")))
                .setActiveDefinitions(new HashSet<>(Arrays.asList("!d2", "!d3")));
        List<String> combinations = new ArrayList<>();
        activeMojoSetup.apply((definition, structure) -> {
            combinations.add(definition.getId() + "|" + structure.getId());
        });
        Assert.assertThat(combinations.size(), Matchers.equalTo(3));
        Assert.assertThat(combinations, Matchers.contains(
                "d1|s1", "d1|s2", "d1|s3"));
    }

    @Test
    public void testNegationTwoActiveStructures() {
        ActiveMojoSetup activeMojoSetup = new ActiveMojoSetup()
                .setProjectDefinitions(Arrays.asList(getDefinition("d1"), getDefinition("d2"), getDefinition("d3")))
                .setProjectStructures(Arrays.asList(getStructure("s1"), getStructure("s2"), getStructure("s3")))
                .setActiveStructures(new HashSet<>(Arrays.asList("!s2", "!s3")));
        List<String> combinations = new ArrayList<>();
        activeMojoSetup.apply((definition, structure) -> {
            combinations.add(definition.getId() + "|" + structure.getId());
        });
        Assert.assertThat(combinations.size(), Matchers.equalTo(3));
        Assert.assertThat(combinations, Matchers.contains(
                "d1|s1",
                "d2|s1",
                "d3|s1"));
    }

    @Test
    public void testNegationAllActiveDefinitions() {
        ActiveMojoSetup activeMojoSetup = new ActiveMojoSetup()
                .setProjectDefinitions(Arrays.asList(getDefinition("d1"), getDefinition("d2"), getDefinition("d3")))
                .setProjectStructures(Arrays.asList(getStructure("s1"), getStructure("s2"), getStructure("s3")))
                .setActiveDefinitions(new HashSet<>(Arrays.asList("!d1", "!d2", "!d3")));
        List<String> combinations = new ArrayList<>();
        activeMojoSetup.apply((definition, structure) -> {
            combinations.add(definition.getId() + "|" + structure.getId());
        });
        Assert.assertThat(combinations.size(), Matchers.equalTo(0));
    }

    @Test
    public void testNegationAllActiveStructures() {
        ActiveMojoSetup activeMojoSetup = new ActiveMojoSetup()
                .setProjectDefinitions(Arrays.asList(getDefinition("d1"), getDefinition("d2"), getDefinition("d3")))
                .setProjectStructures(Arrays.asList(getStructure("s1"), getStructure("s2"), getStructure("s3")))
                .setActiveStructures(new HashSet<>(Arrays.asList("!s1", "!s2", "!s3")));
        List<String> combinations = new ArrayList<>();
        activeMojoSetup.apply((definition, structure) -> {
            combinations.add(definition.getId() + "|" + structure.getId());
        });
        Assert.assertThat(combinations.size(), Matchers.equalTo(0));
    }

    @Test
    public void testNegationNonExistingActiveDefinitions() {
        ActiveMojoSetup activeMojoSetup = new ActiveMojoSetup()
                .setProjectDefinitions(Arrays.asList(getDefinition("d1"), getDefinition("d2"), getDefinition("d3")))
                .setProjectStructures(Arrays.asList(getStructure("s1"), getStructure("s2"), getStructure("s3")))
                .setActiveDefinitions(new HashSet<>(Arrays.asList("d1", "!d100")));
        List<String> combinations = new ArrayList<>();
        activeMojoSetup.apply((definition, structure) -> {
            combinations.add(definition.getId() + "|" + structure.getId());
        });
        Assert.assertThat(combinations.size(), Matchers.equalTo(9));
        Assert.assertThat(combinations, Matchers.contains(
                "d1|s1", "d1|s2", "d1|s3",
                "d2|s1", "d2|s2", "d2|s3",
                "d3|s1", "d3|s2", "d3|s3"));
    }

    @Test
    public void testNegationNonExistingActiveStructures() {
        ActiveMojoSetup activeMojoSetup = new ActiveMojoSetup()
                .setProjectDefinitions(Arrays.asList(getDefinition("d1"), getDefinition("d2"), getDefinition("d3")))
                .setProjectStructures(Arrays.asList(getStructure("s1"), getStructure("s2"), getStructure("s3")))
                .setActiveStructures(new HashSet<>(Arrays.asList("s1", "!s100")));
        List<String> combinations = new ArrayList<>();
        activeMojoSetup.apply((definition, structure) -> {
            combinations.add(definition.getId() + "|" + structure.getId());
        });
        Assert.assertThat(combinations.size(), Matchers.equalTo(9));
        Assert.assertThat(combinations, Matchers.contains(
                "d1|s1", "d1|s2", "d1|s3",
                "d2|s1", "d2|s2", "d2|s3",
                "d3|s1", "d3|s2", "d3|s3"));
    }

    @Test
    public void testNegationCombination() {
        ActiveMojoSetup activeMojoSetup = new ActiveMojoSetup()
                .setProjectDefinitions(Arrays.asList(getDefinition("d1"), getDefinition("d2"), getDefinition("d3")))
                .setProjectStructures(Arrays.asList(getStructure("s1"), getStructure("s2"), getStructure("s3")))
                .setActiveDefinitions(Collections.singleton("!d1"))
                .setActiveStructures(Collections.singleton("!s3"));
        List<String> combinations = new ArrayList<>();
        activeMojoSetup.apply((definition, structure) -> {
            combinations.add(definition.getId() + "|" + structure.getId());
        });
        Assert.assertThat(combinations.size(), Matchers.equalTo(4));
        Assert.assertThat(combinations, Matchers.contains(
                "d2|s1", "d2|s2",
                "d3|s1", "d3|s2"));
    }

    @Test
    public void testNegativeAndPositiveCombination() {
        ActiveMojoSetup activeMojoSetup = new ActiveMojoSetup()
                .setProjectDefinitions(Arrays.asList(getDefinition("d1"), getDefinition("d2"), getDefinition("d3")))
                .setProjectStructures(Arrays.asList(getStructure("s1"), getStructure("s2"), getStructure("s3")))
                .setActiveDefinitions(new HashSet<>(Arrays.asList("d1", "!d2")))
                .setActiveStructures(new HashSet<>(Arrays.asList("!s1", "s3")));
        List<String> combinations = new ArrayList<>();
        activeMojoSetup.apply((definition, structure) -> {
            combinations.add(definition.getId() + "|" + structure.getId());
        });
        Assert.assertThat(combinations.size(), Matchers.equalTo(4));
        Assert.assertThat(combinations, Matchers.contains(
                "d1|s2", "d1|s3",
                "d3|s2", "d3|s3"));
    }

    @Test
    public void testOverlappingActiveDefinitionNegationTakesPrecedence() {
        ActiveMojoSetup activeMojoSetup = new ActiveMojoSetup()
                .setProjectDefinitions(Arrays.asList(getDefinition("d1"), getDefinition("d2"), getDefinition("d3")))
                .setProjectStructures(Arrays.asList(getStructure("s1"), getStructure("s2"), getStructure("s3")))
                .setActiveDefinitions(new HashSet<>(Arrays.asList("!d1", "d1")));
        List<String> combinations = new ArrayList<>();
        activeMojoSetup.apply((definition, structure) -> {
            combinations.add(definition.getId() + "|" + structure.getId());
        });
        Assert.assertThat(combinations.size(), Matchers.equalTo(6));
        Assert.assertThat(combinations, Matchers.contains(
                "d2|s1", "d2|s2", "d2|s3",
                "d3|s1", "d3|s2", "d3|s3"));
    }

    @Test
    public void testOverlappingActiveStructureNegationTakesPrecedence() {
        ActiveMojoSetup activeMojoSetup = new ActiveMojoSetup()
                .setProjectDefinitions(Arrays.asList(getDefinition("d1"), getDefinition("d2"), getDefinition("d3")))
                .setProjectStructures(Arrays.asList(getStructure("s1"), getStructure("s2"), getStructure("s3")))
                .setActiveStructures(new HashSet<>(Arrays.asList("!s1", "s1")));
        List<String> combinations = new ArrayList<>();
        activeMojoSetup.apply((definition, structure) -> {
            combinations.add(definition.getId() + "|" + structure.getId());
        });
        Assert.assertThat(combinations.size(), Matchers.equalTo(6));
        Assert.assertThat(combinations, Matchers.contains(
                "d1|s2", "d1|s3",
                "d2|s2", "d2|s3",
                "d3|s2", "d3|s3"));
    }

    @Test
    public void testConfigSetDefinitionAndStructureDefaultConfig() {
        ProjectDefinition d1 = getDefinition("d1");
        ConfigSet c1 = getConfigSet("c1");
        d1.setConfig(c1);
        ProjectStructure s1 = getStructure("s1");
        ConfigSet c2 = getConfigSet("c2");
        s1.setCommonConfig(c2);
        ActiveMojoSetup activeMojoSetup = new ActiveMojoSetup()
                .setProjectDefinitions(Collections.singletonList(d1))
                .setProjectStructures(Collections.singletonList(s1));
        Assert.assertThat(activeMojoSetup.getActiveConfigSetResolver().apply(d1, s1),
                Matchers.hasItems(c1, c2));
    }

    @Test
    public void testConfigSetStructureEnabledConfig() {
        ProjectDefinition d1 = getDefinition("d1");
        ProjectStructure s1 = getStructure("s1");
        ConfigSet c1 = getConfigSet("c1");
        ConfigSet c2 = getConfigSet("c2");
        s1.setConfigSets(Arrays.asList(c1, c2));
        ActiveMojoSetup activeMojoSetup = new ActiveMojoSetup()
                .setProjectDefinitions(Collections.singletonList(d1))
                .setProjectStructures(Collections.singletonList(s1))
                .setActiveConfigSets(Collections.singleton("c2"));
        Assert.assertThat(activeMojoSetup.getActiveConfigSetResolver().apply(d1, s1),
                Matchers.hasItems(c2));
    }

    @Test
    public void testConfigSetReusableConfig() {
        ProjectDefinition d1 = getDefinition("d1");
        ProjectStructure s1 = getStructure("s1");
        ConfigSet c1 = getConfigSet("c1");
        ConfigSet c2 = getConfigSet("c2");
        ConfigSet c3 = getConfigSet("c3");
        c3.setReusableConfig(c1.getId());
        ConfigSet c4 = getConfigSet("c4");
        c4.setReusableConfig("c2");
        s1.setCommonConfig(c4);
        s1.setConfigSets(Arrays.asList(c3));
        ActiveMojoSetup activeMojoSetup = new ActiveMojoSetup()
                .setProjectDefinitions(Collections.singletonList(d1))
                .setProjectStructures(Collections.singletonList(s1))
                .setReusableConfigSets(Arrays.asList(c1, c2))
                .setActiveConfigSets(Collections.singleton("c3"));
        Assert.assertThat(activeMojoSetup.getActiveConfigSetResolver().apply(d1, s1),
                Matchers.hasItems(c1, c2));
    }

    @Test
    public void testConfigSetReusableConfigNonExistent() {
        ProjectDefinition d1 = getDefinition("d1");
        ProjectStructure s1 = getStructure("s1");
        ConfigSet c2 = getConfigSet("c2");
        ConfigSet c3 = getConfigSet("c3");
        c3.setReusableConfig("c1");
        ConfigSet c4 = getConfigSet("c4");
        s1.setConfigSets(Arrays.asList(c3, c4));
        ActiveMojoSetup activeMojoSetup = new ActiveMojoSetup()
                .setProjectDefinitions(Collections.singletonList(d1))
                .setProjectStructures(Collections.singletonList(s1))
                .setReusableConfigSets(Arrays.asList(c2))
                .setActiveConfigSets(Collections.singleton("c3"));
        exceptionGrabber.expectMessage("ConfigSet " + c3.getReusableConfig() + " not found among reusable config sets.");
        activeMojoSetup.getActiveConfigSetResolver().apply(d1, s1);
    }

    private ProjectDefinition getDefinition(String id) {
        ProjectDefinition def = new ProjectDefinition();
        def.setId(id);
        return def;
    }

    private ProjectStructure getStructure(String id) {
        ProjectStructure structure = new ProjectStructure();
        structure.setId(id);
        return structure;
    }

    private ConfigSet getConfigSet(String id) {
        ConfigSet c = new ConfigSet();
        c.setId(id);
        return c;
    }
}
