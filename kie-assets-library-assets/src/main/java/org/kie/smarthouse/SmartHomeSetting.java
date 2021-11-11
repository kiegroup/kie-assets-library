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

public class SmartHomeSetting {

    private double threshold_low;
    private double threshold_high;

    public double getThreshold_low() {
        return threshold_low;
    }

    public void setThreshold_low(double threshold_low) {
        this.threshold_low = threshold_low;
    }

    public double getThreshold_high() {
        return threshold_high;
    }

    public void setThreshold_high(double threshold_high) {
        this.threshold_high = threshold_high;
    }

    @Override
    public String toString() {
        return "SmartHomeSetting [threshold_high=" + threshold_high + ", threshold_low=" + threshold_low + "]";
    }

}
