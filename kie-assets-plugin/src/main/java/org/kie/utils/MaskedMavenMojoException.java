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

/**
 * RuntimeException to be thrown from java Functional interface to avoid try/catch cluttering.
 */
public class MaskedMavenMojoException extends RuntimeException {
    public MaskedMavenMojoException() {
        super();
    }

    public MaskedMavenMojoException(String message) {
        super(message);
    }

    public MaskedMavenMojoException(String message, Throwable cause) {
        super(message, cause);
    }

    public MaskedMavenMojoException(Throwable cause) {
        super(cause);
    }

    public MaskedMavenMojoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
