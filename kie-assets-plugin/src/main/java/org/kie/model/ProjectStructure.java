package org.kie.model;

import org.apache.maven.model.Resource;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds information defining resulting project structure, mainly the archetype.
 * Provides way to configure cleanup of certain branches within the generated project (to get rid of unnecessary clutter).
 */
public class ProjectStructure {
    private String id;
    private Artifact archetype;
    private String resourcesFolder="src/main/resources";
    private List<Resource> deleteResources=new ArrayList<>();
    private List<DependencyWithId> dependencies = new ArrayList<>();
    private List<Package> copySources = new ArrayList<>();

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

    public List<DependencyWithId> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<DependencyWithId> dependencies) {
        this.dependencies = dependencies;
    }

    public List<Package> getCopySources() {
        return copySources;
    }

    public void setCopySources(List<Package> copySources) {
        this.copySources = copySources;
    }
}
