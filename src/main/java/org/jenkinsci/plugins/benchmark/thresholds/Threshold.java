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
package org.jenkinsci.plugins.benchmark.thresholds;

import hudson.AbortException;
import hudson.ExtensionList;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Run;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.benchmark.exceptions.ValidationException;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Threshold base class
 * <p>
 * CAREFULL, this is connected to JELLY
 *
 * @author Daniel Mercier
 * @since 5/16/2017.
 */
public abstract class Threshold extends AbstractDescribableImpl<Threshold> {

    // Enumeration
    public enum ThresholdTypes {    // Threshold method options
        tt_unknown(null),
        tt_absolute("absolute"),
        tt_percentage("percentage"),
        tt_percentageAverage("percentageaverage"),
        tt_delta("delta"),
        tt_deltaAverage("deltaaverage"),
        tt_deltaMonotonic("deltamonotonic");

        public final String identifier;
        private static final Map<String, ThresholdTypes> ENUM_MAP;

        ThresholdTypes(final String identifier) {
            this.identifier = identifier;
        }

        @Override
        public String toString() {
            return this.identifier;
        }

        static {
            Map<String, ThresholdTypes> map = new HashMap<String, ThresholdTypes>();
            for (ThresholdTypes instance : ThresholdTypes.values()) {
                map.put(instance.toString(), instance);
            }
            ENUM_MAP = Collections.unmodifiableMap(map);
        }

        public static ThresholdTypes resolve(final String name) {
            return ENUM_MAP.get(name);
        }

        public static Threshold resolveNewThreshold(
                final ThresholdTypes thresholdType,
                final Double minimum,
                final Double maximum,
                final Double percentage,
                final Double delta) throws ValidationException {
            switch (thresholdType) {
                case tt_absolute: {
                    return new AbsoluteThreshold(minimum, maximum);
                }
                case tt_percentage: {
                    return new PercentageThreshold(percentage);
                }
                case tt_percentageAverage: {
                    return new PercentageAverageThreshold(percentage);
                }
                case tt_delta: {
                    return new DeltaThreshold(delta);
                }
                case tt_deltaAverage: {
                    return new DeltaAverageThreshold(delta);
                }
                case tt_deltaMonotonic: {
                    return new DeltaMonotonicThreshold(delta);
                }
            }

            return null;
        }

    }

    // Variables
    private final ThresholdTypes type;    // Threshold type(see thresholdTypes for options)
    private final String testGroup;
    private final String testName;

    // Constructor
    protected Threshold(String testGroup, String testName, ThresholdTypes type) {
        this.type = type;
        this.testGroup = testGroup;
        this.testName = testName;
    }

    protected Threshold(ThresholdTypes type) {
        this.type = type;
        this.testGroup = "";
        this.testName = "";
    }

    // Functions
    public static ExtensionList<Threshold> all() {
        return Jenkins.getInstance().getExtensionList(Threshold.class);
    }

    public ThresholdDescriptor getDescriptor() {
        return (ThresholdDescriptor) Jenkins.getInstance().getDescriptorOrDie(getClass());
    }

    public boolean isValid(int value) throws NullPointerException, ValidationException {
        return true;
    }

    public boolean isValid(double value) throws NullPointerException, ValidationException {
        return true;
    }


    // Abstract functions

    /**
     * Evaluates whether the threshold is activated or not
     *
     * @param builds all builds that are saved in Jenkins
     * @return Successful evaluation
     * @throws IllegalArgumentException  if illegal argument
     * @throws IllegalAccessException    If illegal access
     * @throws InvocationTargetException If invocation incorrect
     * @throws AbortException            If action aborded
     * @throws ParseException            If parse failed
     */
    public abstract boolean evaluate(List<? extends Run<?, ?>> builds) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, AbortException, ParseException;

    // Setter
    public void setAverageValue(Double average) {
    }

    public void setPreviousValue(Double average) {
    }

    public void setMaximumValue(Double maximumValue) {
    }

    public void setMinimumValue(Double minimumValue) {
    }


    // Getters
    public String getName() {
        switch (type) {
            case tt_absolute:
                return Messages.Threshold_AbsoluteThreshold();
            case tt_percentage:
                return Messages.Threshold_PercentageFromLastThreshold();
            case tt_percentageAverage:
                return Messages.Threshold_PercentageFromAverageThreshold();
            case tt_delta:
                return Messages.Threshold_DeltaFromLastThreshold();
            case tt_deltaAverage:
                return Messages.Threshold_DeltaFromAverageThreshold();
            case tt_deltaMonotonic:
                return Messages.Threshold_DeltaMonotonicThreshold();
            default:
                return Messages.Threshold_UnknownThreshold();
        }
    }

    public ThresholdTypes getType() {
        return type;
    }

    public String getTestGroup() {
        return testGroup;
    }

    public String getTestName() {
        return testName;
    }

}
