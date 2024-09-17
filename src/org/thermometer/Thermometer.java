package org.thermometer;

import java.util.ArrayList;

enum TemperatureScales {
    FAHRENHEIT_SCALE,
    CELSIUS_SCALE
}

public class Thermometer implements TemperatureDataEventListener {

    private TemperatureScales temperatureScale = TemperatureScales.CELSIUS_SCALE;

    private Float currentTemp = null;
    private Float previousTemp = null;

    private ArrayList<TemperatureThresholdEventListener> temperatureThresholds = new ArrayList<TemperatureThresholdEventListener>();

    /**
     * Default constructor that creates a thermometer and sets the temperature
     * scale as Celsius.
     */
    public Thermometer() {
        this.temperatureScale = TemperatureScales.CELSIUS_SCALE;
    }

    public void addTemperatureThreshold(TemperatureThresholdEventListener temperatureThreshold) {
        this.temperatureThresholds.add(temperatureThreshold);
    }

    @Override
    public void onTemperatureData(float temperature) {
        this.previousTemp = this.currentTemp;
        this.currentTemp = temperature;

        // Asynchronously event the temperate data we just read on all the thresholds
        // that are currently registered with us.
        //
        // The first time data is read there won't be a previous value so guard against
        // it being `null` and causing a NPE when converting from the primitive wrapper
        if (this.previousTemp != null && !this.temperatureThresholds.isEmpty()) {
            new Thread(() -> {
                for (TemperatureThresholdEventListener temperatureThreshold : temperatureThresholds) {
                    temperatureThreshold.onTemperatureRead(this.currentTemp, this.previousTemp);
                }
            }).start();
        }
    }

    public static void main(String[] args) {
        System.out.println("Is it hot or cold in here?");
        TemperatureThreshold temperatureThreshold = new TemperatureThreshold(5.0F);
        Thermometer thermometer = new Thermometer();

        thermometer.addTemperatureThreshold(temperatureThreshold);

        thermometer.onTemperatureData(5.4F);
        thermometer.onTemperatureData(6.7F);
    }

}
