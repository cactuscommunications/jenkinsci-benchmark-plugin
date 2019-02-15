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
import hudson.Extension;
import hudson.model.Run;
import hudson.util.FormValidation;
import org.jenkinsci.plugins.benchmark.core.BenchmarkPublisher;
import org.jenkinsci.plugins.benchmark.exceptions.ValidationException;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

/**
 * Monotonicity requirement with a delta threshold margin
 *
 * @author Marhlder
 * @since 2/12/2017
 */
public class DeltaMonotonicThreshold extends Threshold {

    // Variables
    private final Double delta;
    private Double previous;
    private Double maximumValue;
    private Double minimumValue;

    // Constructors
    @DataBoundConstructor
    public DeltaMonotonicThreshold(String testGroup, String testName, Double delta) {
        super(testGroup, testName, ThresholdTypes.tt_deltaMonotonic);

        this.delta = delta;
        this.previous = null;
    }

    public DeltaMonotonicThreshold(Double delta) throws ValidationException {
        super(ThresholdTypes.tt_deltaMonotonic);

        if (delta == null) {
            throw new ValidationException(Messages.DeltaThreshold_MissingDeltaValue());
        }

        this.delta = delta;
        this.previous = null;
    }

    // Functions
    public boolean evaluate(List<? extends Run<?, ?>> builds) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, AbortException, ParseException {
        return true;
    }

    @Override
    public boolean isValid(int value) throws NullPointerException, ValidationException {
        return this.isValid((double) value);
    }

    @Override
    public boolean isValid(double value) throws NullPointerException, ValidationException {

        // This threshold is always valid for the first build, i.e. when there is not maximum or minimum value yet or no delta
        if (maximumValue == null && minimumValue == null || delta == null) {
            return true;
            //throw new ValidationException(Messages.AbsoluteThreshold_IsAboveMaximum(Double.toString(value), Double.toString(maximumValue)));
        }

        // If delta delta is negative, then a new value should never be bigger than minimum
        if (Math.copySign(1, delta) < 0) {

            if (minimumValue != null && value + delta > minimumValue) {
                //System.out.println(String.format("Value: %f, delta: %f, minimumValue: %f", value, delta, minimumValue));
                return false;
            }

        }
        // Else delta is positive
        else {
            BenchmarkPublisher.logger.println(String.format("Value: %f, delta: %f, maximumValue: %f", value, delta, maximumValue));
            if (maximumValue != null && (value + delta < maximumValue)) {
                return false;
            }
        }

        return true;
    }

    // Setters
    public void setMaximumValue(Double maximumValue) {
        this.maximumValue = maximumValue;
    }

    public void setMinimumValue(Double minimumValue) {
        this.minimumValue = minimumValue;
    }

    public void setPreviousValue(Double previousValue) {
        this.previous = previousValue;
    }

    // Getter
    public Double getPrevious() {
        return previous;
    }

    // Descriptor (active interactor)
    @Extension
    public static class DescriptorImpl extends ThresholdDescriptor {

        @Override
        @Nonnull
        public String getDisplayName() {
            return Messages.DeltaMonotonicThreshold_MonotonicFunctionWithDeltaMargin();
        }

        public FormValidation doCheckDelta(@QueryParameter Double delta) {

            if (delta == null) {
                return FormValidation.error(Messages.DeltaThreshold_DeltaCannotBeEmpty());
            }

            return FormValidation.ok();
        }
    }
}
