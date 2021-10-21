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
package org.kie.mojos;

import java.io.File;
import java.nio.file.Path;

import org.apache.maven.plugin.testing.MojoRule;
import org.hamcrest.io.FileMatchers;
import org.junit.Rule;

import static org.hamcrest.MatcherAssert.assertThat;

public abstract class AbstractMojoTest<T extends AbstractMojoDefiningParameters> {

    private String goal;

    @Rule
    public MojoRule rule = new MojoRule() {
        @Override
        protected void before() throws Throwable {
        }

        @Override
        protected void after() {
        }
    };

    public AbstractMojoTest(String goal) {
        this.goal = goal;
    }

    public T getMojo(File pom) throws Exception {
        return (T) rule.lookupMojo(goal, pom);
    }

    public T getMojo(String projectDirString, String pomFileName) throws Exception {
        Path projectDir = Path.of(projectDirString);
        assertThat("Project directory does not exist", projectDir.toFile(), FileMatchers.anExistingDirectory());
        File pomFile = projectDir.resolve(pomFileName).toFile();
        assertThat("Tested xml file does not exist", pomFile, FileMatchers.anExistingFile());
        return getMojo(pomFile);
    }

}
