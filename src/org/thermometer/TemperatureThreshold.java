package org.thermometer;

import java.util.function.Consumer;

public class TemperatureThreshold implements TemperatureThresholdEventListener {

    private final float temperatureThreshold;
    private final Consumer<Float> thresholdEventCallback;
    private TemperatureScales temperatureScale;
    private float thresholdTriggerPrecision;
    private ThresholdTriggerDirections thresholdTriggerDirection;

    private TemperatureThreshold(
            float temperatureThreshold,
            TemperatureScales temperatureScale,
            float thresholdTriggerPrecision,
            ThresholdTriggerDirections thresholdTriggerDirection,
            Consumer<Float> thresholdEventCallback
    ) {
        this.temperatureThreshold = temperatureThreshold;
        this.temperatureScale = temperatureScale;
        this.thresholdTriggerPrecision = thresholdTriggerPrecision;
        this.thresholdTriggerDirection = thresholdTriggerDirection;
        this.thresholdEventCallback = thresholdEventCallback;
    }

    public static class TemperatureThresholdBuilder {

        private final float temperatureThreshold;
        private final Consumer<Float> thresholdEventCallback;
        private TemperatureScales temperatureScale = TemperatureScales.CELSIUS_SCALE;
        private float thresholdTriggerPrecision = 0.0F;
        private ThresholdTriggerDirections thresholdTriggerDirection = null;

        public TemperatureThresholdBuilder(float temperatureThreshold, Consumer<Float> thresholdEventCallback) {
            this.temperatureThreshold = temperatureThreshold;
            this.thresholdEventCallback = thresholdEventCallback;
        }

        public TemperatureThreshold build() {
            return new TemperatureThreshold(
                    this.temperatureThreshold,
                    this.temperatureScale,
                    this.thresholdTriggerPrecision,
                    this.thresholdTriggerDirection,
                    this.thresholdEventCallback);
        }

        public TemperatureThresholdBuilder temperatureScale(TemperatureScales temperatureScale) {
            this.temperatureScale = temperatureScale;
            return this;
        }

        public TemperatureThresholdBuilder thresholdTriggerPrecision(float thresholdTriggerPrecision) {
            this.thresholdTriggerPrecision = thresholdTriggerPrecision;
            return this;
        }

        public TemperatureThresholdBuilder thresholdTriggerDirection(ThresholdTriggerDirections thresholdTriggerDirection) {
            this.thresholdTriggerDirection = thresholdTriggerDirection;
            return this;
        }
    }

    @Override
    public void onTemperatureRead(float newTemperature, float previousTemperature, TemperatureScales temperatureScale) {
        // TODO: Convert temps

        System.out.println(String.format("onTemperatureRead - newTemp: %f     previousTemp: %f", newTemperature, previousTemperature));

        if (shouldTriggerThresholdEvent(newTemperature, previousTemperature)) {
            thresholdEventCallback.accept(newTemperature);
        }
    }

    private boolean shouldTriggerThresholdEvent(float newTemperature, float previousTemperature) {
        // Check to see if a temperature threshold has been reached along with
        // any additional requirements that the consumer provided and if so
        // trigger the event callback.
        return isTempThresholdReached(temperatureThreshold, newTemperature, previousTemperature)
                && isTempDifferencePrecise(newTemperature, previousTemperature)
                && isTempMovementCorrectDirection(newTemperature, previousTemperature);
    }

    private boolean isTempThresholdReached(float temperatureThreshold, float newTemperature, float previousTemperature) {
        // Temperature threshold can be reached in two cases:
        // 1) Previous temperature is less than the threshold and new temperature
        // is equal or greater than the threshold
        // e.g. temperatureThreshold: 10, previousTemperature: 5, newTemperature: 15 (or 10)
        //
        // 2) Previous temperature is greater than the threshold and new temperature
        // is equal or less than the threshold
        // e.g. temperatureThreshold: 10, previousTemperature: 15, newTemperature: 5 (or 10)
        return (previousTemperature < temperatureThreshold && newTemperature >= temperatureThreshold)
                || (previousTemperature > temperatureThreshold && newTemperature <= temperatureThreshold);
    }

    private boolean isTempDifferencePrecise(float newTemperature, float previousTemperature) {
        return Math.abs(newTemperature - previousTemperature) >= thresholdTriggerPrecision;
    }

    private boolean isTempMovementCorrectDirection(float newTemperature, float previousTemperature) {
        // If no trigger direction was provided indicate that any movement is
        // the correct direction
        if (thresholdTriggerDirection == null) {
            return true;
        }

        return (thresholdTriggerDirection == ThresholdTriggerDirections.DECREASING_TEMP && newTemperature < previousTemperature)
                || (thresholdTriggerDirection == ThresholdTriggerDirections.INCREASING_TEMP && newTemperature > previousTemperature);
    }

}
