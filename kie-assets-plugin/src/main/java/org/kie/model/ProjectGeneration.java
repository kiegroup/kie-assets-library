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

import java.io.File;
import java.util.Properties;

/**
 * Configuration of method to generate new projects.
 */
public class ProjectGeneration {
    private Type type;
    private Properties properties = new Properties();
    private Artifact archetype;
    private MavenPluginConfig mavenPluginConfig;
    private String quarkusExtensions;
    private Artifact quarkusPlatformGav;
    private File settingsFile;
    private boolean useSeparateRepository;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public Artifact getArchetype() {
        return archetype;
    }

    public void setArchetype(Artifact archetype) {
        this.archetype = archetype;
    }

    public MavenPluginConfig getMavenPluginConfig() {
        return mavenPluginConfig;
    }

    public void setMavenPluginConfig(MavenPluginConfig mavenPluginConfig) {
        this.mavenPluginConfig = mavenPluginConfig;
    }

    public String getQuarkusExtensions() {
        return quarkusExtensions;
    }

    public void setQuarkusExtensions(String quarkusExtensions) {
        this.quarkusExtensions = quarkusExtensions;
    }

    public Artifact getQuarkusPlatformGav() {
        return quarkusPlatformGav;
    }

    public void setQuarkusPlatformGav(Artifact quarkusPlatformGav) {
        this.quarkusPlatformGav = quarkusPlatformGav;
    }

    public File getSettingsFile() {
        return settingsFile;
    }

    public void setSettingsFile(File settingsFile) {
        this.settingsFile = settingsFile;
    }

    public boolean useSeparateRepository() {
        return useSeparateRepository;
    }

    public void setUseSeparateRepository(boolean useSeparateRepository) {
        this.useSeparateRepository = useSeparateRepository;
    }

    public enum Type {
        ARCHETYPE,
        MAVEN_PLUGIN,
        QUARKUS_CLI;
    }
}
