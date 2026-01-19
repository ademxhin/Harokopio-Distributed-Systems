package dit.hua.gr.greenride.core.port.impl.dto;

public record OpenMeteoResponse(
        CurrentWeather current_weather
) {

    public record CurrentWeather(
            double temperature,
            int weathercode
    ) { }
}
