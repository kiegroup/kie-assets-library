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

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

import org.hamcrest.Matchers;
import org.hamcrest.io.FileMatchers;
import org.junit.Assert;
import org.junit.Test;

public class FileFilteringUtilsTest {
    Path rootDir = Path.of(Objects.requireNonNull(FileFilteringUtils.class.getResource("/file-filtering-utils/file-root")).getFile()).getParent();

    @Test
    public void testMatchPathsUsingGlobIncludesOnly() throws IOException {
        Assert.assertThat(FileFilteringUtils.matchPathsUsingGlob(Collections.singletonList("*"), Collections.emptyList(), rootDir).stream().map(it -> it.toFile()).collect(Collectors.toList()),
                Matchers.hasItems(
                        FileMatchers.aFileWithAbsolutePath(Matchers.equalTo(rootDir.resolve("file-root").toFile().getAbsolutePath()))));

        Assert.assertThat(FileFilteringUtils.matchPathsUsingGlob(Collections.singletonList("**"), Collections.emptyList(), rootDir).stream().map(it -> it.toFile()).collect(Collectors.toList()),
                Matchers.hasItems(
                        FileMatchers.aFileWithAbsolutePath(Matchers.equalTo(rootDir.resolve("file-root").toFile().getAbsolutePath())),
                        FileMatchers.aFileWithAbsolutePath(Matchers.equalTo(rootDir.resolve("dir-1").resolve("file1").toFile().getAbsolutePath())),
                        FileMatchers.aFileWithAbsolutePath(Matchers.equalTo(rootDir.resolve("dir-2").resolve("file2").toFile().getAbsolutePath())),
                        FileMatchers.aFileWithAbsolutePath(Matchers.equalTo(rootDir.resolve("dir-2").resolve("dir-2-1").resolve("file3").toFile().getAbsolutePath()))));

        Assert.assertThat(FileFilteringUtils.matchPathsUsingGlob(Collections.singletonList("**/*"), Collections.emptyList(), rootDir).stream().map(it -> it.toFile()).collect(Collectors.toList()),
                Matchers.hasItems(
                        FileMatchers.aFileWithAbsolutePath(Matchers.equalTo(rootDir.resolve("dir-1").resolve("file1").toFile().getAbsolutePath())),
                        FileMatchers.aFileWithAbsolutePath(Matchers.equalTo(rootDir.resolve("dir-2").resolve("file2").toFile().getAbsolutePath())),
                        FileMatchers.aFileWithAbsolutePath(Matchers.equalTo(rootDir.resolve("dir-2").resolve("dir-2-1").resolve("file3").toFile().getAbsolutePath()))));

        Assert.assertThat(FileFilteringUtils.matchPathsUsingGlob(Collections.singletonList("dir-2/**"), Collections.emptyList(), rootDir).stream().map(it -> it.toFile()).collect(Collectors.toList()),
                Matchers.hasItems(
                        FileMatchers.aFileWithAbsolutePath(Matchers.equalTo(rootDir.resolve("dir-2").resolve("file2").toFile().getAbsolutePath())),
                        FileMatchers.aFileWithAbsolutePath(Matchers.equalTo(rootDir.resolve("dir-2").resolve("dir-2-1").resolve("file3").toFile().getAbsolutePath()))));

        Assert.assertThat(
                FileFilteringUtils.matchPathsUsingGlob(Collections.singletonList("dir-2/**/*"), Collections.emptyList(), rootDir).stream().map(it -> it.toFile()).collect(Collectors.toList()),
                Matchers.hasItems(
                        FileMatchers.aFileWithAbsolutePath(Matchers.equalTo(rootDir.resolve("dir-2").resolve("dir-2-1").resolve("file3").toFile().getAbsolutePath()))));
    }

