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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;

public class CliUtils {
    private Log log;

    public CliUtils() {
        this.log = new SystemStreamLog();
    }

    public CliUtils(Log log) {
        this.log = log;
    }

    private Log getLog() {
        return this.log;
    }

    /**
     * Execute given command in CLI/terminal.
     *
     * @param command string containing the command
     * @param workDir location where the comamnd is invoked.
     * @throws MojoExecutionException
     */
    public void executeCliCommand(String command, File workDir) throws MojoExecutionException {
        Process process = null;
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        try {
            getLog().info("About to execute '" + command + "' in directory " + workDir.getAbsolutePath());
            process = executeProcess(command, workDir);
            executorService.execute(collectLogs(getLog()::info, process.getInputStream()));
            executorService.execute(collectLogs(getLog()::error, process.getErrorStream()));
            if (!process.waitFor(10, TimeUnit.MINUTES)) {
                throw new MojoExecutionException("CLI command didn't finish in time.");
            }
            if (process.exitValue() != 0) {
                throw new MojoExecutionException("CLI command ended with state " + process.exitValue());
            }
        } catch (IOException | InterruptedException e) {
            throw new MojoExecutionException("Exception while invoking CLI", e);
        } finally {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(1, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
            }
        }
    }

    /**
     * Low-level method to start command as a new process in given directory.
     *
     * @param command
     * @param workDir
     * @return
     * @throws IOException
     */
    private Process executeProcess(String command, File workDir)
            throws IOException {
        Runtime runtime = Runtime.getRuntime();
        return runtime.exec(command,
                null,
                workDir);
    }

    /**
     * Collecting logs from the running process and passing to provided consumer.
     *
     * @param consumer
     * @param stream
     * @return Runnable action to collect logs real-time.
     */
    private Runnable collectLogs(Consumer<String> consumer, InputStream stream) {
        return () -> {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream))) {
                bufferedReader.lines().forEach(consumer);
            } catch (IOException e) {
                getLog().error("Issues when reading from process input stream.", e);
            }
        };
    }
}
