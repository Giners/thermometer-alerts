package org.thermometer;

public interface TemperatureThresholdEventListener {

    public void onTemperatureRead(float newTemperature, float previousTemperature, TemperatureScales temperatureScale);

}
