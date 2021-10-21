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
package org.kie.utils;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class CliUtilsTest {

    @Rule
    public ExpectedException exceptionGrabber = ExpectedException.none();

    @Test
    public void testExecuteCliCommand() throws MojoExecutionException {
        CliUtils utils = new CliUtils();
        utils.executeCliCommand("mvn -version", new File(System.getProperty("java.io.tmpdir")));
    }

    @Test
    public void testExecuteWrongCliCommand() throws MojoExecutionException {
        CliUtils utils = new CliUtils();
        exceptionGrabber.expect(MojoExecutionException.class);
        exceptionGrabber.expectMessage("CLI command ended with state 1");
        utils.executeCliCommand("mvn -wrongparam", new File(System.getProperty("java.io.tmpdir")));
    }

}
