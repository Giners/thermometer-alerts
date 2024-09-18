# thermometer-alerts

Java implementation of a thermometer that can read from an external source and alert once thresholds met.

To compile and run the code execute the following commands:

```
cd src
javac org/thermometer/ProjectTests.java
java -ea org.thermometer.ProjectTests
```

`ProjectTests` implements tests that show how to consume/use the following interfaces/types:

- `Thermometer`
- `TemperatureThreshold`
- `TemperatureThresholdBuilder`

When running `org.thermometer.ProjectTests` you will see minimal output as the tests are all currently passing. You can modify the test assertions to see that it is in fact working/not working as intended.

# Design Thoughts/Decisions

These are some thoughts around my design decisions when implementing a `Temperature` type in Java with the following requirements:

- Read temperature of some external source
- Thermometer temp specified in both Fahrenheit and Celsius
- Define temp thresholds
- When threshold reached inform appropriate callers
- Provide granularity to thresholds
  - Provide fluctuation precision to determine significance of movement
  - Identify temperature movement direction (e.g. increasing/decreasing)

## `Thermometer` consumer knows their data best

Considering the broadness of the requirement around `Read temperature of some external source` I decided that the consumer of the `Thermometer` type knows the data best. For example it is unclear where the data is coming from (e.g. external REST API, hardware telemetry sensor, file on disk, etc.) as well as what format the data is encoded in (e.g. binary, Base64, plaintext, etc.). As a result the `Thermometer` implements the `TemperatureDataEventListener` interface which specifies the method signature `public void onTemperatureData(float currentTemp)`. This signature will be invoked by the consume of the `Thermometer` when they hook up the external data source. Additionally the consumer will specify the data in a `float` thus needing to convert the data themselves as a result of operating under the assumption "the consumer knows the data best."

## Calling back when temperature thresholds reached

While the consumer of the `Thermometer` type knows the data best, the `Thermometer` instance could be exposed to others as a way to abstract away the raw temperature data. Instead those that are interested in knowing when a temperature has reached/crossed a certain threshold create instances of `TemperatureThreshold` with a callback. The `TemperatureThreshold` implements the `TemperatureThresholdEventListener` which provides a method signature of `public void onTemperatureRead(float newTemperature, float previousTemperature, TemperatureScales temperatureScale)`. Instances of `TemperatureThreshold` are then provided to the `Thermometer` where each piece of new temperature data is passed to `onTemperatureRead` and the callback is invoked if a temperature threshold has been reached/crossed.

# Future Design Thoughts/Considerations

## Concurrency/Thread Safety/Optimization

Given the broadness of the requirements the current implementation of the `Thermometer` type spins up a new thread for each new piece of temperature data being read. This approach - while naive - was taken to show a basic pattern of asynchronously processing new temperature data and triggering thresholds. Given the frequency at which data could be read this could have severe performance and resource consumption problems within the JVM.

Future work/considerations should be done to:

- Ensure thread safety with components/data members for any critical data/sections
- Use a well defined thread pool with a set amount of threads

## Improve UX with Customer Feedback/Use Cases

I believe that the UX of these types/interfaces can be improved with developing a customer feedback loop. Talking with a customer I would want to better understand their use cases and what requirements they have that need to be met to satisfy them. With this feedback the types/interfaces that we develop could better fit their needs. Additionally understanding where the data is coming from and what format it is in - maybe different/multiples for both source and format - would reduce the overhead of instantiating a `Thermometer` type as well.
