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
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
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
