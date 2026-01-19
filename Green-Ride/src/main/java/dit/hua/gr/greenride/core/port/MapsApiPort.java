package dit.hua.gr.greenride.core.port;

import dit.hua.gr.greenride.core.port.impl.dto.RouteRequest;
import dit.hua.gr.greenride.core.port.impl.dto.RouteResult;

public interface MapsApiPort {

    RouteResult getRoute(RouteRequest request);
}
