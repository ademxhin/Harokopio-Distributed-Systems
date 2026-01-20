package dit.hua.gr.greenride.core.port;

import dit.hua.gr.greenride.core.port.model.RouteRequest;
import dit.hua.gr.greenride.core.port.model.RouteResult;

public interface MapsApiPort {

    RouteResult getRoute(RouteRequest request);
}
