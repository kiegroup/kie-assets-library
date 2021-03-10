package org.kie.model;

import org.apache.maven.model.Resource;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds information about the project to be created and what resources should be copied into resulting project structure.
 */
public class ProjectDefinition {
    private String groupId;
    private String artifactId;
    private String packageName;
    private List<Resource> copyResources = new ArrayList<>();
    private List<String> configurationIds = new ArrayList<>();

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

    public List<Resource> getCopyResources() {
        return copyResources;
    }

    public void setCopyResources(List<Resource> copyResources) {
        this.copyResources = copyResources;
    }

    public List<String> getConfigurationIds() {
        return configurationIds;
    }

    public void setConfigurationIds(List<String> configurationIds) {
        this.configurationIds = configurationIds;
    }
}
