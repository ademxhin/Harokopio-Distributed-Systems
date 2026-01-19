package dit.hua.gr.greenride.core.port;

import dit.hua.gr.greenride.core.port.impl.dto.WeatherResult;

public interface WeatherApiPort {

    WeatherResult getCurrentWeather(String location);
}
