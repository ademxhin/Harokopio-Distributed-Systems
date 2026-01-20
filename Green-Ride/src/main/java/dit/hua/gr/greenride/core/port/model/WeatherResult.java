package dit.hua.gr.greenride.core.port.model;

public record WeatherResult(
        String condition,
        double temperatureCelsius
) { }
