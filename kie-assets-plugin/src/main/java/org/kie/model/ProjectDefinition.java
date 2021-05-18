package org.kie.model;

import java.util.Objects;

/**
 * Holds information about the project to be created and what resources should be copied into resulting project structure.
 */
public class ProjectDefinition {
    private String id;
    private String groupId;
    private String artifactId;
    private String packageName;
    private String finalName;
    private ConfigSet config = new ConfigSet();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public ConfigSet getConfig() {
        return config;
    }

    public void setConfig(ConfigSet config) {
        this.config = config;
    }

    public String getFinalName() {
        return finalName;
    }

    public ProjectDefinition setFinalName(String finalName) {
        this.finalName = finalName;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectDefinition that = (ProjectDefinition) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ProjectDefinition{" +
                "id='" + id + '\'' +
                ", groupId='" + groupId + '\'' +
                ", artifactId='" + artifactId + '\'' +
                ", packageName='" + packageName + '\'' +
                ", finalName='" + finalName + '\'' +
                ", config=" + config +
                '}';
    }
}
