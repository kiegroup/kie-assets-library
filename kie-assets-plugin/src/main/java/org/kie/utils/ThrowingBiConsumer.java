package org.kie.utils;

import org.kie.model.ProjectDefinition;
import org.kie.model.ProjectStructure;

import java.util.function.BiConsumer;

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
