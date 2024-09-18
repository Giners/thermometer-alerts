package org.thermometer;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class Thermometer implements TemperatureDataEventListener {

    private TemperatureScales temperatureScale = TemperatureScales.CELSIUS_SCALE;

    private final AtomicReference<Float> currentTempAtomic = new AtomicReference<>(null);
    private final AtomicReference<Float> previousTempAtomic = new AtomicReference<>(null);

    private final ArrayList<TemperatureThresholdEventListener> temperatureThresholds = new ArrayList<>();

    /**
     * Default constructor that creates a thermometer and sets the temperature
     * scale as Celsius.
     */
    public Thermometer() {
        this.temperatureScale = TemperatureScales.CELSIUS_SCALE;
    }

    @Override
    public void onTemperatureData(float currentTemp) {
        Float previousTemp = currentTempAtomic.getAndSet(currentTemp);
        previousTempAtomic.getAndSet(previousTemp);

        // Asynchronously event the temperate data we just read on all the thresholds
        // that are currently registered with us.
        //
        // The first time data is read there won't be a previous value so guard against
        // it being `null` and causing a NPE when converting from the primitive wrapper
        if (previousTemp != null
                && Float.compare(previousTemp, currentTemp) != 0
                && !temperatureThresholds.isEmpty()) {

            new Thread(() -> {
                for (TemperatureThresholdEventListener temperatureThreshold : temperatureThresholds) {
                    temperatureThreshold.onTemperatureRead(currentTemp, previousTemp, temperatureScale);
                }
            }).start();
        }
    }

    public void addTemperatureThreshold(TemperatureThresholdEventListener temperatureThreshold) {
        temperatureThresholds.add(temperatureThreshold);
    }

    public void clearTemperatureThresholds() {
        temperatureThresholds.clear();
    }

    public Float getCurrentTemperature() {
        return currentTempAtomic.getAcquire();
    }

    public Float getPreviousTemperature() {
        return previousTempAtomic.getAcquire();
    }

    public TemperatureScales getTemperatureScale() {
        return temperatureScale;
    }

    public void setTemperatureScale(TemperatureScales temperatureScale) throws IllegalArgumentException {
        if (temperatureScale == null) {
            throw new IllegalArgumentException("Temperature scale can't be set to 'null'");
        }

        this.temperatureScale = temperatureScale;
    }

}
