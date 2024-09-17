package org.thermometer;

enum TemperatureScales {
    FAHRENHEIT_SCALE,
    CELSIUS_SCALE
}

public class Thermometer {

    private TemperatureScales temperatureScale;

    /**
     * Default constructor that creates a thermometer and sets the temperature
     * scale as Celsius.
     */
    public Thermometer() {
        this.temperatureScale = TemperatureScales.CELSIUS_SCALE;
    }

    public static void main(String[] args) {
        System.out.println("Is it hot or cold in here?");
    }
}
