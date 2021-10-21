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
import java.util.Properties;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Resource;

/**
 * Artifact to hold configuration of dependencies and source packages to be copied.
 */
public class ConfigSet {
    private String id;
    private String reusableConfig; // used to make this a reference to another ConfigSet, in that case other fields are ignored.
    private List<Dependency> dependencies = new ArrayList<>();
    private List<Package> copySources = new ArrayList<>();
    private List<Resource> copyResources = new ArrayList<>();
    private List<Resource> deleteResources = new ArrayList<>();
    private Properties properties = new Properties();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReusableConfig() {
        return reusableConfig;
    }

    public void setReusableConfig(String reusableConfig) {
        this.reusableConfig = reusableConfig;
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

    public List<Resource> getDeleteResources() {
        return deleteResources;
    }

    public void setDeleteResources(List<Resource> deleteResources) {
        this.deleteResources = deleteResources;
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
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
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
                ", reusableConfig='" + reusableConfig + '\'' +
                ", dependencies=" + dependencies +
                ", copySources=" + copySources +
                ", copyResources=" + copyResources +
                ", deleteResources=" + deleteResources +
                ", properties=" + properties +
                '}';
    }
}
