package dit.hua.gr.greenride.core.port;

import dit.hua.gr.greenride.core.port.impl.dto.RouteRequest;
import dit.hua.gr.greenride.core.port.impl.dto.RouteResult;

/**
 * Port for consuming an external Maps/Geolocation service (black box).
 */
public interface MapsApiPort {

    RouteResult getRoute(RouteRequest request);
}
