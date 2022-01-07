/**
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
import java.nio.file.Files
import java.nio.file.Path


assert Files.list(basedir.toPath().resolve("target")).filter({Files.isDirectory(it)}).count()==6 // 4 projects + 2 local maven repos
checkProjectAndResources("test-generated-quarkus", false)
checkProjectAndResources("test-generated-springboot", false)
checkProjectAndResources("test-generated-quarkus-custom", true)
checkProjectAndResources("test-generated-springboot-custom", true)


def checkProjectAndResources(String projectName, boolean overridden){
    Path projectRoot = basedir.toPath().resolve("target/$projectName");
    assert Files.isDirectory(projectRoot) : "project root does not exist $projectRoot"
    assert Files.isRegularFile(projectRoot.resolve("pom.xml")) : "pom does not exist in $projectRoot"
    def srcMainResources = projectRoot.resolve("src/main/resources")
    assert Files.isDirectory(srcMainResources) : "direcory not exists $srcMainResources"
    String settingsXml = basedir.toPath().resolve("../interpolated-settings.xml").normalize().toString()
    String localRepo = basedir.toPath().resolve("..").resolve("..").resolve("local-repo").normalize().toString()
    if (overridden) {
        settingsXml = basedir.toPath().resolve("settings.xml")
        localRepo = basedir.toPath().resolve("target").resolve(projectName.substring("test-generated".length() + 1) + "-local-repo").toString()
    }
    Path mavenConfig = projectRoot.resolve(".mvn").resolve("maven.config")
    def fileContent = Files.readString(mavenConfig)
    assert fileContent.contains(settingsXml)
    assert fileContent.contains(localRepo)
}