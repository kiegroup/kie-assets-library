package org.kie.model;

import org.apache.maven.model.Dependency;

import java.util.ArrayList;
import java.util.List;

/**
 * Artifact to hold configuration of dependencies and source packages to be copied.
 */
public class ConfigSet {
    private String id;
    private List<Dependency> dependencies = new ArrayList<>();
    private List<Package> copySources = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<Dependency> dependencies) {
        this.dependencies = dependencies;
    }

    public List<Package> getCopySources() {
        return copySources;
    }

    public void setCopySources(List<Package> copySources) {
        this.copySources = copySources;
    }

    @Override
    public String toString() {
        return "ConfigSet{" +
                "id='" + id + '\'' +
                ", dependencies=" + dependencies +
                ", copySources=" + copySources +
                '}';
    }
}
