package org.thermometer;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class ProjectTests {

    public static void main(String[] args) {
        testDefaultThermometerConstructor();
        testSetters();
        testOnTemperatureDataSetsTemperatures();
        testTemperatureThresholdBuilder();
        testIsTempThresholdReached();
        testIsTempDifferencePrecise();
        testIsTempMovementCorrectDirection();
        testThresholdIsTriggered();
    }

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

    public static void testTemperatureThresholdBuilder() {
        // Verify that the default values are set with the required values when
        // using the TemperatureThresholdBuilder type to get an instance of a
        // TemperatureThreshold
        float expectedTemperatureThreshold = 1.0F;

        Consumer<Float> thresholdEventCallback = newTemperature -> {
            // Purposely left blank - will assert this functionality separtely when
            // verifying thresholds
        };

        TemperatureThreshold temperatureThreshold
                = new TemperatureThreshold.TemperatureThresholdBuilder(expectedTemperatureThreshold, thresholdEventCallback).build();

        assert Float.compare(temperatureThreshold.getTemperatureThreshold(), expectedTemperatureThreshold) == 0 :
                "TemperatureThresholdBuilder didn't properly set default value of the temperature threshold";

        assert temperatureThreshold.getTemperatureScale() == TemperatureThreshold.DEFAULT_TEMPERATURE_SCALE :
                "TemperatureThresholdBuilder didn't properly set default value of the temperature scale";

        assert Float.compare(temperatureThreshold.getThresholdTriggerPrecision(), TemperatureThreshold.DEFAULT_THRESHOLD_TRIGGER_PRECISION) == 0 :
                "TemperatureThresholdBuilder didn't properly set default value of the temperature threshold precision";

        assert temperatureThreshold.getThresholdTriggerDirection() == TemperatureThreshold.DEFAULT_THRESHOLD_TRIGGER_DIRECTION :
                "TemperatureThresholdBuilder didn't properly set default value of the temperature threshold trigger direction";

        // Verify that setting alternative values using the setters on the 
        // TemperatureThresholdBuilder type sets the proper values on the
        // instance of the TemperatureThreshold
        TemperatureScales expectedTemperatureScale
                = TemperatureThreshold.DEFAULT_TEMPERATURE_SCALE == TemperatureScales.CELSIUS_SCALE
                        ? TemperatureScales.FAHRENHEIT_SCALE : TemperatureScales.CELSIUS_SCALE;

        float expectedThresholdTriggerPrecision = 5.0F;

        ThresholdTriggerDirections expectedThresholdTriggerDirection = ThresholdTriggerDirections.DECREASING_TEMP;

        temperatureThreshold
                = new TemperatureThreshold.TemperatureThresholdBuilder(expectedTemperatureThreshold, thresholdEventCallback)
                        .temperatureScale(expectedTemperatureScale)
                        .thresholdTriggerPrecision(expectedThresholdTriggerPrecision)
                        .thresholdTriggerDirection(expectedThresholdTriggerDirection)
                        .build();

        assert temperatureThreshold.getTemperatureScale() == expectedTemperatureScale :
                "TemperatureThresholdBuilder didn't properly set the temperature scale";

        assert Float.compare(temperatureThreshold.getThresholdTriggerPrecision(), expectedThresholdTriggerPrecision) == 0 :
                "TemperatureThresholdBuilder didn't properly set the temperature threshold precision";

        assert temperatureThreshold.getThresholdTriggerDirection() == expectedThresholdTriggerDirection :
                "TemperatureThresholdBuilder didn't properly set the temperature threshold trigger direction";
    }

    public static void testIsTempThresholdReached() {
        float testTemperatureThreshold = 1.0F;

        Consumer<Float> thresholdEventCallback = newTemperature -> {
            // Purposely left blank - will assert this functionality separtely when
            // verifying thresholds
        };

        TemperatureThreshold temperatureThreshold
                = new TemperatureThreshold.TemperatureThresholdBuilder(testTemperatureThreshold, thresholdEventCallback).build();

        // Verify that an increasing temperature crossing the threshold is identified
        float previousTemp = testTemperatureThreshold - 1.0F;
        float newTemp = testTemperatureThreshold + 1.0F;

        assert temperatureThreshold.isTempThresholdReached(newTemp, previousTemp) == true :
                String.format("Failed to identify an increasing temperature crossing the threshold");

        // Verify that an increasing temperature landing on the threshold is identified
        previousTemp = testTemperatureThreshold - 1.0F;
        newTemp = testTemperatureThreshold;

        assert temperatureThreshold.isTempThresholdReached(newTemp, previousTemp) == true :
                String.format("Failed to identify an increasing temperature landing on the threshold");

        // Verify that a decresing temperature crossing the threshold is identified
        previousTemp = testTemperatureThreshold + 1.0F;
        newTemp = testTemperatureThreshold - 1.0F;

        assert temperatureThreshold.isTempThresholdReached(newTemp, previousTemp) == true :
                String.format("Failed to identify a decreasing temperature crossing the threshold");

        // Verify that a decresing temperature landing on the threshold is identified
        previousTemp = testTemperatureThreshold + 1.0F;
        newTemp = testTemperatureThreshold;

        assert temperatureThreshold.isTempThresholdReached(newTemp, previousTemp) == true :
                String.format("Failed to identify a decreasing temperature landing on the threshold");
    }

    public static void testIsTempDifferencePrecise() {
        float testTemperatureThreshold = 1.0F;
        float testThresholdTriggerPrecision = 5.0F;

        Consumer<Float> thresholdEventCallback = newTemperature -> {
            // Purposely left blank - will assert this functionality separtely when
            // verifying thresholds
        };

        TemperatureThreshold temperatureThreshold
                = new TemperatureThreshold.TemperatureThresholdBuilder(testTemperatureThreshold, thresholdEventCallback)
                        .thresholdTriggerPrecision(testThresholdTriggerPrecision)
                        .build();

        // Verify that a movement isn't precise enough
        float previousTemp = testTemperatureThreshold;
        float newTemp = testTemperatureThreshold + (testThresholdTriggerPrecision - 1.0F);

        assert temperatureThreshold.isTempDifferencePrecise(newTemp, previousTemp) == false :
                String.format("Failed to identify a movement that isn't precise enough");

        // Verify that a movement is precise enough
        previousTemp = testTemperatureThreshold;
        newTemp = testTemperatureThreshold + (testThresholdTriggerPrecision + 1.0F);

        assert temperatureThreshold.isTempDifferencePrecise(newTemp, previousTemp) == true :
                String.format("Failed to identify a movement that is precise enough");
    }

    public static void testIsTempMovementCorrectDirection() {
        float testTemperatureThreshold = 1.0F;
        ThresholdTriggerDirections testThresholdTriggerDirection = ThresholdTriggerDirections.INCREASING_TEMP;

        Consumer<Float> thresholdEventCallback = newTemperature -> {
            // Purposely left blank - will assert this functionality separtely when
            // verifying thresholds
        };

        TemperatureThreshold temperatureThreshold
                = new TemperatureThreshold.TemperatureThresholdBuilder(testTemperatureThreshold, thresholdEventCallback)
                        .thresholdTriggerDirection(testThresholdTriggerDirection)
                        .build();

        // Verify an increasing movement
        float previousTemp = testTemperatureThreshold;
        float newTemp = testTemperatureThreshold + 1.0F;

        assert temperatureThreshold.isTempMovementCorrectDirection(newTemp, previousTemp) == true :
                String.format("Failed to identify an increasing temp movement");

        // Verify a increasing movement
        testThresholdTriggerDirection = ThresholdTriggerDirections.DECREASING_TEMP;
        temperatureThreshold
                = new TemperatureThreshold.TemperatureThresholdBuilder(testTemperatureThreshold, thresholdEventCallback)
                        .thresholdTriggerDirection(testThresholdTriggerDirection)
                        .build();

        previousTemp = testTemperatureThreshold;
        newTemp = testTemperatureThreshold - 1.0F;

        assert temperatureThreshold.isTempMovementCorrectDirection(newTemp, previousTemp) == true :
                String.format("Failed to identify an decreasing temp movement");
    }

    public static void testThresholdIsTriggered() {
        float expectedTemperatureThreshold = 1.0F;

        AtomicBoolean thresholdHasBeenTriggered = new AtomicBoolean(false);

        Consumer<Float> thresholdEventCallback = newTemperature -> {
            // Purposely left blank - will assert this functionality separtely when
            // verifying thresholds
            thresholdHasBeenTriggered.getAndSet(true);
            System.out.println("Test threshold event triggered");
        };

        TemperatureThreshold temperatureThreshold
                = new TemperatureThreshold.TemperatureThresholdBuilder(expectedTemperatureThreshold, thresholdEventCallback).build();

        Thermometer thermometer = new Thermometer();
        thermometer.addTemperatureThreshold(temperatureThreshold);

        // Provide several increasing pieces of tempeature data to verify that
        // we cross a threshold
        float temperatureData = expectedTemperatureThreshold - 3.0F;

        for (int temperatureDataCount = 0; temperatureDataCount < 10; temperatureDataCount++) {
            thermometer.onTemperatureData(temperatureData);

            temperatureData += 1.0F;
        }

        // At this point the atomic of wheter we have triggered our threshold
        // should be set
        assert thresholdHasBeenTriggered.get() == true : "Failed to trigger threshold on thermometer";
    }

}
