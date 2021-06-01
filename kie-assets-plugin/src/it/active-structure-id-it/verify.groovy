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

def allThree = ["test.dmn", "test2.dmn", "test3.dmn"]
def justTwo = ["test.dmn", "test3.dmn"]
assert Files.list(basedir.toPath().resolve("target")).filter({Files.isDirectory(it)}).count()==2
checkProjectAndResources("test-generated-kjar", allThree)
checkProjectAndResources("test-generated2-kjar", justTwo)


def checkProjectAndResources(String projectName, List<String> resources){
    Path projectRoot = basedir.toPath().resolve("target/$projectName");
    assert Files.isDirectory(projectRoot) : "project root does not exist $projectRoot"
    assert Files.isRegularFile(projectRoot.resolve("pom.xml")) : "pom does not exist in $projectRoot"
    def srcMainResources = projectRoot.resolve("src/main/resources")
    assert Files.isDirectory(srcMainResources) : "direcory not exists $srcMainResources"
    resources.forEach {resource -> assert Files.isRegularFile(srcMainResources.resolve(resource)):"file does not exist $resource"}
}