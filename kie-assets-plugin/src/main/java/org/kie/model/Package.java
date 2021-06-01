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

/**
 * Definition of package to be copied.
 */
public class Package {
    private String name;
    private String type = "main";
    private String language = "java";
    private String filesystemRoot = "./";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getFilesystemRoot() {
        return filesystemRoot;
    }

    public void setFilesystemRoot(String filesystemRoot) {
        this.filesystemRoot = filesystemRoot;
    }

    @Override
    public String toString() {
        return "Package{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", language='" + language + '\'' +
                ", filesystemRoot='" + filesystemRoot + '\'' +
                '}';
    }
}
