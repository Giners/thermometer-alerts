package org.thermometer;

public class ProjectTests {

    public static void main(String[] args) {
        // System.out.println("Is it hot or cold in here?");

        // Consumer<Float> thresholdEventCallback = newTemperature -> {
        //     System.out.println(String.format("TemperatureThresholdEvent - newTemperature read: %f", newTemperature));
        // };
        // TemperatureThreshold temperatureThreshold = new TemperatureThreshold.TemperatureThresholdBuilder(5.0F, thresholdEventCallback).build();
        // Thermometer thermometer = new Thermometer();
        // thermometer.addTemperatureThreshold(temperatureThreshold);
        // thermometer.onTemperatureData(4.4F);
        // thermometer.onTemperatureData(6.7F);
        // thermometer.onTemperatureData(8.1F);
        // thermometer.onTemperatureData(2.9F);
        testDefaultThermometerConstructor();
        testSetters();
        testOnTemperatureDataSetsTemperatures();
    }

    /**
     * Tests that the values are set properly on the Thermometer type instance
     * when using the default constructor
     */
    public static void testDefaultThermometerConstructor() {
        Thermometer thermometer = new Thermometer();

        assert thermometer.getTemperatureScale() == TemperatureScales.CELSIUS_SCALE :
                "Constructor didn't set proper temperature scale";

        assert thermometer.getCurrentTemperature() == null :
                "Constructor didn't properly set current temperature";

        assert thermometer.getPreviousTemperature() == null :
                "Constructor didn't properly set previous temperature";
    }

    public static void testSetters() {
        Thermometer thermometer = new Thermometer();

        TemperatureScales currentTemperatureScale = thermometer.getTemperatureScale();

        // Take the opposite of whatever value the current temperature scale is set at
        // for the expected test value
        TemperatureScales expectedTemperatureScale
                = currentTemperatureScale == TemperatureScales.CELSIUS_SCALE
                        ? TemperatureScales.FAHRENHEIT_SCALE : TemperatureScales.CELSIUS_SCALE;

        thermometer.setTemperatureScale(expectedTemperatureScale);

        assert thermometer.getTemperatureScale() == expectedTemperatureScale :
                "Failed to set temperature scale";

        // Verify that setting the temperature scale to a 'null' value fails
        try {
            thermometer.setTemperatureScale(null);
            assert true == false : "Failed to catch an exception when setting 'null' as the temperature scale";
        } catch (IllegalArgumentException e) {
            // Purposely left blank - if we get here it means we properly caught an
            // exception and didn't execute an assert statement that will always fail
        }
    }

    public static void testOnTemperatureDataSetsTemperatures() {
        Thermometer thermometer = new Thermometer();

        // Verify the first iteration of 'onTemperatureData' being invoked which
        // should should result in the previous value still being null
        float temperatureData1 = 1.0F;

        Float expectedPreviousTemp = null;
        Float expectedCurrentTemp = temperatureData1;

        thermometer.onTemperatureData(temperatureData1);

        assert thermometer.getPreviousTemperature() == expectedPreviousTemp :
                String.format("Previous temp not properly set when reading a temperature of '%f'", temperatureData1);

        assert Float.compare(thermometer.getCurrentTemperature(), expectedCurrentTemp) == 0 :
                String.format("Current temp not properly set when reading a temperature of '%f'", temperatureData1);

        // Verify the second iteration of 'onTemperatureData' being invoked
        float temperatureData2 = 7.0F;

        expectedPreviousTemp = temperatureData1;
        expectedCurrentTemp = temperatureData2;

        thermometer.onTemperatureData(temperatureData2);

        assert Float.compare(thermometer.getPreviousTemperature(), expectedPreviousTemp) == 0 :
                String.format("Previous temp not properly set when reading a temperature of '%f'", temperatureData2);

        assert Float.compare(thermometer.getCurrentTemperature(), expectedCurrentTemp) == 0 :
                String.format("Current temp not properly set when reading a temperature of '%f'", temperatureData2);

        // Verify the third iteration of 'onTemperatureData' being invoked
        float temperatureData3 = -5.0F;

        expectedPreviousTemp = temperatureData2;
        expectedCurrentTemp = temperatureData3;

        thermometer.onTemperatureData(temperatureData3);

        assert Float.compare(thermometer.getPreviousTemperature(), expectedPreviousTemp) == 0 :
                String.format("Previous temp not properly set when reading a temperature of '%f'", temperatureData3);

        assert Float.compare(thermometer.getCurrentTemperature(), expectedCurrentTemp) == 0 :
                String.format("Current temp not properly set when reading a temperature of '%f'", temperatureData3);
    }

}
