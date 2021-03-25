package org.kie.model;

import org.apache.maven.model.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Holds information defining resulting project structure, mainly the archetype.
 * Provides way to configure cleanup of certain branches within the generated project (to get rid of unnecessary clutter).
 */
public class ProjectStructure {
    private String id;
    private Artifact archetype;
    private String resourcesFolder = "src/main/resources";
    private List<Resource> deleteResources = new ArrayList<>();
    private ConfigSet commonConfig = new ConfigSet();
    private List<ConfigSet> configSets = new ArrayList<>();

    public Artifact getArchetype() {
        return archetype;
    }

    public void setArchetype(Artifact archetype) {
        this.archetype = archetype;
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

    public List<Resource> getDeleteResources() {
        return deleteResources;
    }

    public void setDeleteResources(List<Resource> deleteResources) {
        this.deleteResources = deleteResources;
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
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
                ", archetype=" + archetype +
                ", resourcesFolder='" + resourcesFolder + '\'' +
                ", deleteResources=" + deleteResources +
                ", commonConfig=" + commonConfig +
                ", configSets=" + configSets +
                '}';
    }
}
