/**
 * MIT license
 * Copyright 2017 Autodesk, Inc.
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions
 * of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */
package org.jenkinsci.plugins.benchmark.results;

import java.util.Map;

/**
 * Holds the information for numeral test result
 *
 * @author Daniel Mercier
 * @since 5/10/2017
 */
public abstract class NumeralValue<T extends Number & Comparable<T>> extends TestValue<T> {


    // Constructor
    NumeralValue(TestGroup parent, String name, String description, String unit, ValueType type) {
        super(parent, null, name, description, unit, type, ClassType.ct_result);
    }

    NumeralValue(TestGroup parent, String group, String name, String description, String unit, ValueType type) {
        super(parent, group, name, description, unit, type, ClassType.ct_result);
    }

    NumeralValue(TestGroup parent, String name, String description, String unit, ValueType type, ClassType ctype) {
        super(parent, null, name, description, unit, type, ctype);
    }

    NumeralValue(TestGroup parent, String group, String name, String description, String unit, ValueType type, ClassType ctype) {
        super(parent, group, name, description, unit, type, ctype);
    }

    protected boolean isFailedBuild(int build) {
        TestProperty property = this.properties.get(build);
        if (property == null) {
            //throw new IllegalStateException("Build has no property");
            return false;
        } else {
            Boolean failedState = property.getFailedState();
            return failedState != null && failedState;
        }
    }

    private static class en<T> implements Map.Entry<Integer, T> {
        int key;
        T value;

        private en(boolean max) {
            this.key = Integer.MAX_VALUE;
            this.value = null;
        }

        @Override
        public Integer getKey() {
            return key;
        }

        @Override
        public T getValue() {
            return value;
        }

        @Override
        public T setValue(T value) {
            return value;
        }
    }

    // Getters

    /**
     * Get minimum
     *
     * @return minimum if available
     */
    public T getMaximum() {
        int max_build = Integer.MIN_VALUE;
        Map.Entry<Integer, T> max = new en<>(true);
        Map.Entry<Integer, T> max_1 = new en<>(true);
        for (Map.Entry<Integer, T> entry : values.entrySet()) {
            if (isFailedBuild(entry.getKey())) continue;
            if (entry.getKey() > max_build) {
                max_build = entry.getKey();
            }

            if (max.getValue() == null || entry.getValue().compareTo(max.getValue()) > 0) {
                if (max_1.getValue() == null) {
                    max_1 = entry;
                } else if (max.getValue().compareTo(max_1.getValue()) > 0) {
                    max_1 = max;
                }

                max = entry;
            }
        }
        if (max instanceof en) return null;
        return max != max_1 ? max_build == max.getKey() ? max_1.getValue() : max.getValue() : max.getValue();
    }

    public T getMinimum() {
        int min_build = Integer.MIN_VALUE;
        Map.Entry<Integer, T> min = new en<>(false);
        Map.Entry<Integer, T> min_1 = new en<>(false);
        for (Map.Entry<Integer, T> entry : values.entrySet()) {
            if (isFailedBuild(entry.getKey())) continue;
            if (entry.getKey() > min_build) {
                min_build = entry.getKey();
            }
            if (min.getValue() == null || entry.getValue().compareTo(min.getValue()) < 0) {
                if (min_1.getValue() == null) {
                    min_1 = entry;
                } else if (min.getValue().compareTo(min_1.getValue()) < 0) {
                    min_1 = min;
                }

                min = entry;
            }
        }
        if (min instanceof en) return null;
        return min != min_1 ? min_build == min.getKey() ? min_1.getValue() : min.getValue() : min.getValue();
    }

}
