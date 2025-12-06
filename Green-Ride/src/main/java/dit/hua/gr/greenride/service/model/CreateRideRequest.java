package dit.hua.gr.greenride.service.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Request DTO used when a driver creates a new ride.
 */
public record CreateRideRequest(

        @NotBlank
        String startLocation,

        @NotBlank
        String endLocation,

        /**
         * Departure date and time of the ride.
         */
        @NotNull
        LocalDateTime departureTime,

        /**
         * Number of available seats in the car.
         */
        @Min(1)
        int availableSeats
) {
}
