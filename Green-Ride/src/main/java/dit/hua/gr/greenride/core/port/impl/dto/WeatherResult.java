package dit.hua.gr.greenride.core.port.impl.dto;

public record WeatherResult(
        String condition,
        double temperatureCelsius
) { }
