package org.thermometer;

public class TemperatureThreshold implements TemperatureThresholdEventListener {

    private TemperatureThreshold(
            float temperatureThreshold,
            TemperatureScales temperatureScale,
            float thresholdPrecision,
            ThresholdTriggerDirections triggerDirection) {
        this.temperatureThreshold = temperatureThreshold;
        this.temperatureScale = temperatureScale;
        this.thresholdPrecision = thresholdPrecision;
        this.triggerDirection = triggerDirection;
    }

    public static class TemperatureThresholdBuilder {

        private final float temperatureThreshold;
        private TemperatureScales temperatureScale = TemperatureScales.CELSIUS_SCALE;
        private float thresholdPrecision = 0.0F;
        private ThresholdTriggerDirections triggerDirection = null;

        public TemperatureThresholdBuilder(float temperatureThreshold) {
            this.temperatureThreshold = temperatureThreshold;
        }

        public TemperatureThreshold build() {
            return new TemperatureThreshold(
                    this.temperatureThreshold,
                    this.temperatureScale,
                    this.thresholdPrecision,
                    this.triggerDirection);
        }

        public TemperatureThresholdBuilder temperatureScale(TemperatureScales temperatureScale) {
            this.temperatureScale = temperatureScale;
            return this;
        }

        public TemperatureThresholdBuilder thresholdPrecision(float thresholdPrecision) {
            this.thresholdPrecision = thresholdPrecision;
            return this;
        }

        public TemperatureThresholdBuilder triggerDirection(ThresholdTriggerDirections triggerDirection) {
            this.triggerDirection = triggerDirection;
            return this;
        }
    }

    private final float temperatureThreshold;
    private TemperatureScales temperatureScale;
    private float thresholdPrecision;
    private ThresholdTriggerDirections triggerDirection;

    @Override
    public void onTemperatureRead(float newTemperature, float previousTemperature, TemperatureScales temperatureScale) {
        System.out.println(String.format("TemperatureThreshold - newTemperature read: %f", newTemperature));
        System.out.println(String.format("TemperatureThreshold - previousTemperature read: %f", previousTemperature));
    }

}
