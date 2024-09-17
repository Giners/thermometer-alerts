package org.thermometer;

public class TemperatureThreshold implements TemperatureThresholdEventListener {

    private float threshold;
    private float thresholdPrecision = 0.0F;

    public TemperatureThreshold(float threshold) {
        this.threshold = threshold;
    }

    @Override
    public void onTemperatureRead(float newTemperature, float previousTemperature) {
        System.out.println(String.format("TemperatureThreshold - newTemperature read: %f", newTemperature));
        System.out.println(String.format("TemperatureThreshold - previousTemperature read: %f", previousTemperature));
    }

}
