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

import java.util.function.BiConsumer;

import org.kie.model.ProjectDefinition;
import org.kie.model.ProjectStructure;

/**
 * Custom extension of {@linkplain BiConsumer} to enable exception-throwing code to be defined in FunctionalInterface.
 * Handled by catching any Exception and re-throwing runtime {@linkplain MaskedMavenMojoException}.
 */
@FunctionalInterface
public interface ThrowingBiConsumer extends BiConsumer<ProjectDefinition, ProjectStructure> {
    @Override
    default void accept(ProjectDefinition definition, ProjectStructure structure) {
        try {
            acceptThrows(definition, structure);
        } catch (Exception e) {
            throw new MaskedMavenMojoException(e);
        }
    }

    void acceptThrows(ProjectDefinition definition, ProjectStructure structure) throws Exception;
}
