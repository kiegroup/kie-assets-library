package org.kie.model;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * Artifact to hold configuration of dependencies and source packages to be copied.
 */
public class ConfigSet {
    private String id;
    private List<Dependency> dependencies = new ArrayList<>();
    private List<Package> copySources = new ArrayList<>();
    private List<Resource> copyResources = new ArrayList<>();
    private Properties properties = new Properties();

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

    public List<Resource> getCopyResources() {
        return copyResources;
    }

    public void setCopyResources(List<Resource> copyResources) {
        this.copyResources = copyResources;
    }

    public Properties getProperties() {
        return properties;
    }

    public ConfigSet setProperties(Properties properties) {
        this.properties = properties;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigSet configSet = (ConfigSet) o;
        return Objects.equals(id, configSet.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ConfigSet{" +
                "id='" + id + '\'' +
                ", dependencies=" + dependencies +
                ", copySources=" + copySources +
                ", copyResources=" + copyResources +
                ", properties=" + properties +
                '}';
    }
}
