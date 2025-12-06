package dit.hua.gr.greenride.service.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO used when a passenger books a seat on a ride.
 */
public record CreateBookingRequest(

        @NotNull
        Long rideId,

        /**
         * Number of seats to book. For the current system this will
         * typically be 1, but the field allows future extension.
         */
        @Min(1)
        int seats
) {
}
