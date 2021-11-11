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
package org.kie.smarthouse;

import java.util.List;

public class SensorInput {

    private String placement;
    private double current;
    private List<Double> previous;

    public String getPlacement() {
        return placement;
    }

    public void setPlacement(String placement) {
        this.placement = placement;
    }

    public double getCurrent() {
        return current;
    }

    public void setCurrent(double current) {
        this.current = current;
    }

    public List<Double> getPrevious() {
        return previous;
    }

    public void setPrevious(List<Double> previous) {
        this.previous = previous;
    }

    @Override
    public String toString() {
        return "SensorInput [current=" + current + ", placement=" + placement + ", previous=" + previous + "]";
    }

    public enum Placement {
        OUTSIDE,
        INSIDE;
    }
}