    @Test
    public void testMatchPathsUsingGlobExcludes() throws IOException {
        Assert.assertThat(
                FileFilteringUtils.matchPathsUsingGlob(Collections.singletonList("**"), Collections.singletonList("file3"), rootDir).stream().map(it -> it.toFile()).collect(Collectors.toList()),
                Matchers.hasItems(
                        FileMatchers.aFileWithAbsolutePath(Matchers.equalTo(rootDir.resolve("file-root").toFile().getAbsolutePath())),
                        FileMatchers.aFileWithAbsolutePath(Matchers.equalTo(rootDir.resolve("dir-1").resolve("file1").toFile().getAbsolutePath())),
                        FileMatchers.aFileWithAbsolutePath(Matchers.equalTo(rootDir.resolve("dir-2").resolve("file2").toFile().getAbsolutePath())),
                        FileMatchers.aFileWithAbsolutePath(Matchers.equalTo(rootDir.resolve("dir-2").resolve("dir-2-1").resolve("file3").toFile().getAbsolutePath()))));
        Assert.assertThat(
                FileFilteringUtils.matchPathsUsingGlob(Collections.singletonList("**"), Collections.singletonList("*/file3"), rootDir).stream().map(it -> it.toFile()).collect(Collectors.toList()),
                Matchers.hasItems(
                        FileMatchers.aFileWithAbsolutePath(Matchers.equalTo(rootDir.resolve("file-root").toFile().getAbsolutePath())),
                        FileMatchers.aFileWithAbsolutePath(Matchers.equalTo(rootDir.resolve("dir-1").resolve("file1").toFile().getAbsolutePath())),
                        FileMatchers.aFileWithAbsolutePath(Matchers.equalTo(rootDir.resolve("dir-2").resolve("file2").toFile().getAbsolutePath())),
                        FileMatchers.aFileWithAbsolutePath(Matchers.equalTo(rootDir.resolve("dir-2").resolve("dir-2-1").resolve("file3").toFile().getAbsolutePath()))));

        Assert.assertThat(
                FileFilteringUtils.matchPathsUsingGlob(Collections.singletonList("**"), Collections.singletonList("**/file3"), rootDir).stream().map(it -> it.toFile()).collect(Collectors.toList()),
                Matchers.hasItems(
                        FileMatchers.aFileWithAbsolutePath(Matchers.equalTo(rootDir.resolve("file-root").toFile().getAbsolutePath())),
                        FileMatchers.aFileWithAbsolutePath(Matchers.equalTo(rootDir.resolve("dir-1").resolve("file1").toFile().getAbsolutePath())),
                        FileMatchers.aFileWithAbsolutePath(Matchers.equalTo(rootDir.resolve("dir-2").resolve("file2").toFile().getAbsolutePath()))));

        Assert.assertThat(
                FileFilteringUtils.matchPathsUsingGlob(Collections.singletonList("**"), Collections.singletonList("*/*/file3"), rootDir).stream().map(it -> it.toFile()).collect(Collectors.toList()),
                Matchers.hasItems(
                        FileMatchers.aFileWithAbsolutePath(Matchers.equalTo(rootDir.resolve("file-root").toFile().getAbsolutePath())),
                        FileMatchers.aFileWithAbsolutePath(Matchers.equalTo(rootDir.resolve("dir-1").resolve("file1").toFile().getAbsolutePath())),
                        FileMatchers.aFileWithAbsolutePath(Matchers.equalTo(rootDir.resolve("dir-2").resolve("file2").toFile().getAbsolutePath()))));

        Assert.assertThat(FileFilteringUtils.matchPathsUsingGlob(Collections.singletonList("**"), Collections.singletonList("*"), rootDir).stream().map(it -> it.toFile()).collect(Collectors.toList()),
                Matchers.hasItems(
                        FileMatchers.aFileWithAbsolutePath(Matchers.equalTo(rootDir.resolve("dir-1").resolve("file1").toFile().getAbsolutePath())),
                        FileMatchers.aFileWithAbsolutePath(Matchers.equalTo(rootDir.resolve("dir-2").resolve("file2").toFile().getAbsolutePath())),
                        FileMatchers.aFileWithAbsolutePath(Matchers.equalTo(rootDir.resolve("dir-2").resolve("dir-2-1").resolve("file3").toFile().getAbsolutePath()))));

        Assert.assertThat(
                FileFilteringUtils.matchPathsUsingGlob(Collections.singletonList("**"), Collections.singletonList("*/*"), rootDir).stream().map(it -> it.toFile()).collect(Collectors.toList()),
                Matchers.hasItems(
                        FileMatchers.aFileWithAbsolutePath(Matchers.equalTo(rootDir.resolve("file-root").toFile().getAbsolutePath())),
                        FileMatchers.aFileWithAbsolutePath(Matchers.equalTo(rootDir.resolve("dir-2").resolve("dir-2-1").resolve("file3").toFile().getAbsolutePath()))));
        Assert.assertThat(
                FileFilteringUtils.matchPathsUsingGlob(Collections.singletonList("**"), Collections.singletonList("**/*"), rootDir).stream().map(it -> it.toFile()).collect(Collectors.toList()),
                Matchers.hasItems(
                        FileMatchers.aFileWithAbsolutePath(Matchers.equalTo(rootDir.resolve("file-root").toFile().getAbsolutePath()))));
        Assert.assertThat(
                FileFilteringUtils.matchPathsUsingGlob(Collections.singletonList("**"), Collections.singletonList("**"), rootDir).stream().map(it -> it.toFile()).collect(Collectors.toList()),
                Matchers.empty());

    }
}
