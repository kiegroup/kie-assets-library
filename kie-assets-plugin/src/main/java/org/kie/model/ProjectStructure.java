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
package org.kie.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Holds information defining resulting project structure, mainly the archetype.
 * Provides way to configure cleanup of certain branches within the generated project (to get rid of unnecessary clutter).
 */
public class ProjectStructure {
    private String id;
    private ProjectGeneration generate;
    private String resourcesFolder = "src/main/resources";
    private ConfigSet commonConfig = new ConfigSet();
    private List<ConfigSet> configSets = new ArrayList<>();

    public ProjectGeneration getGenerate() {
        return generate;
    }

    public void setGenerate(ProjectGeneration generate) {
        this.generate = generate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResourcesFolder() {
        return resourcesFolder;
    }

    public void setResourcesFolder(String resourcesFolder) {
        this.resourcesFolder = resourcesFolder;
    }

    public ConfigSet getCommonConfig() {
        return commonConfig;
    }

    public void setCommonConfig(ConfigSet commonConfig) {
        this.commonConfig = commonConfig;
    }

    public List<ConfigSet> getConfigSets() {
        return configSets;
    }

    public void setConfigSets(List<ConfigSet> configSets) {
        this.configSets = configSets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ProjectStructure that = (ProjectStructure) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ProjectStructure{" +
                "id='" + id + '\'' +
                ", generate=" + generate +
                ", resourcesFolder='" + resourcesFolder + '\'' +
                ", commonConfig=" + commonConfig +
                ", configSets=" + configSets +
                '}';
    }
}
