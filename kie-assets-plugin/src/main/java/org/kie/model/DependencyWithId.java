package org.kie.model;

import org.apache.maven.model.Dependency;

public class DependencyWithId extends Dependency {
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
