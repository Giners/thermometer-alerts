package org.thermometer;

import java.util.function.Consumer;

public class TemperatureThreshold implements TemperatureThresholdEventListener {

    public static final TemperatureScales DEFAULT_TEMPERATURE_SCALE = TemperatureScales.CELSIUS_SCALE;
    public static final float DEFAULT_THRESHOLD_TRIGGER_PRECISION = 0.0F;
    public static final ThresholdTriggerDirections DEFAULT_THRESHOLD_TRIGGER_DIRECTION = null;

    private final float temperatureThreshold;
    private final Consumer<Float> thresholdEventCallback;
    private final TemperatureScales temperatureScale;
    private final float thresholdTriggerPrecision;
    private final ThresholdTriggerDirections thresholdTriggerDirection;

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
        private TemperatureScales temperatureScale = DEFAULT_TEMPERATURE_SCALE;
        private float thresholdTriggerPrecision = DEFAULT_THRESHOLD_TRIGGER_PRECISION;
        private ThresholdTriggerDirections thresholdTriggerDirection = DEFAULT_THRESHOLD_TRIGGER_DIRECTION;

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
    public void onTemperatureRead(float newTemperature, float previousTemperature, TemperatureScales temperatureScaleUsedForReading) {
        // Ensure that our temperatures are in the correct scale as defined for this threshold
        // in the event that the scale used to read the temperature was different
        float convertedNewTemperature = convertTemperature(newTemperature, temperatureScaleUsedForReading, temperatureScale);
        float convertedPreviousTemperature = convertTemperature(previousTemperature, temperatureScaleUsedForReading, temperatureScale);

        if (shouldTriggerThresholdEvent(convertedNewTemperature, convertedPreviousTemperature)) {
            thresholdEventCallback.accept(convertedNewTemperature);
        }
    }

    private boolean shouldTriggerThresholdEvent(float newTemperature, float previousTemperature) {
        // Check to see if a temperature threshold has been reached along with
        // any additional requirements that the consumer provided and if so
        // trigger the event callback.
        return isTempThresholdReached(newTemperature, previousTemperature)
                && isTempDifferencePrecise(newTemperature, previousTemperature)
                && isTempMovementCorrectDirection(newTemperature, previousTemperature);
    }

    public boolean isTempThresholdReached(float newTemperature, float previousTemperature) {
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

    public boolean isTempDifferencePrecise(float newTemperature, float previousTemperature) {
        return Math.abs(newTemperature - previousTemperature) >= thresholdTriggerPrecision;
    }

    public boolean isTempMovementCorrectDirection(float newTemperature, float previousTemperature) {
        // If no trigger direction was provided indicate that any movement is
        // the correct direction
        if (thresholdTriggerDirection == null) {
            return true;
        }

        return (thresholdTriggerDirection == ThresholdTriggerDirections.DECREASING_TEMP && newTemperature < previousTemperature)
                || (thresholdTriggerDirection == ThresholdTriggerDirections.INCREASING_TEMP && newTemperature > previousTemperature);
    }

    public float getTemperatureThreshold() {
        return temperatureThreshold;
    }

    public TemperatureScales getTemperatureScale() {
        return temperatureScale;
    }

    public float getThresholdTriggerPrecision() {
        return thresholdTriggerPrecision;
    }

    public ThresholdTriggerDirections getThresholdTriggerDirection() {
        return thresholdTriggerDirection;
    }

    public static float convertTemperature(
            float temp,
            TemperatureScales currentTemperatureScale,
            TemperatureScales expectedTemperatureScale) {

        // If the scales are the same then simply return the temp that is provided
        if (currentTemperatureScale == expectedTemperatureScale) {
            return temp;
        }

        // Otherwise convert the temp to the expected temperature scale
        if (expectedTemperatureScale == TemperatureScales.CELSIUS_SCALE) {
            return convertFahrenheitToCelsius(temp);
        }

        return convertCelsiusToFahrenheit(temp);
    }

    public static float convertCelsiusToFahrenheit(float celsiusTemp) {
        return (celsiusTemp * (9 / 5)) + 32;
    }

    public static float convertFahrenheitToCelsius(float fahrenheitTemp) {
        return (fahrenheitTemp - 32) * (5 / 9);
    }

}
