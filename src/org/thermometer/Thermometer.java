package org.thermometer;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

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

    public void addTemperatureThreshold(TemperatureThresholdEventListener temperatureThreshold) {
        temperatureThresholds.add(temperatureThreshold);
    }

    @Override
    public void onTemperatureData(float currentTemp) {
        Float previousTemp = currentTempAtomic.getAndSet(currentTemp);
        previousTempAtomic.getAndSet(previousTemp);

        System.out.println(String.format("onTemperatureData - currentTemp: %f    -    previousTemp: %f", currentTemp, previousTemp));

        // Float previousTemp = this.previousTemp.getAndSet(temperature)
        // previousTemp = currentTemp;
        // currentTemp = temperature;
        // Asynchronously event the temperate data we just read on all the thresholds
        // that are currently registered with us.
        //
        // The first time data is read there won't be a previous value so guard against
        // it being `null` and causing a NPE when converting from the primitive wrapper
        if (previousTemp != null
                && Float.compare(previousTemp, currentTemp) != 0
                && !temperatureThresholds.isEmpty()) {

            System.out.println(String.format(
                    "onTemperatureData conditional is true - previousTemp: %f   currentTemp: %f", previousTemp, currentTemp));

            new Thread(() -> {
                for (TemperatureThresholdEventListener temperatureThreshold : temperatureThresholds) {
                    temperatureThreshold.onTemperatureRead(currentTemp, previousTemp, temperatureScale);
                }
            }).start();
        }
    }

    public static void main(String[] args) {
        System.out.println("Is it hot or cold in here?");

        Consumer<Float> thresholdEventCallback = newTemperature -> {
            System.out.println(String.format("TemperatureThresholdEvent - newTemperature read: %f", newTemperature));
        };

        TemperatureThreshold temperatureThreshold = new TemperatureThreshold.TemperatureThresholdBuilder(5.0F, thresholdEventCallback).build();
        Thermometer thermometer = new Thermometer();

        thermometer.addTemperatureThreshold(temperatureThreshold);

        thermometer.onTemperatureData(4.4F);
        thermometer.onTemperatureData(6.7F);
        thermometer.onTemperatureData(8.1F);
        thermometer.onTemperatureData(2.9F);
    }

}
